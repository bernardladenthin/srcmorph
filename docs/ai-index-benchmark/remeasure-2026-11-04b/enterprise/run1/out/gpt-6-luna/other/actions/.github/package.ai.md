### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:24:11Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Releases the orders backoffice application by verifying versioned builds, publishing container images, and deploying them to production.

#### Purpose
- Automates the tagged or manually triggered release path from Maven verification to production deployment.

#### Responsibilities
- **Build and test:** Verifies the application with Maven’s `ci` profile on Java 21 and 25.
- **Publish and deploy:** Pushes a version-tagged image to `registry.shop.example` and invokes the Kubernetes deployment workflow for production.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tags or manual dispatch.
- `build`, `image`, `deploy` — Sequential jobs for verification, container publishing, and deployment through `./.github/workflows/deploy-k8s.yml`.

#### Data flow
- Workflow trigger starts the Java matrix build; after verification, the image job publishes `registry.shop.example/orders-backoffice` with the leading `v` removed from the tag.
- The deploy job runs after publication, targets `production`, and inherits secrets.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Maven, the `ci` profile, and registry credentials `REGISTRY_USER` and `REGISTRY_TOKEN`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Jobs use `ubuntu-24.04`; the image job uses the `production` environment.
- Workflow permissions are limited to `contents: read` and `packages: write`.
