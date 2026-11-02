### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:38Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> Provides a centralized, immutable mapping of numeric error codes to human‑readable messages for platform failures such as billing, storage, network, report, import, auth, and ledger lock issues.

#### Purpose
- Repository of platform error codes.
- Enables lookup by numeric value.

#### Type
`public enum PlatformErrorCode` – final enum, no generics, no annotations; extends `Enum<PlatformErrorCode>`.

#### Input
- Constructor parameters: `int number`, `String message`.
- Static block iterates over `values()` to populate `BY_NUMBER`.

#### Output
- `getNumber()` returns the numeric code.
- `getMessage()` returns the descriptive message.
- `byNumber(int)` returns the enum constant for a given number (or `null` if not found).

#### Core logic
- Immutable fields `number` and `message`.
- Static `Map<Integer, PlatformErrorCode> BY_NUMBER` populated once in a static initializer.
- `byNumber` performs a map lookup.

#### Public API
- `int getNumber() -> numeric code`
- `String getMessage() -> error description`
- `static PlatformErrorCode byNumber(int) -> enum or null`

#### Dependencies
- `java.util.Map`
- `java.util.HashMap`

#### Exceptions / Errors
- `byNumber` may return `null` if the number is not defined; callers should handle this.

#### Concurrency
- `BY_NUMBER` is populated once in a static block; read‑only thereafter, thread‑safe.
