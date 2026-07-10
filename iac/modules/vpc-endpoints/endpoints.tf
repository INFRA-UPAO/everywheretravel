data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

# S3 Gateway
resource "aws_vpc_endpoint" "s3" {
  vpc_id            = var.vpc_id
  service_name      = "com.amazonaws.${local.region}.s3"
  vpc_endpoint_type = "Gateway"

  route_table_ids = [
    var.rt_private_app_az_a_id,
    var.rt_private_app_az_b_id
  ]

  tags = {
    Name = "${var.prefix}-vpce-s3"
  }
}

data "aws_iam_policy_document" "s3_endpoint_policy" {
  statement {
    sid    = "AllowDocsBucket"
    effect = "Allow"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions = [
      "s3:GetObject",
      "s3:PutObject",
      "s3:DeleteObject",
      "s3:ListBucket"
    ]

    resources = [
      var.s3_docs_bucket_arn,
      "${var.s3_docs_bucket_arn}/*"
    ]

    condition {
      test     = "StringEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }

  statement {
    sid    = "AllowECRLayersBucket"
    effect = "Allow"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["s3:GetObject"]
    resources = ["arn:aws:s3:::prod-${local.region}-starport-layer-bucket/*"]
  }
}

resource "aws_vpc_endpoint_policy" "s3" {
  vpc_endpoint_id = aws_vpc_endpoint.s3.id
  policy          = data.aws_iam_policy_document.s3_endpoint_policy.json
}

# ECR API
resource "aws_vpc_endpoint" "ecr_api" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${local.region}.ecr.api"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.private_app_subnet_ids
  security_group_ids  = [var.sg_vpce_ecr_id]
  private_dns_enabled = true

  tags = {
    Name = "${var.prefix}-vpce-ecr-api"
  }
}

data "aws_iam_policy_document" "ecr_api_endpoint_policy" {
  statement {
    sid    = "AllowECSExecutionRole"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [var.ecs_execution_role_arn]
    }

    actions = [
      "ecr:GetAuthorizationToken",
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage"
    ]

    resources = ["*"]
  }

  statement {
    sid    = "DenyExternalAccess"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["ecr:*"]
    resources = ["*"]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_vpc_endpoint_policy" "ecr_api" {
  vpc_endpoint_id = aws_vpc_endpoint.ecr_api.id
  policy          = data.aws_iam_policy_document.ecr_api_endpoint_policy.json
}

# ECR DkR
resource "aws_vpc_endpoint" "ecr_dkr" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${local.region}.ecr.dkr"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.private_app_subnet_ids
  security_group_ids  = [var.sg_vpce_ecr_id]
  private_dns_enabled = true

  tags = {
    Name = "${var.prefix}-vpce-ecr-dkr"
  }
}

resource "aws_vpc_endpoint_policy" "ecr_dkr" {
  vpc_endpoint_id = aws_vpc_endpoint.ecr_dkr.id
  policy          = data.aws_iam_policy_document.ecr_api_endpoint_policy.json
}

# SQS
resource "aws_vpc_endpoint" "sqs" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${local.region}.sqs"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.private_app_subnet_ids
  security_group_ids  = [var.sg_vpce_sqs_id]
  private_dns_enabled = true

  tags = {
    Name = "${var.prefix}-vpce-sqs"
  }
}

data "aws_iam_policy_document" "sqs_endpoint_policy" {
  statement {
    sid    = "AllowECSTaskRole"
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

    resources = [
      var.sqs_queue_arn,
      var.sqs_dlq_arn
    ]
  }

  statement {
    sid    = "AllowLambdaDocgenRole"
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

    resources = [
      var.sqs_queue_arn,
      var.sqs_dlq_arn
    ]
  }

  statement {
    sid    = "DenyExternalAccess"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["sqs:*"]
    resources = ["*"]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_vpc_endpoint_policy" "sqs" {
  vpc_endpoint_id = aws_vpc_endpoint.sqs.id
  policy          = data.aws_iam_policy_document.sqs_endpoint_policy.json
}

# SM
resource "aws_vpc_endpoint" "secretsmanager" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${local.region}.secretsmanager"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.private_app_subnet_ids
  security_group_ids  = [var.sg_vpce_sm_id]
  private_dns_enabled = true

  tags = {
    Name = "${var.prefix}-vpce-secretsmanager"
  }
}

data "aws_iam_policy_document" "sm_endpoint_policy" {
  statement {
    sid    = "AllowAppRoles"
    effect = "Allow"

    principals {
      type = "AWS"
      identifiers = [
        var.ecs_execution_role_arn,
        var.ecs_task_role_arn,
        var.lambda_docgen_role_arn
      ]
    }

    actions   = ["secretsmanager:GetSecretValue"]
    resources = [var.rds_secret_arn]
  }

  statement {
    sid    = "AllowSecretsManagerRotation"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["secretsmanager.amazonaws.com"]
    }

    actions = [
      "secretsmanager:GetSecretValue",
      "secretsmanager:PutSecretValue",
      "secretsmanager:DescribeSecret",
      "secretsmanager:UpdateSecretVersionStage"
    ]

    resources = [var.rds_secret_arn]

    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [local.account_id]
    }
  }

  statement {
    sid    = "DenyExternalAccess"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["secretsmanager:*"]
    resources = ["*"]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_vpc_endpoint_policy" "secretsmanager" {
  vpc_endpoint_id = aws_vpc_endpoint.secretsmanager.id
  policy          = data.aws_iam_policy_document.sm_endpoint_policy.json
}

# CW LOGS
resource "aws_vpc_endpoint" "cloudwatch_logs" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${local.region}.logs"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.private_app_subnet_ids
  security_group_ids  = [var.sg_vpce_logs_id]
  private_dns_enabled = true

  tags = {
    Name = "${var.prefix}-vpce-cloudwatch-logs"
  }
}

data "aws_iam_policy_document" "cw_logs_endpoint_policy" {
  statement {
    sid    = "AllowLogsWrite"
    effect = "Allow"

    principals {
      type = "AWS"
      identifiers = [
        var.ecs_execution_role_arn,
        var.ecs_task_role_arn,
        var.lambda_docgen_role_arn
      ]
    }

    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents",
      "logs:DescribeLogGroups",
      "logs:DescribeLogStreams"
    ]

    resources = [
      "arn:aws:logs:${local.region}:${local.account_id}:log-group:/aws/ecs/*",
      "arn:aws:logs:${local.region}:${local.account_id}:log-group:/aws/lambda/*"
    ]
  }

  statement {
    sid    = "DenyExternalAccess"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["logs:*"]
    resources = ["*"]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_vpc_endpoint_policy" "cloudwatch_logs" {
  vpc_endpoint_id = aws_vpc_endpoint.cloudwatch_logs.id
  policy          = data.aws_iam_policy_document.cw_logs_endpoint_policy.json
}
