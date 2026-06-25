variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "vpc_id" {
  description = "ID del VPC"
  type        = string
}

variable "private_app_subnet_ids" {
  description = "IDs de las subnets privadas APP para los Interface Endpoints"
  type        = list(string)
}

variable "rt_private_app_az_a_id" {
  description = "ID de la route table privada APP AZ-a (para S3 Gateway)"
  type        = string
}

variable "rt_private_app_az_b_id" {
  description = "ID de la route table privada APP AZ-b (para S3 Gateway)"
  type        = string
}

variable "sg_vpce_sqs_id" {
  description = "ID del Security Group para el endpoint SQS"
  type        = string
}

variable "sg_vpce_sm_id" {
  description = "ID del Security Group para el endpoint Secrets Manager"
  type        = string
}

variable "sg_vpce_logs_id" {
  description = "ID del Security Group para el endpoint CloudWatch Logs"
  type        = string
}

variable "sg_vpce_ecr_id" {
  description = "ID del Security Group para los endpoints ECR"
  type        = string
}

variable "s3_docs_bucket_arn" {
  description = "ARN del bucket S3 de documentos"
  type        = string
}

variable "ecs_task_role_arn" {
  description = "ARN del ECS Task Role"
  type        = string
}

variable "lambda_docgen_role_arn" {
  description = "ARN del Lambda doc-generante Role"
  type        = string
}

variable "ecs_execution_role_arn" {
  description = "ARN del ECS Task Execution Role (necesita ECR)"
  type        = string
}

variable "sqs_queue_arn" {
  description = "ARN de la SQS Queue principal"
  type        = string
}

variable "sqs_dlq_arn" {
  description = "ARN de la SQS Dead Letter Queue"
  type        = string
}

variable "rds_secret_arn" {
  description = "ARN del secret rds-credentials"
  type        = string
}
