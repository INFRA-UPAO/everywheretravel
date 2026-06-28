output "zone_id" {
  description = "ID de la Hosted Zone Route53"
  value       = aws_route53_zone.main.zone_id
}

output "route53_nameservers" {
  description = "Nameservers de Route53 -- copiar a Namecheap (solo prod)"
  value       = aws_route53_zone.main.name_servers
}

output "cloudfront_record_fqdn" {
  description = "FQDN del record A de CloudFront creado"
  value       = aws_route53_record.cloudfront.fqdn
}
