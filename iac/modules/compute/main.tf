data "aws_region" "current" {}

locals {
    region = data.aws_region.current.region
}

# CLOUDWATCH LOG GROUP
resource "aws_cloudwatch_log_group" "ecs" {
    name              = "/aws/ecs/${var.prefix}/monolito"
    retention_in_days = 365
    kms_key_id        = var.kms_logs_arn

    tags = {
        Name = "${var.prefix}-ecs-logs"
    }
}

# ECS CLUSTER (Fargate)
resource "aws_ecs_cluster" "main" {
    name = "${var.prefix}-cluster"

    setting {
        name  = "containerInsights"
        value = "enabled"
    }

    tags = {
        Name = "${var.prefix}-cluster"
    }
}

resource "aws_ecs_cluster_capacity_providers" "main" {
    cluster_name = aws_ecs_cluster.main.name

    capacity_providers = ["FARGATE"]

    default_capacity_provider_strategy {
        capacity_provider = "FARGATE"
        weight            = 1
        base              = 1
    }
}

# ALB — INTERNO
resource "aws_lb" "main" {
    name               = "${var.prefix}-alb"
    internal           = true
    load_balancer_type = "application"
    security_groups    = [var.sg_alb_id]
    subnets            = var.private_app_subnet_ids

    idle_timeout                     = 60
    enable_cross_zone_load_balancing = true
    drop_invalid_header_fields       = true
    enable_deletion_protection       = true

    access_logs {
        bucket  = var.s3_access_logs_bucket
        prefix  = "alb"
        enabled = true
    }

    tags = {
        Name = "${var.prefix}-alb"
    }
}

resource "aws_lb_target_group" "main" {
    name        = "${var.prefix}-ecs-tg"
    port        = var.ecs_app_port
    protocol    = "HTTP"
    vpc_id      = data.aws_vpc.current.id
    target_type = "ip"

    health_check {
        enabled             = true
        path                = "/health"
        port                = "traffic-port"
        protocol            = "HTTP"
        healthy_threshold   = 2
        unhealthy_threshold = 3
        timeout             = 5
        interval            = 30
        matcher             = "200"
    }

    deregistration_delay = 30

    tags = {
        Name = "${var.prefix}-ecs-tg"
    }
}

data "aws_vpc" "current" {
    filter {
        name   = "tag:Name"
        values = ["${var.prefix}-vpc"]
    }
}

# ALB LISTENER
resource "aws_lb_listener" "http" {
    load_balancer_arn = aws_lb.main.arn
    port              = 80
    protocol          = "HTTP"

    default_action {
        type = "redirect"

        redirect {
        port        = "443"
        protocol    = "HTTPS"
        status_code = "HTTP_301"
        }
    }

    tags = {
        Name = "${var.prefix}-alb-http-listener"
    }
}

resource "aws_lb_listener" "main" {
    load_balancer_arn = aws_lb.main.arn
    port              = 443
    protocol          = "HTTPS"
    certificate_arn   = var.alb_certificate_arn
    ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"

    default_action {
        type             = "forward"
        target_group_arn = aws_lb_target_group.main.arn
    }

    tags = {
        Name = "${var.prefix}-alb-https-listener"
    }
}

# ECS TASK
resource "aws_ecs_task_definition" "monolito" {
    family                   = "${var.prefix}-monolito"
    requires_compatibilities = ["FARGATE"]
    network_mode             = "awsvpc"
    cpu                      = var.ecs_cpu
    memory                   = var.ecs_memory
    execution_role_arn       = var.ecs_execution_role_arn
    task_role_arn            = var.ecs_task_role_arn

    container_definitions = jsonencode([
        {
        name      = "monolito"
        image     = "${var.ecr_repo_url}:${var.ecr_image_tag}"
        cpu       = var.ecs_cpu
        memory    = var.ecs_memory
        essential = true

        portMappings = [
            {
            containerPort = var.ecs_app_port
            protocol      = "tcp"
            }
        ]

        environment = [
            { name = "APP_ENV", value = "production" },
            { name = "LOG_LEVEL", value = "INFO" },
            { name = "SQS_QUEUE_URL", value = var.sqs_queue_url },
            { name = "S3_DOCS_BUCKET", value = var.s3_docs_bucket },
            { name = "SERVER_PORT", value = tostring(var.ecs_app_port) }
        ]

        secrets = [
            {
            name      = "DB_HOST"
            valueFrom = "${var.rds_secret_arn}:host::"
            },
            {
            name      = "DB_PORT"
            valueFrom = "${var.rds_secret_arn}:port::"
            },
            {
            name      = "DB_NAME"
            valueFrom = "${var.rds_secret_arn}:dbname::"
            },
            {
            name      = "DB_USER"
            valueFrom = "${var.rds_secret_arn}:username::"
            },
            {
            name      = "DB_PASSWORD"
            valueFrom = "${var.rds_secret_arn}:password::"
            }
        ]

        logConfiguration = {
            logDriver = "awslogs"
            options = {
            "awslogs-group"         = aws_cloudwatch_log_group.ecs.name
            "awslogs-region"        = local.region
            "awslogs-stream-prefix" = "ecs"
            }
        }

        healthCheck = {
            command     = ["CMD-SHELL", "curl -f http://localhost:${var.ecs_app_port}/health || exit 1"]
            interval    = 30
            timeout     = 5
            retries     = 3
            startPeriod = 60
        }

        readonlyRootFilesystem = true
        }
    ])

    tags = {
        Name = "${var.prefix}-monolito-task"
    }
}

# ECS SERVICE
resource "aws_ecs_service" "monolito" {
    name                               = "${var.prefix}-monolito-service"
    cluster                            = aws_ecs_cluster.main.id
    task_definition                    = aws_ecs_task_definition.monolito.arn
    desired_count                      = var.ecs_min_tasks
    launch_type                        = "FARGATE"
    deployment_minimum_healthy_percent = 100
    deployment_maximum_percent         = 200

    network_configuration {
        subnets          = var.private_app_subnet_ids
        security_groups  = [var.sg_ecs_task_id]
        assign_public_ip = false
    }

    load_balancer {
        target_group_arn = aws_lb_target_group.main.arn
        container_name   = "monolito"
        container_port   = var.ecs_app_port
    }

    deployment_circuit_breaker {
        enable   = true
        rollback = true
    }

    health_check_grace_period_seconds = 60

    propagate_tags = "SERVICE"

    lifecycle {
        ignore_changes = [task_definition, desired_count]
    }

    tags = {
        Name = "${var.prefix}-monolito-service"
    }

    depends_on = [
        aws_lb_listener.main,
        aws_cloudwatch_log_group.ecs
    ]
}

# AUTO SCALING
resource "aws_appautoscaling_target" "ecs" {
    max_capacity       = var.ecs_max_tasks
    min_capacity       = var.ecs_min_tasks
    resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.monolito.name}"
    scalable_dimension = "ecs:service:DesiredCount"
    service_namespace  = "ecs"
    }

    resource "aws_appautoscaling_policy" "cpu" {
    name               = "${var.prefix}-ecs-cpu-scaling"
    policy_type        = "TargetTrackingScaling"
    resource_id        = aws_appautoscaling_target.ecs.resource_id
    scalable_dimension = aws_appautoscaling_target.ecs.scalable_dimension
    service_namespace  = aws_appautoscaling_target.ecs.service_namespace

    target_tracking_scaling_policy_configuration {
        target_value       = 70.0
        scale_in_cooldown  = 300
        scale_out_cooldown = 60

        predefined_metric_specification {
        predefined_metric_type = "ECSServiceAverageCPUUtilization"
        }
    }
}

resource "aws_appautoscaling_policy" "memory" {
    name               = "${var.prefix}-ecs-memory-scaling"
    policy_type        = "TargetTrackingScaling"
    resource_id        = aws_appautoscaling_target.ecs.resource_id
    scalable_dimension = aws_appautoscaling_target.ecs.scalable_dimension
    service_namespace  = aws_appautoscaling_target.ecs.service_namespace

    target_tracking_scaling_policy_configuration {
        target_value       = 80.0
        scale_in_cooldown  = 300
        scale_out_cooldown = 60

        predefined_metric_specification {
        predefined_metric_type = "ECSServiceAverageMemoryUtilization"
        }
    }
}

