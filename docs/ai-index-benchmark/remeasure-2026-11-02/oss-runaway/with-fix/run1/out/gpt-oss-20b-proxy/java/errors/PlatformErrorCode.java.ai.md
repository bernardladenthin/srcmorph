### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:24:20Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A centralized catalog of platform error codes, each mapped to a unique integer and a human‑readable message, with reverse lookup by code.

#### Purpose
- Centralized error code repository for the platform.  
- Provides human‑readable messages for each error.

#### Type
`public enum PlatformErrorCode`  
- enum with private final fields `int code` and `String message`.  
- Implements a static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup.

#### Input
- Constructor parameters: `int code`, `String message`.  
- Enum constants supply these values.

#### Output
- Each constant exposes its `code` and `message`.  
- `byNumber(int)` returns the matching enum constant or `null`.

#### Core logic
- Enum constants are instantiated with a unique integer code and descriptive string.  
- Static block populates `BY_NUMBER` with all enum values keyed by `code`.  
- `byNumber` retrieves the enum instance from the map.

#### Public API
- `int getCode()` → numeric error code.  
- `String getMessage()` → human‑readable message.  
- `PlatformErrorCode byNumber(int)` → lookup by code number.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`

#### Exceptions / Errors
- None declared; `byNumber` returns `null` if code not found.

#### Concurrency
- Immutable enum constants; static map populated once at class initialization and read‑only thereafter, thread‑safe for concurrent reads.
