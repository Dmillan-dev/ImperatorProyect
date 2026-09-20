mock_provider "aws" {
  mock_data "aws_caller_identity" {
    defaults = {
      account_id = "123456789012"
      arn        = "arn:aws:iam::123456789012:user/terraform-test"
      user_id    = "AIDATEST"
    }
  }

  mock_data "aws_partition" {
    defaults = {
      partition          = "aws"
      dns_suffix         = "amazonaws.com"
      reverse_dns_prefix = "com.amazonaws"
    }
  }
}

run "bootstrap_plan" {
  command = plan

  override_data {
    target = data.aws_iam_policy_document.terraform_state_key
    values = {
      json = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"arn:aws:iam::123456789012:root\"},\"Action\":\"kms:*\",\"Resource\":\"*\"}]}"
    }
  }

  variables {
    aws_region        = "eu-west-1"
    environment       = "pilot"
    github_owner      = "Dmillan-dev"
    github_repository = "ImperatorProyect"
    state_bucket_name = "imperator-test-123456789012-tfstate"
  }

  assert {
    condition     = aws_s3_bucket.terraform_state.force_destroy == false
    error_message = "The state bucket must resist accidental recursive deletion."
  }

  assert {
    condition     = aws_iam_role.github_build.max_session_duration == 3600
    error_message = "GitHub build credentials must remain short lived."
  }
}
