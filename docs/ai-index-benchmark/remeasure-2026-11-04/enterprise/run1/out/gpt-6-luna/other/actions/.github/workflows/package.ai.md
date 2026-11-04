### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:08:29Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates verification, container publishing, and production deployment of the orders backoffice for releases.

#### Purpose
- Build and test the orders backoffice, publish a version-tagged container image, and trigger its production Kubernetes deployment.

#### Responsibilities
- **Release verification:** Run Maven `verify -Pci` on Java 21 and 25 using a build matrix.
- **Image publication:** Authenticate to the container registry and push `registry.shop.example/orders-backoffice` with the version tag.
- **Deployment handoff:** Invoke the reusable Kubernetes deployment workflow for the production environment.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — Checks out the repository and runs CI verification across the Java matrix.
- `image` — Depends on `build`; builds and pushes the version-tagged backoffice image.
- `deploy` — Depends on `image`; calls `./.github/workflows/deploy-k8s.yml` with `environment: production`.

#### Data flow
- A version-tag push or manual dispatch starts the workflow.
- `build` verifies the application on Java 21 and 25; successful completion gates `image`.
- `image` logs in with registry secrets and pushes the image tagged from `GITHUB_REF_NAME` with the leading `v` removed.
- Successful publication gates `deploy`, which passes the production environment and inherits secrets.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Build tool and runtime distribution: Maven, Temurin.
- Container registry: `registry.shop.example`; image `registry.shop.example/orders-backoffice`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.
- Secrets: `REGISTRY_USER`, `REGISTRY_TOKEN`.

#### Cross-cutting
- Build, image publication, and deployment are ordered through job dependencies.
- Build verification uses a Java-version matrix; registry publishing uses the `production` environment.
- Workflow permissions are limited to `contents: read` and `packages: write`.
