### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:24:52Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Runs the Orders backoffice application and makes it reachable over HTTP for shop operations.

#### Purpose
- Deploys the Orders backoffice application in Kubernetes with three replicas.
- Exposes it internally and routes `backoffice.shop.example` traffic to the application.

#### Responsibilities
- **Application workload:** Runs `orders-backoffice` and checks readiness at `/health/ready`.
- **Network access:** Connects the workload to a Kubernetes Service and exposes it through an Ingress.

#### Key units
- `Deployment/orders-backoffice` — runs `registry.shop.example/orders-backoffice:2.1.0` on port `8080`, with three replicas.
- `Service/orders-backoffice` — exposes the workload on port `80`, targeting port `8080`.
- `Ingress/orders-backoffice` — routes `/` traffic for `backoffice.shop.example` to the Service.

#### Data flow
- Requests for `backoffice.shop.example/` pass through the Ingress to `Service/orders-backoffice`, then to the application on port `8080`.
- The application receives `SHOP_DB_URL` for `orders-db:5432` and `SHOP_DB_PASSWORD` from Secret `orders-db-credentials`, key `password`.

#### Dependencies
- Container image: `registry.shop.example/orders-backoffice:2.1.0`.
- Kubernetes Secret: `orders-db-credentials`.
- Database endpoint: `orders-db:5432`.

#### Cross-cutting
- Deployment and pod selection use label `app=orders-backoffice`.
- Container resource limits are `768Mi` memory and `1` CPU; readiness uses HTTP `GET /health/ready` on port `8080`.
