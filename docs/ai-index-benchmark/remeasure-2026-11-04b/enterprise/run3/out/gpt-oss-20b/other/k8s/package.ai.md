### other/k8s
- H: 1.0
- C: FF5D03E7
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:42:50Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-deployment.yaml](orders-deployment.yaml.ai.md)
---
> Deploys and exposes the orders‑backoffice microservice on Kubernetes, handling database connectivity and health checks.

#### Purpose
- Run the orders‑backoffice application in a scalable, containerized environment.
- Provide internal and external access via Service and Ingress.

#### Responsibilities
- **Deployment**: manage pod lifecycle, replica count, container image, environment, probes, and resource limits.
- **Service**: expose the application internally on a stable port.
- **Ingress**: route external traffic to the Service with host and path rules.
- **Configuration**: inject database URL and credentials from a Secret.

#### Key units
- **Deployment `orders-backoffice`** – orchestrates 3 replicas of the `backoffice` container (`registry.shop.example/orders-backoffice:2.1.0`), sets env vars, readiness probe, and resource limits.
- **Service `orders-backoffice`** – selects pods by label `app: orders-backoffice`, forwards port 80 to container port 8080.
- **Ingress `orders-backoffice`** – routes host `backoffice.shop.example` and path `/` to the Service on port 80.
- **Secret `orders-db-credentials`** – supplies `SHOP_DB_PASSWORD` to the container.

#### Data flow
1. **Deployment** pulls the Docker image and starts pods with env vars `SHOP_DB_URL` and `SHOP_DB_PASSWORD`.
2. Pods expose port 8080; readiness probe checks `/health/ready`.
3. **Service** maps internal traffic from port 80 to pod port 8080.
4. **Ingress** receives external requests on `backoffice.shop.example` and forwards them to the Service.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Kubernetes Secret `orders-db-credentials` (key `password`).
- PostgreSQL database reachable at `orders-db:5432`.

#### Cross-cutting
- Consistent use of labels (`app: orders-backoffice`) for selector matching.
- Environment variables for configuration, with secrets referenced via `valueFrom`.
- Standard readiness probe pattern (`/health/ready` on container port).
- Resource limits (`memory: 768Mi`, `cpu: "1"`) to enforce pod sizing.
