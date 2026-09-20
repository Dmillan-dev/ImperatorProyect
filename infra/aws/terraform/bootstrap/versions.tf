terraform {
  required_version = "= 1.16.3"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "= 6.65.0"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Application = "imperator"
      Environment = var.environment
      ManagedBy   = "terraform"
      Portfolio   = "tfg"
    }
  }
}
