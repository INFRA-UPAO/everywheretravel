# GENERAR RANDOM PASSWORD
resource "random_password" "db" {
  length           = 16
  special          = true
  override_special = "!#$%^&*-_=+?@"
  min_upper        = 1
  min_lower        = 1
  min_numeric      = 1
  min_special      = 1
}

resource "aws_db_subnet_group" "main" {
  name        = "${var.prefix}-rds-subnet-group"
  description = "Subnet group para RDS PostgreSQL - ${var.prefix}"
  subnet_ids  = var.private_data_subnet_ids

  tags = {
    Name = "${var.prefix}-rds-subnet-group"
  }
}

resource "aws_db_parameter_group" "main" {
  name        = "${var.prefix}-rds-params"
  family      = "postgres15"
  description = "Parameter group personalizado para PostgreSQL 15 - ${var.prefix}"

  parameter {
    name         = "max_connections"
    value        = "100"
    apply_method = "pending-reboot"
  }

  parameter {
    name         = "log_min_duration_statement"
    value        = "1000"
    apply_method = "immediate"
  }

  parameter {
    name         = "shared_preload_libraries"
    value        = "pg_stat_statements"
    apply_method = "pending-reboot"
  }

  parameter {
    name         = "rds.force_ssl"
    value        = "1"
    apply_method = "pending-reboot"
  }

  tags = {
    Name = "${var.prefix}-rds-params"
  }
}

resource "aws_db_instance" "main" {
  identifier                            = "${var.prefix}-rds"
  engine                                = "postgres"
  engine_version                        = "15"
  instance_class                        = var.db_instance_class
  allocated_storage                     = 20
  max_allocated_storage                 = 100
  storage_type                          = "gp3"
  storage_encrypted                     = true
  kms_key_id                            = var.kms_rds_arn
  db_name                               = var.db_name
  username                              = var.db_username
  password                              = random_password.db.result
  db_subnet_group_name                  = aws_db_subnet_group.main.name
  vpc_security_group_ids                = [var.sg_rds_id]
  publicly_accessible                   = false
  parameter_group_name                  = aws_db_parameter_group.main.name
  multi_az                              = var.db_multi_az
  backup_retention_period               = 7
  backup_window                         = "03:00-04:00"
  maintenance_window                    = "Sun:04:00-Sun:05:00"
  auto_minor_version_upgrade            = true
  performance_insights_enabled          = true
  performance_insights_kms_key_id       = var.kms_rds_arn
  performance_insights_retention_period = 7
  enabled_cloudwatch_logs_exports       = ["postgresql"]
  iam_database_authentication_enabled    = true
  deletion_protection                   = true
  skip_final_snapshot                   = !var.db_multi_az
  final_snapshot_identifier             = var.db_multi_az ? "${var.prefix}-rds-final-snapshot" : null
  copy_tags_to_snapshot                 = true

  tags = {
    Name = "${var.prefix}-rds"
  }
}