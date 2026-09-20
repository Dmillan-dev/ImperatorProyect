variable "aws_region" {
  description = "AWS Region frozen for the pilot."
  type        = string
  default     = "eu-west-1"

  validation {
    condition     = can(regex("^[a-z]{2}(-gov)?-[a-z]+-[0-9]$", var.aws_region))
    error_message = "aws_region must be a valid AWS Region identifier."
  }
}

variable "environment" {
  description = "Protected GitHub environment and AWS resource suffix."
  type        = string
  default     = "pilot"

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]{1,15}$", var.environment))
    error_message = "environment must contain 2-16 lowercase letters, numbers or hyphens."
  }
}

variable "github_owner" {
  description = "Case-sensitive GitHub repository owner used in OIDC trust conditions."
  type        = string
}

variable "github_repository" {
  description = "Case-sensitive GitHub repository name used in OIDC trust conditions."
  type        = string
}

variable "state_bucket_name" {
  description = "Globally unique S3 bucket name for the pilot Terraform state."
  type        = string

  validation {
    condition     = can(regex("^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$", var.state_bucket_name))
    error_message = "state_bucket_name must be a valid 3-63 character S3 bucket name."
  }
}

variable "existing_github_oidc_provider_arn" {
  description = "Existing token.actions.githubusercontent.com provider ARN, or null to create it."
  type        = string
  default     = null

  validation {
    condition = (
      var.existing_github_oidc_provider_arn == null ||
      can(regex("^arn:[^:]+:iam::[0-9]{12}:oidc-provider/token\\.actions\\.githubusercontent\\.com$", var.existing_github_oidc_provider_arn))
    )
    error_message = "existing_github_oidc_provider_arn must be a GitHub Actions OIDC provider ARN."
  }
}
