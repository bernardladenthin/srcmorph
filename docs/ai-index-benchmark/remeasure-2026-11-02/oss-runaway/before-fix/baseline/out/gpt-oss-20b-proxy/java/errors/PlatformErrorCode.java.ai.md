### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T07:59:54Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A central, immutable catalog of platform error codes, each with a unique numeric identifier and descriptive message, used for billing, storage, network, report, import, and authentication failures.

#### Purpose
- Repository of error codes for billing, storage, network, report, import, and auth failures.

#### Type
`public enum PlatformErrorCode` – enum, public, final, implements `java.lang.Enum`; fields: `private final int number; private final String message;` static map `BY_NUMBER` for reverse lookup.

#### Input
- Constructor parameters: `int number, String message`.

#### Output
- Immutable fields: `int number`, `String message`.
- Public getters: `int getNumber()`, `String getMessage()`.
- Static lookup: `byNumber(int)` returns the enum constant or `null`.

#### Core logic
- Each enum constant is initialized with a unique numeric code and message.
- Static block populates `BY_NUMBER` mapping each `number` to its constant.
- `byNumber` performs a map lookup for reverse resolution.

#### Public API
- `int getNumber() -> int` – retrieve numeric code.  
- `String getMessage() -> String` – retrieve message.  
- `PlatformErrorCode byNumber(int) -> PlatformErrorCode` – lookup by number.

#### Dependencies
- `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.

#### Exceptions / Errors
- `byNumber` returns `null` if number not found; no exceptions thrown.

#### Concurrency
- `BY_NUMBER` is immutable after static initialization; thread‑safe read access.
