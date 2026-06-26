variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "lambda_memory" {
  description = "Memoria en MB para Lambda"
  type        = number
}

variable "lambda_timeout" {
  description = "Timeout en segundos para Lambda"
  type        = number
}

variable "private_app_subnet_ids" {
  description = "IDs de las subnets privadas APP"
  type        = list(string)
}

variable "sg_lambda_id" {
  description = "ID del Security Group de Lambda"
  type        = string
}

variable "lambda_docgen_role_arn" {
  description = "ARN del Lambda doc-generante Role"
  type        = string
}

variable "sqs_queue_arn" {
  description = "ARN de la SQS Queue principal"
  type        = string
}

variable "sqs_queue_url" {
  description = "URL de la SQS Queue principal"
  type        = string
}

variable "s3_docs_bucket" {
  description = "Nombre del bucket S3 de documentos"
  type        = string
}

variable "rds_secret_arn" {
  description = "ARN del secret rds-credentials"
  type        = string
}

variable "kms_logs_arn" {
  description = "ARN de la llave KMS para CloudWatch Logs"
  type        = string
}
variable "lambda_reserved_concurrency" {
  description = "Límite de ejecuciones concurrentes para la Lambda"
  type        = number
  default     = 10
}
