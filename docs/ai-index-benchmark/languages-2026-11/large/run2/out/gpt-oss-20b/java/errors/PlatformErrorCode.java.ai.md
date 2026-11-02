### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:43Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> A central catalogue of platform error codes, each with a numeric ID and a human‑readable message, and a lookup facility by code number.

#### Purpose
- Centralised error catalogue for the platform.  
- Provides a unique identifier and human‑readable description for each error.

#### Type
`public enum PlatformErrorCode` – enum, public, no modifiers.  
Implements `java.lang.Enum`.  
Contains a static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup.

#### Input
- Enum constants defined with `(int number, String message)`.  
- Static block populates `BY_NUMBER` from all constants.

#### Output
- `int getNumber()` – numeric error code.  
- `String getMessage()` – descriptive message.  
- `static PlatformErrorCode byNumber(int)` – lookup by numeric code.

#### Core logic
- Constructor stores `number` and `message`.  
- Static initializer iterates over `values()` to fill `BY_NUMBER`.  
- `byNumber` retrieves the constant from the map.

#### Public API
- `int getNumber()` → returns error code number.  
- `String getMessage()` → returns error description.  
- `static PlatformErrorCode byNumber(int)` → lookup by numeric code.

#### Dependencies
- `java.util.Map`  
- `java.util.HashMap`  
- `java.lang.Enum`

#### Exceptions / Errors
- `byNumber` returns `null` if the number is not defined; callers must handle `null`.

#### Concurrency
- `BY_NUMBER` is populated once in a static initializer; the map is effectively immutable thereafter, ensuring thread safety.

#### Notes
- The enum contains **4,200** constants, ranging from `BILLING_00001` to `AUTH_04200`. Each constant follows the pattern `<CATEGORY>_<NNNNN>(<code>, "<description>")`.
