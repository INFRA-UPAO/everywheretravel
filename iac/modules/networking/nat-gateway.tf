resource "aws_eip" "nat" {
  count      = var.nat_gateway_count
  domain     = "vpc"
  depends_on = [aws_internet_gateway.main]

  tags = {
    Name = "${var.prefix}-nat-eip-${count.index == 0 ? "az-a" : "az-b"}"
  }
}

resource "aws_nat_gateway" "main" {
  count         = var.nat_gateway_count
  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = count.index == 0 ? aws_subnet.public_az_a.id : aws_subnet.public_az_b.id
  depends_on    = [aws_internet_gateway.main]

  tags = {
    Name = "${var.prefix}-nat-gw-${count.index == 0 ? "az-a" : "az-b"}"
  }
}
