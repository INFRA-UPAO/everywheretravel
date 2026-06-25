variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "sns_alerts_arn" {
  description = "ARN del SNS Topic de alertas"
  type        = string
}

variable "sqs_dlq_name" {
  description = "Nombre de la SQS Dead Letter Queue"
  type        = string
}

variable "alb_arn_suffix" {
  description = "ARN suffix del ALB (para métricas CloudWatch)"
  type        = string
}

variable "target_group_arn_suffix" {
  description = "ARN suffix del Target Group"
  type        = string
}

variable "rds_identifier" {
  description = "Identificador de la instancia RDS"
  type        = string
}

variable "ecs_cluster_name" {
  description = "Nombre del ECS Cluster"
  type        = string
}

variable "ecs_service_name" {
  description = "Nombre del ECS Service"
  type        = string
}

variable "lambda_function_name" {
  description = "Nombre de la función Lambda doc-generante"
  type        = string
}

variable "nat_gateway_az_a_id" {
  description = "ID del NAT Gateway AZ-a"
  type        = string
}

variable "nat_gateway_az_b_id" {
  description = "ID del NAT Gateway AZ-b (null en dev)"
  type        = string
  default     = null
}

variable "has_nat_az_b" {
  description = "true si existe NAT Gateway en AZ-b (resuelve count en plan-time)"
  type        = bool
  default     = false
}

variable "api_id" {
  description = "ID de la API Gateway"
  type        = string
}
