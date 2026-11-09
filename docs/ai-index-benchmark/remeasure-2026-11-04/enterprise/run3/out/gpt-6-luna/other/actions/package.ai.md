### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:21:18Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates verification, container release, and production deployment of the Orders Backoffice application.

#### Purpose
- Release the application on version-tag pushes or manual dispatch.
- Verify builds, publish a versioned container image, and hand off production deployment.

#### Responsibilities
- Run Maven verification with the `ci` profile across Java 21 and 25.
- Build and push `orders-backoffice` to `registry.shop.example`.
- Invoke the reusable Kubernetes deployment workflow for production.

#### Key units
- `release-backoffice` — workflow triggered by `v*` tags or `workflow_dispatch`.
- `build` — checks out the repository and runs `mvn -B verify -Pci` across the Java version matrix.
- `image` — authenticates to the registry and publishes the release-tagged image.
- `deploy` — calls `./.github/workflows/deploy-k8s.yml`.

#### Data flow
- Release trigger or manual dispatch → Java matrix verification → image build and push → production deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, and `docker/login-action@v3`.
- Maven, `registry.shop.example`, and `secrets.REGISTRY_USER` / `secrets.REGISTRY_TOKEN`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Workflow permissions grant `contents: read` and `packages: write`.
- Image publication and deployment follow successful preceding jobs; registry credentials are supplied through secrets.
