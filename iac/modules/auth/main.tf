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
}