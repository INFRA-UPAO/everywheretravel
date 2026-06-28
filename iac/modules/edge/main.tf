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


# LAMBDA@EDGE — VIEWER REQUEST
data "archive_file" "lambda_edge_zip" {
  type        = "zip"
  output_path = "${path.module}/lambda_edge.zip"

  source {
    filename = "index.js"
    content  = <<-EOF
      'use strict';
 
      exports.handler = async (event) => {
        const request = event.Records[0].cf.request;
        const uri = request.uri;
 
        if (!uri.includes('.')) {
          request.uri = '/index.html';
        }
 
        return request;
      };
    EOF
  }
}

resource "aws_lambda_function" "viewer_request" {
  # checkov:skip=CKV_AWS_272: Lambda@Edge no soporta code signing configuration
  provider = aws.edge

  function_name    = "${var.prefix}-viewer-request"
  role             = var.lambda_edge_role_arn
  runtime          = "nodejs20.x"
  handler          = "index.handler"
  filename         = data.archive_file.lambda_edge_zip.output_path
  source_code_hash = data.archive_file.lambda_edge_zip.output_base64sha256
  timeout          = 5
  memory_size      = 128
  publish          = true
  tracing_config {
    mode = "PassThrough"
  }
  tags = {
    Name = "${var.prefix}-viewer-request"
  }
}

# CLOUDFRONT
resource "aws_cloudfront_origin_access_control" "s3" {
  provider = aws.edge

  name                              = "${var.prefix}-oac"
  description                       = "OAC para S3 Frontend - ${var.prefix}"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# CLOUDFRONT — RESPONSE HEADERS POLICY
resource "aws_cloudfront_response_headers_policy" "security" {
  provider = aws.edge
  name     = "${var.prefix}-security-headers"

  security_headers_config {
    strict_transport_security {
      access_control_max_age_sec = 31536000
      include_subdomains         = true
      preload                    = true
      override                   = true
    }

    content_type_options {
      override = true
    }

    frame_options {
      frame_option = "DENY"
      override     = true
    }

    xss_protection {
      mode_block = true
      protection = true
      override   = true
    }

    referrer_policy {
      referrer_policy = "strict-origin-when-cross-origin"
      override        = true
    }
  }
}

# CLOUDFRONT — DISTRIBUTION
resource "aws_cloudfront_distribution" "main" {
  provider = aws.edge

  enabled             = true
  is_ipv6_enabled     = true
  comment             = "Everywhere Travel - ${var.prefix}"
  default_root_object = "index.html"
  aliases             = [var.domain_name]
  price_class         = "PriceClass_200"
  web_acl_id          = aws_wafv2_web_acl.main.arn

  # ORIGEN 1 — S3 Frontend (Angular)
  origin {
    domain_name              = "${var.s3_frontend_bucket_id}.s3.${data.aws_region.main.region}.amazonaws.com"
    origin_id                = "s3-frontend"
    origin_access_control_id = aws_cloudfront_origin_access_control.s3.id
  }

  # ORIGEN 2 — API Gateway (requests /api/*)

  origin {
    domain_name = local.api_gateway_host
    origin_id   = "api-gateway"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  ordered_cache_behavior {
    path_pattern     = "/api/*"
    target_origin_id = "api-gateway"
    allowed_methods  = ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
    cached_methods   = ["GET", "HEAD"]

    viewer_protocol_policy = "redirect-to-https"
    cache_policy_id        = "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"

    origin_request_policy_id   = "b689b0a8-53d0-40ab-baf2-68738e2966ac"
    response_headers_policy_id = aws_cloudfront_response_headers_policy.security.id

    compress = false
  }

  default_cache_behavior {
    target_origin_id = "s3-frontend"
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]

    viewer_protocol_policy     = "redirect-to-https"
    response_headers_policy_id = aws_cloudfront_response_headers_policy.security.id
    compress                   = true

    cache_policy_id = "658327ea-f89d-4fab-a63d-7e88639e58f6"

    lambda_function_association {
      event_type   = "viewer-request"
      lambda_arn   = aws_lambda_function.viewer_request.qualified_arn
      include_body = false
    }
  }

  custom_error_response {
    error_code            = 403
    response_code         = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 0
  }

  custom_error_response {
    error_code            = 404
    response_code         = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 0
  }

  viewer_certificate {
    acm_certificate_arn      = aws_acm_certificate_validation.main.certificate_arn
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2021"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  logging_config {
    bucket          = "${var.s3_access_logs_bucket}.s3.amazonaws.com"
    prefix          = "cloudfront/"
    include_cookies = false
  }

  tags = {
    Name = "${var.prefix}-cloudfront"
  }

  depends_on = [aws_acm_certificate_validation.main]
}


# S3 FRONTEND BUCKET POLICY
data "aws_iam_policy_document" "frontend_bucket_policy" {
  statement {
    sid    = "AllowCloudFrontOAC"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["cloudfront.amazonaws.com"]
    }

    actions   = ["s3:GetObject"]
    resources = ["${var.s3_frontend_bucket_arn}/*"]

    condition {
      test     = "StringEquals"
      variable = "AWS:SourceArn"
      values   = [aws_cloudfront_distribution.main.arn]
    }
  }

  statement {
    sid    = "DenyNonHTTPS"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions = ["s3:*"]
    resources = [
      var.s3_frontend_bucket_arn,
      "${var.s3_frontend_bucket_arn}/*"
    ]

    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }
}

resource "aws_s3_bucket_policy" "frontend" {
  bucket = var.s3_frontend_bucket_id
  policy = data.aws_iam_policy_document.frontend_bucket_policy.json

  depends_on = [aws_cloudfront_distribution.main]
}
