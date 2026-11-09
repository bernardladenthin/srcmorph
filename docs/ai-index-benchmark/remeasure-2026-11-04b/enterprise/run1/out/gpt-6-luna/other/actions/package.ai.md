### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:24:15Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Verifies, publishes, and deploys versioned orders backoffice releases to production.

#### Purpose
- Automates the release path from Maven verification through container publication and production deployment.

#### Responsibilities
- **Build verification:** Runs the Maven `ci` profile on Java 21 and 25.
- **Image publishing and deployment:** Publishes a versioned container image and invokes the Kubernetes deployment workflow.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tags or manual dispatch.
- `build`, `image`, `deploy` — Sequential jobs for verification, image publishing, and production deployment.

#### Data flow
- A release trigger starts the Java matrix build; on success, the image job publishes `registry.shop.example/orders-backoffice` using the tag without its leading `v`.
- The deploy job runs after publication, targets `production`, and inherits secrets.

#### Dependencies
- Maven with the `ci` profile; registry credentials `REGISTRY_USER` and `REGISTRY_TOKEN`.
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, and `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Jobs run on `ubuntu-24.04`; the image job uses the `production` environment.
- Workflow permissions are limited to `contents: read` and `packages: write`.
