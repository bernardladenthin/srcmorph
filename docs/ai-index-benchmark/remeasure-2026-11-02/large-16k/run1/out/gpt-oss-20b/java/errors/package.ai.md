### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:28:32Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Centralizes platform error codes, providing numeric-to-enum mapping and descriptive messages for consistent error handling across the system.

#### Purpose
- Store and expose platform error codes as an enum.
- Enable lookup of error codes by numeric identifier.

#### Responsibilities
- **Error code definition** – declare constants with numeric ID and message.
- **Lookup service** – map numeric IDs to enum instances.
- **API exposure** – expose getters for number and message.

#### Key units
- **`PlatformErrorCode` enum** – holds constants, `number`, `message`, static `BY_NUMBER` map, `byNumber(int)` lookup, `getNumber()`, `getMessage()`.

#### Data flow
1. **Definition**: constants initialized with `(int number, String message)`.
2. **Static init**: populate `BY_NUMBER` map from `values()`.
3. **Lookup**: callers invoke `PlatformErrorCode.byNumber(id)` to retrieve enum.
4. **Access**: callers use `getNumber()` / `getMessage()` for details.

#### Dependencies
- Java standard collections: `java.util.Map`, `java.util.HashMap`.

#### Cross-cutting
- Thread‑safe static initialization guarantees safe concurrent access.
- No external libraries or configuration; purely in‑memory mapping.
