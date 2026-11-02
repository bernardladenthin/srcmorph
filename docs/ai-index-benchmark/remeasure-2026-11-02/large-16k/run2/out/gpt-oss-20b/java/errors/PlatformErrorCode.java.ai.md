### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:28:45Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A single source of truth for platform‑wide error identification, mapping numeric codes to human‑readable messages for billing, storage, network, report, import, auth, and ledger lock failures.

#### Purpose
- Central repository for all platform error codes.  
- Enables consistent error handling across the platform.

#### Type
`public enum PlatformErrorCode` – enum, public, no modifiers.  
Implements no interfaces.  
No annotations.

#### Input
- Constructor parameters: `int code`, `String message`.  
- No external inputs; all constants are defined statically.

#### Output
- Each enum constant holds an immutable `int code` and `String message`.  
- `getNumber()` returns the numeric code.  
- `getMessage()` returns the human‑readable message.  
- `byNumber(int)` retrieves the enum instance for a given code, or `null` if absent.

#### Core logic
- Enum constants are declared with a unique numeric code and descriptive message.  
- Constructor assigns these values to fields.  
- A static `Map<Integer, PlatformErrorCode> BY_NUMBER` is populated in a static block, mapping each code to its enum instance.  
- `byNumber` performs a constant‑time map lookup to return the corresponding enum.

#### Public API
- `int getNumber() -> numeric code`  
- `String getMessage() -> message`  
- `PlatformErrorCode byNumber(int) -> enum instance`  
- Enum constants (e.g., `BILLING_00001`, `STORAGE_00002`, …, `LEDGER_LOCK_02899`, `BILLING_03620`, etc.) – **N total** constants.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`

#### Exceptions / Errors
- `byNumber` returns `null` if the code is not present; callers must handle this.  
- Constructor does not perform validation; assumes valid arguments.

#### Concurrency
- Immutable enum constants; thread‑safe by design.  
- `BY_NUMBER` is populated once in a static block, safe for concurrent reads.
