### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:05:20Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized catalog of immutable platform error codes used across billing, storage, network, report, import, and authentication subsystems.

#### Purpose
- Store and expose a fixed set of error codes with numeric identifiers and descriptive messages.

#### Responsibilities
- Define error code constants.
- Provide immutable accessors for code number and message.
- Enable reverse lookup of an enum constant by its numeric code.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants, each with `int number` and `String message`; public getters `getNumber()`, `getMessage()`.
- **`BY_NUMBER` map** – static `HashMap<Integer, PlatformErrorCode>` for reverse lookup; populated in a static block.
- **`byNumber(int)`** – static method returning the enum constant for a given number or `null`.

#### Data flow
1. On class load, static block populates `BY_NUMBER` mapping each code number to its enum constant.
2. Clients call `byNumber(code)` to retrieve the corresponding enum; then use `getNumber()` or `getMessage()` as needed.

#### Dependencies
- Java standard library: `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.

#### Cross-cutting
- Thread‑safe read access due to immutable map after initialization.
- No exception handling; `byNumber` returns `null` for unknown codes.
