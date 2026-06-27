terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.49.0"
    }
  }
}

provider "aws" {
  region = "us-east-2"
}

# checkov:skip=CKV2_AWS_62: Bucket de state management no requiere event notifications
resource "aws_s3_bucket" "tfstate" {
  #checkov:skip=CKV_AWS_144:Cross-region replication no aplica para bucket de tfstate con versionado habilitado
  #checkov:skip=CKV2_AWS_62:Event notifications no requeridas para bucket de gestion de estado
  #checkov:skip=CKV_AWS_18:Access logging omitido, no existe bucket de logs en bootstrap
  bucket        = "everywhere-travel-tfstate"
  force_destroy = false

  tags = {
    Name      = "everywhere-travel-tfstate"
    ManagedBy = "terraform-bootstrap"
  }
}

resource "aws_s3_bucket_versioning" "tfstate" {
  bucket = aws_s3_bucket.tfstate.id
  versioning_configuration {
    status = "Enabled"
  }
}

# Fix CKV_AWS_145: cifrado KMS en lugar de AES256.
# Usa la llave AWS-managed (alias/aws/s3) para evitar costo de CMK.
resource "aws_s3_bucket_server_side_encryption_configuration" "tfstate" {
  bucket = aws_s3_bucket.tfstate.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = "alias/aws/s3"
    }
    bucket_key_enabled = true
  }
}

# Fix CKV2_AWS_61: lifecycle configuration para gestionar versiones antiguas del state.
resource "aws_s3_bucket_lifecycle_configuration" "tfstate" {
  bucket = aws_s3_bucket.tfstate.id

  rule {
    id     = "cleanup-old-state-versions"
    status = "Enabled"

    filter {}

    noncurrent_version_expiration {
      noncurrent_days = 90
    }

    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }

  depends_on = [aws_s3_bucket_versioning.tfstate]
}

resource "aws_s3_bucket_public_access_block" "tfstate" {
  bucket                  = aws_s3_bucket.tfstate.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
