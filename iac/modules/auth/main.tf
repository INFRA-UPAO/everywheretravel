# USER POOL
resource "aws_cognito_user_pool" "main" {
  name = "${var.prefix}-user-pool"

  password_policy {
    minimum_length                   = 8
    require_uppercase                = true
    require_lowercase                = true
    require_numbers                  = true
    require_symbols                  = true
    temporary_password_validity_days = 7
  }

  mfa_configuration = "OPTIONAL"

  software_token_mfa_configuration {
    enabled = true
  }

  auto_verified_attributes = ["email"]
  username_attributes      = ["email"]

  username_configuration {
    case_sensitive = false
  }

  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  admin_create_user_config {
    allow_admin_create_user_only = true

    invite_message_template {
      email_subject = "Bienvenido a Everywhere Travel"
      email_message = "Tu usuario es {username} y tu contraseña temporal es {####}"
      sms_message   = "Tu usuario es {username} y tu contraseña es {####}"
    }
  }

  schema {
    name                = "email"
    attribute_data_type = "String"
    required            = true
    mutable             = true

    string_attribute_constraints {
      min_length = 5
      max_length = 100
    }
  }

  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }

  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Código de verificación - Everywhere Travel"
    email_message        = "Tu código de verificación es {####}"
  }

  tags = {
    Name = "${var.prefix}-user-pool"
  }
}

# APP CLIENT
resource "aws_cognito_user_pool_client" "main" {
  name                   = "${var.prefix}-spa-client"
  user_pool_id           = aws_cognito_user_pool.main.id
  generate_secret        = false
  access_token_validity  = 60
  id_token_validity      = 60
  refresh_token_validity = 30

  token_validity_units {
    access_token  = "minutes"
    id_token      = "minutes"
    refresh_token = "days"
  }
}