### AiCalibration.java
- H: 1.0
- C: C4C9EE6C
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:36:44Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 8; TODO/FIXME: 0; @Override: 0; methods (approx): 7; constructors: 1; field declarations (w/ modifier): 3

> Holds per-model, per-machine timing calibration data for AI index planning.

#### Purpose
- Stores calibration metrics for AI model performance.
- Enables accurate time estimation in AI index plans.

#### Type
- Class `AiCalibration` (public, mutable JavaBean).  
- Annotated with `@ToString` (Lombok).  
- No equals/hashCode generated.

#### Input
- Constructor: no parameters.  
- Setters: `setPrefillTokensPerSecond(double)`, `setDecodeTokensPerSecond(double)`, `setCharsPerToken(double)`.  
- Fields: `prefillTokensPerSecond`, `decodeTokensPerSecond`, `charsPerToken`.

#### Output
- Getters: `getPrefillTokensPerSecond()`, `getDecodeTokensPerSecond()`, `getCharsPerToken()`.  
- `toString()` via Lombok.

#### Core logic
- Simple storage of three double values.  
- No validation or computation; values are directly assigned and retrieved.

#### Public API
- `AiCalibration()` – creates a new calibration instance.  
- `double getPrefillTokensPerSecond()` – return prefill throughput.  
- `void setPrefillTokensPerSecond(double)` – set prefill throughput.  
- `double getDecodeTokensPerSecond()` – return decode throughput.  
- `void setDecodeTokensPerSecond(double)` – set decode throughput.  
- `double getCharsPerToken()` – return characters per token.  
- `void setCharsPerToken(double)` – set characters per token.  
- `String toString()` – generated string representation.

#### Dependencies
- `lombok.ToString`.  
- `net.ladenthin.maven.llamacpp.aiindex.config` package.

#### Exceptions / Errors
- No explicit exceptions thrown.  
- No null handling needed; primitive doubles used.

#### Concurrency
- No synchronization; mutable state is not thread‑safe.  
- Intended for single‑threaded configuration use.
