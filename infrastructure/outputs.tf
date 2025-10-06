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

output "msk_cluster_arn" {
  value       = var.enable_msk ? aws_msk_serverless_cluster.this[0].arn : null
  description = "ARN of the MSK Serverless cluster"
}

output "rds_endpoint" {
  value       = var.enable_rds ? aws_db_instance.postgres[0].address : null
  description = "RDS endpoint hostname"
}

output "rds_connection_string" {
  value       = var.enable_rds ? "postgresql://${var.db_username}:${var.db_password}@${aws_db_instance.postgres[0].address}:5432/${var.db_name}" : null
  description = "RDS connection URI"
  sensitive   = true
}
