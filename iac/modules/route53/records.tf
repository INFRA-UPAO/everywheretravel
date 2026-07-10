# MX records — los tres servidores de Zoho Mail.
resource "aws_route53_record" "zoho_mx" {
  count   = var.is_prod ? 1 : 0
  zone_id = local.zone_id
  name    = var.domain_name
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
