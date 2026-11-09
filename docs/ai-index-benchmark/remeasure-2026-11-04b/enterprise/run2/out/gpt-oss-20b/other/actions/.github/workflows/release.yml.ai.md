### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:32:32Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds, tests, containerizes, and deploys the back‑office service to production Kubernetes.

#### Purpose
- Automate release of the back‑office application.
- Build Java artifacts, create Docker image, and deploy to Kubernetes.

#### Type
- GitHub Actions workflow (`.github/workflows/release.yml`), API v3.

#### Definitions
- **Workflow name**: `release-backoffice`.
- **Triggers**:  
  - `push` on tags matching `v*`.  
  - `workflow_dispatch` manual trigger.
- **Jobs**:
  - `build`: runs on `ubuntu-24.04`, matrix `java: [21, 25]`.  
    - Steps: checkout, setup Java (`temurin`, `${{ matrix.java }}`), run `mvn -B verify -Pci`.  
  - `image`: depends on `build`, runs on `ubuntu-24.04`, environment `production`.  
    - Steps: checkout, login to `registry.shop.example` with `REGISTRY_USER` and `REGISTRY_TOKEN`, build and push image `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`.  
  - `deploy`: depends on `image`, uses reusable workflow `.github/workflows/deploy-k8s.yml` with `environment: production`, inherits secrets.

#### Configuration
- `java-version`: `${{ matrix.java }}` (21, 25).
- Docker image tag: `${GITHUB_REF_NAME#v}` (tag without leading `v`).
- Registry credentials: `REGISTRY_USER`, `REGISTRY_TOKEN` from secrets.

#### Dependencies
- Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Docker registry: `registry.shop.example`.
- Reusable workflow: `.github/workflows/deploy-k8s.yml`.
