output "aws_region" {
  description = "Region AWS principal usada por el despliegue"
  value       = var.aws_region
}

output "domain_name" {
  description = "Dominio principal de la aplicacion"
  value       = var.domain_name
}

output "ecr_repo_url" {
  description = "URL del repositorio ECR del backend"
  value       = module.ecr.ecr_repo_url
}

output "ecs_cluster_name" {
  description = "Nombre del cluster ECS"
  value       = module.compute.ecs_cluster_name
}

output "ecs_service_name" {
  description = "Nombre del servicio ECS del backend"
  value       = module.compute.ecs_service_name
}

output "s3_frontend_bucket" {
  description = "Bucket S3 donde se publica el frontend"
  value       = module.s3.s3_frontend_bucket
}

output "s3_docs_bucket" {
  description = "Bucket S3 donde la Lambda guarda documentos generados"
  value       = module.s3.s3_docs_bucket
}

output "cloudfront_distribution_id" {
  description = "ID de la distribucion CloudFront del frontend"
  value       = module.edge.cloudfront_distribution_id
}

output "cloudfront_domain_name" {
  description = "Dominio CloudFront del frontend"
  value       = module.edge.cloudfront_domain_name
}

output "cloudfront_record_fqdn" {
  description = "FQDN del record A que apunta el dominio a CloudFront"
  value       = aws_route53_record.cloudfront.fqdn
}

output "lambda_function_name" {
  description = "Nombre de la funcion Lambda doc generator"
  value       = module.lambda.lambda_function_name
}

output "sqs_queue_url" {
  description = "URL de la cola SQS que dispara la Lambda"
  value       = module.sqs.sqs_queue_url
}

output "cognito_user_pool_id" {
  description = "ID del User Pool de Cognito"
  value       = module.auth.cognito_user_pool_id
}

output "cognito_app_client_id" {
  description = "ID del App Client de Cognito"
  value       = module.auth.cognito_app_client_id
}

output "cognito_issuer_url" {
  description = "Issuer URL de Cognito"
  value       = module.auth.cognito_issuer_url
}
