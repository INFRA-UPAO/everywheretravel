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
