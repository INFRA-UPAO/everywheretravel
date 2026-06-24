output "vpc_id" {
    description = "ID del VPC principal"
    value       = aws_vpc.main.id
}

output "vpc_cidr" {
    description = "CIDR block del VPC"
    value       = aws_vpc.main.cidr_block
}

# Subnets públicas
output "public_subnet_ids" {
    description = "IDs de las subnets públicas [az-a, az-b]"
    value       = [aws_subnet.public_az_a.id, aws_subnet.public_az_b.id]
}

output "public_subnet_az_a_id" {
    description = "ID de la subnet pública AZ-a"
    value       = aws_subnet.public_az_a.id
}

output "public_subnet_az_b_id" {
    description = "ID de la subnet pública AZ-b"
    value       = aws_subnet.public_az_b.id
}

# Subnets privadas APP
output "private_app_subnet_ids" {
    description = "IDs de las subnets privadas APP [az-a, az-b]"
    value       = [aws_subnet.private_app_az_a.id, aws_subnet.private_app_az_b.id]
}

output "private_app_subnet_az_a_id" {
    description = "ID de la subnet privada APP AZ-a"
    value       = aws_subnet.private_app_az_a.id
}

output "private_app_subnet_az_b_id" {
    description = "ID de la subnet privada APP AZ-b"
    value       = aws_subnet.private_app_az_b.id
}

# Subnets privadas DATA
output "private_data_subnet_ids" {
    description = "IDs de las subnets privadas DATA [az-a, az-b]"
    value       = [aws_subnet.private_data_az_a.id, aws_subnet.private_data_az_b.id]
}

output "rt_private_app_az_a_id" {
    description = "ID de la route table privada APP AZ-a"
    value       = aws_route_table.private_app_az_a.id
}

output "rt_private_app_az_b_id" {
    description = "ID de la route table privada APP AZ-b"
    value       = aws_route_table.private_app_az_b.id
}

output "nat_gateway_ids" {
    description = "IDs de los NAT Gateways creados"
    value       = aws_nat_gateway.main[*].id
}

output "nat_gateway_az_a_id" {
    description = "ID del NAT Gateway AZ-a (siempre existe)"
    value       = aws_nat_gateway.main[0].id
}

output "nat_gateway_az_b_id" {
    description = "ID del NAT Gateway AZ-b (solo en prod, null en dev)"
    value       = var.nat_gateway_count > 1 ? aws_nat_gateway.main[1].id : null
}

