### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:36:19Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates the continuous delivery of the back‑office Java service from source to a Kubernetes cluster.

#### Purpose
- Provide a CI/CD pipeline that builds, tests, containerizes, and deploys the back‑office application.

#### Responsibilities
- **Build**: compile Java artifacts and run tests across multiple Java versions.
- **Containerize**: create and publish Docker images to a private registry.
- **Deploy**: trigger a reusable Kubernetes deployment workflow.

#### Key units
- **Workflow `release-backoffice`** (`.github/workflows/release.yml`): orchestrates the full pipeline.
- **Job `build`**: Maven `verify` on Java 21 and 25.
- **Job `image`**: Docker login, build `orders-backoffice:<tag>`, push to `registry.shop.example`.
- **Job `deploy`**: calls reusable `.github/workflows/deploy-k8s.yml` for production.

#### Data flow
`push` → `build` → `image` → `deploy` → Kubernetes cluster.

#### Dependencies
- GitHub Actions: `checkout`, `setup-java`, `docker/login-action`.
- Docker registry: `registry.shop.example`.
- Reusable workflow: `.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Matrix strategy for Java versions.
- Secrets `REGISTRY_USER` and `REGISTRY_TOKEN` for registry authentication.
- Permissions block for deployment access.
