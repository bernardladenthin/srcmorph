### AiRangeCondition.java
- H: 1.0
- C: 694E254D
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T23:09:57Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 8; TODO/FIXME: 0; @Override: 0; methods (approx): 7; constructors: 1; field declarations (w/ modifier): 2

> Defines a numeric range used by AI index conditions for file size or line count.

#### Purpose
- Holds an exclusive lower bound and an inclusive upper bound for numeric conditions.
- Enables checking whether a value falls within the configured range.

#### Type
- Class `AiRangeCondition` (public).
- Uses Lombok `@ToString`.
- No inheritance or interfaces.

#### Input
- `min` and `max` fields set via setters or Maven plugin configuration.
- Constructor initializes an empty instance.

#### Output
- `contains(long)` returns whether a value is within bounds.
- `hasBound()` indicates if any bound is active.
- Lombok-generated `toString()` representation.

#### Core logic
- `contains(value)`:
  1. If `min > 0` and `value <= min` → `false`.
  2. If `max > 0` and `value > max` → `false`.
  3. Otherwise → `true`.
- `hasBound()` returns `true` when `min > 0` or `max > 0`.

#### Public API
- `AiRangeCondition()` → constructs empty range.
- `getMin() -> long` – returns exclusive lower bound.
- `setMin(long)` – sets exclusive lower bound.
- `getMax() -> long` – returns inclusive upper bound.
- `setMax(long)` – sets inclusive upper bound.
- `contains(long) -> boolean` – checks value inside range.
- `hasBound() -> boolean` – checks if range has active bounds.

#### Dependencies
- `lombok.ToString`.

#### Exceptions / Errors
- No checked or unchecked exceptions thrown.

#### Concurrency
- No synchronization; instance is mutable and not thread‑safe.
