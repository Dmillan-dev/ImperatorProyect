output "application_url" {
  description = "Public HTTPS entry point."
  value       = local.application_origin
}

output "cluster_name" {
  description = "ECS cluster used by services and one-shot jobs."
  value       = aws_ecs_cluster.main.name
}

output "private_subnet_ids" {
  description = "Private subnet IDs for one-shot ECS tasks."
  value       = aws_subnet.private[*].id
}

output "database_job_security_group_id" {
  description = "Security group for migration and permission tasks."
  value       = aws_security_group.backend.id
}

output "migration_task_definition_arn" {
  description = "Task definition to run before database permission provisioning."
  value       = aws_ecs_task_definition.migration.arn
}

output "database_permissions_task_definition_arn" {
  description = "Task definition to enforce the imperator_app least-privilege role."
  value       = aws_ecs_task_definition.database_permissions.arn
}

output "backend_service_name" {
  value = aws_ecs_service.backend.name
}

output "frontend_service_name" {
  value = aws_ecs_service.frontend.name
}

output "database_endpoint" {
  description = "Private RDS endpoint; not a credential."
  value       = aws_db_instance.main.endpoint
}

output "rds_master_secret_arn" {
  description = "AWS-managed owner secret metadata used only by one-shot database jobs."
  value       = aws_db_instance.main.master_user_secret[0].secret_arn
}
