variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "is_prod" {
  description = "Booleano para saber si es prod o dev; el proxy solo se crea en prod"
  type        = bool
}

variable "vpc_id" {
  description = "ID de la VPC"
  type        = string
}

variable "private_data_subnet_ids" {
  description = "IDs de las subnets privadas DATA donde vive RDS"
  type        = list(string)
}

variable "sg_rds_id" {
  description = "ID del Security Group de RDS, para permitir ingreso desde el proxy"
  type        = string
}

variable "sg_ecs_task_id" {
  description = "ID del Security Group de las ECS Tasks, clientes del proxy"
  type        = string
}

variable "rds_instance_id" {
  description = "Identificador de la instancia RDS destino del proxy"
  type        = string
}

variable "rds_secret_arn" {
  description = "ARN del secret de Secrets Manager con las credenciales de RDS"
  type        = string
}

variable "kms_secrets_arn" {
  description = "ARN de la llave KMS que cifra el secret de RDS"
  type        = string
}
