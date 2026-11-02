### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:10Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Provides a stable numeric catalog of platform errors and their human-readable descriptions for callers to identify and interpret failures.

#### Purpose
- Defines named error codes for billing, storage, network, reporting, import, authentication, and ledger-lock failures.
- Supports resolving an error-code value from its numeric identifier.

#### Responsibilities
- Maintain the `PlatformErrorCode` catalogue of named numeric codes and corresponding messages.
- Index enum values by number for lookup; catalogued codes cover numbers 1–6000, including `LEDGER_LOCK` codes in the 2701–3300 range.

#### Key units
- `PlatformErrorCode` — enum storing each error’s integer code and message; exposes `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- Enum constants provide code numbers and message strings; static initialization indexes them in `BY_NUMBER`.
- `byNumber(int)` uses that index to return the matching enum value, or `null` if no code matches.

#### Dependencies
- `java.util.HashMap` and `java.util.Map` — hold the number-to-enum lookup index.

#### Cross-cutting
- Enum values and their fields are immutable after initialization; the private `BY_NUMBER` map is populated at class initialization and not modified afterward.
