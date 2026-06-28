variable "prefix" {
  description = "Prefijo unico por workspace"
  type        = string
}

variable "domain_name" {
  description = "Dominio del workspace (everywheretravel.online o dev.everywheretravel.online)"
  type        = string
}

variable "is_prod" {
  description = "true en workspace prod -- crea la zona y records de Zoho"
  type        = bool
}

variable "kms_route53_logs_arn" {
  description = "ARN de la llave KMS para Route53 query logs y DNSSEC (us-east-1)"
  type        = string
}

variable "cloudfront_domain_name" {
  description = "Domain name de la distribucion CloudFront"
  type        = string
}

variable "cloudfront_hosted_zone_id" {
  description = "Hosted Zone ID de CloudFront (siempre Z2FDTNDATAQYW2)"
  type        = string
  default     = "Z2FDTNDATAQYW2"
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
