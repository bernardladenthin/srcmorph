### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T23:14:08Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
#### Key units
- `catalog/` (contains the core reporting logic)
- `catalog/` (generates PDF exports)

#### Data flow
- The `catalog/` package processes data from the `catalog/` package to generate reports
- The `catalog/` package then generates PDF exports of the generated reports
