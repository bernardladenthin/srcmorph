### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T22:51:51Z
- G: 
- A: 0.0.0
- X: file
- P: 8E48B9CB
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 3 of 3 chunks summarized

Here is the consolidated markdown summary for the `PlatformErrorCode.java` file:

````markdown
## Purpose
- Represents platform error codes with associated messages.

## Type
- `enum PlatformErrorCode` (final class)

## Input
- None (constructor parameters: int number, String message)

## Output
- int getNumber() -> number
- String getMessage() -> message

#### Core logic
- Enum values represent platform error codes
- Static block populates BY_NUMBER map for lookup by number

#### Public API
- PlatformErrorCode(int number, String message) -> PlatformErrorCode
- getNumber() -> int
- getMessage() -> String
- byNumber(int number) -> PlatformErrorCode (returns null if not found)

#### Dependencies
- java.util.HashMap
- java.util.Map

#### Exceptions / Errors
- None

#### Concurrency
- Immutable enum, thread-safe
````
