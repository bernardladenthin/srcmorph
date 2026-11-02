### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:50:50Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and human-readable messages for billing, storage, network, reporting, import, authentication, and ledger-lock failures.

#### Purpose
- `PlatformErrorCode` maps each catalogued platform failure to an integer number and message.
- Supports lookup of an error-code enum value by its numeric identifier.

#### Type
- `PlatformErrorCode` — public enum; no explicit superclass or implemented interfaces.
- Each constant is initialized with an integer and message.
- Constants cover numbers 1–6000; `LEDGER_LOCK` is used for 2701–3300, with other domain prefixes appearing in the surrounding ranges.

#### Input
- Enum constants: numeric code and message string.
- `byNumber(int number)`: integer lookup key.

#### Output
- Each enum value stores its code number and message.
- `getNumber()` and `getMessage()` expose those values.
- `byNumber(int)` returns the matching `PlatformErrorCode`, or `null` if absent.
- Static initialization builds `BY_NUMBER`, mapping each code number to its enum constant.

#### Core logic
- Defines a generated catalogue of named error constants, with numeric identifiers and corresponding descriptive messages.
- On class initialization, iterates over `values()` and indexes each constant by `code.number` in `BY_NUMBER`.
- `byNumber` retrieves the mapped constant from the map.

#### Public API
- `getNumber() -> int` — return numeric error code.
- `getMessage() -> String` — return error description.
- `byNumber(int number) -> PlatformErrorCode` — find code by number.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for an unrecognized number; no explicit exception handling or validation.

#### Concurrency
- Enum constants and their fields are immutable after initialization.
- `BY_NUMBER` is a private mutable `HashMap`, populated during static initialization and not modified afterward; no synchronization is used.
