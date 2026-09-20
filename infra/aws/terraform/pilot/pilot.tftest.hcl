mock_provider "aws" {
  mock_data "aws_availability_zones" {
    defaults = {
      names = ["eu-west-1a", "eu-west-1b", "eu-west-1c"]
    }
  }

  mock_data "aws_partition" {
    defaults = {
      partition          = "aws"
      dns_suffix         = "amazonaws.com"
      reverse_dns_prefix = "com.amazonaws"
    }
  }

  mock_resource "aws_db_instance" {
    defaults = {
      address  = "imperator-test.abcdefghijkl.eu-west-1.rds.amazonaws.com"
      port     = 5432
      endpoint = "imperator-test.abcdefghijkl.eu-west-1.rds.amazonaws.com:5432"
      master_user_secret = [{
        kms_key_id    = "arn:aws:kms:eu-west-1:123456789012:key/00000000-0000-0000-0000-000000000000"
        secret_arn    = "arn:aws:secretsmanager:eu-west-1:123456789012:secret:rds!db-test"
        secret_status = "active"
      }]
    }
  }
}

run "inactive_pilot_plan" {
  command = plan

  override_data {
    target = data.aws_iam_policy_document.ecs_task_trust
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ecs-tasks.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}"
    }
  }

  override_data {
    target = data.aws_iam_policy_document.bedrock[0]
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"bedrock:InvokeModel\",\"Resource\":\"arn:aws:bedrock:eu-west-1:123456789012:application-inference-profile/test-profile\"}]}"
    }
  }

  override_data {
    target = data.aws_iam_policy_document.aws_evidence[0]
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"sts:GetCallerIdentity\",\"Resource\":\"*\"}]}"
    }
  }

  override_data {
    target = data.aws_iam_policy_document.backend_secret
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"secretsmanager:GetSecretValue\",\"Resource\":\"arn:aws:secretsmanager:eu-west-1:123456789012:secret:imperator/pilot/database-app-Test\"}]}"
    }
  }

  override_data {
    target = data.aws_iam_policy_document.database_job_secrets
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"secretsmanager:GetSecretValue\",\"Resource\":\"*\"}]}"
    }
  }

  override_data {
    target = data.aws_iam_policy_document.audit_key
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"arn:aws:iam::123456789012:root\"},\"Action\":\"kms:*\",\"Resource\":\"*\"}]}"
    }
  }

  variables {
    aws_region                 = "eu-west-1"
    expected_account_id        = "123456789012"
    environment                = "pilot"
    allowed_https_egress_cidrs = ["203.0.113.10/32"]

    domain_name     = "imperator.example.com"
    route53_zone_id = "Z0123456789EXAMPLE"
    certificate_arn = "arn:aws:acm:eu-west-1:123456789012:certificate/00000000-0000-0000-0000-000000000000"

    backend_image_uri        = "123456789012.dkr.ecr.eu-west-1.amazonaws.com/imperator/pilot/backend@sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    frontend_image_uri       = "123456789012.dkr.ecr.eu-west-1.amazonaws.com/imperator/pilot/frontend@sha256:bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
    migration_image_uri      = "123456789012.dkr.ecr.eu-west-1.amazonaws.com/imperator/pilot/migration@sha256:cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
    database_admin_image_uri = "123456789012.dkr.ecr.eu-west-1.amazonaws.com/imperator/pilot/database-admin@sha256:dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"

    application_database_secret_arn = "arn:aws:secretsmanager:eu-west-1:123456789012:secret:imperator/pilot/database-app-Test"

    jwt_issuer_uri  = "https://identity.example.com/realms/imperator"
    jwt_jwk_set_uri = "https://identity.example.com/realms/imperator/protocol/openid-connect/certs"

    bedrock_enabled  = true
    bedrock_model_id = "eu.anthropic.claude-sonnet-test-v1:0"
    bedrock_invoke_resource_arns = [
      "arn:aws:bedrock:eu-west-1:123456789012:application-inference-profile/test-profile",
    ]

    monthly_budget_usd = 130
    budget_alert_email = "operator@example.com"
    activate_services  = false
  }

  assert {
    condition     = aws_ecs_service.backend.desired_count == 0 && aws_ecs_service.frontend.desired_count == 0
    error_message = "Services must remain stopped before the database one-shot jobs pass."
  }

  assert {
    condition     = aws_db_instance.main.publicly_accessible == false
    error_message = "RDS must remain private."
  }

  assert {
    condition     = aws_ecs_service.backend.network_configuration[0].assign_public_ip == false
    error_message = "Backend tasks must not receive public IP addresses."
  }
}
