resource "aws_route53_key_signing_key" "main" {
  name                       = "${var.prefix}-ksk"
  hosted_zone_id             = aws_route53_zone.main.id
  key_management_service_arn = var.kms_route53_logs_arn
  status                     = "ACTIVE"
}

resource "aws_route53_hosted_zone_dnssec" "main" {
  hosted_zone_id = aws_route53_key_signing_key.main.hosted_zone_id

  depends_on = [
    aws_route53_key_signing_key.main
  ]
}
