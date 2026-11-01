### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:13:20Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A central catalogue of platform error codes, each with a numeric ID and a descriptive message.

#### Purpose
- Provides a consistent error code reference for the platform.
- Enables lookup of error codes by number.

#### Type
`public enum PlatformErrorCode` – enum, public, no modifiers.

#### Input
- None (constants defined statically).

#### Output
- `int code` – numeric identifier.
- `String message` – human‑readable description.

#### Core logic
- Each enum constant is instantiated with a unique integer and a message string.
- A static `Map<Integer, PlatformErrorCode> BY_NUMBER` maps numbers to constants.
- `byNumber(int)` performs a map lookup, returning the enum or `null`.

#### Public API
- `int getNumber() -> numeric code`  
- `String getMessage() -> error description`  
- `static PlatformErrorCode byNumber(int) -> enum instance or null`

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- `byNumber` may return `null` for unknown codes.

#### Concurrency
- Map is populated once in a static initializer; read‑only thereafter, thread‑safe.
