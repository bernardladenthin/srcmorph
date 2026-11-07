### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:22:13Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds and tests the orders backoffice application, publishes its container image, then deploys it to production.

#### Purpose
- Release workflow for the orders backoffice application.
- Builds and tests on Java 21 and 25, pushes a version-tagged image, and invokes the production Kubernetes deployment workflow.

#### Type
- GitHub Actions workflow; no API version declared.

#### Definitions
- Workflow `release-backoffice`
- Triggers:
  - `push` tags matching `v*`
  - `workflow_dispatch`
- Workflow permissions: `contents: read`, `packages: write`
- Job `build`:
  - Runner: `ubuntu-24.04`
  - Matrix: `java: [21, 25]`
  - Steps, in order:
    1. `actions/checkout@v4`
    2. `actions/setup-java@v4` with `distribution: temurin`, `java-version: ${{ matrix.java }}`
    3. `Build and test`: `mvn -B verify -Pci`
- Job `image`:
  - Needs: `build`
  - Runner: `ubuntu-24.04`
  - Environment: `production`
  - Steps, in order:
    1. `actions/checkout@v4`
    2. `Log in to the registry`: `docker/login-action@v3`
    3. `Push the image`: builds and pushes `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`
- Job `deploy`:
  - Needs: `image`
  - Reusable workflow: `./.github/workflows/deploy-k8s.yml`
  - Input: `environment: production`
  - Secrets: `inherit`

#### Configuration
- `registry`: `registry.shop.example`
- `username`: `${{ secrets.REGISTRY_USER }}`
- `password`: `${{ secrets.REGISTRY_TOKEN }}`
- Image tag expression: `${GITHUB_REF_NAME#v}`
- `environment`: `production`
- `java`: matrix values `21`, `25`

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`
- Registry and image: `registry.shop.example/orders-backoffice`
- Build tool: Maven; command uses profile `ci`
