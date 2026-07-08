### AiFactCounter.java
- H: 1.0
- C: 2BFA8358
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:42:30Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 6; TODO/FIXME: 0; @Override: 0; methods (approx): 5; constructors: 1; field declarations (w/ modifier): 2

> Provides a mutable configuration object for counting regex matches in source files.

#### Purpose
- Holds a label and a regex pattern for counting matches.
- Supplies getters and setters for Maven plugin reflection.

#### Type
- Class `AiFactCounter` (public, mutable JavaBean).

#### Input
- Constructor: none.
- Fields set via `setLabel(String)` and `setPattern(String)` (via reflection or code).

#### Output
- `getLabel()` returns the human‑readable label.
- `getPattern()` returns the regex pattern.
- `toString()` (Lombok) provides diagnostic representation.

#### Core logic
- None beyond field accessors; purely a data holder.

#### Public API
- `AiFactCounter()` – creates an empty instance.
- `String getLabel()` – returns current label.
- `void setLabel(String)` – assigns label.
- `String getPattern()` – returns current pattern.
- `void setPattern(String)` – assigns pattern.

#### Dependencies
- `lombok.ToString` for `toString()` generation.

#### Exceptions / Errors
- No exceptions thrown; methods accept and return `String` without validation.

#### Concurrency
- No thread‑safety guarantees; intended for single‑thread configuration usage.
