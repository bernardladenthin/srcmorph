### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:31:22Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Runs the orders backoffice application and makes it available internally and at `backoffice.shop.example`.

#### Purpose
- Deploys the orders backoffice application on Kubernetes with database configuration and HTTP access.

#### Responsibilities
- Define the application Deployment, including replicas, container image, port, readiness probe, and resource limits.
- Provide pod access through a Service and route external HTTP traffic through an Ingress.

#### Key units
- `Deployment/orders-backoffice` — runs three `backoffice` replicas on port `8080`.
- `Service/orders-backoffice` — exposes selected pods on port `80`, targeting `8080`.
- `Ingress/orders-backoffice` — routes `/` requests for `backoffice.shop.example` to the Service.

#### Data flow
- Ingress traffic for `backoffice.shop.example/` routes through Service `orders-backoffice` to application pods on port `8080`.
- The application receives its database URL and a password from Secret `orders-db-credentials`.

#### Dependencies
- Container image: `registry.shop.example/orders-backoffice:2.1.0`.
- Database endpoint: `orders-db:5432`.
- Secret: `orders-db-credentials`, key `password`.

#### Cross-cutting
- Readiness is checked with HTTP `GET /health/ready` on port `8080`.
- Container resource limits are `768Mi` memory and `1` CPU.
