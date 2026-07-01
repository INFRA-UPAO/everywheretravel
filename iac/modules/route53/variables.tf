variable "prefix" {
  description = "Prefijo unico por workspace"
  type        = string
}

variable "domain_name" {
  description = "Dominio del workspace (everywheretravel.online o dev.everywheretravel.online)"
  type        = string
}

variable "manage_hosted_zone" {
  description = "Si es true, el modulo crea/administra la hosted zone. Si es false, usa una hosted zone publica existente."
  type        = bool
  default     = true
}

variable "is_prod" {
  description = "true en workspace prod -- crea la zona y records de Zoho"
  type        = bool
}

variable "kms_route53_logs_arn" {
  description = "ARN de la llave KMS para Route53 query logs (us-east-1)"
  type        = string
}

variable "kms_dnssec_arn" {
  description = "ARN de la llave KMS asimetrica para DNSSEC (us-east-1)"
  type        = string
}

variable "zoho_verification_token" {
  description = "Token TXT de verificacion de dominio Zoho (solo prod)"
  type        = string
  default     = ""
}

variable "zoho_dkim_cname_value" {
  description = "Valor CNAME para DKIM de Zoho (solo prod)"
  type        = string
  default     = ""
}
