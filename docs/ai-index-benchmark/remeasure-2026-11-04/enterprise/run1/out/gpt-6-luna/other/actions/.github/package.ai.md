### other/actions/.github
- H: 1.0
- C: E39F967D
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:08:34Z
- G: 
- A: 0.0.0
- X: package
- F: [workflows/](workflows/package.ai.md)
---
> Automates verification, container release, and production deployment of the orders backoffice.

#### Purpose
- Coordinate release checks, version-tagged image publishing, and production deployment.

#### Responsibilities
- **Release workflow routing:** Expose the `workflows/` automation that builds, publishes, and deploys the backoffice.

#### Key units
- `workflows/` — Contains the release workflow and its build, image-publication, and deployment stages.

#### Data flow
- Release workflow details and execution flow are summarized under `workflows/`.
