### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:37:06Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A central catalogue of platform error codes, mapping numeric identifiers to descriptive messages and enabling lookup by number.

#### Purpose
- Centralises all platform error codes for billing, storage, network, report, import, auth, and ledger‑lock failures.
- Provides numeric code and human‑readable message for each error.

#### Type
`public enum PlatformErrorCode` – enum, public, final, implements `java.lang.Enum`.  
Extends `Enum<PlatformErrorCode>`.  
Contains a static `Map<Integer, PlatformErrorCode> BY_NUMBER` and a constructor `PlatformErrorCode(int, String)`.

#### Input
- Enum constants defined with an integer code and a message string.
- Static block populates `BY_NUMBER` from `values()`.

#### Output
- `int getNumber()` – error numeric code.
- `String getMessage()` – error description.
- `PlatformErrorCode byNumber(int)` – lookup by numeric code, returns `null` if not found.
- Inherited `values()` and `valueOf(String)` for enum constants.

#### Core logic
- Each enum constant is instantiated with a code and message.
- Static initializer iterates over `values()`, inserting each constant into `BY_NUMBER`.
- `byNumber` performs a map lookup to retrieve the constant.

#### Public API
- `getNumber() -> int` – returns error code number.  
- `getMessage() -> String` – returns error message.  
- `byNumber(int) -> PlatformErrorCode` – lookup by numeric code.  
- `values() -> PlatformErrorCode[]` – array of all error codes.  
- `valueOf(String) -> PlatformErrorCode` – enum constant by name.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum` (implicit)

#### Exceptions / Errors
- `valueOf(String)` throws `IllegalArgumentException` if name not found.
- `byNumber(int)` may return `null` if code not found.

#### Concurrency
- Immutable enum; `BY_NUMBER` is populated once in a static block and never modified thereafter, making reads thread‑safe.
