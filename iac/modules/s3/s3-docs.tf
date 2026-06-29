resource "aws_s3_bucket" "docs" {
  # checkov:skip=CKV_AWS_144: La replicación cross-region no es requerida para el RTO/RPO de este proyecto.
  bucket        = "${var.prefix}-docs-bucket"
  force_destroy = false

  tags = {
    Name = "${var.prefix}-docs-bucket"
  }
}

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
