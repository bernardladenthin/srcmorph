### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:16:13Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A centralized, immutable catalog of over 4,000 platform error codes, each mapped to a unique numeric ID and descriptive message, with efficient reverse lookup by code number.

#### Purpose
- Store and expose platform error codes for billing, storage, network, report, import, auth, and ledger‑lock failures.
- Provide consistent error handling across the platform.

#### Type
- `public enum PlatformErrorCode`  
  *Implements `java.lang.Enum<PlatformErrorCode>` implicitly.*  
  *Fields: `int number`, `String message`.*  
  *Static final `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup.*  
  *Constructor: `PlatformErrorCode(int number, String message)`.*

#### Input
- Enum constants defined with a numeric code and a message string.
- Constructor receives these values.
- Static block populates `BY_NUMBER` from all enum values.

#### Output
- `getNumber()` returns the numeric code.
- `getMessage()` returns the human‑readable message.
- `byNumber(int)` returns the enum constant for a given code or `null` if absent.

#### Core logic
- Enumerate constants with unique integer and message.
- In a static initializer, iterate over `values()` to fill `BY_NUMBER` map.
- Provide immutable accessors for code number and message.
- Enable efficient lookup of error codes by integer key.

#### Public API
- `int getNumber() -> int` – retrieve numeric code  
- `String getMessage() -> String` – retrieve error message  
- `PlatformErrorCode byNumber(int) -> PlatformErrorCode` – lookup enum by number  

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  

#### Exceptions / Errors
- `byNumber(int)` may return `null` if the number is not defined; no exception thrown.

#### Concurrency
- Static map is populated in a thread‑safe static initializer; read‑only thereafter.
