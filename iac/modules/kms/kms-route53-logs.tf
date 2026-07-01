resource "aws_kms_key" "route53_logs" {
  provider                = aws.edge
  description             = "Cifrado CloudWatch Logs Route53 query log - us-east-1 - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-route53-logs"
  }
}

resource "aws_kms_alias" "route53_logs" {
  provider      = aws.edge
  name          = "alias/${var.prefix}/route53-logs"
  target_key_id = aws_kms_key.route53_logs.key_id
}

resource "aws_kms_key_policy" "route53_logs" {
  provider = aws.edge
  key_id   = aws_kms_key.route53_logs.id

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
        Sid    = "AllowCloudWatchLogs"
        Effect = "Allow"
        Principal = {
          Service = "logs.us-east-1.amazonaws.com"
        }
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
          "kms:DescribeKey"
        ]
        Resource = "*"
        Condition = {
          ArnEquals = {
            "kms:EncryptionContext:aws:logs:arn" = "arn:aws:logs:us-east-1:${local.account_id}:*"
          }
        }
      }
    ]
  })
}
