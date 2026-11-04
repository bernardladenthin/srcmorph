### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:40:03Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Runs the Orders back-office application on Kubernetes, connects it to the shop database, and exposes it over HTTP.

#### Purpose
- Deploys the Orders back-office application with three replicas.
- Provides internal service access and external routing at `backoffice.shop.example`.

#### Responsibilities
- **Workload:** Runs `orders-backoffice` with database connection settings, a readiness probe, and resource limits.
- **Networking:** Routes cluster traffic through a Service and external HTTP traffic through an Ingress.

#### Key units
- `Deployment/orders-backoffice` — runs the `backoffice` container from `registry.shop.example/orders-backoffice:2.1.0` on port `8080`.
- `Service/orders-backoffice` — maps port `80` to container port `8080`.
- `Ingress/orders-backoffice` — routes prefix path `/` on `backoffice.shop.example` to the Service.

#### Data flow
- The Deployment obtains its database URL from `SHOP_DB_URL` and its password from Secret `orders-db-credentials`, key `password`.
- Incoming HTTP requests for `backoffice.shop.example/` route through the Ingress to Service port `80`, then to container port `8080`.

#### Dependencies
- PostgreSQL endpoint `orders-db:5432`, database `shop`.
- Kubernetes Secret `orders-db-credentials`, key `password`.
- Container image `registry.shop.example/orders-backoffice:2.1.0`.

#### Cross-cutting
- Configures three replicas, resource limits of `768Mi` memory and `1` CPU, and readiness checks via `/health/ready` on port `8080`.
