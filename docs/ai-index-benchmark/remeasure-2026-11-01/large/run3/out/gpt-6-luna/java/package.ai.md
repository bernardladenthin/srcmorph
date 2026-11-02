### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:12:47Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> Provides a stable numeric error-code catalogue for platform failures, with lookup from numeric codes to their corresponding error identifiers.

#### Purpose
- Centralizes platform error identifiers, numeric codes, and messages.
- Supports resolving an error identifier from a numeric code.

#### Responsibilities
- Catalogues errors across billing, storage, networking, reporting, imports, authentication, and ledger-lock failures.
- Builds a number-to-error lookup when the catalogue is initialized.

#### Key units
- `PlatformErrorCode` — enum of 6,000 error constants, each carrying a numeric code and message.
- `getNumber()` and `getMessage()` — expose an error constant’s code and message.
- `byNumber(int)` — returns the matching error constant, or `null` when no code matches.

#### Data flow
- `PlatformErrorCode` constants initialize with codes and messages; initialization indexes them in `BY_NUMBER` for numeric lookup.

#### Dependencies
- Java `HashMap` and `Map` — store the numeric-code index.

#### Cross-cutting
- Error constants and their fields are immutable; `BY_NUMBER` is populated during class initialization and not subsequently mutated.
