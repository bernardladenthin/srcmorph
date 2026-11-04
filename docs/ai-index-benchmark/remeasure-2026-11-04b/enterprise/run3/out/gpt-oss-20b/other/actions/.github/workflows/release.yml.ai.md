### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:41:05Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds, tests, containerizes, and deploys the Backoffice service to production Kubernetes.

#### Purpose
- Automate release of Backoffice.
- Build Java artifacts, create Docker image, push to registry, trigger Kubernetes deployment.

#### Type
- GitHub Actions workflow (`.github/workflows/release.yml`).
- API: GitHub Actions v4.

#### Definitions
- **Workflow name**: `release-backoffice`.
- **Triggers**:
  - `push` on tags matching `v*`.
  - `workflow_dispatch` manual trigger.
- **Jobs**:
  - `build`:
    - `runs-on`: `ubuntu-24.04`.
    - `strategy.matrix.java`: `[21, 25]`.
    - Steps:
      - `actions/checkout@v4`.
      - `actions/setup-java@v4` with `distribution: temurin`, `java-version: ${{ matrix.java }}`.
      - `Build and test`: `mvn -B verify -Pci`.
  - `image`:
    - `needs`: `build`.
    - `runs-on`: `ubuntu-24.04`.
    - `environment`: `production`.
    - Steps:
      - `actions/checkout@v4`.
      - `Log in to the registry`: `docker/login-action@v3` with `registry: registry.shop.example`, `username: ${{ secrets.REGISTRY_USER }}`, `password: ${{ secrets.REGISTRY_TOKEN }}`.
      - `Push the image`: builds `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}` and pushes it.
  - `deploy`:
    - `needs`: `image`.
    - `uses`: `./.github/workflows/deploy-k8s.yml`.
    - `with.environment`: `production`.
    - `secrets`: `inherit`.

#### Configuration
- `permissions.contents`: `read`.
- `permissions.packages`: `write`.
- `strategy.matrix.java`: `21`, `25`.
- `docker/login-action` registry: `registry.shop.example`.
- Image tag: `${GITHUB_REF_NAME#v}` (tag without leading `v`).

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Docker registry: `registry.shop.example`.
- External workflow: `./.github/workflows/deploy-k8s.yml`.
