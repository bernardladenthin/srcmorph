### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:39:17Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Validates the orders backoffice across supported Java versions, publishes a version-tagged container image, and delegates production Kubernetes deployment.

#### Purpose
- Automates release verification, container publishing, and production deployment for the orders backoffice.
- Runs on version-tag pushes (`v*`) or manual dispatch.

#### Responsibilities
- **Build and test:** Runs Maven `verify` with the `ci` profile on Java 21 and 25.
- **Publish and deploy:** Pushes the version-tagged image to the registry, then invokes the production deployment workflow.

#### Key units
- `release-backoffice` — GitHub Actions release workflow with sequential `build`, `image`, and `deploy` jobs.
- `build` — Checks out the repository and verifies it using Temurin Java versions from the matrix.
- `image` — Logs in to `registry.shop.example` and pushes `orders-backoffice:${GITHUB_REF_NAME#v}`.
- `deploy` — Calls `./.github/workflows/deploy-k8s.yml` for the production environment.

#### Data flow
- A version-tag push or manual dispatch starts the workflow.
- The `build` matrix runs `mvn -B verify -Pci` on Java 21 and 25.
- After successful builds, `image` authenticates to the registry and publishes the image tagged with the version without the `v` prefix.
- After image publishing, `deploy` passes control to the Kubernetes deployment workflow with production environment and inherited secrets.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Build tool: Maven; container registry and image: `registry.shop.example/orders-backoffice`.

#### Cross-cutting
- Workflow permissions are `contents: read` and `packages: write`.
- Image publishing uses `REGISTRY_USER` and `REGISTRY_TOKEN` secrets; deployment inherits secrets.
- Build matrix covers Java 21 and 25; publishing and deployment use the `production` environment.
