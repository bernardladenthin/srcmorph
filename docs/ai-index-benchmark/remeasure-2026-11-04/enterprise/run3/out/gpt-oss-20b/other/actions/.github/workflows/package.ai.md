### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:24:04Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates building, testing, containerizing, and deploying a Java Backoffice service to a production Kubernetes cluster via GitHub Actions.

#### Purpose
- Continuous integration and delivery pipeline for the Backoffice service.

#### Responsibilities
- **Build & Test**: Compile Java code and run unit tests across multiple Java versions.
- **Containerization**: Build Docker images and push them to a private registry.
- **Deployment**: Trigger a Kubernetes deployment workflow after image push.

#### Key units
- **`release.yml`** – GitHub Actions workflow orchestrating build, image, and deploy jobs.
- **`build` job** – Runs Maven (`mvn -B verify -Pci`) on `ubuntu-24.04` with Java 21 and 25 matrices.
- **`image` job** – Builds Docker image, tags with release version, and pushes to `registry.shop.example`.
- **`deploy` job** – Calls reusable workflow `deploy-k8s.yml` to deploy to the `production` environment.

#### Data flow
`push` to a tag → `build` → `image` → `deploy` → Kubernetes cluster.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Docker registry: `registry.shop.example` (credentials from `REGISTRY_USER` and `REGISTRY_TOKEN` secrets).
- Reusable workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Matrix strategy for Java versions.
- Secret handling for registry credentials.
- Environment‑specific configuration (`production`).
- Permission block to restrict actions to required scopes.
