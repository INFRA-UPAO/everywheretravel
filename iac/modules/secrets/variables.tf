variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "kms_secrets_arn" {
  description = "ARN de la llave KMS para Secrets Manager"
  type        = string
}

variable "db_username" {
  description = "Nombre de usuario de la base de datos"
  type        = string
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
}

variable "db_password" {
  description = "Password de la base de datos"
  type        = string
  sensitive   = true
}

variable "db_host" {
  description = "Host de la base de datos"
  type        = string
}

variable "db_port" {
  description = "Puerto de la base de datos PostgreSQL"
  type        = number
}

variable "ecs_execution_role_arn" {
  description = "ARN del ECS Task Execution Role"
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

variable "private_app_subnet_ids" {
  description = "IDs de las subnets privadas de aplicacion para la Lambda de rotacion"
  type        = list(string)
}

variable "sg_lambda_id" {
  description = "ID del security group de Lambda para acceso a RDS y VPC endpoints"
  type        = string
}

variable "rds_instance_arn" {
  description = "ARN de la instancia RDS para permisos de rotacion"
  type        = string
}
