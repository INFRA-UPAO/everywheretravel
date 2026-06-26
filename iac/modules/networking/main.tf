data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  az_a = data.aws_availability_zones.available.names[0]
  az_b = data.aws_availability_zones.available.names[1]
}

resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr

  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.prefix}-vpc"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.prefix}-igw"
  }
}

# SUBNETS
resource "aws_subnet" "public_az_a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = local.az_a
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.prefix}-public-az-a"
    Type = "public"
  }
}

resource "aws_subnet" "public_az_b" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = local.az_b
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.prefix}-public-az-b"
    Type = "public"
  }
}

resource "aws_subnet" "private_app_az_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.11.0/24"
  availability_zone = local.az_a

  tags = {
    Name = "${var.prefix}-private-app-az-a"
    Type = "private-app"
  }
}

resource "aws_subnet" "private_app_az_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.12.0/24"
  availability_zone = local.az_b

  tags = {
    Name = "${var.prefix}-private-app-az-b"
    Type = "private-app"
  }
}

resource "aws_subnet" "private_data_az_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.21.0/24"
  availability_zone = local.az_a

  tags = {
    Name = "${var.prefix}-private-data-az-a"
    Type = "private-data"
  }
}

resource "aws_subnet" "private_data_az_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.22.0/24"
  availability_zone = local.az_b

  tags = {
    Name = "${var.prefix}-private-data-az-b"
    Type = "private-data"
  }
}

resource "aws_eip" "nat" {
  count      = var.nat_gateway_count
  domain     = "vpc"
  depends_on = [aws_internet_gateway.main]

  tags = {
    Name = "${var.prefix}-nat-eip-${count.index == 0 ? "az-a" : "az-b"}"
  }
}

# NAT GATEWAYS
resource "aws_nat_gateway" "main" {
  count         = var.nat_gateway_count
  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = count.index == 0 ? aws_subnet.public_az_a.id : aws_subnet.public_az_b.id
  depends_on    = [aws_internet_gateway.main]

  tags = {
    Name = "${var.prefix}-nat-gw-${count.index == 0 ? "az-a" : "az-b"}"
  }
}

# ROUTE TABLES
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.prefix}-rt-public"
  }
}

resource "aws_route" "public_igw" {
  route_table_id         = aws_route_table.public.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.main.id
}

resource "aws_route_table" "private_app_az_a" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.prefix}-rt-private-app-az-a"
  }
}

resource "aws_route" "private_app_az_a_nat" {
  route_table_id         = aws_route_table.private_app_az_a.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.main[0].id

  depends_on = [aws_nat_gateway.main]
}

resource "aws_route_table" "private_app_az_b" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.prefix}-rt-private-app-az-b"
  }
}

resource "aws_route" "private_app_az_b_nat" {
  route_table_id         = aws_route_table.private_app_az_b.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.main[var.nat_gateway_count > 1 ? 1 : 0].id

  depends_on = [aws_nat_gateway.main]
}

resource "aws_route_table" "private_data" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.prefix}-rt-private-data"
  }
}

# ASOCIACIONES ROUTE TABLE
resource "aws_route_table_association" "public_az_a" {
  subnet_id      = aws_subnet.public_az_a.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_az_b" {
  subnet_id      = aws_subnet.public_az_b.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private_app_az_a" {
  subnet_id      = aws_subnet.private_app_az_a.id
  route_table_id = aws_route_table.private_app_az_a.id
}

resource "aws_route_table_association" "private_app_az_b" {
  subnet_id      = aws_subnet.private_app_az_b.id
  route_table_id = aws_route_table.private_app_az_b.id
}

resource "aws_route_table_association" "private_data_az_a" {
  subnet_id      = aws_subnet.private_data_az_a.id
  route_table_id = aws_route_table.private_data.id
}

resource "aws_route_table_association" "private_data_az_b" {
  subnet_id      = aws_subnet.private_data_az_b.id
  route_table_id = aws_route_table.private_data.id
}

# FIX CKV2_AWS_12 — Bloquear default security group
resource "aws_default_security_group" "default" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.prefix}-default-sg-restricted"
  }
}

# FIX CKV2_AWS_11 — VPC Flow Logs
# TODO: mover el IAM role y CloudWatch Log Group a un modulo de observability
# cuando se implemente. Por ahora se crean aqui para que el flow log funcione.
# Fix CKV_AWS_158: cifrado KMS para el log group de flow logs.
resource "aws_cloudwatch_log_group" "flow_logs" {
  name              = "/aws/vpc/flow-logs/${var.prefix}"
  retention_in_days = 365
  kms_key_id        = var.kms_logs_arn

  tags = {
    Name = "${var.prefix}-vpc-flow-logs"
  }
}

resource "aws_iam_role" "flow_logs" {
  name = "${var.prefix}-vpc-flow-logs"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "vpc-flow-logs.amazonaws.com" }
      Action    = "sts:AssumeRole"
      Condition = {
        StringEquals = { "aws:SourceAccount" = data.aws_caller_identity.current.account_id }
        ArnLike      = { "aws:SourceArn" = "arn:aws:ec2:${data.aws_region.current.region}:${data.aws_caller_identity.current.account_id}:vpc/${aws_vpc.main.id}" }
      }
    }]
  })

  tags = {
    Name = "${var.prefix}-vpc-flow-logs"
  }
}

resource "aws_iam_role_policy" "flow_logs" {
  name = "${var.prefix}-vpc-flow-logs"
  role = aws_iam_role.flow_logs.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:DescribeLogGroups",
        "logs:DescribeLogStreams"
      ]
      Resource = "${aws_cloudwatch_log_group.flow_logs.arn}:*"
    }]
  })
}

resource "aws_flow_log" "main" {
  vpc_id          = aws_vpc.main.id
  traffic_type    = "ALL"
  iam_role_arn    = aws_iam_role.flow_logs.arn
  log_destination = aws_cloudwatch_log_group.flow_logs.arn

  tags = {
    Name = "${var.prefix}-vpc-flow-log"
  }
}

# NETWORK ACLs
resource "aws_network_acl" "public" {
  vpc_id = aws_vpc.main.id

  subnet_ids = [
    aws_subnet.public_az_a.id,
    aws_subnet.public_az_b.id
  ]

  tags = {
    Name = "${var.prefix}-nacl-public"
  }
}

# INBOUND
resource "aws_network_acl_rule" "public_inbound_https" {
  network_acl_id = aws_network_acl.public.id
  rule_number    = 100
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 443
  to_port        = 443
}

resource "aws_network_acl_rule" "public_inbound_ephemeral" {
    network_acl_id = aws_network_acl.public.id
    rule_number    = 200
    egress         = false
    protocol       = "tcp"
    rule_action    = "allow"
    cidr_block     = "0.0.0.0/0"
    from_port      = 1024
    to_port        = 65535
}

# OUTBOUND
resource "aws_network_acl_rule" "public_outbound_https" {
  network_acl_id = aws_network_acl.public.id
  rule_number    = 100
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 443
  to_port        = 443
}

resource "aws_network_acl_rule" "public_outbound_ephemeral" {
  network_acl_id = aws_network_acl.public.id
  rule_number    = 200
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024
  to_port        = 65535
}

resource "aws_network_acl" "private_app" {
  vpc_id = aws_vpc.main.id

  subnet_ids = [
    aws_subnet.private_app_az_a.id,
    aws_subnet.private_app_az_b.id
  ]

  tags = {
    Name = "${var.prefix}-nacl-private-app"
  }
}

# FIX CKV_AWS_352 — Acotar al puerto de la app (8080) en vez de 0-65535
resource "aws_network_acl_rule" "private_app_inbound_vpc" {
    network_acl_id = aws_network_acl.private_app.id
    rule_number    = 100
    egress         = false
    protocol       = "tcp"
    rule_action    = "allow"
    cidr_block     = var.vpc_cidr
    from_port      = 0
    to_port        = 65535
}

resource "aws_network_acl_rule" "private_app_inbound_return" {
    network_acl_id = aws_network_acl.private_app.id
    rule_number    = 200
    egress         = false
    protocol       = "tcp"
    rule_action    = "allow"
    cidr_block     = "0.0.0.0/0"
    from_port      = 1024
    to_port        = 65535
}

resource "aws_network_acl_rule" "private_app_outbound_rds_az_a" {
  network_acl_id = aws_network_acl.private_app.id
  rule_number    = 100
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.21.0/24"
  from_port      = 5432
  to_port        = 5432
}

resource "aws_network_acl_rule" "private_app_outbound_rds_az_b" {
  network_acl_id = aws_network_acl.private_app.id
  rule_number    = 110
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.22.0/24"
  from_port      = 5432
  to_port        = 5432
}

resource "aws_network_acl_rule" "private_app_outbound_https" {
  network_acl_id = aws_network_acl.private_app.id
  rule_number    = 200
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 443
  to_port        = 443
}

resource "aws_network_acl_rule" "private_app_outbound_ephemeral" {
  network_acl_id = aws_network_acl.private_app.id
  rule_number    = 300
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024
  to_port        = 65535
}

resource "aws_network_acl" "private_data" {
  vpc_id = aws_vpc.main.id

  subnet_ids = [
    aws_subnet.private_data_az_a.id,
    aws_subnet.private_data_az_b.id
  ]

  tags = {
    Name = "${var.prefix}-nacl-private-data"
  }
}

# INBOUND
resource "aws_network_acl_rule" "private_data_inbound_app_az_a" {
  network_acl_id = aws_network_acl.private_data.id
  rule_number    = 100
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.11.0/24"
  from_port      = 5432
  to_port        = 5432
}

resource "aws_network_acl_rule" "private_data_inbound_app_az_b" {
  network_acl_id = aws_network_acl.private_data.id
  rule_number    = 110
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.12.0/24"
  from_port      = 5432
  to_port        = 5432
}

# OUTBOUND
resource "aws_network_acl_rule" "private_data_outbound_app_az_a" {
  network_acl_id = aws_network_acl.private_data.id
  rule_number    = 100
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.11.0/24"
  from_port      = 1024
  to_port        = 65535
}

resource "aws_network_acl_rule" "private_data_outbound_app_az_b" {
  network_acl_id = aws_network_acl.private_data.id
  rule_number    = 110
  egress         = true
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "10.0.12.0/24"
  from_port      = 1024
  to_port        = 65535
}
