### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:50:06Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides a numbered catalogue of platform errors with category labels and messages for reporting and lookup.

#### Purpose
- `PlatformErrorCode` defines stable integer error identifiers and human-readable messages across billing, storage, network, reporting, import, authentication, and ledger-lock domains.
- Supports retrieving an error code by its numeric identifier.

#### Type
- `PlatformErrorCode` — public enum (implicitly final); package `example.errors`.

#### Input
- Each enum constant is initialized with an `int number` and a `String message`.
- `byNumber(int number)` accepts a numeric error identifier.

#### Output
- Enum constants expose their number and message through accessors.
- `byNumber` returns the matching `PlatformErrorCode`, or `null` when absent.
- Static initialization builds the number-to-code lookup map.

#### Core logic
- Defines 6,000 enum constants numbered consecutively from 1 through 6,000.
- Constants 1–2,700 and 3,301–6,000 cycle through `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH`; 2,701–3,300 are `LEDGER_LOCK`.
- During class initialization, iterates over `values()` and indexes each constant in `BY_NUMBER` by its numeric field.
- `byNumber` retrieves the indexed constant from `BY_NUMBER`.

#### Public API
- `getNumber() -> int` — return numeric error identifier
- `getMessage() -> String` — return error message
- `byNumber(int) -> PlatformErrorCode` — find code by number

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for an unknown number; no explicit exceptions or null checks.

#### Concurrency
- Enum constants and their fields are immutable.
- `BY_NUMBER` is a mutable `HashMap`, populated during static initialization and only read by the declared API.
