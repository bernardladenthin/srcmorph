### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:18:30Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Deploys and exposes the orders‑backoffice microservice in a Kubernetes cluster, handling database connectivity and health checks.

#### Purpose
- Run the orders‑backoffice application.
- Expose it via a Service and Ingress for HTTP traffic.

#### Responsibilities
- **Deployment**: manage pod lifecycle, replica count, container image, environment, probes, and resource limits.
- **Service**: provide stable cluster‑internal endpoint for the pods.
- **Ingress**: route external HTTP requests to the Service.

#### Key units
- **Deployment `orders-backoffice`** – 3 replicas, container `backoffice` (image `registry.shop.example/orders-backoffice:2.1.0`, port `8080`), env `SHOP_DB_URL`, secret‑backed `SHOP_DB_PASSWORD`, readiness probe `/health/ready`, limits `memory: 768Mi`, `cpu: 1`.
- **Service `orders-backoffice`** – selects pods by `app: orders-backoffice`, exposes port `80` → target `8080`.
- **Ingress `orders-backoffice`** – host `backoffice.shop.example`, path `/`, routes to Service `orders-backoffice` port `80`.

#### Data flow
1. **Ingress** receives HTTP requests on `backoffice.shop.example/`.
2. Routes to **Service** `orders-backoffice` on port `80`.
3. Service forwards to one of the **Deployment** pods on port `8080`.
4. Pod reads `SHOP_DB_URL` and `SHOP_DB_PASSWORD` (from secret) to connect to `orders-db`.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Secret `orders-db-credentials` (key `password`).
- Kubernetes API objects: `Deployment`, `Service`, `Ingress` (networking.k8s.io/v1).

#### Cross-cutting
- Environment variables for database connectivity.
- Readiness probe ensures pod is healthy before receiving traffic.
- Resource limits enforce CPU/memory constraints.
