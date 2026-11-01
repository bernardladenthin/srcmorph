### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:02Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Provides stable numeric identifiers and human-readable messages for platform billing, storage, network, reporting, import, authentication, and ledger-lock errors.

#### Purpose
- Catalogues platform errors as named values with numeric codes and display messages.
- Supports lookup of an error by numeric code.

#### Responsibilities
- Defines error categories and assigns each error a stable number and message.
- Builds a numeric-code index for efficient lookup.

#### Key units
- `PlatformErrorCode` — enum of 6,000 platform errors, exposing each value’s number and message.
- `PlatformErrorCode.byNumber(int)` — returns the matching enum value or `null` when no code matches.
- `PlatformErrorCode.BY_NUMBER` — static map from numeric codes to enum values.

#### Data flow
- Enum constants are initialized with a number and message; static initialization indexes them in `BY_NUMBER`.
- `byNumber(int)` queries that index and returns the corresponding error value, if present.

#### Dependencies
- Uses `java.util.HashMap` and `java.util.Map` for the code-to-enum index.

#### Cross-cutting
- Enum values and their number/message fields are immutable; `BY_NUMBER` is populated during class initialization and not modified afterward.
- Codes use `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH` prefixes, with a `LEDGER_LOCK` range at codes 2701–3300.
