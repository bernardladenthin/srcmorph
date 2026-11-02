### ai
- H: 1.0
- C: 3CFB6AF5
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:28:39Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
---
> A centralized, type‑safe mapping of platform error codes to numeric identifiers and human‑readable messages for consistent error handling across the system.

#### Purpose
- Store platform error codes as an enum.
- Enable lookup of error codes by numeric ID.

#### Responsibilities
- **Error code definition** – declare constants with numeric ID and message.  
- **Lookup service** – map numeric IDs to enum instances.  
- **API exposure** – expose getters for number and message.

#### Key units
- **`PlatformErrorCode` enum** – holds constants, `number`, `message`, static `BY_NUMBER` map, `byNumber(int)` lookup, `getNumber()`, `getMessage()`.

#### Data flow
1. Constants initialized with `(int number, String message)`.  
2. Static init populates `BY_NUMBER` map from `values()`.  
3. Callers use `PlatformErrorCode.byNumber(id)` to retrieve enum.  
4. Callers access `getNumber()` / `getMessage()` for details.

#### Dependencies
- Java standard collections: `java.util.Map`, `java.util.HashMap`.

#### Cross-cutting
- Thread‑safe static initialization guarantees safe concurrent access.  
- Purely in‑memory mapping; no external libraries or configuration.
