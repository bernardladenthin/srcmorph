### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:40Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> Provides a central, immutable catalogue of platform error codes with numeric identifiers and human‑readable messages, and a fast lookup by code.

#### Purpose
- Central repository for platform error codes and messages.

#### Type
`public enum PlatformErrorCode` – public enum, no modifiers, implements `java.lang.Enum`.

#### Input
- Enum constants defined with an `int number` and a `String message`.
- Static initializer populates a `Map<Integer, PlatformErrorCode>`.

#### Output
- `getNumber()` → numeric code.
- `getMessage()` → descriptive message.
- `byNumber(int)` → enum constant for a given code (or `null`).

#### Core logic
- Static block iterates over `values()` to fill `BY_NUMBER` map.
- `byNumber` performs a map lookup to return the matching enum constant.

#### Public API
- `int getNumber() -> numeric code`  
- `String getMessage() -> error description`  
- `static PlatformErrorCode byNumber(int) -> lookup by code`  
- `static PlatformErrorCode[] values() -> all constants`  
- `static PlatformErrorCode valueOf(String) -> constant by name`

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- No declared exceptions; `byNumber` may return `null` if code not found.

#### Concurrency
- Static map initialized once during class loading; read‑only thereafter – thread‑safe.
