### PlatformErrorCode.java
- H: 1.0
- C: D59876EE
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T02:06:45Z
- G: 
- A: 0.0.0
- X: file
- P: 356CFF79
- N: AE37EC5A
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

**Coverage:** 3 of 3 chunks summarized

```markdown
## PlatformErrorCode.java

### Purpose
Maps integer error codes to human-readable descriptions for platform error handling.

### Type
final class PlatformErrorCode extends Object implements Serializable

### Input
- Constructor parameters: number (int), message (String)
- Static block populating BY_NUMBER map with all enum values

### Output
- number (int): Getter returns the error code identifier
- message (String): Getter returns the human-readable error description

### Core logic
- Enum values are stored in values() array
- Static block populates BY_NUMBER map for O(1) lookup by number
- byNumber(int) method retrieves PlatformErrorCode from BY_NUMBER

### Public API
- PlatformErrorCode(int number, String message) -> PlatformErrorCode
- getNumber() -> int
- getMessage() -> String
- byNumber(int) -> PlatformErrorCode

### Dependencies
- java.util.HashMap
- java.util.Map

### Exceptions / Errors
- None

### Concurrency
BY_NUMBER map is immutable after static initialization, thread-safe
No shared mutable state, safe for concurrent access
```
