### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:12:04Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Deploys and exposes the `orders-backoffice` microservice in a Kubernetes cluster.

#### Purpose
- Deploy the `orders-backoffice` application.
- Expose it via a Service and Ingress on HTTP port 80.

#### Responsibilities
- **Deployment**: manage pod replicas, container image, environment, probes, and resource limits.
- **Service**: route internal traffic to the pods.
- **Ingress**: provide external HTTP access with host and path routing.

#### Key units
- **Deployment `orders-backoffice`** – 3 replicas, container `backoffice` (image `registry.shop.example/orders-backoffice:2.1.0`, port 8080, env `SHOP_DB_URL`, secret‑backed `SHOP_DB_PASSWORD`, readiness probe `/health/ready`, limits `memory:768Mi`, `cpu:1`).
- **Service `orders-backoffice`** – selector `app: orders-backoffice`, port 80 → targetPort 8080.
- **Ingress `orders-backoffice`** – host `backoffice.shop.example`, path `/` → service `orders-backoffice` port 80.

#### Data flow
1. **Deployment** creates pods with the backoffice container, injecting DB URL and password from the `orders-db-credentials` secret.
2. **Service** forwards internal traffic on port 80 to pod port 8080.
3. **Ingress** receives external HTTP requests on `backoffice.shop.example` and routes them to the Service.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Secret `orders-db-credentials` (key `password`).
- PostgreSQL database at `orders-db:5432/shop`.

#### Cross-cutting
- Environment variables for DB connectivity.
- Secret usage for sensitive data.
- Readiness probe ensures pod availability before traffic routing.
