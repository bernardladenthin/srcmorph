### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:15:45Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Runs the Shop orders backoffice and makes it reachable over HTTP while connecting it to the orders database.

#### Purpose
- Deploys and exposes the orders backoffice application in Kubernetes.
- Configures its database connection and readiness check.

#### Responsibilities
- Application workload: runs three replicas of the backoffice container with resource limits and an HTTP readiness probe.
- Network access: provides an internal Service and routes ingress traffic for `backoffice.shop.example`.
- Runtime configuration: supplies the database URL and obtains the database password from a Kubernetes Secret.

#### Key units
- Deployment `orders-backoffice` — runs `registry.shop.example/orders-backoffice:2.1.0` on port `8080`.
- Service `orders-backoffice` — maps port `80` to the application’s port `8080`.
- Ingress `orders-backoffice` — routes `/` requests for `backoffice.shop.example` to the Service.

#### Data flow
- Ingress traffic reaches the Service on port `80`, which forwards it to application pods on port `8080`.
- The application connects to `orders-db:5432` using `SHOP_DB_URL` and the `password` key from Secret `orders-db-credentials`.
- Kubernetes checks readiness through `/health/ready` on port `8080`.

#### Dependencies
- Container image `registry.shop.example/orders-backoffice:2.1.0`.
- Secret `orders-db-credentials`, key `password`.
- Database endpoint `orders-db:5432`, database `shop`.

#### Cross-cutting
- Deployment uses three replicas selected by the label `app: orders-backoffice`.
- Readiness probe uses HTTP; resource limits are `768Mi` memory and `1` CPU.
