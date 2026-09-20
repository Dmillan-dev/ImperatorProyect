resource "aws_security_group" "alb" {
  name        = "${local.name_prefix}-alb"
  description = "Public HTTPS entry point"
  vpc_id      = aws_vpc.main.id

  tags = { Name = "${local.name_prefix}-alb" }
}

resource "aws_security_group" "frontend" {
  name        = "${local.name_prefix}-frontend"
  description = "Frontend Fargate tasks"
  vpc_id      = aws_vpc.main.id

  tags = { Name = "${local.name_prefix}-frontend" }
}

resource "aws_security_group" "backend" {
  name        = "${local.name_prefix}-backend"
  description = "Backend and one-shot database jobs"
  vpc_id      = aws_vpc.main.id

  tags = { Name = "${local.name_prefix}-backend" }
}

resource "aws_security_group" "database" {
  name        = "${local.name_prefix}-database"
  description = "Private RDS PostgreSQL"
  vpc_id      = aws_vpc.main.id

  tags = { Name = "${local.name_prefix}-database" }
}

resource "aws_vpc_security_group_ingress_rule" "alb_http" {
  security_group_id = aws_security_group.alb.id
  description       = "HTTP redirect only"
  ip_protocol       = "tcp"
  from_port         = 80
  to_port           = 80
  cidr_ipv4         = "0.0.0.0/0"
}

resource "aws_vpc_security_group_ingress_rule" "alb_https" {
  security_group_id = aws_security_group.alb.id
  description       = "HTTPS"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_ipv4         = "0.0.0.0/0"
}

resource "aws_vpc_security_group_egress_rule" "alb_frontend" {
  security_group_id            = aws_security_group.alb.id
  description                  = "Frontend targets"
  ip_protocol                  = "tcp"
  from_port                    = 3000
  to_port                      = 3000
  referenced_security_group_id = aws_security_group.frontend.id
}

resource "aws_vpc_security_group_egress_rule" "alb_backend" {
  security_group_id            = aws_security_group.alb.id
  description                  = "Backend API targets"
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  referenced_security_group_id = aws_security_group.backend.id
}

resource "aws_vpc_security_group_ingress_rule" "frontend_alb" {
  security_group_id            = aws_security_group.frontend.id
  description                  = "ALB to Next.js"
  ip_protocol                  = "tcp"
  from_port                    = 3000
  to_port                      = 3000
  referenced_security_group_id = aws_security_group.alb.id
}

resource "aws_vpc_security_group_egress_rule" "frontend_https" {
  for_each = toset(var.allowed_https_egress_cidrs)

  security_group_id = aws_security_group.frontend.id
  description       = "HTTPS egress through controlled NAT"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_ipv4         = each.value
}

resource "aws_vpc_security_group_ingress_rule" "backend_alb" {
  security_group_id            = aws_security_group.backend.id
  description                  = "ALB to API"
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  referenced_security_group_id = aws_security_group.alb.id
}

resource "aws_vpc_security_group_egress_rule" "backend_https" {
  for_each = toset(var.allowed_https_egress_cidrs)

  security_group_id = aws_security_group.backend.id
  description       = "HTTPS to Keycloak, Bedrock and AWS APIs"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_ipv4         = each.value
}

resource "aws_vpc_security_group_egress_rule" "backend_database" {
  security_group_id            = aws_security_group.backend.id
  description                  = "PostgreSQL"
  ip_protocol                  = "tcp"
  from_port                    = 5432
  to_port                      = 5432
  referenced_security_group_id = aws_security_group.database.id
}

resource "aws_vpc_security_group_ingress_rule" "database_backend" {
  security_group_id            = aws_security_group.database.id
  description                  = "Backend and database jobs"
  ip_protocol                  = "tcp"
  from_port                    = 5432
  to_port                      = 5432
  referenced_security_group_id = aws_security_group.backend.id
}
