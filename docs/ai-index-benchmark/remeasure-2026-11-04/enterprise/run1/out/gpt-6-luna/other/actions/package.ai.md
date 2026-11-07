### other/actions
- H: 1.0
- C: EACCDA73
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:08:37Z
- G: 
- A: 0.0.0
- X: package
- F: [.github/](.github/package.ai.md)
---
> Automates verification, container-image release, and production deployment of the orders backoffice.

#### Purpose
- Coordinate release checks and production delivery of the orders backoffice.

#### Responsibilities
- **Release automation:** Route workflow stages for building, publishing version-tagged images, and deploying to production.

#### Key units
- `workflows/` — Contains the release workflow and its build, image-publication, and deployment stages.
