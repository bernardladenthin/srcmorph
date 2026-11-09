### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:21:56Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Runs the Orders back-office application and makes it available internally and through HTTP at `backoffice.shop.example`.

#### Purpose
- Deploys the Orders back-office web application on Kubernetes.
- Provides Service-based access and external Ingress routing.

#### Responsibilities
- Defines the `orders-backoffice` Deployment with three replicas, database environment settings, a readiness probe, and resource limits.
- Exposes the pods through a Service on port `80` and an Ingress routing `/` to that Service.

#### Key units
- `orders-backoffice` Deployment — runs image `registry.shop.example/orders-backoffice:2.1.0` on container port `8080`.
- `orders-backoffice` Service — maps port `80` to container port `8080`.
- `orders-backoffice` Ingress — routes requests for `backoffice.shop.example` with `/` prefix to the Service.

#### Data flow
- Ingress sends HTTP traffic for `backoffice.shop.example/` to the `orders-backoffice` Service, which forwards it to matching pods on port `8080`.
- The application receives `SHOP_DB_URL` for `orders-db:5432` and `SHOP_DB_PASSWORD` from Secret `orders-db-credentials`, key `password`.

#### Dependencies
- Container image: `registry.shop.example/orders-backoffice:2.1.0`.
- Kubernetes Secret: `orders-db-credentials`; database endpoint: `orders-db:5432`.

#### Cross-cutting
- Deployment uses three replicas and label/selector `app: orders-backoffice`.
- Readiness uses HTTP GET `/health/ready` on port `8080`; resource limits are `768Mi` memory and `1` CPU.
