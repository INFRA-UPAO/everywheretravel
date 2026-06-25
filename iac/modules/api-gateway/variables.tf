variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "domain_name" {
  description = "Dominio de la aplicación para CORS"
  type        = string
}

variable "cognito_issuer_url" {
  description = "Issuer URL de Cognito para el JWT Authorizer"
  type        = string
}

variable "cognito_app_client_id" {
  description = "App Client ID de Cognito (audience del JWT)"
  type        = string
}

variable "alb_listener_arn" {
  description = "ARN del ALB Listener (destino de la integración VPC Link)"
  type        = string
}

variable "private_app_subnet_ids" {
  description = "IDs de las subnets privadas APP para el VPC Link"
  type        = list(string)
}

variable "sg_vpclink_id" {
  description = "ID del Security Group del VPC Link"
  type        = string
}

variable "kms_logs_arn" {
  description = "ARN de la llave KMS para CloudWatch Logs"
  type        = string
}