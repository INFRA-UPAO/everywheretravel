terraform {
  backend "s3" {
    bucket       = "everywhere-travel-tfstate"
    key          = "terraform.tfstate"
    region       = "us-east-2"
    use_lockfile = true
    encrypt      = true
  }
}
