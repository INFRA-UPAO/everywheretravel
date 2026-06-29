resource "aws_route53_key_signing_key" "main" {
  name                       = "${var.prefix}-ksk"
  hosted_zone_id             = var.manage_hosted_zone ? aws_route53_zone.main[0].zone_id : data.aws_route53_zone.main[0].zone_id
  key_management_service_arn = var.kms_dnssec_arn
  status                     = "ACTIVE"
}

resource "aws_route53_hosted_zone_dnssec" "main" {
  hosted_zone_id = aws_route53_key_signing_key.main.hosted_zone_id

  depends_on = [
    aws_route53_key_signing_key.main
  ]
}
