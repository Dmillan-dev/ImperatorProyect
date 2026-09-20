variable "aws_region" {
  description = "Frozen AWS Region for the pilot and Bedrock runtime."
  type        = string
  default     = "eu-west-1"
}

variable "expected_account_id" {
  description = "Twelve-digit AWS account ID allowed by the provider."
  type        = string

  validation {
    condition     = can(regex("^[0-9]{12}$", var.expected_account_id))
    error_message = "expected_account_id must contain exactly 12 digits."
  }
}

variable "environment" {
  description = "Environment suffix. The initial deployment is the bounded pilot only."
  type        = string
  default     = "pilot"

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]{1,15}$", var.environment))
    error_message = "environment must contain 2-16 lowercase letters, numbers or hyphens."
  }
}

variable "vpc_cidr" {
  description = "Pilot VPC CIDR; four non-overlapping /24 subnets are derived from it."
  type        = string
  default     = "10.42.0.0/20"

  validation {
    condition     = can(cidrsubnet(var.vpc_cidr, 4, 0))
    error_message = "vpc_cidr must be a valid CIDR with room for derived subnets."
  }
}

variable "availability_zones" {
  description = "Exactly two AZs, or an empty list to select the first two available AZs."
  type        = list(string)
  default     = []

  validation {
    condition     = length(var.availability_zones) == 0 || length(var.availability_zones) == 2
    error_message = "availability_zones must be empty or contain exactly two AZs."
  }
}

variable "allowed_https_egress_cidrs" {
  description = "Reviewed IPv4 CIDRs for Keycloak and required AWS HTTPS endpoints; never 0.0.0.0/0."
  type        = list(string)

  validation {
    condition = (
      length(var.allowed_https_egress_cidrs) > 0 &&
      alltrue([for cidr in var.allowed_https_egress_cidrs : can(cidrhost(cidr, 0))]) &&
      !contains(var.allowed_https_egress_cidrs, "0.0.0.0/0")
    )
    error_message = "allowed_https_egress_cidrs must contain valid reviewed CIDRs and must not contain 0.0.0.0/0."
  }
}

variable "domain_name" {
  description = "Public HTTPS hostname served by the Application Load Balancer."
  type        = string

  validation {
    condition     = can(regex("^[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)+$", var.domain_name))
    error_message = "domain_name must be a lower-case DNS hostname."
  }
}

variable "route53_zone_id" {
  description = "Public Route 53 hosted zone ID for domain_name."
  type        = string
}

variable "certificate_arn" {
  description = "Validated ACM certificate ARN in the same Region as the ALB."
  type        = string

  validation {
    condition     = can(regex("^arn:[^:]+:acm:[^:]+:[0-9]{12}:certificate/[0-9a-f-]+$", var.certificate_arn))
    error_message = "certificate_arn must be an ACM certificate ARN."
  }
}

variable "backend_image_uri" {
  description = "ECR backend image pinned by sha256 digest."
  type        = string

  validation {
    condition     = can(regex("^[^@]+@sha256:[0-9a-f]{64}$", var.backend_image_uri))
    error_message = "backend_image_uri must be digest pinned."
  }
}

variable "frontend_image_uri" {
  description = "ECR frontend image built for https://domain_name and pinned by digest."
  type        = string

  validation {
    condition     = can(regex("^[^@]+@sha256:[0-9a-f]{64}$", var.frontend_image_uri))
    error_message = "frontend_image_uri must be digest pinned."
  }
}

variable "migration_image_uri" {
  description = "ECR one-shot Flyway image pinned by sha256 digest."
  type        = string

  validation {
    condition     = can(regex("^[^@]+@sha256:[0-9a-f]{64}$", var.migration_image_uri))
    error_message = "migration_image_uri must be digest pinned."
  }
}

variable "database_admin_image_uri" {
  description = "ECR PostgreSQL administration image pinned by sha256 digest."
  type        = string

  validation {
    condition     = can(regex("^[^@]+@sha256:[0-9a-f]{64}$", var.database_admin_image_uri))
    error_message = "database_admin_image_uri must be digest pinned."
  }
}

variable "application_database_secret_arn" {
  description = "Pre-created Secrets Manager secret ARN whose value is the imperator_app password."
  type        = string

  validation {
    condition     = can(regex("^arn:[^:]+:secretsmanager:[^:]+:[0-9]{12}:secret:[A-Za-z0-9/_+=.@-]+$", var.application_database_secret_arn))
    error_message = "application_database_secret_arn must be a Secrets Manager secret ARN."
  }
}

variable "application_database_kms_key_arn" {
  description = "Optional customer-managed KMS key ARN used by the application password secret."
  type        = string
  default     = null

  validation {
    condition = (
      var.application_database_kms_key_arn == null ||
      can(regex("^arn:[^:]+:kms:[^:]+:[0-9]{12}:key/[0-9a-f-]+$", var.application_database_kms_key_arn))
    )
    error_message = "application_database_kms_key_arn must be a KMS key ARN or null."
  }
}

variable "jwt_issuer_uri" {
  description = "D095-approved external Keycloak HTTPS issuer URI."
  type        = string

  validation {
    condition     = startswith(var.jwt_issuer_uri, "https://")
    error_message = "jwt_issuer_uri must use HTTPS."
  }
}

variable "jwt_jwk_set_uri" {
  description = "D095-approved external Keycloak HTTPS JWKS URI."
  type        = string

  validation {
    condition     = startswith(var.jwt_jwk_set_uri, "https://")
    error_message = "jwt_jwk_set_uri must use HTTPS."
  }
}

variable "bedrock_enabled" {
  description = "Enable the optional audited explanation provider."
  type        = bool
  default     = true
}

variable "bedrock_model_id" {
  description = "Approved Bedrock model or inference profile ID used by the application."
  type        = string
}

variable "bedrock_invoke_resource_arns" {
  description = "Exact model and/or inference-profile ARNs allowed for InvokeModel."
  type        = list(string)

  validation {
    condition = (
      !var.bedrock_enabled ||
      (length(var.bedrock_invoke_resource_arns) > 0 && alltrue([
        for arn in var.bedrock_invoke_resource_arns : can(regex("^arn:[^:]+:bedrock:[^:]+:[0-9]*:(foundation-model|inference-profile|application-inference-profile)/.+$", arn))
      ]))
    )
    error_message = "Enabled Bedrock requires one or more exact model/profile ARNs."
  }
}

variable "aws_evidence_enabled" {
  description = "Enable the existing read-only AWS Evidence connector."
  type        = bool
  default     = true
}

variable "database_instance_class" {
  description = "Single-AZ pilot database size. Reassess before production use."
  type        = string
  default     = "db.t4g.micro"
}

variable "database_deletion_protection" {
  description = "Protect RDS from deletion. Keep false only for an explicitly disposable sandbox."
  type        = bool
  default     = false
}

variable "database_skip_final_snapshot" {
  description = "Skip final RDS snapshot on teardown. Keep true only for synthetic pilot data."
  type        = bool
  default     = true
}

variable "activate_services" {
  description = "Start frontend/backend tasks only after migration and role-provision tasks pass."
  type        = bool
  default     = false
}

variable "log_retention_days" {
  description = "CloudWatch application log retention."
  type        = number
  default     = 30

  validation {
    condition     = contains([7, 14, 30, 60, 90, 120, 150, 180, 365], var.log_retention_days)
    error_message = "log_retention_days must be a supported CloudWatch retention value."
  }
}

variable "monthly_budget_usd" {
  description = "Approved monthly pilot budget in USD."
  type        = number

  validation {
    condition     = var.monthly_budget_usd >= 20
    error_message = "monthly_budget_usd must be at least 20 USD."
  }
}

variable "budget_alert_email" {
  description = "Verified operator address for 80% and 100% forecast notifications."
  type        = string

  validation {
    condition     = can(regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", var.budget_alert_email))
    error_message = "budget_alert_email must be a valid email address."
  }
}
