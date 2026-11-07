### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:21:15Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Automates verification, versioned container publication, and production deployment for the Orders Backoffice application.

#### Purpose
- Release Orders Backoffice on version-tag pushes or manual dispatch.
- Verify the application, publish its image, and hand off production deployment.

#### Responsibilities
- Build and verify with Maven’s `ci` profile across Java 21 and 25.
- Publish `orders-backoffice` to `registry.shop.example` using the release name as the image tag.
- Invoke the reusable Kubernetes deployment workflow for production.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tags or `workflow_dispatch`.
- `build` — checks out the repository and runs `mvn -B verify -Pci` across the Java version matrix.
- `image` — authenticates to the registry and builds and pushes the versioned image.
- `deploy` — calls `./.github/workflows/deploy-k8s.yml` for production.

#### Data flow
- Release trigger or manual dispatch → Java matrix verification → image build and push → production deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, and `docker/login-action@v3`.
- Maven, `registry.shop.example`, and `secrets.REGISTRY_USER` / `secrets.REGISTRY_TOKEN`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Permissions grant `contents: read` and `packages: write`.
- Image publication and deployment follow successful preceding jobs; registry credentials are provided through secrets.
