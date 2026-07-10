resource "aws_cloudwatch_metric_alarm" "dlq_messages" {
  alarm_name          = "${var.prefix}-dlq-messages"
  alarm_description   = "Hay mensajes en la DLQ — revisar PDFs fallidos"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  threshold           = 0
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/SQS"
  metric_name = "ApproximateNumberOfMessagesVisible"
  statistic   = "Sum"
  period      = 60

  dimensions = {
    QueueName = var.sqs_dlq_name
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-dlq-messages-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "ecs_cpu" {
  alarm_name          = "${var.prefix}-ecs-cpu"
  alarm_description   = "ECS CPU > 70% — posible necesidad de más Tasks"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  threshold           = 70
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/ECS"
  metric_name = "CPUUtilization"
  statistic   = "Average"
  period      = 60

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_service_name
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-ecs-cpu-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "ecs_5xx" {
  alarm_name          = "${var.prefix}-ecs-5xx"
  alarm_description   = "ALB recibe > 2% de errores 5XX del backend"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 2
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "error_rate"
    expression  = "(errors/requests)*100"
    label       = "5XX Error Rate %"
    return_data = true
  }

  metric_query {
    id = "errors"
    metric {
      namespace   = "AWS/ApplicationELB"
      metric_name = "HTTPCode_Target_5XX_Count"
      stat        = "Sum"
      period      = 60
      dimensions = {
        LoadBalancer = var.alb_arn_suffix
        TargetGroup  = var.target_group_arn_suffix
      }
    }
  }

  metric_query {
    id = "requests"
    metric {
      namespace   = "AWS/ApplicationELB"
      metric_name = "RequestCount"
      stat        = "Sum"
      period      = 60
      dimensions = {
        LoadBalancer = var.alb_arn_suffix
      }
    }
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-ecs-5xx-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "rds_connections" {
  alarm_name          = "${var.prefix}-rds-connections"
  alarm_description   = "RDS tiene > 80 conexiones activas — revisar pool config"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  threshold           = 80
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/RDS"
  metric_name = "DatabaseConnections"
  statistic   = "Average"
  period      = 60

  dimensions = {
    DBInstanceIdentifier = var.rds_identifier
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-rds-connections-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "rds_free_storage" {
  alarm_name          = "${var.prefix}-rds-free-storage"
  alarm_description   = "RDS tiene < 5 GB libres — storage al 75% de capacidad"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 2
  threshold           = 5368709120
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/RDS"
  metric_name = "FreeStorageSpace"
  statistic   = "Average"
  period      = 300

  dimensions = {
    DBInstanceIdentifier = var.rds_identifier
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-rds-free-storage-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "rds_read_latency" {
  alarm_name          = "${var.prefix}-rds-read-latency"
  alarm_description   = "Latencia de lectura RDS > 100ms — revisar queries lentas"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 0.1 # 100ms en segundos
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/RDS"
  metric_name = "ReadLatency"
  statistic   = "Average"
  period      = 60

  dimensions = {
    DBInstanceIdentifier = var.rds_identifier
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-rds-read-latency-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "lambda_errors" {
  alarm_name          = "${var.prefix}-lambda-errors"
  alarm_description   = "Lambda doc-generante falla > 5% de invocaciones"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 5
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "error_rate"
    expression  = "(errors/invocations)*100"
    label       = "Lambda Error Rate %"
    return_data = true
  }

  metric_query {
    id = "errors"
    metric {
      namespace   = "AWS/Lambda"
      metric_name = "Errors"
      stat        = "Sum"
      period      = 60
      dimensions = {
        FunctionName = var.lambda_function_name
      }
    }
  }

  metric_query {
    id = "invocations"
    metric {
      namespace   = "AWS/Lambda"
      metric_name = "Invocations"
      stat        = "Sum"
      period      = 60
      dimensions = {
        FunctionName = var.lambda_function_name
      }
    }
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-lambda-errors-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "lambda_duration" {
  alarm_name          = "${var.prefix}-lambda-duration"
  alarm_description   = "Lambda doc-generante p99 > 25s — cerca del timeout de 30s"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  threshold           = 25000 # 25 segundos en ms
  treat_missing_data  = "notBreaching"

  namespace          = "AWS/Lambda"
  metric_name        = "Duration"
  extended_statistic = "p99"
  period             = 60

  dimensions = {
    FunctionName = var.lambda_function_name
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-lambda-duration-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "apigw_4xx" {
  alarm_name          = "${var.prefix}-apigw-4xx"
  alarm_description   = "API Gateway recibe > 10% de errores 4XX"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 10
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "error_rate"
    expression  = "(errors/requests)*100"
    label       = "4XX Error Rate %"
    return_data = true
  }

  metric_query {
    id = "errors"
    metric {
      namespace   = "AWS/ApiGateway"
      metric_name = "4XXError"
      stat        = "Sum"
      period      = 60
      dimensions = {
        ApiId = var.api_id
      }
    }
  }

  metric_query {
    id = "requests"
    metric {
      namespace   = "AWS/ApiGateway"
      metric_name = "Count"
      stat        = "Sum"
      period      = 60
      dimensions = {
        ApiId = var.api_id
      }
    }
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-apigw-4xx-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "nat_error_az_a" {
  alarm_name          = "${var.prefix}-nat-error-az-a"
  alarm_description   = "NAT Gateway AZ-a no puede asignar puertos"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  threshold           = 0
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/NatGateway"
  metric_name = "ErrorPortAllocation"
  statistic   = "Sum"
  period      = 60

  dimensions = {
    NatGatewayId = var.nat_gateway_az_a_id
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-nat-error-az-a-alarm" }
}

resource "aws_cloudwatch_metric_alarm" "nat_error_az_b" {
  count = var.has_nat_az_b ? 1 : 0

  alarm_name          = "${var.prefix}-nat-error-az-b"
  alarm_description   = "NAT Gateway AZ-b no puede asignar puertos"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  threshold           = 0
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/NatGateway"
  metric_name = "ErrorPortAllocation"
  statistic   = "Sum"
  period      = 60

  dimensions = {
    NatGatewayId = var.nat_gateway_az_b_id
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-nat-error-az-b-alarm" }
}
