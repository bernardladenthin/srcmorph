### other/actions/.github/workflows
- H: 1.0
- C: 5978594B
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:42:11Z
- G: 
- A: 0.0.0
- X: package
- F: [release.yml](release.yml.ai.md)
---
> Automates the continuous integration, containerization, and Kubernetes deployment of the Backoffice service.

#### Purpose
- Orchestrate CI/CD for the Backoffice Java application.
- Build, test, containerize, and push artifacts to a registry, then trigger a Kubernetes rollout.

#### Responsibilities
- **Build & Test**: Compile Java code, run unit and integration tests across multiple Java versions.
- **Containerization**: Build a Docker image from the built artifacts and publish it to a private registry.
- **Deployment**: Invoke a reusable Kubernetes deployment workflow to roll out the new image to production.

#### Key units
- **`release-backoffice` workflow** (`.github/workflows/release.yml`): defines triggers, jobs, and permissions for the entire release pipeline.
- **`build` job**: runs Maven (`mvn -B verify -Pci`) on Java 21 and 25, ensuring compatibility.
- **`image` job**: logs into `registry.shop.example`, tags the image as `orders-backoffice:<tag>`, and pushes it.
- **`deploy` job**: calls the external `deploy-k8s.yml` workflow, passing the production environment and inheriting secrets.

#### Data flow
1. **Trigger**: Tag push (`v*`) or manual dispatch starts the workflow.
2. **Build** → **Test**: Maven verifies the project.
3. **Image**: Docker image is built from the Maven output and pushed to the registry.
4. **Deploy**: The image is deployed to Kubernetes via the reusable workflow.

#### Dependencies
- GitHub Actions: `checkout@v4`, `setup-java@v4`, `docker/login-action@v3`.
- Maven profiles: `ci`.
- Docker registry: `registry.shop.example`.
- External workflow: `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- **Matrix strategy**: tests against Java 21 and 25.
- **Secrets**: `REGISTRY_USER`, `REGISTRY_TOKEN` for registry authentication.
- **Permissions**: read contents, write packages.
- **Environment**: `production` used for image tagging and deployment.
