### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:15:02Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Automates validation, publication, and production deployment handoff for versioned Orders Backoffice container images.

#### Purpose
- Validate release candidates across Java versions, publish a version-tagged image, and trigger Kubernetes deployment.

#### Responsibilities
- **Release validation:** Run Maven CI verification on Java 21 and 25.
- **Image publishing:** Build and push the Orders Backoffice image to `registry.shop.example`.
- **Deployment handoff:** Invoke the reusable Kubernetes deployment workflow after publishing.

#### Key units
- `release-backoffice` — workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — runs `mvn -B verify -Pci` across the Java-version matrix.
- `image` — publishes `registry.shop.example/orders-backoffice` using the version without its `v` prefix.
- `deploy` — calls `./.github/workflows/deploy-k8s.yml` for the `production` environment.

#### Data flow
- A tag push or manual dispatch starts the workflow; successful matrix verification precedes image publishing.
- The image tag is derived from `GITHUB_REF_NAME`; publishing is followed by the Kubernetes deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, `docker/login-action@v3`.
- Maven, Temurin Java, and registry credentials `REGISTRY_USER` and `REGISTRY_TOKEN`.
- Container registry `registry.shop.example/orders-backoffice`; reusable workflow `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Workflow permissions are limited to `contents: read` and `packages: write`.
- Image publishing and deployment use the `production` environment.
