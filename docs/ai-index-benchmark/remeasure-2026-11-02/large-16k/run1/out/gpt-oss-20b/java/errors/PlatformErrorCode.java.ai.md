### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:21:41Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> Provides a central lookup for platform error codes, mapping numeric identifiers to descriptive messages.

#### Purpose
- Repository of platform error codes.
- Enables numeric-to-enum mapping.

#### Type
`public enum PlatformErrorCode`  
- Implements `values()`, `byNumber(int)` lookup.  
- Fields: `int number`, `String message`.  
- Static map `BY_NUMBER` for reverse lookup.

#### Input
- Enum constants defined with `(int number, String message)`.

#### Output
- `getNumber()` → `int`  
- `getMessage()` → `String`  
- `byNumber(int)` → `PlatformErrorCode` (or `null` if not found)

#### Core logic
- Static block iterates over `values()` to populate `BY_NUMBER` map.  
- `byNumber` retrieves enum instance by key.

#### Public API
- `getNumber() -> int` – returns numeric code.  
- `getMessage() -> String` – returns error description.  
- `byNumber(int) -> PlatformErrorCode` – lookup by numeric code.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`

#### Exceptions / Errors
- No checked exceptions; `byNumber` returns `null` if code absent.

#### Concurrency
- Static initialization ensures thread‑safe population of `BY_NUMBER`.
