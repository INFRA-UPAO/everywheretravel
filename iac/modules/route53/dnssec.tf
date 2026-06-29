resource "aws_route53_key_signing_key" "main" {
  count = var.enable_dnssec ? 1 : 0

  name                       = "${var.prefix}-ksk"
  hosted_zone_id             = local.zone_id
  key_management_service_arn = var.kms_dnssec_arn
  status                     = "ACTIVE"
}

resource "aws_route53_hosted_zone_dnssec" "main" {
  count = var.enable_dnssec ? 1 : 0

  hosted_zone_id = aws_route53_key_signing_key.main[0].hosted_zone_id

  depends_on = [
    aws_route53_key_signing_key.main
  ]
}
