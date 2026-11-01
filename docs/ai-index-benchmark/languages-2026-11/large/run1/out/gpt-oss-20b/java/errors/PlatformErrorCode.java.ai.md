### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T01:03:12Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> A central catalogue of platform error codes, each with a numeric ID and a descriptive message.

#### Purpose
- Centralised repository for platform error codes.
- Enables lookup of error details by numeric code.

#### Type
`public enum PlatformErrorCode`  
- Extends `java.lang.Enum<PlatformErrorCode>`.  
- Fields: `private final int number; private final String message; private static final Map<Integer, PlatformErrorCode> BY_NUMBER;`.  
- Constructor: `PlatformErrorCode(int number, String message)`.  
- Static initializer populates `BY_NUMBER` with all constants.

#### Input
- Enum constants: each defined as `CATEGORY_XXXX(number, "message")`.  
- Constructor parameters: `int number`, `String message`.

#### Output
- `getNumber()` → numeric code.  
- `getMessage()` → descriptive message.  
- `byNumber(int)` → enum constant for a given number or `null`.

#### Core logic
- **Static map construction**: On class load, all enum constants are inserted into `BY_NUMBER` keyed by their numeric value.  
- **Lookup**: `byNumber` performs a constant‑time map retrieval.  
- **Accessors**: Simple getters expose the code number and message.

#### Public API
- `int getNumber() -> numeric code`  
- `String getMessage() -> error description`  
- `static PlatformErrorCode byNumber(int) -> enum constant or null`

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- No checked exceptions.  
- `byNumber` may return `null` for unknown codes.

#### Concurrency
- Enum constants are immutable.  
- Static map is populated once at class initialization, thread‑safe.
