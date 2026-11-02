### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:32:15Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and messages for billing, storage, network, reporting, import, authentication, and ledger-lock failures.

#### Purpose
- `PlatformErrorCode` is a generated catalogue mapping named platform errors to numeric codes and display messages.
- Supports lookup of an error code by its numeric identifier.

#### Type
- `PlatformErrorCode` — enum; package `example.errors`.
- Declares 6,000 error constants, each initialized with an `int` number and `String` message.

#### Input
- Enum constants receive a numeric code and message at initialization.
- `byNumber(int number)` accepts a numeric code for lookup.

#### Output
- Each enum value exposes its number and message.
- `byNumber` returns the matching `PlatformErrorCode`, or `null` if absent.
- Static `BY_NUMBER` map stores code-to-enum mappings.

#### Core logic
- Codes 1–2700 use repeating `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH` prefixes.
- Codes 2701–3300 use the `LEDGER_LOCK` prefix.
- Codes 3301–6000 resume the six-prefix sequence.
- Static initialization iterates over `values()` and inserts each enum constant into `BY_NUMBER` keyed by its number.
- Each enum constant retains its constructor-provided number and message.

#### Public API
- `getNumber()` -> `int` — returns numeric code
- `getMessage()` -> `String` — returns error message
- `byNumber(int)` -> `PlatformErrorCode` — looks up numeric code

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for unknown numbers; no explicit exception handling or validation.

#### Concurrency
- Enum constants and their `number` and `message` fields are immutable.
- `BY_NUMBER` is populated during class initialization and not modified afterward.
