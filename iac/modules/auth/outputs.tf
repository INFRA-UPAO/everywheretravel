output "cognito_user_pool_id" {
  description = "ID del Cognito User Pool"
  value       = aws_cognito_user_pool.main.id
}

output "cognito_user_pool_arn" {
  description = "ARN del Cognito User Pool"
  value       = aws_cognito_user_pool.main.arn
}

output "cognito_app_client_id" {
  description = "ID del App Client (usado en API Gateway JWT Authorizer)"
  value       = aws_cognito_user_pool_client.main.id
}

output "cognito_issuer_url" {
  description = "Issuer URL para el JWT Authorizer de API Gateway"
  value       = "https://cognito-idp.${data.aws_region.current.region}.amazonaws.com/${aws_cognito_user_pool.main.id}"
}

output "cognito_hosted_ui_domain" {
  description = "Dominio de la Hosted UI de Cognito"
  value       = "https://${aws_cognito_user_pool_domain.main.domain}.auth.${data.aws_region.current.region}.amazoncognito.com"
}

output "cognito_jwks_uri" {
  description = "URI de las claves públicas JWT para verificación"
  value       = "https://cognito-idp.${data.aws_region.current.region}.amazonaws.com/${aws_cognito_user_pool.main.id}/.well-known/jwks.json"
}

data "aws_region" "current" {}