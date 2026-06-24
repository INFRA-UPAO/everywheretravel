variable "prefix" {
  description = "Prefijo único por workspace"
  type        = string
}

variable "domain_name" {
  description = "Dominio del workspace (everywheretravel.online o dev.everywheretravel.online)"
  type        = string
}

variable "is_prod" {
  description = "true en workspace prod — crea la zona y records de Zoho"
  type        = bool
}

variable "route53_zone_id" {
  description = "ID de la Hosted Zone Route 53 creada en el root"
  type        = string
}

variable "cloudfront_domain_name" {
  description = "Domain name de la distribución CloudFront"
  type        = string
}

variable "cloudfront_hosted_zone_id" {
  description = "Hosted Zone ID de CloudFront (siempre Z2FDTNDATAQYW2)"
  type        = string
  default     = "Z2FDTNDATAQYW2"
}

variable "zoho_verification_token" {
  description = "Token TXT de verificación de dominio Zoho (solo prod)"
  type        = string
  default     = ""
}

variable "zoho_dkim_cname_value" {
  description = "Valor CNAME para DKIM de Zoho (solo prod)"
  type        = string
  default     = ""
}
