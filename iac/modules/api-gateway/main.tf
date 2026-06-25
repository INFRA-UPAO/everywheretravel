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

# HTTP API v2
resource "aws_apigatewayv2_api" "main" {
  name          = "${var.prefix}-api"
  protocol_type = "HTTP"
  description   = "API HTTP v2 para Everywhere Travel - ${var.prefix}"

  cors_configuration {
    allow_origins = [
      "https://${var.domain_name}",
      "http://localhost:4200"
    ]
    allow_methods = [
      "GET", "POST", "PUT",
      "PATCH", "DELETE", "OPTIONS"
    ]
    allow_headers = [
      "Authorization",
      "Content-Type",
      "X-Request-ID"
    ]
    expose_headers    = ["X-Request-ID"]
    allow_credentials = true
    max_age           = 3600
  }

  tags = {
    Name = "${var.prefix}-api"
  }
}