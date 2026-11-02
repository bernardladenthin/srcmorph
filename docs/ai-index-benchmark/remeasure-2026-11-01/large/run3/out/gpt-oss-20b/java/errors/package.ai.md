### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:16:10Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A central catalogue of platform error codes that maps numeric identifiers to descriptive messages for consistent error handling across the system.

#### Purpose
- Provide a unified source of error codes for the platform.
- Enable lookup of error codes by numeric ID.

#### Responsibilities
- Define a set of error code constants with IDs and messages.
- Offer a lookup mechanism (`byNumber`) to retrieve an enum instance from its numeric code.

#### Key units
- **`PlatformErrorCode` enum** – holds error constants, each with an `int code` and `String message`.  
- **`BY_NUMBER` map** – static `Map<Integer, PlatformErrorCode>` for reverse lookup.  
- **`byNumber(int)`** – static method returning the enum instance or `null` if the code is unknown.

#### Data flow
1. On class load, the static initializer populates `BY_NUMBER` with all enum constants.  
2. When an error occurs, code calls `PlatformErrorCode.byNumber(code)` to obtain the corresponding enum.  
3. The caller retrieves the numeric code via `getNumber()` and the message via `getMessage()` for logging or response.

#### Dependencies
- Java standard library: `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.

#### Cross-cutting
- Thread‑safe read‑only map after static initialization.  
- Consistent error code handling across the platform.
