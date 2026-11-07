### orders-deployment.yaml
- H: 1.0
- C: 16BC7D8B
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:17:23Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: C433AA07
---
**Facts (exact, whole file):** resources: 3; workloads: 1; Services, Ingresses and Routes: 2; ConfigMaps and Secrets: 0; container images: 1; container ports: 1; probes: 1; secret references: 1; Helm control actions (approx): 0; .Values references: 0

> Deploys the `orders-backoffice` application, exposing it via a Service and Ingress.

#### Purpose
- Runs the orders back‑office microservice.
- Provides HTTP access through a Service and Ingress.

#### Type
- Kubernetes `Deployment`, `Service`, and `Ingress` manifests (apiVersion `apps/v1`, `v1`, `networking.k8s.io/v1`).

#### Definitions
- **Deployment** `orders-backoffice`
  - 3 replicas.
  - Container `backoffice`:
    - Image `registry.shop.example/orders-backoffice:2.1.0`.
    - Exposes port `8080`.
    - Environment:
      - `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`.
      - `SHOP_DB_PASSWORD` from secret `orders-db-credentials` key `password`.
    - Readiness probe: HTTP GET `/health/ready` on port `8080`.
    - Resource limits: `memory: 768Mi`, `cpu: "1"`.
- **Service** `orders-backoffice`
  - Selects pods with label `app: orders-backoffice`.
  - Exposes port `80` mapped to target port `8080`.
- **Ingress** `orders-backoffice`
  - Host `backoffice.shop.example`.
  - Path `/` (Prefix) routes to Service `orders-backoffice` port `80`.

#### Configuration
- `replicas=3`
- `image=registry.shop.example/orders-backoffice:2.1.0`
- `containerPort=8080`
- `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`
- `SHOP_DB_PASSWORD` from `orders-db-credentials.password`
- `readinessProbe.httpGet.path=/health/ready`
- `readinessProbe.httpGet.port=8080`
- `resources.limits.memory=768Mi`
- `resources.limits.cpu="1"`
- Service port `80` → target `8080`
- Ingress host `backoffice.shop.example`, path `/`, service `orders-backoffice` port `80`.

#### Dependencies
- Docker image `registry.shop.example/orders-backoffice:2.1.0`.
- Secret `orders-db-credentials` (key `password`).
- Service `orders-backoffice` (defined in same file).
- Ingress controller handling `networking.k8s.io/v1` Ingress.
