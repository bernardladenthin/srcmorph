### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:01:14Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: 3A96AE1D
- N: DB22947F
- M: 2FA139DC
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A Java enum that catalogs platform error codes with numeric IDs and messages, providing lookup by number for consistent error handling across the application.

#### Purpose
- Central catalogue of error codes for billing, storage, network, report, import, auth, and ledger‑lock failures.  
- Enables retrieval of error details by numeric code.

#### Type
- `public enum PlatformErrorCode`  
  - Implements `values()`, `byNumber(int)`, `getNumber()`, `getMessage()`.  
  - Fields: `private final int number;`, `private final String message;`.  
  - Constructor: `PlatformErrorCode(int number, String message)`.  
  - Static field: `private static final Map<Integer, PlatformErrorCode> BY_NUMBER`.  
  - Static initializer populates `BY_NUMBER` from `values()`.

#### Input
- Constructor parameters: `int number`, `String message` for each enum constant.  
- `byNumber(int number)` accepts an error number.

#### Output
- `getNumber()` returns the numeric code.  
- `getMessage()` returns the associated message.  
- `byNumber(int)` returns the enum constant for a given number or `null` if not found.

#### Core logic
- Static block iterates over `values()` to populate `BY_NUMBER` mapping `number → enum`.  
- `byNumber(int)` performs `BY_NUMBER.get(number)` lookup.

#### Public API
- `getNumber() -> int` – returns error numeric ID.  
- `getMessage() -> String` – returns error description.  
- `byNumber(int) -> PlatformErrorCode` – retrieves enum by number.  
- Enum constants (e.g., `BILLING_00001`, `STORAGE_00002`, …, `LEDGER_LOCK_02701`, …, `BILLING_03800`, …).

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.util.Enum`

#### Exceptions / Errors
- None declared or thrown; `byNumber` may return `null` for unknown codes.

#### Concurrency
- Immutable enum; static map populated once in a static initializer and read‑only thereafter, thread‑safe.
