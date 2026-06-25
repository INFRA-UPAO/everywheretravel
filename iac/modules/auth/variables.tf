variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "domain_name" {
  description = "Dominio de la aplicación (dev.everywheretravel.online o everywheretravel.online)"
  type        = string
}

variable "env" {
  description = "Nombre del workspace: dev o prod"
  type        = string
}