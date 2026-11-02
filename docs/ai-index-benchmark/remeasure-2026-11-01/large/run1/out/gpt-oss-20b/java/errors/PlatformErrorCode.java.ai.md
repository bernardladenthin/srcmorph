### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:39Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A central catalogue of platform error codes, each mapped to a numeric ID and a descriptive message for use across billing, storage, network, report, import, auth, and ledger‑lock operations.

#### Purpose
* Central repository of error codes for platform operations.

#### Type
`public enum PlatformErrorCode` – enum, public, no generics.  
Implements no interfaces.  
No annotations.

#### Input
* Enum constants defined at compile time with `int number` and `String message`.  
* Static initializer populates a `Map<Integer, PlatformErrorCode>` for lookup.

#### Output
* `int getNumber()` – numeric error code.  
* `String getMessage()` – human‑readable description.  
* `int getCode()` – alias for numeric code (if present).  
* `String getDescription()` – alias for message (if present).  
* `static PlatformErrorCode byNumber(int)` – lookup by code.  
* Standard enum methods: `valueOf(String)`, `values()`.

#### Core logic
* Each enum constant holds a numeric code and a message.  
* Static block builds a `HashMap` mapping codes to enum instances.  
* `byNumber` performs a map lookup, returning `null` if not found.

#### Public API
* `int getNumber() -> numeric code`  
* `String getMessage() -> message text`  
* `int getCode() -> numeric code`  
* `String getDescription() -> message text`  
* `static PlatformErrorCode byNumber(int) -> enum instance`  
* `static PlatformErrorCode valueOf(String) -> enum instance`  
* `static PlatformErrorCode[] values() -> array`

#### Dependencies
* `java.util.Map`  
* `java.util.HashMap`

#### Exceptions / Errors
* `byNumber` returns `null` if the code is not present.  
* No other methods throw exceptions.

#### Concurrency
* Enum instances are immutable and thread‑safe.  
* The lookup map is populated once in a static block and never modified thereafter.
