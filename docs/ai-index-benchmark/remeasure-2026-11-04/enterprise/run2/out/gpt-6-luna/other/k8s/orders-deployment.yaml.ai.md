### orders-deployment.yaml
- H: 1.0
- C: 16BC7D8B
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:14:08Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: C433AA07
---
**Facts (exact, whole file):** resources: 3; workloads: 1; Services, Ingresses and Routes: 2; ConfigMaps and Secrets: 0; container images: 1; container ports: 1; probes: 1; secret references: 1; Helm control actions (approx): 0; .Values references: 0

> Deploys the Shop orders backoffice application with three replicas, internal service access, and HTTP ingress at `backoffice.shop.example`.

#### Purpose
- Runs the orders backoffice application and exposes it through a Kubernetes Service and Ingress.

#### Type
- Kubernetes manifest; `apps/v1` Deployment, `v1` Service, `networking.k8s.io/v1` Ingress.

#### Definitions
- Deployment `orders-backoffice`: 3 replicas; pod label and selector `app: orders-backoffice`.
  - Container `backoffice`: image `registry.shop.example/orders-backoffice:2.1.0`; container port `8080`.
  - Environment: `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`; `SHOP_DB_PASSWORD` from Secret `orders-db-credentials`, key `password`.
  - Readiness probe: HTTP GET `/health/ready` on port `8080`.
  - Resource limits: memory `768Mi`, CPU `"1"`.
- Service `orders-backoffice`: selects `app: orders-backoffice`; port `80` targets `8080`.
- Ingress `orders-backoffice`: host `backoffice.shop.example`; path `/` with `Prefix` path type routes to Service `orders-backoffice`, port `80`.

#### Configuration
- `replicas=3`
- `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`
- `SHOP_DB_PASSWORD` references Secret `orders-db-credentials`, key `password`.
- `containerPort=8080`
- Readiness path: `/health/ready`; port `8080`.
- Resource limits: `memory=768Mi`, `cpu="1"`.
- Service: `port=80`, `targetPort=8080`.
- Ingress: `host=backoffice.shop.example`, `path=/`, `pathType=Prefix`, backend port `80`.

#### Dependencies
- Image: `registry.shop.example/orders-backoffice:2.1.0`
- Secret: `orders-db-credentials` (`password`)
- Database endpoint: `orders-db:5432` (database `shop`)
