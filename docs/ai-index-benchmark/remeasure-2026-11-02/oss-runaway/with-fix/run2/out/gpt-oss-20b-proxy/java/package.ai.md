### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:34:33Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> Provides a centralized, immutable mapping of platform error codes to descriptive messages for consistent error handling across the system.

#### Purpose
- Store platform error codes as immutable constants.
- Enable fast lookup of error codes by numeric identifier.

#### Responsibilities
- Define an enum of error codes with numeric IDs and messages.
- Build a read‑only map for reverse lookup.
- Expose getters and a static lookup method.

#### Key units
- **`PlatformErrorCode` enum** – constants with `int number` and `String message`; static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup; methods `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
- Static block populates `BY_NUMBER` from `values()`.
- Callers use `byNumber(code)` to retrieve the enum or `null`.

#### Dependencies
- `java.util.Map`, `java.util.HashMap`.

#### Cross-cutting
- Thread‑safe, immutable map after initialization.
- No external libraries or frameworks.
