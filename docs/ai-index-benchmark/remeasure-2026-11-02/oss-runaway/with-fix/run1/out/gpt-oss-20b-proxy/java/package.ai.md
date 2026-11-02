### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:12Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> A centralized catalog of platform error codes with immutable constants and reverse‑lookup support.

#### Purpose
- Store platform error codes and their human‑readable messages.
- Provide a lookup mechanism by numeric code.

#### Responsibilities
- Define immutable error code constants.
- Enable reverse lookup of an error code by its integer value.

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
