### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:24:48Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Deploys and exposes the orders‑backoffice microservice in a Kubernetes cluster.

#### Purpose
- Run the orders‑backoffice application.
- Expose it via HTTP on port 80.

#### Responsibilities
- **Deployment**: manage pod lifecycle, replica count, container image, environment, probes, and resource limits.
- **Service**: provide stable cluster‑internal endpoint for the pods.
- **Ingress**: route external traffic to the service.

#### Key units
- **Deployment `orders-backoffice`** – 3 replicas, container `backoffice` (image `registry.shop.example/orders-backoffice:2.1.0`, port 8080), env `SHOP_DB_URL` and `SHOP_DB_PASSWORD` from secret, readiness probe `/health/ready`, limits `memory:768Mi`, `cpu:1`.
- **Service `orders-backoffice`** – selector `app: orders-backoffice`, port 80 → target 8080.
- **Ingress `orders-backoffice`** – host `backoffice.shop.example`, path `/` → service `orders-backoffice` port 80.

#### Data flow
1. **Ingress** receives external HTTP requests on `backoffice.shop.example/`.
2. Routes to **Service** `orders-backoffice` on port 80.
3. Service forwards to one of the **Deployment** pods on port 8080.
4. Pod serves requests, exposing readiness at `/health/ready`.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Secret `orders-db-credentials` (key `password`) for DB credentials.
- PostgreSQL database at `orders-db:5432`.

#### Cross-cutting
- Environment variables for DB connectivity.
- Readiness probe ensures traffic only reaches healthy pods.
- Resource limits enforce CPU/memory constraints.
