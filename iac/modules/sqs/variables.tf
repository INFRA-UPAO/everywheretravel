
variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "kms_sqs_arn" {
  description = "ARN de la llave KMS para SQS"
  type        = string
}

variable "ecs_task_role_arn" {
  description = "ARN del ECS Task Role (produce mensajes)"
  type        = string
}

variable "lambda_docgen_role_arn" {
  description = "ARN del Lambda doc-generante Role (consume mensajes)"
  type        = string
}

variable "sns_alerts_arn" {
  description = "ARN del SNS Topic de alertas (para alarma DLQ)"
  type        = string
}
