### AiFactCounter.java
- H: 1.0
- C: 2BFA8358
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:48:10Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 6; TODO/FIXME: 0; @Override: 0; methods (approx): 5; constructors: 1; field declarations (w/ modifier): 2

> Counts non‑overlapping regex matches for a given label in source files.

#### Purpose
- Stores a label and a Java regex used to count occurrences in source.
- Provides mutable JavaBean for Maven plugin configuration.

#### Type
- Class, public, mutable. No interfaces or generics.
- Annotated `@ToString` (Lombok). Suppresses uninitialized field warnings.

#### Input
- Constructor: no parameters.
- `setLabel(String)` – sets human‑readable label.
- `setPattern(String)` – sets regex pattern.
- Fields `label`, `pattern` are read by getters.

#### Output
- `getLabel()` returns stored label.
- `getPattern()` returns stored regex.
- Lombok `toString()` outputs `label` and `pattern`.

#### Core logic
- Simple field assignments in setters.
- Getters return current field values.

#### Public API
- `AiFactCounter()` – instantiate counter.  
- `getLabel() -> String` – retrieve label.  
- `setLabel(String)` – set label.  
- `getPattern() -> String` – retrieve pattern.  
- `setPattern(String)` – set pattern.

#### Dependencies
- `lombok.ToString`

#### Exceptions / Errors
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization; class is not thread‑safe.
