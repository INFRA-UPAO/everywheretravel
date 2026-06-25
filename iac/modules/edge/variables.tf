variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "domain_name" {
  description = "Dominio principal (everywheretravel.online o dev.everywheretravel.online)"
  type        = string
}

variable "s3_frontend_bucket_id" {
  description = "ID del bucket S3 Frontend (para bucket policy y origin)"
  type        = string
}

variable "s3_frontend_bucket_arn" {
  description = "ARN del bucket S3 Frontend"
  type        = string
}

variable "s3_access_logs_bucket" {
  description = "Nombre del bucket S3 para access logs de CloudFront"
  type        = string
}

variable "s3_waf_logs_bucket_arn" {
  description = "ARN del bucket S3 para logs de WAF"
  type        = string
}

variable "api_endpoint" {
  description = "Endpoint de API Gateway (origen para /api/*)"
  type        = string
}

variable "lambda_edge_role_arn" {
  description = "ARN del Lambda@Edge Role (creado en us-east-1 via IAM global)"
  type        = string
}

variable "route53_zone_id" {
  description = "ID de la Hosted Zone Route 53 para validacion de certificados"
  type        = string
}
