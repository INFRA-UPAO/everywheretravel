output "api_endpoint" {
  description = "Endpoint de la API Gateway (usado por CloudFront como origen)"
  value       = aws_apigatewayv2_api.main.api_endpoint
}

output "api_id" {
  description = "ID de la API Gateway (para CloudWatch alarms)"
  value       = aws_apigatewayv2_api.main.id
}

output "vpc_link_id" {
  description = "ID del VPC Link"
  value       = aws_apigatewayv2_vpc_link.main.id
}

output "stage_invoke_url" {
  description = "URL de invocación del stage $default"
  value       = aws_apigatewayv2_stage.default.invoke_url
}
