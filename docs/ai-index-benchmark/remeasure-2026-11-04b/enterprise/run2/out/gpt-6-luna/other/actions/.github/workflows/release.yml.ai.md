### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:28:41Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds and tests the orders backoffice with Java 21 and 25, publishes a version-tagged container image, and deploys it to production.

#### Purpose
- Release workflow for the orders backoffice application.
- Builds and verifies the project, pushes its container image, then invokes the Kubernetes deployment workflow.

#### Type
- GitHub Actions workflow named `release-backoffice`.

#### Definitions
- Triggers:
  - `push` tags matching `v*`.
  - `workflow_dispatch`.
- Workflow permissions: `contents: read`, `packages: write`.
- Job `build`:
  - Runner: `ubuntu-24.04`; matrix: `java` values `21`, `25`.
  - Steps, in order:
    1. `actions/checkout@v4`.
    2. `actions/setup-java@v4` with `distribution: temurin` and `java-version: ${{ matrix.java }}`.
    3. `Build and test`: `mvn -B verify -Pci`.
- Job `image`:
  - Needs `build`; runner: `ubuntu-24.04`; environment: `production`.
  - Steps, in order:
    1. `actions/checkout@v4`.
    2. `Log in to the registry`: `docker/login-action@v3`.
    3. `Push the image`: builds and pushes `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`.
- Job `deploy`:
  - Needs `image`; reuses `./.github/workflows/deploy-k8s.yml`.
  - Passes `environment: production`; inherits secrets.

#### Configuration
- `strategy.matrix.java`: `[21, 25]`.
- `distribution`: `temurin`.
- Registry: `registry.shop.example`.
- Registry credentials: `REGISTRY_USER=***`, `REGISTRY_TOKEN=***`.
- Image tag: `${GITHUB_REF_NAME#v}`.
- Reusable workflow input `environment`: `production`.

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Registry and image: `registry.shop.example/orders-backoffice`.
- Build command: `mvn -B verify -Pci`.
