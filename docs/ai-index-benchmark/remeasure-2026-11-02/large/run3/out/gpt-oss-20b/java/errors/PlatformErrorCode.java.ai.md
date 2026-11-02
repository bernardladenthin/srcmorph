### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:55Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> Provides a central catalogue of platform error codes with numeric IDs, messages, and fast lookup by code.

#### Purpose
- Central repository of platform error codes for billing, storage, network, report, import, auth, and ledger lock failures.
- Enables retrieval of an enum instance by its numeric code.

#### Type
- `public enum PlatformErrorCode`  
  - Implements `java.lang.Enum`.  
  - No additional modifiers.  
  - No annotations.

#### Input
- Constructor parameters: `int number`, `String message`.  
- Static initializer iterates over `values()` to populate a lookup map.

#### Output
- `getNumber()` → `int` – numeric code.  
- `getMessage()` → `String` – error description.  
- `byNumber(int)` → `PlatformErrorCode` – enum instance for a given number (or `null` if absent).

#### Core logic
- Builds a `Map<Integer, PlatformErrorCode>` (`BY_NUMBER`) mapping each numeric code to its enum constant during class initialization.  
- `byNumber(int)` performs a map lookup to return the corresponding enum.

#### Public API
- `getNumber() -> int` – returns numeric code.  
- `getMessage() -> String` – returns error message.  
- `byNumber(int) -> PlatformErrorCode` – lookup by numeric code.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- `byNumber` returns `null` if the number is not present; no checked exceptions.

#### Concurrency
- Static map is populated once during class initialization; read‑only thereafter, making it thread‑safe for concurrent reads.
