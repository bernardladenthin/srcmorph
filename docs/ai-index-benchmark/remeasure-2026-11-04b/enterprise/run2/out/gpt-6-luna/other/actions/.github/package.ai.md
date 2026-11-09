### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:30:40Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Version-tagged orders backoffice releases are built and verified, published as container images, and handed off for production deployment.

#### Purpose
- Automate verification and release of the orders backoffice.
- Publish release images and trigger Kubernetes deployment.

#### Responsibilities
- **Build and verification:** Run `mvn -B verify -Pci` on Java 21 and 25.
- **Image publishing:** Push the versioned image to `registry.shop.example`.
- **Production deployment:** Call the reusable Kubernetes deployment workflow.

#### Key units
- `release-backoffice` — Workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — Java matrix build and verification job.
- `image` — Production-environment container publication job.
- `deploy` — Job invoking `./.github/workflows/deploy-k8s.yml`.

#### Data flow
- Tag or manual trigger → Java matrix build and verification → publish `orders-backoffice` image with the `v` prefix removed → production deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Registry and image: `registry.shop.example/orders-backoffice`.
- Build command: `mvn -B verify -Pci`.

#### Cross-cutting
- Uses an Ubuntu 24.04 runner; permissions are `contents: read` and `packages: write`.
- Registry credentials are supplied as secrets; deployment inherits secrets.
