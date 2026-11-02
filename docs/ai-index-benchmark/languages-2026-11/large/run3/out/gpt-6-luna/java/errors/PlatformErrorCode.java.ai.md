### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:24:09Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and messages for billing, storage, network, reporting, import, and authentication failures.

#### Purpose
- `PlatformErrorCode` catalogues errors as enum values, each with a numeric code and message.
- Supports looking up an error enum by numeric code.

#### Type
- `PlatformErrorCode` — public enum; package `example.errors`.
- 6,000 constants, numbered 1–6000; category prefixes cycle through `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH`.

#### Input
- Enum constants are initialized with an `int number` and `String message`.
- `byNumber(int number)` accepts a numeric error code.

#### Output
- Each constant exposes its numeric code and message.
- `byNumber` returns the matching `PlatformErrorCode`, or `null` if absent.
- Static initialization populates a private number-to-enum map.

#### Core logic
- Each enum constant stores its supplied number and message.
- During class initialization, iterates over `values()` and indexes each constant in `BY_NUMBER` by its number.
- `byNumber` retrieves the corresponding constant from `BY_NUMBER`.

#### Public API
- `getNumber() -> int` — returns numeric error code.
- `getMessage() -> String` — returns error message.
- `byNumber(int number) -> PlatformErrorCode` — looks up error by number.
- Enum constants: 6,000 total, indices 00001–06000.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for an unrecognized number; no explicit exceptions or null checks.

#### Concurrency
- Enum constants and their fields are immutable.
- `BY_NUMBER` is a mutable `HashMap` populated during static initialization and only read afterward.
