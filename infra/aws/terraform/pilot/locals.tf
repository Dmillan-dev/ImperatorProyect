data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_partition" "current" {}

locals {
  name_prefix          = "imperator-${var.environment}"
  availability_zones   = length(var.availability_zones) == 2 ? var.availability_zones : slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnet_cidrs  = [for index in range(2) : cidrsubnet(var.vpc_cidr, 4, index)]
  private_subnet_cidrs = [for index in range(2) : cidrsubnet(var.vpc_cidr, 4, index + 8)]
  application_origin   = "https://${var.domain_name}"
  backend_log_group    = "/imperator/${var.environment}/backend"
  frontend_log_group   = "/imperator/${var.environment}/frontend"
  migration_log_group  = "/imperator/${var.environment}/database-jobs"
}
