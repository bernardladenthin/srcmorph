### release.yml
- H: 1.0
- C: 5E84EC24
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:25:51Z
- G: 
- A: 0.0.0
- X: file
- P: 34F16BF8
- N: 653C8098
---
**Facts (exact, whole file):** jobs on a runner: 2; reusable workflow calls: 1; steps (approx): 6; actions used: 4; secret references: 2; cron schedules: 0; matrices: 1; permissions blocks: 1

> Builds, tests, containerizes, and deploys the Orders Backoffice application to a production Kubernetes cluster when a new tag is pushed.

#### Purpose
- Automate release pipeline for Orders Backoffice.
- Build Java artifacts, create Docker image, push to registry, and trigger Kubernetes deployment.

#### Type
- GitHub Actions workflow  
- API version: default (GitHub Actions)

#### Definitions
- **Workflow name**: `release-backoffice`
- **Triggers**:  
  - `push` on tags matching `v*`  
  - `workflow_dispatch`
- **Jobs**:
  - `build`  
    - `runs-on`: `ubuntu-24.04`  
    - `strategy.matrix.java`: `[21, 25]`  
    - Steps: `checkout`, `setup-java` (Temurin, `${{ matrix.java }}`), `mvn -B verify -Pci`
  - `image` (needs `build`)  
    - `runs-on`: `ubuntu-24.04`  
    - `environment`: `production`  
    - Steps: `checkout`, `docker/login-action@v3` (registry `registry.shop.example`, credentials from secrets), `docker build` and `docker push` using tag `${GITHUB_REF_NAME#v}`
  - `deploy` (needs `image`)  
    - Uses external workflow `.github/workflows/deploy-k8s.yml`  
    - Passes `environment: production` and inherits secrets

#### Configuration
- `REGISTRY_USER` and `REGISTRY_TOKEN` secrets for Docker registry authentication.
- Docker image tag derived from tag name: `registry.shop.example/orders-backoffice:${GITHUB_REF_NAME#v}`.

#### Dependencies
- `actions/checkout@v4`
- `actions/setup-java@v4` (Temurin distribution)
- `docker/login-action@v3`
- External workflow `.github/workflows/deploy-k8s.yml` (Kubernetes deployment).
