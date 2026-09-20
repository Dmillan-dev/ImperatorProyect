output "account_id" {
  description = "AWS account verified during bootstrap."
  value       = data.aws_caller_identity.current.account_id
}

output "state_bucket_name" {
  description = "S3 backend bucket for the pilot stack."
  value       = aws_s3_bucket.terraform_state.id
}

output "state_kms_key_arn" {
  description = "KMS key protecting the pilot Terraform state."
  value       = aws_kms_key.terraform_state.arn
}

output "github_build_role_arn" {
  description = "Repository variable AWS_BUILD_ROLE_ARN."
  value       = aws_iam_role.github_build.arn
}

output "github_deploy_role_arn" {
  description = "Protected pilot environment variable AWS_DEPLOY_ROLE_ARN."
  value       = aws_iam_role.github_deploy.arn
}

output "ecr_repository_urls" {
  description = "Immutable application image repositories."
  value       = { for name, repository in aws_ecr_repository.application : name => repository.repository_url }
}
