resource "aws_cloudwatch_log_group" "backend" {
  name              = local.backend_log_group
  retention_in_days = var.log_retention_days
}

resource "aws_cloudwatch_log_group" "frontend" {
  name              = local.frontend_log_group
  retention_in_days = var.log_retention_days
}

resource "aws_cloudwatch_log_group" "database_jobs" {
  name              = local.migration_log_group
  retention_in_days = var.log_retention_days
}

resource "aws_ecs_cluster" "main" {
  name = local.name_prefix

  setting {
    name  = "containerInsights"
    value = "enhanced"
  }
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = ["FARGATE", "FARGATE_SPOT"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = 1
  }
}

resource "aws_ecs_task_definition" "backend" {
  family                   = "${local.name_prefix}-backend"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 512
  memory                   = 1024
  execution_role_arn       = aws_iam_role.task_execution["backend"].arn
  task_role_arn            = aws_iam_role.backend_task.arn

  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([{
    name      = "backend"
    image     = var.backend_image_uri
    essential = true
    user      = "10001:10001"

    readonlyRootFilesystem = true

    portMappings = [{
      name          = "http"
      containerPort = 8080
      hostPort      = 8080
      protocol      = "tcp"
      appProtocol   = "http"
    }]

    environment = [
      { name = "IMPERATOR_AWS_ENABLED", value = tostring(var.aws_evidence_enabled) },
      { name = "IMPERATOR_AWS_EXPECTED_ACCOUNT_ID", value = var.expected_account_id },
      { name = "IMPERATOR_AWS_REGION", value = var.aws_region },
      { name = "IMPERATOR_BEDROCK_ENABLED", value = tostring(var.bedrock_enabled) },
      { name = "IMPERATOR_BEDROCK_MODEL_ID", value = var.bedrock_model_id },
      { name = "IMPERATOR_BEDROCK_REGION", value = var.aws_region },
      { name = "IMPERATOR_GITHUB_ENABLED", value = "false" },
      { name = "IMPERATOR_POSTGRESQL_ENABLED", value = "true" },
      { name = "IMPERATOR_POSTGRESQL_URL", value = "jdbc:postgresql://${aws_db_instance.main.address}:${aws_db_instance.main.port}/imperator?sslmode=require" },
      { name = "IMPERATOR_POSTGRESQL_USERNAME", value = "imperator_app" },
      { name = "JAVA_TOOL_OPTIONS", value = "-XX:MaxRAMPercentage=75.0 -Djava.io.tmpdir=/tmp" },
      { name = "SERVER_PORT", value = "8080" },
      { name = "SPRING_MAIN_BANNER_MODE", value = "off" },
      { name = "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_AUDIENCES", value = "imperator-api" },
      { name = "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI", value = var.jwt_issuer_uri },
      { name = "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI", value = var.jwt_jwk_set_uri },
      { name = "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWS_ALGORITHMS", value = "RS256" },
    ]

    secrets = [{
      name      = "IMPERATOR_POSTGRESQL_PASSWORD"
      valueFrom = var.application_database_secret_arn
    }]

    linuxParameters = {
      initProcessEnabled = true
      capabilities       = { drop = ["ALL"] }
      tmpfs = [{
        containerPath = "/tmp"
        size          = 64
        mountOptions  = ["rw", "noexec", "nosuid", "nodev"]
      }]
    }

    healthCheck = {
      command     = ["CMD-SHELL", "bash -ec 'exec 3<>/dev/tcp/127.0.0.1/8080; printf \"GET /readyz HTTP/1.1\\r\\nHost: localhost\\r\\nConnection: close\\r\\n\\r\\n\" >&3; head -n 1 <&3 | grep -q \" 200 \"'"]
      interval    = 30
      timeout     = 5
      retries     = 3
      startPeriod = 30
    }

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.backend.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "ecs"
      }
    }
  }])
}

resource "aws_ecs_task_definition" "frontend" {
  family                   = "${local.name_prefix}-frontend"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.task_execution["frontend"].arn
  task_role_arn            = aws_iam_role.frontend_task.arn

  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([{
    name      = "frontend"
    image     = var.frontend_image_uri
    essential = true
    user      = "1000:1000"

    readonlyRootFilesystem = true

    portMappings = [{
      name          = "http"
      containerPort = 3000
      hostPort      = 3000
      protocol      = "tcp"
      appProtocol   = "http"
    }]

    environment = [
      { name = "HOSTNAME", value = "0.0.0.0" },
      { name = "IMPERATOR_API_ORIGIN", value = local.application_origin },
      { name = "IMPERATOR_RUNTIME_PROFILE", value = "aws-pilot" },
      { name = "NEXT_TELEMETRY_DISABLED", value = "1" },
      { name = "NODE_ENV", value = "production" },
      { name = "PORT", value = "3000" },
    ]

    linuxParameters = {
      initProcessEnabled = true
      capabilities       = { drop = ["ALL"] }
      tmpfs = [{
        containerPath = "/tmp"
        size          = 32
        mountOptions  = ["rw", "noexec", "nosuid", "nodev"]
      }]
    }

    healthCheck = {
      command     = ["CMD", "node", "-e", "fetch('http://127.0.0.1:3000/').then(r=>{if(!r.ok)process.exit(1)}).catch(()=>process.exit(1))"]
      interval    = 30
      timeout     = 5
      retries     = 3
      startPeriod = 20
    }

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.frontend.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "ecs"
      }
    }
  }])
}

resource "aws_ecs_task_definition" "migration" {
  family                   = "${local.name_prefix}-migration"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.task_execution["database-job"].arn
  task_role_arn            = aws_iam_role.database_job_task.arn

  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([{
    name      = "migration"
    image     = var.migration_image_uri
    essential = true
    command   = ["migrate"]

    readonlyRootFilesystem = true

    environment = [
      { name = "FLYWAY_CLEAN_DISABLED", value = "true" },
      { name = "FLYWAY_LOCATIONS", value = "filesystem:/flyway/sql" },
      { name = "FLYWAY_URL", value = "jdbc:postgresql://${aws_db_instance.main.address}:${aws_db_instance.main.port}/imperator?sslmode=require" },
      { name = "FLYWAY_USER", value = "imperator_owner" },
      { name = "FLYWAY_VALIDATE_MIGRATION_NAMING", value = "true" },
    ]

    secrets = [{
      name      = "FLYWAY_PASSWORD"
      valueFrom = "${aws_db_instance.main.master_user_secret[0].secret_arn}:password::"
    }]

    linuxParameters = {
      capabilities = { drop = ["ALL"] }
      tmpfs = [{
        containerPath = "/tmp"
        size          = 32
        mountOptions  = ["rw", "noexec", "nosuid", "nodev"]
      }]
    }

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.database_jobs.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "migration"
      }
    }
  }])
}

resource "aws_ecs_task_definition" "database_permissions" {
  family                   = "${local.name_prefix}-database-permissions"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.task_execution["database-job"].arn
  task_role_arn            = aws_iam_role.database_job_task.arn

  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }

  container_definitions = jsonencode([{
    name       = "database-permissions"
    image      = var.database_admin_image_uri
    essential  = true
    user       = "999:999"
    entryPoint = ["/opt/imperator/provision-app-role.sh"]

    readonlyRootFilesystem = true

    environment = [
      { name = "IMPERATOR_POSTGRESQL_DATABASE", value = "imperator" },
      { name = "IMPERATOR_POSTGRESQL_HOST", value = aws_db_instance.main.address },
      { name = "IMPERATOR_POSTGRESQL_OWNER_USERNAME", value = "imperator_owner" },
      { name = "IMPERATOR_POSTGRESQL_PORT", value = tostring(aws_db_instance.main.port) },
    ]

    secrets = [
      {
        name      = "PGPASSWORD"
        valueFrom = "${aws_db_instance.main.master_user_secret[0].secret_arn}:password::"
      },
      {
        name      = "IMPERATOR_APP_PASSWORD"
        valueFrom = var.application_database_secret_arn
      },
    ]

    linuxParameters = {
      capabilities = { drop = ["ALL"] }
      tmpfs = [{
        containerPath = "/tmp"
        size          = 16
        mountOptions  = ["rw", "noexec", "nosuid", "nodev"]
      }]
    }

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.database_jobs.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "permissions"
      }
    }
  }])
}

resource "aws_ecs_service" "backend" {
  name            = "backend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = var.activate_services ? 1 : 0
  launch_type     = "FARGATE"

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  deployment_maximum_percent         = 200
  deployment_minimum_healthy_percent = var.activate_services ? 100 : 0
  health_check_grace_period_seconds  = 60
  enable_execute_command             = false

  network_configuration {
    assign_public_ip = false
    subnets          = aws_subnet.private[*].id
    security_groups  = [aws_security_group.backend.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend.arn
    container_name   = "backend"
    container_port   = 8080
  }

  depends_on = [aws_lb_listener_rule.api]
}

resource "aws_ecs_service" "frontend" {
  name            = "frontend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = var.activate_services ? 1 : 0
  launch_type     = "FARGATE"

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  deployment_maximum_percent         = 200
  deployment_minimum_healthy_percent = var.activate_services ? 100 : 0
  health_check_grace_period_seconds  = 60
  enable_execute_command             = false

  network_configuration {
    assign_public_ip = false
    subnets          = aws_subnet.private[*].id
    security_groups  = [aws_security_group.frontend.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.frontend.arn
    container_name   = "frontend"
    container_port   = 3000
  }

  depends_on = [aws_lb_listener.https]
}
