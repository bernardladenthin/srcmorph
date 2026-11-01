### java/errors
- H: 1.0
- C: BBE675B9
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:23:09Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A comprehensive catalogue of platform error codes, each mapped to a unique numeric identifier and a human‑readable message, with efficient lookup by code number.

#### Purpose
- Centralised error catalogue for the platform.
- Provides unique identifiers and descriptions for all platform errors.

#### Responsibilities
- Define and store all platform error codes.
- Offer reverse lookup from numeric code to enum constant.
- Expose error code number and message to callers.

#### Key units
- **`PlatformErrorCode` enum** – holds ~4,200 constants (`<CATEGORY>_<NNNNN>(int, String)`), a static `Map<Integer, PlatformErrorCode> BY_NUMBER`, and methods `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
1. Enum constants are instantiated with a numeric code and message.  
2. Static initializer populates `BY_NUMBER` mapping each code to its constant.  
3. Callers use `byNumber(code)` to retrieve the enum, then `getNumber()` or `getMessage()` for details.

#### Dependencies
- Java collections: `Map`, `HashMap`.
- Standard `Enum` functionality.

#### Cross-cutting
- Thread‑safe immutable map after static initialization.  
- No external libraries; purely Java SE.  
- Error handling: `byNumber` returns `null` for unknown codes, requiring caller checks.
