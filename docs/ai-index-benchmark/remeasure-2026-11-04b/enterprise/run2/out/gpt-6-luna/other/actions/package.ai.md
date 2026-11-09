### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:30:44Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates verification, container-image publication, and production deployment of versioned orders backoffice releases.

#### Purpose
- Release the orders backoffice from version-tagged or manually dispatched builds.
- Verify the application, publish its container image, and hand it off for Kubernetes deployment.

#### Responsibilities
- **Build and verification:** Run `mvn -B verify -Pci` across Java 21 and 25.
- **Image publishing:** Publish the versioned `orders-backoffice` image to the production registry.
- **Deployment handoff:** Invoke the reusable Kubernetes deployment workflow.

#### Key units
- `release-backoffice` — Workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — Java matrix build and verification job.
- `image` — Production-environment image publication job.
- `deploy` — Job calling `./.github/workflows/deploy-k8s.yml`.

#### Data flow
- Tag or manual dispatch → Java matrix verification → publish `registry.shop.example/orders-backoffice` with the `v` prefix removed → Kubernetes deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Container registry: `registry.shop.example`.
- Build command: `mvn -B verify -Pci`.

#### Cross-cutting
- Runs on Ubuntu 24.04; permissions include `contents: read` and `packages: write`.
- Registry credentials use secrets; deployment inherits secrets.
