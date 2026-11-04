### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:21:10Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates verification, container-image publication, and production Kubernetes deployment for the Orders Backoffice application.

#### Purpose
- Release Orders Backoffice from version-tag pushes or manual dispatch.
- Verify the application, publish its versioned image, and hand off production deployment.

#### Responsibilities
- Build and test with Maven across Java 21 and 25 using the `ci` profile.
- Build and push the `orders-backoffice` container image to `registry.shop.example`.
- Invoke the reusable Kubernetes deployment workflow for production.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tags or `workflow_dispatch`.
- `build` — checks out the repository and runs `mvn -B verify -Pci` across the Java version matrix.
- `image` — logs in to the registry and publishes `registry.shop.example/orders-backoffice` tagged from the release name.
- `deploy` — calls `./.github/workflows/deploy-k8s.yml` for the production environment.

#### Data flow
- Triggered release or manual run → Java matrix build and verification → image build and push → production deployment workflow call.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Maven with the `ci` profile; `registry.shop.example` and `secrets.REGISTRY_USER` / `secrets.REGISTRY_TOKEN`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Workflow permissions grant `contents: read` and `packages: write`.
- Image publication and deployment run after successful preceding jobs; registry credentials are supplied through secrets.
