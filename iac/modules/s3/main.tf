data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

# ==============================================================================
# BUCKET 1 — FRONTEND ANGULAR
# ==============================================================================

resource "aws_s3_bucket" "frontend" {
  # checkov:skip=CKV_AWS_144: La replicación cross-region no es requerida para el RTO/RPO de este proyecto.
  bucket        = "${var.prefix}-frontend"
  force_destroy = false

  tags = {
    Name = "${var.prefix}-frontend"
  }
}

# FIX CKV2_AWS_62 — Event Notifications
resource "aws_s3_bucket_notification" "frontend_events" {
  bucket      = aws_s3_bucket.frontend.id
  eventbridge = true
}

resource "aws_s3_bucket_ownership_controls" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "frontend" {
  bucket = aws_s3_bucket.frontend.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = var.kms_s3_frontend_id
    }
    bucket_key_enabled = true
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "frontend" {
  bucket     = aws_s3_bucket.frontend.id
  depends_on = [aws_s3_bucket_versioning.frontend]

  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"
    filter {}
    noncurrent_version_expiration {
      noncurrent_days = 30
    }
    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}

resource "aws_s3_bucket_logging" "frontend" {
  bucket        = aws_s3_bucket.frontend.id
  target_bucket = aws_s3_bucket.access_logs.id
  target_prefix = "frontend-bucket/"
}

# ==============================================================================
# BUCKET 2 — DOCUMENTOS PDF
# ==============================================================================

resource "aws_s3_bucket" "docs" {
  # checkov:skip=CKV_AWS_144: La replicación cross-region no es requerida para el RTO/RPO de este proyecto.
  bucket        = "${var.prefix}-docs-bucket"
  force_destroy = false

  tags = {
    Name = "${var.prefix}-docs-bucket"
  }
}

# FIX CKV2_AWS_62 — Event Notifications
resource "aws_s3_bucket_notification" "docs_events" {
  bucket      = aws_s3_bucket.docs.id
  eventbridge = true
}

resource "aws_s3_bucket_ownership_controls" "docs" {
  bucket = aws_s3_bucket.docs.id
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

resource "aws_s3_bucket_public_access_block" "docs" {
  bucket = aws_s3_bucket.docs.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "docs" {
  bucket = aws_s3_bucket.docs.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "docs" {
  bucket = aws_s3_bucket.docs.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = var.kms_s3_docs_id
    }
    bucket_key_enabled = true
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "docs" {
  bucket     = aws_s3_bucket.docs.id
  depends_on = [aws_s3_bucket_versioning.docs]

  rule {
    id     = "generated-docs-lifecycle"
    status = "Enabled"
    filter {
      prefix = "generated/"
    }
    transition {
      days          = 90
      storage_class = "STANDARD_IA"
    }
    expiration {
      days = 365
    }
  }

  rule {
    id     = "temp-cleanup"
    status = "Enabled"
    filter {
      prefix = "temp/"
    }
    expiration {
      days = 1
    }
  }

  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"
    filter {}
    noncurrent_version_expiration {
      noncurrent_days = 30
    }
    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}

resource "aws_s3_bucket_logging" "docs" {
  bucket        = aws_s3_bucket.docs.id
  target_bucket = aws_s3_bucket.access_logs.id
  target_prefix = "docs-bucket/"
}

data "aws_iam_policy_document" "docs_bucket_policy" {
  statement {
    sid    = "AllowECSAccess"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [var.ecs_task_role_arn]
    }
    actions   = ["s3:PutObject", "s3:GetObject"]
    resources = ["${aws_s3_bucket.docs.arn}/*"]
  }

  statement {
    sid    = "AllowLambdaAccess"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [var.lambda_docgen_role_arn]
    }
    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.docs.arn}/generated/*"]
  }

  statement {
    sid    = "AllowBackupAccess"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [var.backup_role_arn]
    }
    actions = [
      "s3:GetObject", "s3:GetObjectVersion", "s3:ListBucket",
      "s3:GetBucketLocation", "s3:GetObjectAcl", "s3:GetBucketAcl"
    ]
    resources = [aws_s3_bucket.docs.arn, "${aws_s3_bucket.docs.arn}/*"]
  }

  statement {
    sid    = "DenyNonHTTPS"
    effect = "Deny"
    principals {
      type        = "*"
      identifiers = ["*"]
    }
    actions   = ["s3:*"]
    resources = [aws_s3_bucket.docs.arn, "${aws_s3_bucket.docs.arn}/*"]
    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }
}

resource "aws_s3_bucket_policy" "docs" {
  bucket     = aws_s3_bucket.docs.id
  policy     = data.aws_iam_policy_document.docs_bucket_policy.json
  depends_on = [aws_s3_bucket_public_access_block.docs]
}

# ==============================================================================
# BUCKET 3 — WAF LOGS
# ==============================================================================

resource "aws_s3_bucket" "waf_logs" {
  # checkov:skip=CKV_AWS_144: La replicación cross-region no es requerida para el RTO/RPO de este proyecto.
  bucket        = "aws-waf-logs-${var.prefix}"
  force_destroy = false

  tags = {
    Name = "aws-waf-logs-${var.prefix}"
  }
}

# FIX CKV2_AWS_62 — Event Notifications
resource "aws_s3_bucket_notification" "waf_logs_events" {
  bucket      = aws_s3_bucket.waf_logs.id
  eventbridge = true
}

resource "aws_s3_bucket_ownership_controls" "waf_logs" {
  bucket = aws_s3_bucket.waf_logs.id
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

resource "aws_s3_bucket_public_access_block" "waf_logs" {
  bucket = aws_s3_bucket.waf_logs.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "waf_logs" {
  bucket = aws_s3_bucket.waf_logs.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = var.kms_logs_id
    }
    bucket_key_enabled = true
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "waf_logs" {
  bucket = aws_s3_bucket.waf_logs.id

  rule {
    id     = "waf-logs-retention"
    status = "Enabled"
    filter {}
    expiration {
      days = 90
    }
    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}

resource "aws_s3_bucket_versioning" "waf_logs" {
  bucket = aws_s3_bucket.waf_logs.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_logging" "waf_logs" {
  bucket        = aws_s3_bucket.waf_logs.id
  target_bucket = aws_s3_bucket.access_logs.id
  target_prefix = "waf-logs/"
}

# BUCKET 4 — ACCESS LOGS

resource "aws_s3_bucket" "access_logs" {
  # checkov:skip=CKV_AWS_144: La replicación cross-region no es requerida para el RTO/RPO de este proyecto.
  # checkov:skip=CKV_AWS_18: No se activa access logging sobre el propio bucket de logs para evitar bucles infinitos.
  bucket        = "${var.prefix}-access-logs"
  force_destroy = false

  tags = {
    Name = "${var.prefix}-access-logs"
  }
}

# FIX CKV2_AWS_62 — Event Notifications
resource "aws_s3_bucket_notification" "access_logs_events" {
  bucket      = aws_s3_bucket.access_logs.id
  eventbridge = true
}

resource "aws_s3_bucket_ownership_controls" "access_logs" {
  bucket = aws_s3_bucket.access_logs.id
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
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
      type        = "Service"
      identifiers = ["delivery.logs.amazonaws.com"]
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

  # S3 access logging escribe via logging.s3.amazonaws.com.
  # SourceArn limita a solo el bucket de docs, no cualquier bucket.
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