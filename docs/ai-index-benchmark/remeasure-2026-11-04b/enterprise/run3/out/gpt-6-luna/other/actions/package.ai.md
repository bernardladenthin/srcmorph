### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:39:27Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates verification, versioned container publishing, and production deployment for the orders backoffice.

#### Purpose
- Releases the orders backoffice on a version-tag push or manual workflow dispatch.
- Gates publishing and deployment on successful CI verification.

#### Responsibilities
- **Build and test:** Runs Maven verification with the `ci` profile on Java 21 and 25.
- **Publish and deploy:** Publishes a version-tagged container image, then delegates production deployment to Kubernetes.

#### Key units
- `release-backoffice` — GitHub Actions workflow coordinating sequential `build`, `image`, and `deploy` jobs.
- `build` — Checks out the repository and runs `mvn -B verify -Pci` across the Java version matrix.
- `image` — Publishes `registry.shop.example/orders-backoffice` using the version tag without its `v` prefix.
- `deploy` — Invokes `./.github/workflows/deploy-k8s.yml` for production.

#### Data flow
- A version-tag push or manual dispatch starts the workflow; successful matrix builds gate image publishing, which gates deployment.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, and `docker/login-action@v3`.
- Maven and `./.github/workflows/deploy-k8s.yml`.
- Container registry: `registry.shop.example/orders-backoffice`.

#### Cross-cutting
- Workflow permissions are `contents: read` and `packages: write`.
- Image publishing uses `REGISTRY_USER` and `REGISTRY_TOKEN`; deployment inherits secrets.
- Publishing and deployment use the `production` environment.
