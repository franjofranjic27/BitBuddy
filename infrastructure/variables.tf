variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-central-1"
}

variable "aws_profile" {
  description = "AWS CLI profile"
  type        = string
  default     = "default"
}

variable "name_prefix" {
  description = "Name prefix for all resources"
  type        = string
  default     = "bitbuddy"
}

# VPC
variable "vpc_cidr" {
  description = "CIDR for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

# Is 1 just because of free tier; for production use at least 2
variable "az_count" {
  description = "Number of AZs to spread across"
  type        = number
  default     = 1
}

# EKS
variable "eks_cluster_version" {
  description = "EKS version"
  type        = string
  default     = "1.30"
}

variable "eks_node_instance_type" {
  description = "Instance type for the managed node group"
  type        = string
  default     = "t3.small"
}

# For free tier, use t3.small with 1 node; for production, at least t3.medium with 2+ nodes
variable "eks_node_desired_size" {
  description = "Desired nodes"
  type        = number
  default     = 1
}

variable "eks_node_min_size" {
  description = "Min nodes"
  type        = number
  default     = 1
}

variable "eks_node_max_size" {
  description = "Max nodes"
  type        = number
  default     = 1
}

# RDS
variable "rds_engine_version" {
  description = "Postgres engine version"
  type        = string
  default     = "16.3"
}

variable "rds_instance_class" {
  description = "DB instance class"
  type        = string
  default     = "db.t4g.micro"
}

variable "rds_allocated_storage" {
  description = "Storage (GB)"
  type        = number
  default     = 5
}
variable "enable_rds" {
  type = bool,
  default = false
}

variable "enable_msk" {
  type = bool,
  default = false
}

variable "deploy_helm_services"  {
  type = bool,
  default = false
}


variable "rds_publicly_accessible" {
  description = "Publicly accessible DB (true for your current testing)"
  type        = bool
  default     = true
}

variable "db_name" {
  description = "DB name"
  type        = string
  default     = "appdb"
}

variable "db_username" {
  description = "Master username"
  type        = string
  default     = "appuser"
}

variable "db_password" {
  description = "Master password"
  type        = string
  sensitive   = true
}

# MSK Serverless
variable "msk_cluster_name" {
  description = "MSK Serverless cluster name"
  type        = string
  default     = "msk-sls"
}

# Helm (your services)
# Example assumes the *same* chart used 3 times with different release names/namespaces/values
variable "helm_chart" {
  description = "Path or OCI ref to your Helm chart"
  type        = string
  default     = "./charts/my-microservice" # change to your chart folder or oci://...
}

variable "helm_chart_version" {
  description = "Chart version (if using a repo/OCI)"
  type        = string
  default     = null
}

variable "services" {
  description = <<EOT
List of services to deploy. Each object:
  name        - Helm release name
  namespace   - Kubernetes namespace
  values_file - Path to a values.yaml per service (optional)
EOT
  type = list(object({
    name        = string
    namespace   = string
    values_file = optional(string)
  }))

  default = [
    { name = "svc-a", namespace = "apps-a" },
    { name = "svc-b", namespace = "apps-b" },
    { name = "svc-c", namespace = "apps-c" },
  ]
}
