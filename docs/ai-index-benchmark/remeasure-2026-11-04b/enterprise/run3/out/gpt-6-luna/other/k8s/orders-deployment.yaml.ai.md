### orders-deployment.yaml
- H: 1.0
- C: 16BC7D8B
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:38:23Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: C433AA07
---
**Facts (exact, whole file):** resources: 3; workloads: 1; Services, Ingresses and Routes: 2; ConfigMaps and Secrets: 0; container images: 1; container ports: 1; probes: 1; secret references: 1; Helm control actions (approx): 0; .Values references: 0

> Deploys the Orders back-office application to Kubernetes and exposes it at `backoffice.shop.example`.

#### Purpose
- Runs three replicas of the Orders back-office application.
- Provides cluster-internal service access and external HTTP routing.

#### Type
- Kubernetes multi-resource manifest; Deployment `apps/v1`, Service `v1`, Ingress `networking.k8s.io/v1`.

#### Definitions
- Deployment `orders-backoffice`:
  - Labels and selector: `app: orders-backoffice`.
  - Replicas: `3`.
  - Container `backoffice`: image `registry.shop.example/orders-backoffice:2.1.0`; container port `8080`.
  - Environment: `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`; `SHOP_DB_PASSWORD` from Secret `orders-db-credentials`, key `password`.
  - Readiness probe: HTTP GET `/health/ready` on port `8080`.
  - Resource limits: memory `768Mi`, CPU `"1"`.
- Service `orders-backoffice`:
  - Selects `app: orders-backoffice`.
  - Port `80` targets port `8080`.
- Ingress `orders-backoffice`:
  - Host `backoffice.shop.example`.
  - Prefix path `/` routes to Service `orders-backoffice`, port `80`.

#### Configuration
- `spec.replicas=3`
- `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`
- `SHOP_DB_PASSWORD` references `orders-db-credentials`, key `password`.
- Readiness endpoint: `/health/ready`, port `8080`.
- Resource limits: `memory=768Mi`, `cpu="1"`.
- Service port mapping: `80` → `8080`.
- Ingress host: `backoffice.shop.example`; path `/`, `pathType=Prefix`.

#### Dependencies
- Image: `registry.shop.example/orders-backoffice:2.1.0`
- Secret: `orders-db-credentials`, key `password`
- Database endpoint: `orders-db:5432`, database `shop`
- Service: `orders-backoffice` (port `80`)
