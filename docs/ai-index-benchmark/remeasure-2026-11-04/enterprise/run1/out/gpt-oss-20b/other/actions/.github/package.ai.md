### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:11:28Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Automates the CI/CD pipeline for the Backoffice Java application, building, testing, containerizing, and deploying it to production.

#### Purpose
- Orchestrate continuous integration and delivery for the Backoffice service.

#### Responsibilities
- Compile and test Java code across multiple Java versions.
- Build and publish Docker images to a private registry.
- Trigger Kubernetes deployments in the production environment.

#### Key units
- **GitHub Actions workflow** `.github/workflows/release.yml` – defines jobs `build`, `image`, and `deploy`.
- **Build job** – runs Maven `verify` with CI profile on `ubuntu-24.04` for Java 21 and 25.
- **Image job** – logs into `registry.shop.example`, builds and pushes `orders-backoffice` image tagged with the release version.
- **Deploy job** – calls external workflow `.github/workflows/deploy-k8s.yml` to roll out the new image to Kubernetes.

#### Data flow
`push` to a tag `v*` → `build` job compiles/tests → `image` job builds Docker image → `deploy` job triggers Kubernetes rollout.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- External workflow `.github/workflows/deploy-k8s.yml`.
- Docker registry `registry.shop.example`.
- Secrets: `REGISTRY_USER`, `REGISTRY_TOKEN`.

#### Cross-cutting
- Matrix strategy for Java versions.
- Unified secret inheritance across jobs.
- Environment variable `GITHUB_REF_NAME` used to derive image tag.
