resource "aws_cloudwatch_log_group" "route53_query_log" {
  provider          = aws.edge
  name              = "/aws/route53/${var.domain_name}"
  retention_in_days = 365
  kms_key_id        = var.kms_route53_logs_arn

  tags = {
    Name = "${var.prefix}-route53-query-log"
  }
}

resource "aws_route53_query_log" "main" {
  zone_id                  = local.zone_id
  cloudwatch_log_group_arn = aws_cloudwatch_log_group.route53_query_log.arn

  depends_on = [
    aws_cloudwatch_log_resource_policy.route53_query_logging_policy
  ]
}

data "aws_iam_policy_document" "route53_query_logging_policy" {
  statement {
    actions = [
      "logs:CreateLogStream",
      "logs:PutLogEvents",
    ]
    resources = ["${aws_cloudwatch_log_group.route53_query_log.arn}:*"]
    principals {
      identifiers = ["route53.amazonaws.com"]
      type        = "Service"
    }
  }
}

resource "aws_cloudwatch_log_resource_policy" "route53_query_logging_policy" {
  provider        = aws.edge
  policy_document = data.aws_iam_policy_document.route53_query_logging_policy.json
  policy_name     = "route53-query-logging-policy"
}
