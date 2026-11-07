### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:27:31Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Deploys and exposes the orders‑backoffice microservice on Kubernetes, handling database connectivity, health checks, and external routing.

#### Purpose
- Run the orders‑backoffice application in a scalable, containerized environment.
- Provide internal service discovery and external HTTP access.

#### Responsibilities
- **Deployment**: orchestrate pod replicas, container image, environment, probes, and resource limits.
- **Service**: expose the pods on a stable cluster IP and port.
- **Ingress**: route external traffic to the service with host/path rules.

#### Key units
- **Deployment `orders-backoffice`** – 3 replicas, container `backoffice` (image `registry.shop.example/orders-backoffice:2.1.0`), port `8080`, readiness probe `/health/ready`, env `SHOP_DB_URL` and `SHOP_DB_PASSWORD` from secret.
- **Service `orders-backoffice`** – selector `app: orders-backoffice`, port `80` → target `8080`.
- **Ingress `orders-backoffice`** – host `backoffice.shop.example`, path `/` → Service `orders-backoffice` port `80`.

#### Data flow
1. **Ingress** receives HTTP requests on `backoffice.shop.example/`.
2. Routes to **Service** `orders-backoffice` on port `80`.
3. Service forwards to one of the **Deployment** pods on port `8080`.
4. Pod reads `SHOP_DB_URL` and `SHOP_DB_PASSWORD` (from secret) to connect to the orders database.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Kubernetes secret `orders-db-credentials` (key `password`).
- PostgreSQL database reachable at `orders-db:5432`.

#### Cross-cutting
- Consistent use of environment variables for database configuration.
- Health‑check via HTTP readiness probe to ensure pod readiness before traffic is routed.
- Resource limits (`memory: 768Mi`, `cpu: "1"`) enforce pod sizing.
