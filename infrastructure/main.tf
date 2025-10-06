########################
# VPC (public + private)
########################

locals {
  azs = slice(data.aws_availability_zones.available.names, 0, var.az_count)
}

data "aws_caller_identity" "current" {}
data "aws_availability_zones" "available" {}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.8"

  name = "${var.name_prefix}-vpc"
  cidr = var.vpc_cidr

  azs             = local.azs
  public_subnets  = [for i, az in local.azs : cidrsubnet(var.vpc_cidr, 4, i)]
  private_subnets = [for i, az in local.azs : cidrsubnet(var.vpc_cidr, 4, i + 8)]

  enable_nat_gateway   = true
  single_nat_gateway   = true # keep cost lower
  enable_dns_hostnames = true
  enable_dns_support   = true
}

########################
# Security groups
########################

# Allow EKS nodes <-> RDS and MSK
resource "aws_security_group" "eks_nodes" {
  name        = "${var.name_prefix}-eks-nodes-sg"
  description = "EKS nodes"
  vpc_id      = module.vpc.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.name_prefix}-eks-nodes-sg" }
}

# RDS SG
resource "aws_security_group" "rds" {
  name        = "${var.name_prefix}-rds-sg"
  description = "RDS Postgres"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "Postgres from EKS nodes"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_nodes.id]
  }

  # If publicly accessible, you might want to allow your IP (for quick tests)
  # TIP: replace with your IP/CIDR; or remove this to keep private.
  dynamic "ingress" {
    for_each = var.rds_publicly_accessible ? [1] : []
    content {
      description = "Postgres from my IP (temp)"
      from_port   = 5432
      to_port     = 5432
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"] # <-- tighten to your IP: ["x.x.x.x/32"]
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.name_prefix}-rds-sg" }
}

# MSK SG (allow from EKS nodes; IAM auth used)
resource "aws_security_group" "msk" {
  name        = "${var.name_prefix}-msk-sg"
  description = "MSK Serverless"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "Kafka from EKS nodes"
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    security_groups = [aws_security_group.eks_nodes.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.name_prefix}-msk-sg" }
}

########################
# EKS (managed node group)
########################

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.24"

  cluster_name    = "${var.name_prefix}-eks"
  cluster_version = var.eks_cluster_version

  cluster_endpoint_public_access = true

  vpc_id                                = module.vpc.vpc_id
  subnet_ids                            = module.vpc.private_subnets
  cluster_additional_security_group_ids = [aws_security_group.eks_nodes.id]

  manage_aws_auth_configmap = true

  eks_managed_node_groups = {
    default = {
      instance_types = [var.eks_node_instance_type]
      min_size       = var.eks_node_min_size
      max_size       = var.eks_node_max_size
      desired_size   = var.eks_node_desired_size

      subnet_ids = module.vpc.private_subnets
      ami_type   = "AL2_x86_64" # or AL2023_x86_64, per your needs
      disk_size  = 20

      # Spot can be cheaper; uncomment if desired:
      # capacity_type = "SPOT"
    }
  }

  tags = { Environment = "test" }
}

########################
# MSK Serverless
########################

resource "aws_msk_serverless_cluster" "this" {
  count        = var.enable_msk ? 1 : 0
  cluster_name = "${var.name_prefix}-${var.msk_cluster_name}"

  vpc_config {
    subnet_ids         = module.vpc.private_subnets
    security_group_ids = [aws_security_group.msk.id]
  }

  client_authentication {
    sasl {
      iam = true
    }
  }

  tags = { Environment = "test" }
}

# MSK bootstrap broker string (IAM auth)
data "aws_msk_bootstrap_brokers_v2" "this" {
  count       = var.enable_msk ? 1 : 0
  cluster_arn = aws_msk_serverless_cluster.this.arn
}

########################
# RDS Postgres
########################

resource "aws_db_subnet_group" "this" {
  name       = "${var.name_prefix}-db-subnet-group"
  subnet_ids = var.rds_publicly_accessible ? module.vpc.public_subnets : module.vpc.private_subnets
  tags       = { Name = "${var.name_prefix}-db-subnet-group" }
}


resource "aws_db_instance" "postgres" {
  count                      = var.enable_rds ? 1 : 0
  identifier                 = "${var.name_prefix}-pg"
  engine                     = "postgres"
  engine_version             = var.rds_engine_version
  db_name                    = var.db_name
  username                   = var.db_username
  password                   = var.db_password
  instance_class             = var.rds_instance_class
  allocated_storage          = var.rds_allocated_storage
  db_subnet_group_name       = aws_db_subnet_group.this.name
  vpc_security_group_ids     = [aws_security_group.rds.id]
  publicly_accessible        = var.rds_publicly_accessible
  skip_final_snapshot        = true
  deletion_protection        = false
  auto_minor_version_upgrade = true
  backup_retention_period    = 0

  # If using public subnets temporarily, ensure they have route to IGW (module handles)
  tags = { Name = "${var.name_prefix}-postgres" }
}

########################
# Kubernetes namespaces + Helm releases
########################

# Create namespaces for each service
resource "kubernetes_namespace" "svc_ns" {
  for_each = var.deploy_helm_services ? { for s in var.services : s.namespace => s } : {}
  metadata {
    name = each.value.namespace
  }
}

# Deploy your 3 releases
resource "helm_release" "services" {
  for_each  = var.deploy_helm_services ? { for s in var.services : s.name => s } : {}
  name      = each.value.name
  namespace = kubernetes_namespace.svc_ns[each.value.namespace].metadata[0].name
  chart     = var.helm_chart
  version   = var.helm_chart_version # null OK if chart is local path

  # If using a repo or OCI, add repository info here (example):
  # repository = "oci://ghcr.io/yourorg/charts"
  # or repository = "https://charts.bitnami.com/bitnami"

  # Optional per-service values file
  values = [
    try(file(each.value.values_file), "") != "" ? file(each.value.values_file) : "{}"
  ]

  # Wait for resources to be ready (helps before running tests)
  wait          = true
  recreate_pods = true

  depends_on = [module.eks]
}
