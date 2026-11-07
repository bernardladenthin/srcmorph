### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:39:22Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Automates verification, versioned container publishing, and production deployment for the orders backoffice.

#### Purpose
- Releases the orders backoffice when a version tag is pushed or the workflow is manually dispatched.

#### Responsibilities
- **Build and test:** Runs Maven verification with the `ci` profile on Java 21 and 25.
- **Publish and deploy:** Publishes a version-tagged image, then delegates production deployment to a Kubernetes workflow.

#### Key units
- `release-backoffice` — GitHub Actions workflow coordinating sequential `build`, `image`, and `deploy` jobs.
- `build` — Checks out the repository and runs `mvn -B verify -Pci` across the Java version matrix.
- `image` — Publishes `registry.shop.example/orders-backoffice` with the version tag stripped of its `v` prefix.
- `deploy` — Calls `./.github/workflows/deploy-k8s.yml` for the production environment.

#### Data flow
- A version-tag push or manual dispatch starts the workflow; successful Java matrix builds gate image publishing, which in turn gates deployment.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Maven and `./.github/workflows/deploy-k8s.yml`.
- Registry: `registry.shop.example/orders-backoffice`.

#### Cross-cutting
- Permissions are `contents: read` and `packages: write`.
- Image publishing uses `REGISTRY_USER` and `REGISTRY_TOKEN`; deployment inherits secrets.
- Publishing and deployment use the `production` environment.
