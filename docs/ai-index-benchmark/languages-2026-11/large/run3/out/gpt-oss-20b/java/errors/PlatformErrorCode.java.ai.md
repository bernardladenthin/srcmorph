### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:26:26Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> A central catalogue of platform error codes with numeric IDs and human‑readable messages, supporting lookup by code.

#### Purpose
- Repository of error codes for billing, storage, network, report, import, and authentication failures.

#### Type
- `public enum PlatformErrorCode` – public enum, final, implements `java.lang.Enum<PlatformErrorCode>`.  
  - Fields: `private final int number; private final String message; private static final Map<Integer, PlatformErrorCode> BY_NUMBER;`  
  - Constructor: `PlatformErrorCode(int number, String message)`.

#### Input
- Enum constants defined with an integer `number` and a `String message`.  
- Static initializer populates `BY_NUMBER` map.

#### Output
- `getNumber()` returns the numeric code.  
- `getMessage()` returns the descriptive message.  
- `byNumber(int)` returns the enum constant for a given number (or `null` if not found).

#### Core logic
- Each constant holds a unique error number and message.  
- Static block iterates over `values()` to fill `BY_NUMBER`.  
- `byNumber` performs a map lookup for efficient retrieval.

#### Public API
- `int getNumber() -> numeric error code`  
- `String getMessage() -> error description`  
- `static PlatformErrorCode byNumber(int) -> enum constant or null`

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- `byNumber` returns `null` if the number is not present; no exceptions thrown.

#### Concurrency
- `BY_NUMBER` is a `HashMap` populated once in a static block; read‑only thereafter, thread‑safe for concurrent reads.
