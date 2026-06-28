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
