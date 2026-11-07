### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:10:12Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds, tests, containerizes, and deploys the Backoffice service to production.

#### Purpose
- Build and test the Java application.
- Build and push a Docker image to a registry.
- Trigger a Kubernetes deployment workflow.

#### Type
- GitHub Actions workflow (`.github/workflows/release.yml`), API v4.

#### Definitions
- **Triggers**: `push` on tags matching `v*`; `workflow_dispatch`.
- **Jobs**:
  - `build`: runs on `ubuntu-24.04`; matrix `java: [21, 25]`; steps:
    - `actions/checkout@v4`
    - `actions/setup-java@v4` with `distribution: temurin`, `java-version: ${{ matrix.java }}`
    - `Build and test` runs `mvn -B verify -Pci`
  - `image`: depends on `build`; runs on `ubuntu-24.04`; environment `production`; steps:
    - `actions/checkout@v4`
    - `Log in to the registry` uses `docker/login-action@v3` with `registry: registry.shop.example`, `username: ${{ secrets.REGISTRY_USER }}`, `password: ${{ secrets.REGISTRY_TOKEN }}`
    - `Push the image` runs Docker build and push for `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`
  - `deploy`: depends on `image`; uses external workflow `.github/workflows/deploy-k8s.yml` with `environment: production`; inherits secrets.

#### Configuration
- `java-version`: matrix values `21`, `25`.
- Docker image tag: `${GITHUB_REF_NAME#v}` (tag without leading `v`).
- Registry credentials: `REGISTRY_USER`, `REGISTRY_TOKEN` (secrets).

#### Dependencies
- `actions/checkout@v4`
- `actions/setup-java@v4`
- `docker/login-action@v3`
- External workflow `.github/workflows/deploy-k8s.yml`
- Docker registry `registry.shop.example`
- Secrets `REGISTRY_USER`, `REGISTRY_TOKEN` (inherited for deploy).
