### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:19:28Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds and publishes the Orders Backoffice container image, then deploys it to the production Kubernetes environment.

#### Purpose
- Release workflow for building and testing the application, publishing its container image, and invoking the production deployment workflow.

#### Type
- GitHub Actions workflow named `release-backoffice`.

#### Definitions
- Triggers: pushes of tags matching `v*`; manual `workflow_dispatch`.
- Workflow permissions: `contents: read`, `packages: write`.
- Job `build`:
  - Runner: `ubuntu-24.04`; matrix `java`: `21`, `25`.
  - Steps: `actions/checkout@v4`; `actions/setup-java@v4` with `distribution: temurin` and `java-version: ${{ matrix.java }}`; runs `mvn -B verify -Pci` (`Build and test`).
- Job `image`:
  - Needs: `build`; runner: `ubuntu-24.04`; environment: `production`.
  - Steps: `actions/checkout@v4`; `docker/login-action@v3` logs in to `registry.shop.example`; builds and pushes `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`.
- Job `deploy`:
  - Needs: `image`; calls `./.github/workflows/deploy-k8s.yml` with `environment: production`; inherits secrets.

#### Configuration
- `name`: `release-backoffice`
- `on.push.tags`: `v*`
- `workflow_dispatch`: enabled
- `permissions.contents`: `read`
- `permissions.packages`: `write`
- `build.strategy.matrix.java`: `21`, `25`
- `actions/setup-java` `distribution`: `temurin`
- `actions/setup-java` `java-version`: `${{ matrix.java }}`
- Registry login `registry`: `registry.shop.example`
- Registry login `username`: `${{ secrets.REGISTRY_USER }}`
- Registry login `password`: `${{ secrets.REGISTRY_TOKEN }}`
- Image tag expression: `${GITHUB_REF_NAME#v}`

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Build tool and profile: Maven, `-Pci`.
- Registry and image: `registry.shop.example`, `registry.shop.example/orders-backoffice`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Secrets: `secrets.REGISTRY_USER`, `secrets.REGISTRY_TOKEN`.
