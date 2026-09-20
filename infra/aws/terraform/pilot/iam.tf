data "aws_iam_policy_document" "ecs_task_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "task_execution" {
  for_each = toset([
    "frontend",
    "backend",
    "database-job",
  ])

  name               = "${local.name_prefix}-${each.key}-execution"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_trust.json
}

resource "aws_iam_role_policy_attachment" "task_execution" {
  for_each = aws_iam_role.task_execution

  role       = each.value.name
  policy_arn = "arn:${data.aws_partition.current.partition}:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

data "aws_iam_policy_document" "backend_secret" {
  statement {
    sid       = "ReadApplicationDatabaseCredential"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [var.application_database_secret_arn]
  }

  dynamic "statement" {
    for_each = var.application_database_kms_key_arn == null ? [] : [var.application_database_kms_key_arn]
    content {
      sid       = "DecryptApplicationDatabaseSecret"
      actions   = ["kms:Decrypt"]
      resources = [statement.value]
    }
  }
}

resource "aws_iam_role_policy" "backend_secret" {
  name   = "${local.name_prefix}-application-database-secret"
  role   = aws_iam_role.task_execution["backend"].id
  policy = data.aws_iam_policy_document.backend_secret.json
}

data "aws_iam_policy_document" "database_job_secrets" {
  statement {
    sid       = "ReadOwnerAndApplicationDatabaseCredentials"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [var.application_database_secret_arn, aws_db_instance.main.master_user_secret[0].secret_arn]
  }

  dynamic "statement" {
    for_each = var.application_database_kms_key_arn == null ? [] : [var.application_database_kms_key_arn]
    content {
      sid       = "DecryptApplicationDatabaseSecret"
      actions   = ["kms:Decrypt"]
      resources = [statement.value]
    }
  }
}

resource "aws_iam_role_policy" "database_job_secrets" {
  name   = "${local.name_prefix}-database-job-secrets"
  role   = aws_iam_role.task_execution["database-job"].id
  policy = data.aws_iam_policy_document.database_job_secrets.json
}

resource "aws_iam_role" "backend_task" {
  name               = "${local.name_prefix}-backend-task"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_trust.json
}

data "aws_iam_policy_document" "bedrock" {
  count = var.bedrock_enabled ? 1 : 0

  statement {
    sid       = "InvokeApprovedBedrockModels"
    actions   = ["bedrock:InvokeModel"]
    resources = var.bedrock_invoke_resource_arns
  }
}

resource "aws_iam_role_policy" "bedrock" {
  count = var.bedrock_enabled ? 1 : 0

  name   = "${local.name_prefix}-bedrock-invoke"
  role   = aws_iam_role.backend_task.id
  policy = data.aws_iam_policy_document.bedrock[0].json
}

data "aws_iam_policy_document" "aws_evidence" {
  count = var.aws_evidence_enabled ? 1 : 0

  statement {
    sid       = "VerifyCallerIdentity"
    actions   = ["sts:GetCallerIdentity"]
    resources = ["*"]
  }

  statement {
    sid = "ReadBoundedAwsEvidence"
    actions = [
      "ce:GetCostAndUsage",
      "cloudwatch:GetMetricData",
      "cloudwatch:GetMetricStatistics",
      "cloudwatch:ListMetrics",
      "tag:GetResources",
    ]
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "aws_evidence" {
  count = var.aws_evidence_enabled ? 1 : 0

  name   = "${local.name_prefix}-read-evidence"
  role   = aws_iam_role.backend_task.id
  policy = data.aws_iam_policy_document.aws_evidence[0].json
}

resource "aws_iam_role" "frontend_task" {
  name               = "${local.name_prefix}-frontend-task"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_trust.json
}

resource "aws_iam_role" "database_job_task" {
  name               = "${local.name_prefix}-database-job-task"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_trust.json
}
