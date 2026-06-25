data "aws_region" "current" {}

locals {
  region = data.aws_region.current.region
}

# CW LOG GROUP — ACCESS LOGS
resource "aws_cloudwatch_log_group" "api_access_logs" {
  name              = "/aws/apigateway/${var.prefix}/access-logs"
  retention_in_days = 30
  kms_key_id        = var.kms_logs_arn

  tags = {
    Name = "${var.prefix}-api-access-logs"
  }
}