data "archive_file" "lambda_edge_zip" {
  type        = "zip"
  output_path = "${path.module}/lambda_edge.zip"

  source {
    filename = "index.js"
    content  = <<-EOF
      'use strict';

      exports.handler = async (event) => {
        const request = event.Records[0].cf.request;
        const uri = request.uri;

        if (!uri.includes('.')) {
          request.uri = '/index.html';
        }

        return request;
      };
    EOF
  }
}

resource "aws_lambda_function" "viewer_request" {
  #checkov:skip=CKV_AWS_117:Lambda@Edge no puede ejecutarse dentro de una VPC por diseño de AWS
  #checkov:skip=CKV_AWS_116:Lambda@Edge no soporta Dead Letter Queue por limitación de AWS
  #checkov:skip=CKV_AWS_272:Lambda@Edge no soporta code signing configuration

  provider = aws.edge

  function_name    = "${var.prefix}-viewer-request"
  role             = var.lambda_edge_role_arn
  runtime          = "nodejs20.x"
  handler          = "index.handler"
  filename         = data.archive_file.lambda_edge_zip.output_path
  source_code_hash = data.archive_file.lambda_edge_zip.output_base64sha256
  timeout          = 5
  memory_size      = 128
  publish          = true

  # FIX CKV_AWS_115 — límite de concurrencia a nivel función
  reserved_concurrent_executions = 100

  tracing_config {
    mode = "PassThrough"
  }

  tags = {
    Name = "${var.prefix}-viewer-request"
  }
}
