### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:15:29Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and messages for billing, storage, network, reporting, import, and authentication failures.

#### Purpose
- `PlatformErrorCode` is a generated catalogue mapping each platform error identifier to a numeric code and message.
- Supports lookup of an error enum value by numeric code.

#### Type
- `PlatformErrorCode` — enum; package `example.errors`.
- Contains 6,000 constants, `BILLING_00001` through `AUTH_06000`; category prefixes cycle through BILLING, STORAGE, NETWORK, REPORT, IMPORT, and AUTH.

#### Input
- Each enum constant is initialized with an `int number` and a `String message`.
- `byNumber(int number)` accepts a numeric error code.

#### Output
- Each constant exposes its number and message through accessors.
- `byNumber` returns the matching `PlatformErrorCode`, or `null` when no number matches.
- Builds a static `Map<Integer, PlatformErrorCode>` index from all enum constants.

#### Core logic
- Enum constants associate sequential numbers 1–6000 with category-prefixed identifiers and messages such as `"billing failure 1"`.
- A static initializer iterates over `values()` and populates `BY_NUMBER` using each constant’s `number`.
- `byNumber` retrieves the enum value from `BY_NUMBER`.

#### Public API
- `getNumber()` -> `int` — numeric error code.
- `getMessage()` -> `String` — error description.
- `byNumber(int number)` -> `PlatformErrorCode` — look up numeric code.
- `BILLING_00001..AUTH_06000` (6,000 total) — error catalogue constants.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` for unknown numbers; no explicit exceptions are thrown or caught.

#### Concurrency
- Enum constants and their fields are immutable.
- `BY_NUMBER` is a mutable `HashMap` populated during class initialization; the source performs no subsequent mutations.
