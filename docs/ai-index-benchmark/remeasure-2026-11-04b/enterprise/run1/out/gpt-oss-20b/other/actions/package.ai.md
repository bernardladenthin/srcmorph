### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:27:03Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates the CI/CD pipeline for the Orders Backoffice application, building Java artifacts, containerizing them, and deploying to a production Kubernetes cluster on tag pushes.

#### Purpose
- Automate release workflow for Orders Backoffice.
- Build, test, containerize, and deploy on tag push.

#### Responsibilities
- **CI**: compile and test Java code across multiple JDK versions.
- **Containerization**: build Docker image and push to registry.
- **Deployment**: trigger Kubernetes rollout via reusable workflow.

#### Key units
- **Workflow `release-backoffice`**: orchestrates build, image, and deploy jobs.
- **Job `build`**: runs Maven tests on JDK 21 and 25.
- **Job `image`**: logs into `registry.shop.example`, builds image, pushes tag derived from Git tag.
- **Job `deploy`**: calls external `.github/workflows/deploy-k8s.yml` with production environment.

#### Data flow
`push` on a tag → `build` job → `image` job (needs `build`) → `deploy` job (needs `image`) → Kubernetes cluster update.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- External workflow `.github/workflows/deploy-k8s.yml`.
- Secrets: `REGISTRY_USER`, `REGISTRY_TOKEN`.

#### Cross-cutting
- Matrix strategy for JDK versions.
- Single environment (`production`) used across image and deploy jobs.
- Secret handling for Docker registry authentication.
