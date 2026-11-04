### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:06:49Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds and tests the orders backoffice on Java 21 and 25, publishes its container image on version-tagged releases, and invokes the production Kubernetes deployment workflow.

#### Purpose
- Release pipeline for the orders backoffice application.
- Builds and tests with Maven, pushes a container image, then delegates deployment.

#### Type
- GitHub Actions workflow named `release-backoffice`.

#### Definitions
- Triggers:
  - `push` tags matching `v*`.
  - `workflow_dispatch`.
- Permissions: `contents: read`, `packages: write`.
- Job `build`:
  - Runner: `ubuntu-24.04`.
  - Matrix: `java` values `21`, `25`.
  - Steps, in order:
    1. `actions/checkout@v4`.
    2. `actions/setup-java@v4` with Temurin and `${{ matrix.java }}`.
    3. `Build and test`: `mvn -B verify -Pci`.
- Job `image`:
  - Needs `build`; runner `ubuntu-24.04`; environment `production`.
  - Steps, in order:
    1. `actions/checkout@v4`.
    2. `Log in to the registry`: `docker/login-action@v3`.
    3. `Push the image`: builds and pushes `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`.
- Job `deploy`:
  - Needs `image`.
  - Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
  - Passes `environment: production`; inherits secrets.

#### Configuration
- `name=release-backoffice`.
- `permissions.contents=read`.
- `permissions.packages=write`.
- `strategy.matrix.java`: `21`, `25`.
- `distribution=temurin`.
- `java-version=${{ matrix.java }}`.
- `registry=registry.shop.example`.
- `username=${{ secrets.REGISTRY_USER }}`.
- `password=${{ secrets.REGISTRY_TOKEN }}`.
- `environment=production`.
- Image tag expression: `${GITHUB_REF_NAME#v}`.
- Deploy input `environment=production`; `secrets=inherit`.

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Build tool: Maven.
- Registry and image: `registry.shop.example/orders-backoffice`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Secrets: `secrets.REGISTRY_USER`, `secrets.REGISTRY_TOKEN`.
