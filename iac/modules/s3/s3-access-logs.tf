data "aws_caller_identity" "current" {}
data "aws_region" "current" {}
data "aws_elb_service_account" "main" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

resource "aws_s3_bucket" "access_logs" {
  bucket        = "${var.prefix}-access-logs"
  force_destroy = false

  tags = {
    Name = "${var.prefix}-access-logs"
  }
}

resource "aws_s3_bucket_notification" "access_logs_events" {
  bucket      = aws_s3_bucket.access_logs.id
  eventbridge = true
}

resource "aws_s3_bucket_ownership_controls" "access_logs" {
  bucket = aws_s3_bucket.access_logs.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_acl" "access_logs" {
  bucket = aws_s3_bucket.access_logs.id
  acl    = "log-delivery-write"

  depends_on = [aws_s3_bucket_ownership_controls.access_logs]
}

resource "aws_s3_bucket_public_access_block" "access_logs" {
  bucket = aws_s3_bucket.access_logs.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "access_logs" {
  # checkov:skip=CKV_AWS_145: SSE-S3 (AES256) es intencional y necesario por compatibilidad de entrega de logs de servicios de AWS (ej. ALB).
  bucket = aws_s3_bucket.access_logs.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "access_logs" {
  # checkov:skip=CKV2_AWS_61: El lifecycle existe y se declara aquí (prevención de falsos positivos en el análisis estático).
  bucket = aws_s3_bucket.access_logs.id
  rule {
    id     = "access-logs-retention"
    status = "Enabled"
    filter {}
    expiration {
      days = 30
    }
    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}

resource "aws_s3_bucket_versioning" "access_logs" {
  bucket = aws_s3_bucket.access_logs.id
  versioning_configuration {
    status = "Enabled"
  }
}

data "aws_iam_policy_document" "access_logs_policy" {
  statement {
    sid    = "AllowALBLogging"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [data.aws_elb_service_account.main.arn]
    }

    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.access_logs.arn}/alb/*"]
    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [local.account_id]
    }

    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"
      values   = ["bucket-owner-full-control"]
    }
  }

  statement {
    sid    = "AllowS3AccessLogging"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["logging.s3.amazonaws.com"]
    }

    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.access_logs.arn}/docs-bucket/*"]

    condition {
      test     = "ArnLike"
      variable = "aws:SourceArn"
      values   = [aws_s3_bucket.docs.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [local.account_id]
    }
  }

  statement {
    sid    = "AllowS3FrontendLogging"
    effect = "Allow"
    principals {
      type        = "Service"
      identifiers = ["logging.s3.amazonaws.com"]
    }
    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.access_logs.arn}/frontend-bucket/*"]
    condition {
      test     = "ArnLike"
      variable = "aws:SourceArn"
      values   = [aws_s3_bucket.frontend.arn]
    }
    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [local.account_id]
    }
  }
  statement {
    sid    = "AllowS3WafLogging"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["logging.s3.amazonaws.com"]
    }
    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.access_logs.arn}/waf-logs/*"]
    condition {
      test     = "ArnLike"
      variable = "aws:SourceArn"
      values   = [aws_s3_bucket.waf_logs.arn]
    }
    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_s3_bucket_policy" "access_logs" {
  bucket = aws_s3_bucket.access_logs.id
  policy = data.aws_iam_policy_document.access_logs_policy.json

  depends_on = [aws_s3_bucket_public_access_block.access_logs]
}
