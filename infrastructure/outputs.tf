output "vpc_id" {
  value       = module.vpc.vpc_id
  description = "VPC ID"
}

output "private_subnets" {
  value       = module.vpc.private_subnets
  description = "Private subnet IDs"
}

output "public_subnets" {
  value       = module.vpc.public_subnets
  description = "Public subnet IDs"
}

output "eks_cluster_name" {
  value       = module.eks.cluster_name
  description = "EKS cluster name"
}

output "eks_cluster_endpoint" {
  value       = module.eks.cluster_endpoint
  description = "EKS API endpoint"
}

output "msk_bootstrap_brokers_sasl_iam" {
  value       = var.enable_msk ? data.aws_msk_bootstrap_brokers_v2.this[0].bootstrap_broker_string_sasl_iam : null
  description = "MSK bootstrap brokers (SASL/IAM)"
}

output "rds_endpoint" {
  value       = var.enable_rds ? aws_db_instance.postgres[0].address : null
  description = "RDS endpoint hostname"
}

output "rds_connection_string" {
  value       = "postgresql://${var.db_username}:${var.db_password}@${aws_db_instance.postgres.address}:5432/${var.db_name}"
  description = "RDS connection URI"
  sensitive   = true
}
