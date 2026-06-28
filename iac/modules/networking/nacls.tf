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

resource "aws_network_acl_rule" "public_inbound_ephemeral_1" {
  network_acl_id = aws_network_acl.public.id
  rule_number    = 200
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024
  to_port        = 3388
}

resource "aws_network_acl_rule" "public_inbound_ephemeral_2" {
  network_acl_id = aws_network_acl.public.id
  rule_number    = 210
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 3390 
  to_port        = 65535
}

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

resource "aws_network_acl_rule" "private_app_inbound_return_1" {
  network_acl_id = aws_network_acl.private_app.id
  rule_number    = 200
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 1024
  to_port        = 3388
}

resource "aws_network_acl_rule" "private_app_inbound_return_2" {
  network_acl_id = aws_network_acl.private_app.id
  rule_number    = 210
  egress         = false
  protocol       = "tcp"
  rule_action    = "allow"
  cidr_block     = "0.0.0.0/0"
  from_port      = 3390
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
