data "aws_region" "main" {}
data "aws_caller_identity" "current" {}

locals {
  account_id       = data.aws_caller_identity.current.account_id
  api_gateway_host = replace(var.api_endpoint, "https://", "")
}

# ACM CERTIFICATE — us-east-1
resource "aws_acm_certificate" "main" {
  provider          = aws.edge
  domain_name       = var.domain_name
  validation_method = "DNS"

  subject_alternative_names = ["*.${var.domain_name}"]

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name = "${var.prefix}-certificate"
  }

  depends_on = [aws_route53_record.caa]
}

resource "aws_route53_record" "caa" {
  zone_id = var.route53_zone_id
  name    = var.domain_name
  type    = "CAA"
  ttl     = 300

  records = [
    "0 issue \"amazon.com\"",
    "0 issue \"amazontrust.com\"",
    "0 issue \"awstrust.com\""
  ]
}

resource "aws_route53_record" "cert_validation" {
  for_each = {
    for dvo in aws_acm_certificate.main.domain_validation_options :
    dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  zone_id = var.route53_zone_id
  name    = each.value.name
  type    = each.value.type
  records = [each.value.record]
  ttl     = 60

  allow_overwrite = true
}

resource "aws_acm_certificate_validation" "main" {
  provider        = aws.edge
  certificate_arn = aws_acm_certificate.main.arn

  validation_record_fqdns = [
    for record in aws_route53_record.cert_validation : record.fqdn
  ]
}

# WAF 
resource "aws_wafv2_web_acl" "main" {
  provider    = aws.edge
  name        = "${var.prefix}-waf"
  description = "WAF para CloudFront - ${var.prefix}"
  scope       = "CLOUDFRONT"

  default_action {
    allow {}
  }

  # Regla 1 — OWASP Top 10 (XSS, path traversal, CSRF)
  rule {
    name     = "CommonRuleSet"
    priority = 1

    override_action {
      none {}
    }

    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesCommonRuleSet"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "${var.prefix}-waf-common"
      sampled_requests_enabled   = true
    }
  }

  # Regla 2 — Inyección SQL avanzada
  rule {
    name     = "SQLiRuleSet"
    priority = 2

    override_action {
      none {}
    }

    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesSQLiRuleSet"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "${var.prefix}-waf-sqli"
      sampled_requests_enabled   = true
    }
  }

  # Regla 3 — Exploits conocidos (Log4Shell, SSRF)
  rule {
    name     = "KnownBadInputs"
    priority = 3

    override_action {
      none {}
    }

    statement {
      managed_rule_group_statement {
        vendor_name = "AWS"
        name        = "AWSManagedRulesKnownBadInputsRuleSet"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "${var.prefix}-waf-badinputs"
      sampled_requests_enabled   = true
    }
  }

  # Regla 4 — Rate limiting por IP
  rule {
    name     = "RateLimit"
    priority = 4

    action {
      block {}
    }

    statement {
      rate_based_statement {
        limit              = 2000
        aggregate_key_type = "IP"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "${var.prefix}-waf-ratelimit"
      sampled_requests_enabled   = true
    }
  }

  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name                = "${var.prefix}-waf"
    sampled_requests_enabled   = true
  }

  tags = {
    Name = "${var.prefix}-waf"
  }
}

resource "aws_wafv2_web_acl_logging_configuration" "main" {
  provider                = aws.edge
  log_destination_configs = [var.s3_waf_logs_bucket_arn]
  resource_arn            = aws_wafv2_web_acl.main.arn
}

