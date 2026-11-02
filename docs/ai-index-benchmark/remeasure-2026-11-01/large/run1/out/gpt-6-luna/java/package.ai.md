### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:08Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> Provides a searchable catalogue of platform error codes and their human-readable messages across billing, storage, networking, reporting, imports, authentication, and ledger locking.

#### Purpose
- Defines stable numeric identifiers and display messages for platform errors.
- Supports lookup of an error by numeric code.

#### Responsibilities
- Groups named errors under `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH` prefixes, with a `LEDGER_LOCK` range at codes 2701–3300.
- Indexes error values by number for lookup.

#### Key units
- `PlatformErrorCode` — enum of platform errors, each with a numeric code and message.
- `PlatformErrorCode.byNumber(int)` — returns the matching enum value or `null` if no code matches.
- `PlatformErrorCode.BY_NUMBER` — static map from numeric codes to enum values.

#### Data flow
- Enum constants initialize with their numeric codes and messages; class initialization populates `BY_NUMBER`.
- `byNumber(int)` queries the index and returns the corresponding error value, if present.

#### Dependencies
- Uses `java.util.HashMap` and `java.util.Map` for the code-to-enum index.

#### Cross-cutting
- Enum values and their code/message fields are immutable; `BY_NUMBER` is populated during class initialization and not modified afterward.
