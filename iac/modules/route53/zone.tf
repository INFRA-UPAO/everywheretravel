resource "aws_route53_zone" "main" {
  count = var.manage_hosted_zone ? 1 : 0

  name = var.domain_name

  tags = {
    Name = var.domain_name
  }
}

data "aws_route53_zone" "main" {
  count = var.manage_hosted_zone ? 0 : 1

  name         = var.domain_name
  private_zone = false
}

locals {
  zone_id = var.manage_hosted_zone ? aws_route53_zone.main[0].zone_id : data.aws_route53_zone.main[0].zone_id
  name_servers = (
    var.manage_hosted_zone
    ? aws_route53_zone.main[0].name_servers
    : data.aws_route53_zone.main[0].name_servers
  )
}
