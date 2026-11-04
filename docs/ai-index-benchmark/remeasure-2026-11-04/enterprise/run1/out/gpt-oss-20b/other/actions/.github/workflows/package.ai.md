### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:11:26Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates the CI/CD pipeline for the Backoffice service, building, testing, containerizing, and deploying it to production.

#### Purpose
- Orchestrate continuous integration and delivery for the Backoffice Java application.

#### Responsibilities
- Build and test the Java code across multiple Java versions.
- Create and publish a Docker image to a private registry.
- Trigger a Kubernetes deployment workflow in the production environment.

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
