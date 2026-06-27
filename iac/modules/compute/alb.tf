resource "aws_lb" "main" {
  # checkov:skip=CKV2_AWS_20:ALB interno - solo recibe tráfico de API Gateway via VPC Link
  name               = "${var.prefix}-alb"
  internal           = true
  load_balancer_type = "application"
  security_groups    = [var.sg_alb_id]
  subnets            = var.private_app_subnet_ids

  idle_timeout                     = 60
  enable_cross_zone_load_balancing = true
  drop_invalid_header_fields       = true
  enable_deletion_protection       = var.is_prod ? true : false

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
  # checkov:skip=CKV_AWS_378:Target group HTTP interno - comunicación dentro de VPC
  name        = "${var.prefix}-ecs-tg"
  port        = var.ecs_app_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
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

resource "aws_lb_listener" "main" {
  # checkov:skip=CKV_AWS_2:Listener HTTP interno - tráfico viene de API Gateway via VPC Link
  # checkov:skip=CKV_AWS_103:Listener HTTP interno - TLS se maneja en CloudFront y API Gateway
  load_balancer_arn = aws_lb.main.arn
  port              = var.ecs_app_port
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.main.arn
  }

  tags = {
    Name = "${var.prefix}-alb-listener"
  }
}
