### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:24:06Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates release of the orders backoffice application from version-tagged builds through container publishing and production deployment.

#### Purpose
- Builds and verifies the application across Java 21 and 25, publishes a version-tagged container image, and deploys it to production.

#### Responsibilities
- **Build and test:** Runs `mvn -B verify -Pci` on an `ubuntu-24.04` matrix for Java 21 and 25.
- **Publish image:** Logs in to `registry.shop.example` and pushes `registry.shop.example/orders-backoffice` with the version tag derived from the Git reference.
- **Deploy:** Calls the Kubernetes deployment workflow for the `production` environment after image publication.

#### Key units
- `release-backoffice` — GitHub Actions workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — Matrix build and verification job.
- `image` — Production-environment job that builds and pushes the container image.
- `deploy` — Reusable-workflow job invoking `./.github/workflows/deploy-k8s.yml`.

#### Data flow
- A version-tag push or manual dispatch starts the workflow.
- The Java matrix build and verification must complete before the image job runs.
- The image job publishes the application image with the leading `v` removed from the Git reference name.
- The deploy job runs after publication, passes `environment: production`, and inherits secrets.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Maven with the `ci` profile.
- Registry credentials from `REGISTRY_USER` and `REGISTRY_TOKEN`.
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Workflow permissions are limited to `contents: read` and `packages: write`.
- Build and image jobs run on `ubuntu-24.04`; the image job uses the `production` environment.
