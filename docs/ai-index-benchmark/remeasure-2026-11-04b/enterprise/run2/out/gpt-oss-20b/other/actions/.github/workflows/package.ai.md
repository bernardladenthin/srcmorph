### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:36:15Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates the build, containerization, and Kubernetes deployment of the back‑office service.

#### Purpose
- Continuous delivery pipeline for the back‑office application.

#### Responsibilities
- **Build**: compile Java artifacts and run tests across multiple Java versions.
- **Containerize**: build and push Docker images to a private registry.
- **Deploy**: trigger a reusable Kubernetes deployment workflow.

#### Key units
- **Workflow `release-backoffice`** (`.github/workflows/release.yml`): orchestrates build, image, and deploy jobs.
- **Job `build`**: runs Maven `verify` on Java 21 and 25.
- **Job `image`**: logs into `registry.shop.example`, builds image `orders-backoffice:<tag>`, pushes to registry.
- **Job `deploy`**: invokes `.github/workflows/deploy-k8s.yml` with production environment.

#### Data flow
`push` to a tag → `build` → `image` → `deploy` → Kubernetes cluster.

#### Dependencies
- GitHub Actions: `checkout`, `setup-java`, `docker/login-action`.
- Docker registry: `registry.shop.example`.
- Reusable workflow: `.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Uses matrix strategy for Java versions.
- Secrets `REGISTRY_USER` and `REGISTRY_TOKEN` for registry authentication.
- Permissions block ensures required access for deployment.
