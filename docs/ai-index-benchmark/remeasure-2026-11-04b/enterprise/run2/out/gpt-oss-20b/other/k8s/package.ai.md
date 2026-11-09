### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:36:45Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Deploys and exposes the orders‑backoffice microservice in a Kubernetes cluster, providing external HTTP access via an Ingress.

#### Purpose
- Run the orders‑backoffice application.
- Expose it externally through a Service and Ingress.

#### Responsibilities
- **Deployment**: manage pod replicas, container image, environment, probes, and resource limits.
- **Service**: expose the application internally on port 80.
- **Ingress**: route external traffic from `backoffice.shop.example` to the Service.

#### Key units
- **Deployment `orders-backoffice`** – 3 replicas, container `backoffice` (image `registry.shop.example/orders-backoffice:2.1.0`, port 8080, env `SHOP_DB_URL`, secret‑backed `SHOP_DB_PASSWORD`), readiness probe `/health/ready`, resources limits `memory: 768Mi`, `cpu: 1`.
- **Service `orders-backoffice`** – selector `app: orders-backoffice`, port 80 → target 8080.
- **Ingress `orders-backoffice`** – host `backoffice.shop.example`, path `/` → Service `orders-backoffice` port 80.

#### Data flow
1. External request to `backoffice.shop.example/` → Ingress forwards to Service `orders-backoffice` port 80.  
2. Service routes to one of the 3 pod replicas.  
3. Pod serves HTTP on port 8080, using `SHOP_DB_URL` and `SHOP_DB_PASSWORD` to connect to the orders database.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.  
- Kubernetes secret `orders-db-credentials` (key `password`).  
- PostgreSQL database at `orders-db:5432`.

#### Cross-cutting
- Environment variables for database connectivity.  
- Readiness probe ensures pod is healthy before receiving traffic.  
- Resource limits enforce CPU and memory constraints.
