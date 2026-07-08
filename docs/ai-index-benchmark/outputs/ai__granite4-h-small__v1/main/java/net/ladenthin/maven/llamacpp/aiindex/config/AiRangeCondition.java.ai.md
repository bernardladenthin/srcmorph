### AiRangeCondition.java
- H: 1.0
- C: 694E254D
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T22:04:44Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 8; TODO/FIXME: 0; @Override: 0; methods (approx): 7; constructors: 1; field declarations (w/ modifier): 2

> A numeric range definition used by the Maven plugin to filter files by size or line count.

#### Purpose
- Represents a leaf condition for numeric ranges in `AiCondition`.
- Supports exclusive lower and inclusive upper bounds.
- Serves as a mutable JavaBean for Maven configuration.

#### Type
Class, public, not abstract or sealed.  
Lombok `@ToString` annotation.

#### Input
- Constructor: no parameters.
- `setMin(long)` and `setMax(long)` set the bounds.
- `contains(long)` checks a value against the bounds.

#### Output
- `getMin()` and `getMax()` return the configured bounds.
- `contains(long)` returns `true` if a value is within the range.
- `hasBound()` indicates if any bound is active.
- Lombok-generated `toString()` provides a textual representation.

#### Core logic
- `contains(long)`:
  1. If `min > 0` and `value <= min` → false.
  2. If `max > 0` and `value > max` → false.
  3. Otherwise → true.
- `hasBound()` returns true when `min > 0` or `max > 0`.

#### Public API
- `AiRangeCondition()` → construct an empty range.  
- `long getMin()` → get exclusive lower bound.  
- `void setMin(long)` → set exclusive lower bound.  
- `long getMax()` → get inclusive upper bound.  
- `void setMax(long)` → set inclusive upper bound.  
- `boolean contains(long)` → test value against range.  
- `boolean hasBound()` → check if range is usable.  

#### Dependencies
- `lombok.ToString`.

#### Exceptions / Errors
- No explicit exceptions thrown.  
- Methods assume non-null primitive types; no null checks needed.

#### Concurrency
- No synchronization; mutable state is not thread‑safe.  
- Designed for single‑thread use within Maven configuration.
