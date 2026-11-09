### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:18:04Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates the CI/CD pipeline for the Backoffice Java service, building, testing, containerizing, and deploying it to a Kubernetes cluster.

#### Purpose
- Continuous integration and deployment of the Backoffice service.  
- Automate Docker image creation and Kubernetes rollout.

#### Responsibilities
- **Build & Test**: Compile Java code and run unit tests across multiple Java versions.  
- **Containerization**: Build and push Docker images to a private registry.  
- **Deployment**: Trigger a Kubernetes deployment workflow with environment‑specific configuration.

#### Key units
- **`release.yml` workflow** – orchestrates build, image, and deploy jobs.  
- **`build` job** – runs Maven `verify` with CI profile on Java 21 and 25.  
- **`image` job** – logs into `registry.shop.example`, builds image `orders-backoffice:${tag}`, pushes to registry.  
- **`deploy` job** – calls external `.github/workflows/deploy-k8s.yml` for production rollout.

#### Data flow
`push` to a tag → `build` job → `image` job → `deploy` job → Kubernetes cluster.

#### Dependencies
- GitHub Actions: `checkout@v4`, `setup-java@v4`, `docker/login-action@v3`.  
- External workflow: `.github/workflows/deploy-k8s.yml`.  
- Docker registry: `registry.shop.example`.  
- Secrets: `REGISTRY_USER`, `REGISTRY_TOKEN`.

#### Cross-cutting
- Matrix strategy for Java versions.  
- Single environment (`production`) for image and deploy stages.  
- Secret handling for registry credentials.
