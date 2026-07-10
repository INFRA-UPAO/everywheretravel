output "cloudfront_domain_name" {
  description = "Domain name de la distribución CloudFront (para Route53)"
  value       = aws_cloudfront_distribution.main.domain_name
}

output "cloudfront_distribution_id" {
  description = "ID de la distribución CloudFront (para invalidaciones)"
  value       = aws_cloudfront_distribution.main.id
}

output "cloudfront_arn" {
  description = "ARN de la distribución CloudFront"
  value       = aws_cloudfront_distribution.main.arn
}

output "acm_certificate_arn" {
  description = "ARN del certificado ACM en us-east-1"
  value       = aws_acm_certificate_validation.main.certificate_arn
}

output "waf_web_acl_arn" {
  description = "ARN del WAF WebACL"
  value       = aws_wafv2_web_acl.main.arn
}

output "cloudfront_hosted_zone_id" {
  description = "Hosted Zone ID de CloudFront (fijo globalmente)"
  value       = "Z2FDTNDATAQYW2"
}
