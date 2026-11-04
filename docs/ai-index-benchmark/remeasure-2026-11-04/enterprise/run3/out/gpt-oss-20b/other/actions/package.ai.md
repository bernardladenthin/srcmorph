### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:24:10Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates the CI/CD pipeline for a Java Backoffice service, from build and test through Docker image creation to Kubernetes deployment.

#### Purpose
- Continuous integration and delivery for the Backoffice service.

#### Responsibilities
- **Build & Test**: Compile Java code and run unit tests across multiple Java versions.
- **Containerization**: Build Docker images and push them to a private registry.
- **Deployment**: Trigger a Kubernetes deployment workflow after image push.

#### Key units
- **`release.yml`** – orchestrates build, image, and deploy jobs.
- **`build` job** – Maven (`mvn -B verify -Pci`) on `ubuntu-24.04` with Java 21/25 matrix.
- **`image` job** – Builds Docker image, tags with release version, pushes to `registry.shop.example`.
- **`deploy` job** – Calls reusable workflow `deploy-k8s.yml` for production deployment.

#### Data flow
`push` to a tag → `build` → `image` → `deploy` → Kubernetes cluster.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Docker registry: `registry.shop.example` (credentials from `REGISTRY_USER`/`REGISTRY_TOKEN`).
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Java version matrix strategy.
- Secret handling for registry credentials.
- Environment‑specific configuration (`production`).
- Permission block to restrict actions to required scopes.
