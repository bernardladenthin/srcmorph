### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:09:10Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Runs the shop orders back office and makes it available internally and through an HTTP endpoint.

#### Purpose
- Deploys the orders back-office application in Kubernetes with three replicas.
- Routes traffic from `backoffice.shop.example` to the application.

#### Responsibilities
- **Workload:** Runs `orders-backoffice` and checks readiness at `/health/ready`.
- **Service and ingress:** Exposes container port `8080` internally on service port `80` and routes `/` traffic to that service.
- **Runtime configuration:** Sets the shop database URL and supplies the database password from a Kubernetes Secret.

#### Key units
- `Deployment/orders-backoffice` — runs image `registry.shop.example/orders-backoffice:2.1.0` with three replicas.
- `Service/orders-backoffice` — selects the back-office pods and targets port `8080`.
- `Ingress/orders-backoffice` — routes requests for `backoffice.shop.example` to the service on port `80`.

#### Data flow
- HTTP requests for `backoffice.shop.example/` route through the Ingress to the Service, then to an `orders-backoffice` pod.
- The application receives `SHOP_DB_URL` for `orders-db:5432/shop` and `SHOP_DB_PASSWORD` from Secret `orders-db-credentials`, key `password`.

#### Dependencies
- Image: `registry.shop.example/orders-backoffice:2.1.0`.
- Kubernetes Secret: `orders-db-credentials`.
- Database endpoint: `orders-db:5432`, database `shop`.

#### Cross-cutting
- Pod selection uses the shared label `app: orders-backoffice`.
- Readiness is determined by HTTP GET `/health/ready` on port `8080`.
- Resource limits are memory `768Mi` and CPU `"1"`.
