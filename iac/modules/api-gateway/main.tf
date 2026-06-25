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

# JWT AUTHORIZER — COGNITO
resource "aws_apigatewayv2_authorizer" "cognito" {
  api_id           = aws_apigatewayv2_api.main.id
  authorizer_type  = "JWT"
  name             = "${var.prefix}-cognito-authorizer"
  identity_sources = ["$request.header.Authorization"]

  jwt_configuration {
    issuer   = var.cognito_issuer_url
    audience = [var.cognito_app_client_id]
  }
}

# VPC LINK
resource "aws_apigatewayv2_vpc_link" "main" {
  name               = "${var.prefix}-vpc-link"
  security_group_ids = [var.sg_vpclink_id]
  subnet_ids         = var.private_app_subnet_ids

  tags = {
    Name = "${var.prefix}-vpc-link"
  }
}

# VPC LINK → ALB
resource "aws_apigatewayv2_integration" "alb" {
  api_id                 = aws_apigatewayv2_api.main.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = var.alb_listener_arn
  connection_type        = "VPC_LINK"
  connection_id          = aws_apigatewayv2_vpc_link.main.id
  timeout_milliseconds   = 29000
  payload_format_version = "1.0"
}

# RUTA — ANY /{proxy+}
resource "aws_apigatewayv2_route" "proxy" {
  api_id             = aws_apigatewayv2_api.main.id
  route_key          = "ANY /{proxy+}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.cognito.id

  target = "integrations/${aws_apigatewayv2_integration.alb.id}"
}

# STAGE — $default
resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.main.id
  name        = "$default"
  auto_deploy = true

  default_route_settings {
    throttling_burst_limit   = 500
    throttling_rate_limit    = 100
    detailed_metrics_enabled = true
    logging_level            = "INFO"
  }

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_access_logs.arn

    format = jsonencode({
      requestId        = "$context.requestId"
      ip               = "$context.identity.sourceIp"
      httpMethod       = "$context.httpMethod"
      routeKey         = "$context.routeKey"
      status           = "$context.status"
      protocol         = "$context.protocol"
      responseLength   = "$context.responseLength"
      integrationError = "$context.integrationErrorMessage"
      latency          = "$context.responseLatency"
      userAgent        = "$context.identity.userAgent"
      requestTime      = "$context.requestTime"
    })
  }

  tags = {
    Name = "${var.prefix}-api-stage"
  }
}
