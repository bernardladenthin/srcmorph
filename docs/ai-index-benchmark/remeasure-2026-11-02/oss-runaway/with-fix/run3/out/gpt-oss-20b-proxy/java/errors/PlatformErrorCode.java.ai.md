### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:35:27Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A central catalogue that maps thousands of platform error codes to numeric IDs and human‑readable messages, enabling reverse lookup by code.

#### Purpose
- Store platform error codes with unique numeric IDs and descriptions.
- Provide lookup of an enum instance by its numeric code.

#### Type
- `public enum PlatformErrorCode`  
  - No generics, no annotations.  
  - Holds `int number`, `String message`.  
  - Static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup.

#### Input
- Constructor parameters: `int number`, `String message`.  
- Static block populates `BY_NUMBER` with all enum values.

#### Output
- `getNumber()` → `int` error code.  
- `getMessage()` → `String` description.  
- `byNumber(int)` → `PlatformErrorCode` or `null` if not found.

#### Core logic
- Enumerate 6000+ error codes as enum constants.  
- In static initializer, iterate `values()` and map each `number` to its enum instance.  
- `byNumber` performs a map lookup.

#### Public API
- `int getNumber() -> return error code`  
- `String getMessage() -> return description`  
- `static PlatformErrorCode byNumber(int) -> lookup by code`

#### Dependencies
- `java.util.Map`, `java.util.HashMap`, `java.util.Enum`

#### Exceptions / Errors
- No checked exceptions; `byNumber` returns `null` if code absent.

#### Concurrency
- Immutable enum instances; static map is populated once at class load, thread‑safe.
