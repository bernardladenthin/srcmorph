### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:10:01Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: 3A96AE1D
- N: DB22947F
- M: 2FA139DC
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 2 of 2 chunks summarized

> A central, type‑safe catalogue of platform error codes with numeric IDs and human‑readable messages, supporting lookup by name or number.

#### Purpose
- Centralises platform error codes for all domains (auth, billing, storage, network, report, import).  
- Enables lookup of an error code by its numeric value.  
- Provides immutable, thread‑safe error constants.

#### Type
- `public enum PlatformErrorCode`  
  - **Fields**: `private final int number;`, `private final String message;`, `private static final Map<Integer, PlatformErrorCode> BY_NUMBER = new HashMap<>();`  
  - **Constructor**: `PlatformErrorCode(int number, String message)`  
  - **Methods**: `public int getNumber()`, `public String getMessage()`, `public static PlatformErrorCode byNumber(int number)`  
  - **Static block**: populates `BY_NUMBER` with all enum values.  
  - Implements `java.lang.Enum<PlatformErrorCode>`; no annotations.

#### Input
- Constructor receives an `int number` and a `String message`.  
- Static initializer iterates over `values()` to populate the lookup map.

#### Output
- Immutable enum constants each holding a numeric code and message.  
- `getNumber()` returns the numeric ID.  
- `getMessage()` returns the descriptive string.  
- `byNumber(int)` returns the corresponding enum constant or `null` if absent.

#### Core logic
- Static block: `for (PlatformErrorCode e : values()) BY_NUMBER.put(e.number, e);`  
- `byNumber(int number)` performs `return BY_NUMBER.get(number);`.

#### Public API
- `getNumber() -> int` – retrieve numeric code.  
- `getMessage() -> String` – retrieve message string.  
- `byNumber(int) -> PlatformErrorCode` – lookup by number.  
- Enum constants (e.g., `AUTH_03942`, `BILLING_03943`, …, `IMPORT_06000`).

#### Dependencies
- `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.

#### Exceptions / Errors
- No explicit exceptions thrown; `byNumber` may return `null` if the number is not defined.

#### Concurrency
- Enum constants are inherently thread‑safe.  
- The static lookup map is populated once in a static block and then read‑only, ensuring safe concurrent access.
