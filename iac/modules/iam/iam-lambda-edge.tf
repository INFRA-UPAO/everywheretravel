data "aws_iam_policy_document" "lambda_edge_trust" {
    statement {
        sid     = "AllowLambdaAndEdgeLambdaAssume"
        effect  = "Allow"
        actions = ["sts:AssumeRole"]

        principals {
        type = "Service"
        identifiers = [
            "lambda.amazonaws.com",
            "edgelambda.amazonaws.com"
        ]
        }
    }
}

resource "aws_iam_role" "lambda_edge" {
    name               = "${var.prefix}-lambda-edge-role"
    assume_role_policy = data.aws_iam_policy_document.lambda_edge_trust.json

    tags = {
        Name = "${var.prefix}-lambda-edge-role"
    }
}

data "aws_iam_policy_document" "lambda_edge_permissions" {
    statement {
        sid    = "CloudWatchLogsEdge"
        effect = "Allow"
        actions = [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
        ]
        resources = ["arn:aws:logs:*:${local.account_id}:log-group:/aws/lambda/*"]
    }
}

resource "aws_iam_role_policy" "lambda_edge" {
    name   = "${var.prefix}-lambda-edge-policy"
    role   = aws_iam_role.lambda_edge.id
    policy = data.aws_iam_policy_document.lambda_edge_permissions.json
}

resource "aws_iam_role_policies_exclusive" "lambda_edge" {
    role_name    = aws_iam_role.lambda_edge.name
    policy_names = [aws_iam_role_policy.lambda_edge.name]
}
