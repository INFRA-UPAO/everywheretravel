resource "aws_s3_bucket" "frontend" {
  # checkov:skip=CKV_AWS_144: La replicación cross-region no es requerida para el RTO/RPO de este proyecto.
  bucket        = "${var.prefix}-frontend"
  force_destroy = false

  tags = {
    Name = "${var.prefix}-frontend"
  }
}

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
