### AiCalibration.java
- H: 1.0
- C: C4C9EE6C
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:42:43Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 8; TODO/FIXME: 0; @Override: 0; methods (approx): 7; constructors: 1; field declarations (w/ modifier): 3

> Holds per‑model calibration data for AI inference timing and token‑to‑character ratios, used by the estimator to adjust time estimates to the target hardware.

#### Purpose
- Stores calibration metrics for AI model inference.
- Supplies values to the estimator when both prefill and decode rates are set.

#### Type
- `class`  
  `public`  
  `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`  
  `@ToString` (Lombok)

#### Input
- Constructor: no arguments.  
- Setters:  
  - `setPrefillTokensPerSecond(double)`  
  - `setDecodeTokensPerSecond(double)`  
  - `setCharsPerToken(double)`  

#### Output
- Getters return the stored calibration values.  
- `toString()` provides a string representation of all fields.

#### Core logic
- Simple storage of three double values.  
- No additional computation or validation performed.

#### Public API
- `getPrefillTokensPerSecond() -> double` – read prefill rate  
- `setPrefillTokensPerSecond(double) -> void` – set prefill rate  
- `getDecodeTokensPerSecond() -> double` – read decode rate  
- `setDecodeTokensPerSecond(double) -> void` – set decode rate  
- `getCharsPerToken() -> double` – read chars‑per‑token  
- `setCharsPerToken(double) -> void` – set chars‑per‑token  

#### Dependencies
- `lombok.ToString`

#### Exceptions / Errors
- No checked exceptions.  
- No null handling; primitive types.

#### Concurrency
- Not thread‑safe; intended for single‑threaded configuration use.
