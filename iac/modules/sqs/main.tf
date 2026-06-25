data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

# DLQ
resource "aws_sqs_queue" "dlq" {
  name = "${var.prefix}-docs-generation-dlq"

  message_retention_seconds = 1209600

  kms_master_key_id                 = var.kms_sqs_arn
  kms_data_key_reuse_period_seconds = 300

  tags = {
    Name = "${var.prefix}-docs-generation-dlq"
  }
}

resource "aws_sqs_queue_redrive_allow_policy" "dlq" {
  queue_url = aws_sqs_queue.dlq.url

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue"
    sourceQueueArns = [
      "arn:aws:sqs:${local.region}:${local.account_id}:${var.prefix}-docs-generation-queue"
    ]
  })
}

# QUEUE
resource "aws_sqs_queue" "main" {
  name = "${var.prefix}-docs-generation-queue"

  visibility_timeout_seconds = 360

  message_retention_seconds = 345600 # 4 días

  max_message_size = 262144

  receive_wait_time_seconds = 20

  kms_master_key_id                 = var.kms_sqs_arn
  kms_data_key_reuse_period_seconds = 300

  tags = {
    Name = "${var.prefix}-docs-generation-queue"
  }
}

resource "aws_sqs_queue_redrive_policy" "main" {
  queue_url = aws_sqs_queue.main.url

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.dlq.arn
    maxReceiveCount     = 3
  })
}

data "aws_iam_policy_document" "main_queue_policy" {
  statement {
    sid    = "AllowECSProducer"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [var.ecs_task_role_arn]
    }

    actions = [
      "sqs:SendMessage",
      "sqs:GetQueueAttributes",
      "sqs:GetQueueUrl"
    ]

    resources = [aws_sqs_queue.main.arn]
  }

  statement {
    sid    = "AllowLambdaConsumer"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [var.lambda_docgen_role_arn]
    }

    actions = [
      "sqs:ReceiveMessage",
      "sqs:DeleteMessage",
      "sqs:GetQueueAttributes",
      "sqs:ChangeMessageVisibility"
    ]

    resources = [aws_sqs_queue.main.arn]
  }

  statement {
    sid    = "DenyExternalAccess"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["sqs:*"]
    resources = [aws_sqs_queue.main.arn]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_sqs_queue_policy" "main" {
  queue_url = aws_sqs_queue.main.url
  policy    = data.aws_iam_policy_document.main_queue_policy.json
}
