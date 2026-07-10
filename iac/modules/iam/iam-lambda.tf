data "aws_iam_policy_document" "lambda_docgen_trust" {
  statement {
    sid     = "AllowLambdaAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "lambda_docgen" {
  name               = "${var.prefix}-lambda-docgen-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_docgen_trust.json

  tags = {
    Name = "${var.prefix}-lambda-docgen-role"
  }
}

data "aws_iam_policy_document" "lambda_docgen_permissions" {
  statement {
    sid    = "SQSConsume"
    effect = "Allow"
    actions = [
      "sqs:ReceiveMessage",
      "sqs:DeleteMessage",
      "sqs:GetQueueAttributes",
      "sqs:ChangeMessageVisibility"
    ]
    resources = [
      local.sqs_queue_arn,
      local.sqs_dlq_arn
    ]
  }

  statement {
    sid       = "SQSDlqSend"
    effect    = "Allow"
    actions   = ["sqs:SendMessage"]
    resources = [local.sqs_dlq_arn]
  }

  statement {
    sid    = "KMSSQSDecrypt"
    effect = "Allow"
    actions = [
      "kms:Decrypt",
      "kms:GenerateDataKey"
    ]
    resources = [var.kms_sqs_arn]
  }

  statement {
    sid       = "S3DocsPut"
    effect    = "Allow"
    actions   = ["s3:PutObject"]
    resources = ["${local.s3_docs_bucket_arn}/generated/*"]
  }

  statement {
    sid    = "KMSDocsAccess"
    effect = "Allow"
    actions = [
      "kms:GenerateDataKey",
      "kms:Decrypt"
    ]
    resources = [var.kms_s3_docs_arn]
  }

  statement {
    sid       = "SecretsManagerGet"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [local.rds_secret_arn_pattern]
  }

  statement {
    sid       = "KMSSecretsDecrypt"
    effect    = "Allow"
    actions   = ["kms:Decrypt"]
    resources = [var.kms_secrets_arn]
  }

  statement {
    sid    = "CloudWatchLogs"
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = ["arn:aws:logs:${local.region}:${local.account_id}:log-group:/aws/lambda/*"]
  }

  statement {
    sid    = "VPCDescribeOnly"
    effect = "Allow"
    actions = [
      "ec2:DescribeNetworkInterfaces",
      "ec2:DescribeSubnets",
      "ec2:DescribeSecurityGroups",
      "ec2:DescribeVpcs"
    ]
    resources = ["*"]
  }

  statement {
    sid    = "VPCNetworkInterfaceWrite"
    effect = "Allow"
    actions = [
      "ec2:CreateNetworkInterface",
      "ec2:DeleteNetworkInterface",
      "ec2:AttachNetworkInterface",
      "ec2:DetachNetworkInterface"
    ]
    resources = [
      "arn:aws:ec2:${local.region}:${local.account_id}:network-interface/*",
      "arn:aws:ec2:${local.region}:${local.account_id}:subnet/*",
      "arn:aws:ec2:${local.region}:${local.account_id}:security-group/*"
    ]
  }
}

resource "aws_iam_role_policy" "lambda_docgen" {
  name   = "${var.prefix}-lambda-docgen-policy"
  role   = aws_iam_role.lambda_docgen.id
  policy = data.aws_iam_policy_document.lambda_docgen_permissions.json
}

resource "aws_iam_role_policy_attachment" "lambda_vpc_access" {
  role       = aws_iam_role.lambda_docgen.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policies_exclusive" "lambda_docgen" {
  role_name    = aws_iam_role.lambda_docgen.name
  policy_names = [aws_iam_role_policy.lambda_docgen.name]
}
