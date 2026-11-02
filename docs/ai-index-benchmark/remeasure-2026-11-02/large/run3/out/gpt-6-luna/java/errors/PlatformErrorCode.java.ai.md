### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:05:50Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable, domain-prefixed error codes and messages for identifying billing, storage, network, reporting, import, authentication, and ledger-lock failures.

#### Purpose
- `PlatformErrorCode` maps each named platform failure to a numeric code and human-readable message.
- Supports lookup of an error-code enum value by its numeric identifier.

#### Type
- `PlatformErrorCode` — public enum.
- Enum constants cover numeric codes 1–6000; prefixes include `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, `AUTH`, and `LEDGER_LOCK`.
- Each constant carries an `int` number and `String` message.

#### Input
- Enum constant construction receives an `int number` and `String message`.
- `byNumber` receives an integer code for lookup.

#### Output
- `getNumber()` returns the numeric error code.
- `getMessage()` returns the associated message.
- `byNumber(int)` returns the matching `PlatformErrorCode`, or `null` when no number is mapped.
- Static initialization builds the private `BY_NUMBER` lookup map.

#### Core logic
- Each enum constant binds a unique number to a category-prefixed name and corresponding failure message.
- During class initialization, iterates over `values()` and indexes every constant by its `number` in `BY_NUMBER`.
- Numeric lookup retrieves the enum constant from the map.

#### Public API
- `getNumber() -> int` — return numeric error code.
- `getMessage() -> String` — return failure message.
- `byNumber(int) -> PlatformErrorCode` — find code by number.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for an unmapped number; it does not throw for missing codes.
- No explicit exception handling or validation.

#### Concurrency
- Enum constants and their fields are immutable.
- `BY_NUMBER` is a private static `HashMap`, populated during class initialization and read afterward; no synchronization is used.
