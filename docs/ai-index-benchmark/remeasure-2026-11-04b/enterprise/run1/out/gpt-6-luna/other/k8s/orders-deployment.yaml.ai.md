### orders-deployment.yaml
- H: 1.0
- C: 16BC7D8B
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:23:18Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: C433AA07
---
**Facts (exact, whole file):** resources: 3; workloads: 1; Services, Ingresses and Routes: 2; ConfigMaps and Secrets: 0; container images: 1; container ports: 1; probes: 1; secret references: 1; Helm control actions (approx): 0; .Values references: 0

> Deploys the Orders backoffice application with three replicas, exposes it internally, and routes `backoffice.shop.example` traffic to it.

#### Purpose
- Runs the Orders backoffice application in Kubernetes.
- Exposes the application through a Service and Ingress.

#### Type
- Kubernetes manifest; API versions `apps/v1`, `v1`, and `networking.k8s.io/v1`.

#### Definitions
- `Deployment` `orders-backoffice`: 3 replicas; container `backoffice` uses `registry.shop.example/orders-backoffice:2.1.0`.
  - Container port: `8080`.
  - Readiness probe: HTTP `GET /health/ready` on port `8080`.
  - Resource limits: memory `768Mi`, CPU `"1"`.
- `Service` `orders-backoffice`: selects `app: orders-backoffice`; port `80` targets port `8080`.
- `Ingress` `orders-backoffice`: routes host `backoffice.shop.example`, path `/` (`Prefix`), to Service `orders-backoffice` port `80`.

#### Configuration
- `replicas=3`
- `SHOP_DB_URL=jdbc:postgresql://orders-db:5432/shop`
- `SHOP_DB_PASSWORD=***` — sourced from Secret `orders-db-credentials`, key `password`.
- Deployment and pod label selector: `app=orders-backoffice`
- `containerPort=8080`
- `readinessProbe.path=/health/ready`
- `resources.limits.memory=768Mi`
- `resources.limits.cpu="1"`
- Service `port=80`, `targetPort=8080`
- Ingress `host=backoffice.shop.example`, `path=/`, `pathType=Prefix`

#### Dependencies
- Image: `registry.shop.example/orders-backoffice:2.1.0`
- Secret: `orders-db-credentials`, key `password`
- Database endpoint: `orders-db:5432`
