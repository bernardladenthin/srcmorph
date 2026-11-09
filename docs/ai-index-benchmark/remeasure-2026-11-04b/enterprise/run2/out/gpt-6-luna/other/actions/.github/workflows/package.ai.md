### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:30:36Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates verification, container publication, and production deployment of the orders backoffice on version-tagged releases.

#### Purpose
- Build and test the orders backoffice using Java 21 and 25.
- Publish its version-tagged container image and invoke production deployment.

#### Responsibilities
- **Build and verification:** Run `mvn -B verify -Pci` with Temurin Java 21 and 25.
- **Image publishing:** Log in to the registry and push the orders backoffice image.
- **Production deployment:** Invoke the reusable Kubernetes deployment workflow after image publication.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — Matrix build and verification job.
- `image` — Production-environment job that publishes the container image.
- `deploy` — Job that calls `./.github/workflows/deploy-k8s.yml` with `environment: production`.

#### Data flow
- Tag or manual trigger → Java matrix build and verification → container image push to `registry.shop.example/orders-backoffice` using the version without the `v` prefix → reusable production deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Container registry: `registry.shop.example`; image: `orders-backoffice`.
- Maven build command: `mvn -B verify -Pci`.

#### Cross-cutting
- Workflow permissions: `contents: read`, `packages: write`.
- Registry credentials are provided as secrets; deployment inherits secrets.
- Build uses an Ubuntu 24.04 runner and a Java 21/25 matrix.
