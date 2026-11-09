### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:15:06Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Validates, publishes, and hands off versioned Orders Backoffice container releases for production deployment.

#### Purpose
- Automate release checks and production delivery of Orders Backoffice images.

#### Responsibilities
- **Release validation:** Run Maven CI verification on Java 21 and 25.
- **Image publishing and deployment handoff:** Publish the version-tagged image, then invoke the reusable Kubernetes deployment workflow.

#### Key units
- `release-backoffice` — workflow triggered by `v*` tag pushes or manual dispatch.
- `build` — runs `mvn -B verify -Pci` across the Java-version matrix.
- `image` — publishes `registry.shop.example/orders-backoffice` using the version without its `v` prefix.
- `deploy` — calls `./.github/workflows/deploy-k8s.yml` for the `production` environment.

#### Data flow
- A tag push or manual dispatch starts validation; successful matrix checks precede image publishing.
- The image tag comes from `GITHUB_REF_NAME`; publishing is followed by the Kubernetes deployment workflow.

#### Dependencies
- GitHub Actions: `actions/checkout@v4`, `actions/setup-java@v4`, and `docker/login-action@v3`.
- Maven, Temurin Java, and registry credentials `REGISTRY_USER` and `REGISTRY_TOKEN`.
- Container registry `registry.shop.example/orders-backoffice` and reusable workflow `./.github/workflows/deploy-k8s.yml`.

#### Cross-cutting
- Workflow permissions are limited to `contents: read` and `packages: write`.
- Image publishing and deployment use the `production` environment.
