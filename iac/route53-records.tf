resource "aws_route53_record" "cloudfront" {
  provider = aws.main

  zone_id = module.route53.zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = module.edge.cloudfront_domain_name
    zone_id                = module.edge.cloudfront_hosted_zone_id
    evaluate_target_health = false
  }
}
