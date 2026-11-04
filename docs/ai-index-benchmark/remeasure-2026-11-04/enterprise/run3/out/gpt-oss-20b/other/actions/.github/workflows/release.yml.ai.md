### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:22:51Z
- G: 
- A: 0.0.0
- X: file
- P: 7217E4BA
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds, tests, containerizes, and deploys the Backoffice service to production Kubernetes.

#### Purpose
- Build and test Java application.
- Create and push Docker image.
- Trigger Kubernetes deployment.

#### Type
- GitHub Actions workflow (`release.yml`), API v4.

#### Definitions
- **Triggers**: `push` on tags matching `v*`; `workflow_dispatch`.
- **Jobs**:
  - `build`: runs on `ubuntu-24.04`, matrix `java: [21, 25]`.
    - Steps: `actions/checkout@v4`, `actions/setup-java@v4` (distribution `temurin`, `java-version` from matrix), `Build and test` (`mvn -B verify -Pci`).
  - `image`: depends on `build`, runs on `ubuntu-24.04`, environment `production`.
    - Steps: `actions/checkout@v4`, `docker/login-action@v3` (registry `registry.shop.example`, credentials from secrets), `Push the image` (`docker build` and `docker push` with tag `${GITHUB_REF_NAME#v}`).
  - `deploy`: depends on `image`, uses reusable workflow `./.github/workflows/deploy-k8s.yml` with `environment: production`, inherits secrets.

#### Configuration
- `java-version`: `${{ matrix.java }}`.
- Docker image tag: `${GITHUB_REF_NAME#v}`.
- Registry credentials: `REGISTRY_USER`, `REGISTRY_TOKEN` from secrets.

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Docker registry: `registry.shop.example`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
