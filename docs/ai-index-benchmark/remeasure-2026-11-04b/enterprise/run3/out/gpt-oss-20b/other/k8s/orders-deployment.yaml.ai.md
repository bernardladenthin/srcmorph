### orders-deployment.yaml
- H: 1.0
- C: 16BC7D8B
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:41:37Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: C433AA07
---
**Facts (exact, whole file):** resources: 3; workloads: 1; Services, Ingresses and Routes: 2; ConfigMaps and Secrets: 0; container images: 1; container ports: 1; probes: 1; secret references: 1; Helm control actions (approx): 0; .Values references: 0

> Deploys the `orders-backoffice` application as a Kubernetes Deployment, Service, and Ingress.

#### Purpose
- Runs the orders back‑office microservice.
- Exposes it internally via a Service and externally via an Ingress.

#### Type
- Kubernetes manifests (`apiVersion: apps/v1`, `v1`, `networking.k8s.io/v1`).

#### Definitions
- **Deployment** `orders-backoffice`
  - 3 replicas.
  - Container `backoffice`:
    - Image `registry.shop.example/orders-backoffice:2.1.0`.
    - Port `8080`.
    - Env:
      - `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`.
      - `SHOP_DB_PASSWORD` from secret `orders-db-credentials` key `password`.
    - Readiness probe: HTTP GET `/health/ready` on port `8080`.
    - Resources: `limits` `memory: 768Mi`, `cpu: "1"`.
- **Service** `orders-backoffice`
  - Selector `app: orders-backoffice`.
  - Port `80` → target `8080`.
- **Ingress** `orders-backoffice`
  - Host `backoffice.shop.example`.
  - Path `/` (Prefix) routes to Service `orders-backoffice` port `80`.

#### Configuration
- `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`
- `SHOP_DB_PASSWORD` from `orders-db-credentials.password`
- `replicas=3`
- `containerPort=8080`
- `servicePort=80`
- `ingressHost=backoffice.shop.example`

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Secret `orders-db-credentials` (key `password`).
- Service `orders-backoffice` (used by Ingress).
