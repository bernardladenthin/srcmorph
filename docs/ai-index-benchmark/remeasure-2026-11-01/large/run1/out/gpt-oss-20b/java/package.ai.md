### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:37:44Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> A centralized catalogue of platform‑wide error codes used across billing, storage, network, report, import, auth, and ledger‑lock modules.

#### Purpose
- Provide a single source of truth for numeric error codes and their human‑readable messages.

#### Responsibilities
- Define immutable error code constants.
- Offer fast lookup of an error code by its numeric ID.
- Expose getters for code number and description.

#### Key units
- **`PlatformErrorCode` enum** – holds constants each with `int number` and `String message`; supplies `getNumber()`, `getMessage()`, `getCode()`, `getDescription()`, and static `byNumber(int)` for reverse lookup.

#### Data flow
- At class load, a static `HashMap<Integer, PlatformErrorCode>` is populated mapping each numeric code to its enum instance.  
- Clients call `PlatformErrorCode.byNumber(code)` to retrieve the enum, then use `getMessage()` or `getNumber()` as needed.

#### Dependencies
- `java.util.Map` and `java.util.HashMap` for the lookup map.

#### Cross-cutting
- Immutable enum instances guarantee thread safety.  
- No external libraries or configuration; all data is compile‑time constants.
