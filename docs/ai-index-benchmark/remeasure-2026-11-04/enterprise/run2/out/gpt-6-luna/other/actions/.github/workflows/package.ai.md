### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:14:56Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Validates and releases versioned Orders Backoffice container images, then hands them off for production deployment.

#### Purpose
- Runs Maven verification across Java 21 and 25 for release candidates.
- Builds and pushes a version-tagged image, then invokes the Kubernetes deployment workflow for production.

#### Responsibilities
- **Release validation:** Runs `mvn -B verify -Pci` on a Java-version matrix.
- **Image publishing:** Authenticates to `registry.shop.example` and pushes `registry.shop.example/orders-backoffice` using the version tag.
- **Production deployment handoff:** Calls the reusable Kubernetes deployment workflow after image publishing.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — verifies the application on `ubuntu-24.04` with Temurin Java 21 and 25.
- `image` — builds and pushes the release-tagged Orders Backoffice image in the `production` environment.
- `deploy` — calls `./.github/workflows/deploy-k8s.yml` with the `production` environment and inherited secrets.

#### Data flow
- A tag push or manual dispatch starts the workflow; the build matrix must complete before image publishing.
- The image job derives the image tag from `GITHUB_REF_NAME` by removing the `v` prefix.
- After publishing, the deploy job invokes the Kubernetes workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Build tool: Maven (`mvn`); Java distribution: Temurin.
- Registry: `registry.shop.example/orders-backoffice`.
- Secrets: `REGISTRY_USER`, `REGISTRY_TOKEN`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Workflow permissions are limited to `contents: read` and `packages: write`.
- The image and deployment stages use the `production` environment.
