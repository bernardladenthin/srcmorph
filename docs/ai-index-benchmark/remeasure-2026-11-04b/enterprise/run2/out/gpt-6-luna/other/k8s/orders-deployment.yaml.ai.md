### orders-deployment.yaml
- H: 1.0
- C: 16BC7D8B
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:29:43Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: C433AA07
---
**Facts (exact, whole file):** resources: 3; workloads: 1; Services, Ingresses and Routes: 2; ConfigMaps and Secrets: 0; container images: 1; container ports: 1; probes: 1; secret references: 1; Helm control actions (approx): 0; .Values references: 0

> Deploys the orders backoffice application with three replicas, internal service access, and HTTP ingress at `backoffice.shop.example`.

#### Purpose
- Runs the orders backoffice application in Kubernetes and exposes it through a Service and Ingress.

#### Type
- Kubernetes multi-document manifest: Deployment (`apps/v1`), Service (`v1`), Ingress (`networking.k8s.io/v1`).

#### Definitions
- Deployment `orders-backoffice`:
  - 3 replicas; selector and pod label `app: orders-backoffice`.
  - Container `backoffice`, image `registry.shop.example/orders-backoffice:2.1.0`; port `8080`.
  - Environment: `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`; `SHOP_DB_PASSWORD` from secret `orders-db-credentials`, key `password`.
  - Readiness probe: HTTP `GET /health/ready` on port `8080`.
  - Resource limits: memory `768Mi`, CPU `1`.
- Service `orders-backoffice`:
  - Selects pods with `app: orders-backoffice`; port `80` targets port `8080`.
- Ingress `orders-backoffice`:
  - Host `backoffice.shop.example`; path `/` with `Prefix` routing to Service `orders-backoffice` on port `80`.

#### Configuration
- `replicas=3`
- `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`
- `SHOP_DB_PASSWORD=***` (reference: `orders-db-credentials`, key `password`)
- Readiness path `/health/ready`; port `8080`
- Resource limits: `memory=768Mi`, `cpu=1`
- Ingress host `backoffice.shop.example`; path `/`; `pathType=Prefix`

#### Dependencies
- Image: `registry.shop.example/orders-backoffice:2.1.0`
- Database endpoint: `orders-db:5432`
- Secret: `orders-db-credentials` (key `password`)
- Ingress backend Service: `orders-backoffice`
