variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
}

variable "db_username" {
  description = "Usuario master de la base de datos"
  type        = string
}

variable "db_instance_class" {
  description = "Tipo de instancia RDS (db.t3.micro en dev, db.t3.small en prod)"
  type        = string
}

variable "db_multi_az" {
  description = "Habilitar Multi-AZ (false en dev, true en prod)"
  type        = bool
}

variable "kms_rds_arn" {
  description = "ARN de la llave KMS para RDS"
  type        = string
}

variable "private_data_subnet_ids" {
  description = "IDs de las subnets privadas DATA para RDS"
  type        = list(string)
}

variable "sg_rds_id" {
  description = "ID del Security Group de RDS"
  type        = string
}
