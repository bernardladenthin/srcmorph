### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:36:34Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Provides stable numeric error codes and messages for billing, storage, network, reporting, import, and authentication failures.

#### Purpose
- `PlatformErrorCode` is the generated catalogue of platform error identifiers, numeric codes, and message text.
- Supports lookup of an error identifier by its numeric code.

#### Type
- `PlatformErrorCode` — public enum.
- 6,000 enum constants, from `BILLING_00001` through `AUTH_06000`; names cycle through `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH`.

#### Input
- Each enum constant is initialized with an `int number` and a `String message`.
- `byNumber(int number)` accepts a numeric error code.

#### Output
- Each enum constant stores its number and message.
- Static `BY_NUMBER` map provides numeric-code-to-enum lookup.

#### Core logic
- Enum constants pair sequential numbers 1–6000 with category-prefixed names and messages such as `"billing failure 1"`.
- A static initializer iterates over `values()` and populates `BY_NUMBER` using each constant’s `number`.
- `byNumber` returns the mapped `PlatformErrorCode` for a supplied number.

#### Public API
- `getNumber()` -> `int` — return numeric error code.
- `getMessage()` -> `String` — return error message.
- `byNumber(int)` -> `PlatformErrorCode` — look up code by number.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Exceptions / Errors
- `byNumber` returns `null` when the number is not present; it does not throw an explicit exception.

#### Concurrency
- Enum constants and their fields are immutable.
- `BY_NUMBER` is a mutable `HashMap` populated during class initialization; no later mutation is shown.
