### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:37:34Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds and tests the orders backoffice on Java 21 and 25, publishes a version-tagged container image, and invokes the production Kubernetes deployment workflow.

#### Purpose
- Release workflow for the orders backoffice application.
- Verifies the application, pushes its container image, then delegates production deployment.

#### Type
- GitHub Actions workflow.

#### Definitions
- Workflow `release-backoffice`.
- Triggers: push tags matching `v*`; `workflow_dispatch`.
- Permissions: `contents: read`, `packages: write`.
- Job `build`: runs on `ubuntu-24.04`; matrix `java` values `21`, `25`.
  - `actions/checkout@v4`
  - `actions/setup-java@v4`: `distribution: temurin`, `java-version: ${{ matrix.java }}`
  - `Build and test`: `mvn -B verify -Pci`
- Job `image`: needs `build`; runs on `ubuntu-24.04`; environment `production`.
  - `actions/checkout@v4`
  - `Log in to the registry`: `docker/login-action@v3`; registry `registry.shop.example`; username `${{ secrets.REGISTRY_USER }}`; password `${{ secrets.REGISTRY_TOKEN }}`
  - `Push the image`: builds and pushes `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`
- Job `deploy`: needs `image`; calls `./.github/workflows/deploy-k8s.yml` with `environment: production`; inherits secrets.

#### Configuration
- `java`: matrix values `21`, `25`.
- `distribution: temurin`.
- Maven build: `-B verify -Pci`.
- `registry: registry.shop.example`.
- Image tag: `${GITHUB_REF_NAME#v}`.
- `environment: production`.
- `REGISTRY_USER=***` (`secrets.REGISTRY_USER`).
- `REGISTRY_TOKEN=***` (`secrets.REGISTRY_TOKEN`).

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Registry and image: `registry.shop.example/orders-backoffice`.
- Build tool: Maven.
