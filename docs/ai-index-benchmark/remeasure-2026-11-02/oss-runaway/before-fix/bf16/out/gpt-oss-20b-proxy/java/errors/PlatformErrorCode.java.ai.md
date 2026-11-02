### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:06:03Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A centralized catalog of platform error codes, each with a numeric ID and human‑readable message, and a fast lookup map for retrieving an enum instance by its code.

#### Purpose
- Store all platform error codes in a single enum.
- Enable O(1) lookup of an error by its numeric identifier.

#### Type
- `public enum PlatformErrorCode`  
  - No additional modifiers.  
  - Implements `java.lang.Enum`.  
  - Private constructor `PlatformErrorCode(int, String)`.

#### Input
- Constructor arguments: `int number`, `String message`.  
- Enum constants defined with these two arguments.  
- Static block iterates over `values()` to populate the lookup map.

#### Output
- `getNumber()` → `int` error code.  
- `getMessage()` → `String` description.  
- `byNumber(int)` → `PlatformErrorCode` or `null`.

#### Core logic
- Enumerate all error codes with unique integer IDs and messages.  
- Build a static `Map<Integer, PlatformErrorCode> BY_NUMBER` in a static block for reverse lookup.  
- Provide accessor methods and a static lookup method.

#### Public API
- `PlatformErrorCode(int, String)` → constructs an enum constant.  
- `int getNumber()` → numeric code.  
- `String getMessage()` → description.  
- `PlatformErrorCode byNumber(int)` → lookup by code.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`

#### Exceptions / Errors
- No checked exceptions; `byNumber` returns `null` if the code is not present.

#### Concurrency
- Enum instances are immutable.  
- Static map is populated once at class load and then read‑only, making concurrent reads thread‑safe.
