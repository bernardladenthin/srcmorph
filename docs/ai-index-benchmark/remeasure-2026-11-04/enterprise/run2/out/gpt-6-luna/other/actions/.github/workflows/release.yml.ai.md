### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:13:13Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds and publishes the Orders Backoffice container image on version-tag pushes, then deploys it to production.

#### Purpose
- Verifies the application with Maven on two Java versions.
- Publishes a version-tagged image and invokes the Kubernetes deployment workflow for production.

#### Type
- GitHub Actions workflow named `release-backoffice`.

#### Definitions
- Triggers:
  - `push` on tags matching `v*`.
  - `workflow_dispatch`.
- Permissions: `contents: read`, `packages: write`.
- Job `build`:
  - Runner: `ubuntu-24.04`; matrix: `java: [21, 25]`.
  - Steps, in order:
    1. `actions/checkout@v4`
    2. `actions/setup-java@v4` with `distribution: temurin`, `java-version: ${{ matrix.java }}`
    3. `Build and test`: `mvn -B verify -Pci`
- Job `image`:
  - Needs `build`; runner: `ubuntu-24.04`; environment: `production`.
  - Steps, in order:
    1. `actions/checkout@v4`
    2. `Log in to the registry`: `docker/login-action@v3`
    3. `Push the image`: builds and pushes `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`
- Job `deploy`:
  - Needs `image`; calls `./.github/workflows/deploy-k8s.yml`.
  - Passes `environment: production`; inherits secrets.

#### Configuration
- `name`: `release-backoffice`
- `permissions.contents`: `read`
- `permissions.packages`: `write`
- `strategy.matrix.java`: `21`, `25`
- `distribution`: `temurin`
- `registry`: `registry.shop.example`
- `username`: `${{ secrets.REGISTRY_USER }}`
- `password`: `secrets.REGISTRY_TOKEN=***`
- `environment`: `production`
- Image tag expression: `${GITHUB_REF_NAME#v}`

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`
- Build tool: `mvn`
- Registry/image: `registry.shop.example/orders-backoffice`
- Secrets: `secrets.REGISTRY_USER`, `secrets.REGISTRY_TOKEN`
