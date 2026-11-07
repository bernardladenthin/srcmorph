### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:27Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: 3A96AE1D
- N: DB22947F
- M: 2FA139DC
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized (1 part summary cut at the output budget)

> A catalog of platform error codes mapping numeric identifiers to descriptive messages for consistent error handling across the application.

#### Purpose
- Provides a comprehensive list of error codes for billing, storage, network, report, import, auth, and ledger lock failures.
- Enables consistent error handling and numeric lookup of error details.

#### Type
- `public enum PlatformErrorCode`  
  - Implements `java.lang.Enum<PlatformErrorCode>`.  
  - Fields: `private final int code; private final String message;`  
  - Constructor: `PlatformErrorCode(int code, String message)`  
  - Static field: `private static final Map<Integer, PlatformErrorCode> BY_NUMBER = new HashMap<>();`  
  - Static initializer: populates `BY_NUMBER` with all enum constants.  
  - Methods: `public int getNumber()`, `public String getMessage()`, `public static PlatformErrorCode byNumber(int number)`.

#### Input
- Constructor parameters: `int code`, `String message`.  
- Each enum constant supplies these values.

#### Output
- Immutable `PlatformErrorCode` instances with code and message.  
- `byNumber` returns the corresponding enum constant or `null` if not found.

#### Core logic
- Static block iterates over `values()` and inserts each constant into `BY_NUMBER` keyed by `code`.  
- `byNumber(int)` retrieves the constant from `BY_NUMBER` using the provided key.

#### Public API
- `getNumber() -> int` – returns numeric code.  
- `getMessage() -> String` – returns error description.  
- `byNumber(int) -> PlatformErrorCode` – lookup by numeric code.  
- Enum constants (over 3,300 entries) such as:  
  - `BILLING_00001(1, "billing failure 1")`  
  - `STORAGE_00002(2, "storage failure 2")`  
  - `NETWORK_00003(3, "network failure 3")`  
  - `REPORT_00004(4, "report failure 4")`  
  - `IMPORT_00005(5, "import failure 5")`  
  - `AUTH_00006(6, "auth failure 6")`  
  - …

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- No explicit exceptions thrown; `byNumber` may return `null` for unknown codes.

#### Concurrency
- Map populated once at class initialization; no synchronization required.
