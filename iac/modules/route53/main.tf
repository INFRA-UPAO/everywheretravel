locals {
  zone_id = var.route53_zone_id
}

# Record A
resource "aws_route53_record" "cloudfront" {
  zone_id = local.zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = var.cloudfront_domain_name
    zone_id                = var.cloudfront_hosted_zone_id
    evaluate_target_health = false
  }
}

# MX records — los tres servidores de Zoho Mail.
resource "aws_route53_record" "zoho_mx" {
  count   = var.is_prod ? 1 : 0
  zone_id = local.zone_id
  name    = "everywheretravel.online"
  type    = "MX"
  ttl     = 300

  records = [
    "10 mx.zoho.com.",
    "20 mx2.zoho.com.",
    "50 mx3.zoho.com."
  ]

  lifecycle {
    ignore_changes = [records]
  }
}

# TXT combinado - SPF + Verificación Zoho
resource "aws_route53_record" "zoho_txt" {
  count   = var.is_prod ? 1 : 0
  zone_id = local.zone_id
  name    = var.domain_name
  type    = "TXT"
  ttl     = 300

  records = [
    "v=spf1 include:zoho.com ~all",
    var.zoho_verification_token
  ]

  lifecycle {
    ignore_changes = [records]
  }
}

# DKIM - Zoho
resource "aws_route53_record" "zoho_dkim" {
  count   = var.is_prod ? 1 : 0
  zone_id = local.zone_id
  name    = "zmail._domainkey.${var.domain_name}"
  type    = "TXT"
  ttl     = 300

  records = [var.zoho_dkim_cname_value]

  lifecycle {
    ignore_changes = [records]
  }
}

# Verificación de dominio Zoho.
# Zoho la requiere para confirmar que eres el propietario
# del dominio antes de habilitar el correo.
resource "aws_route53_record" "zoho_verification" {
  count   = var.is_prod ? 1 : 0
  zone_id = local.zone_id
  name    = "everywheretravel.online"
  type    = "TXT"
  ttl     = 300

  records = [var.zoho_verification_token]
}
