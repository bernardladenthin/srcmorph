### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:11Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized catalog of platform error codes, each mapped to a unique integer and a human‑readable message, with reverse lookup by code.

#### Purpose
- Central repository for platform error codes.
- Provides human‑readable messages for each error.

#### Responsibilities
- Define immutable error code constants.
- Enable lookup of an error code by its numeric value.

#### Key units
- **`PlatformErrorCode` enum** – holds `int code`, `String message`; exposes `getCode()`, `getMessage()`, and static `byNumber(int)` for reverse lookup.

#### Data flow
- Enum constants are instantiated with a code and message.
- A static `Map<Integer, PlatformErrorCode>` is populated once with all constants.
- `byNumber` queries this map to return the matching enum or `null`.

#### Dependencies
- `java.util.Map`, `java.util.HashMap` for the reverse‑lookup map.

#### Cross-cutting
- Immutable enum design ensures thread safety.
- No external libraries or complex error handling beyond simple null return.
