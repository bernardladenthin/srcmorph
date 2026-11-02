### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:34:32Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Centralizes platform error codes, mapping numeric identifiers to descriptive messages for consistent error handling across the system.

#### Purpose
- Store immutable platform error codes.
- Provide lookup by numeric value.

#### Responsibilities
- Define error code enum with code/message pairs.
- Populate a read‑only map for fast reverse lookup.
- Expose getters and a static lookup method.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants each with `int number` and `String message`; static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup; methods `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
- On class load, static block iterates over `values()` to fill `BY_NUMBER`.  
- Callers request `byNumber(code)` → map lookup → return enum or `null`.

#### Dependencies
- `java.util.Map`, `java.util.HashMap`.

#### Cross-cutting
- Thread‑safe initialization; map is immutable after static block.  
- No external libraries or frameworks.
