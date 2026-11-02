### PlatformErrorCode.java
- H: 1.0
- C: F561DB4F
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T23:19:15Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

```markdown
## Purpose
Calculates VAT for invoices

## Type
enum

## Input
- None

## Output
- PlatformErrorCode object

## Core Logic
- Enum values represent different error codes
- BY_NUMBER map provides quick lookup by error code number

## Public API
- None

## Dependencies
- None

## Exceptions / Errors
- None

## Concurrency
- Thread-safe as an immutable enum
```
