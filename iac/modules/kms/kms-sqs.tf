resource "aws_kms_key" "sqs" {
  description             = "Cifrado SSE-KMS para SQS Queue y DLQ - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-sqs"
  }
}

resource "aws_kms_alias" "sqs" {
  name          = "alias/${var.prefix}/sqs"
  target_key_id = aws_kms_key.sqs.key_id
}

resource "aws_kms_key_policy" "sqs" {
  key_id = aws_kms_key.sqs.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EnableRootAccess"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${local.account_id}:root"
        }
        Action   = "kms:*"
        Resource = "*"
      },
      {
        Sid    = "AllowSQSService"
        Effect = "Allow"
        Principal = {
          Service = "sqs.amazonaws.com"
        }
        Action = [
          "kms:GenerateDataKey",
          "kms:Decrypt"
        ]
        Resource = "*"
      }
    ]
  })
}
