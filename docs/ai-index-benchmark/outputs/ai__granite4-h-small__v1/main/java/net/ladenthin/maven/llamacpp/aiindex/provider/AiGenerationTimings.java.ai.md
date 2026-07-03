### AiGenerationTimings.java
- H: 1.0
- C: 1D58E849
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:08:07Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 7; TODO/FIXME: 0; @Override: 0; methods (approx): 6; constructors: 1; field declarations (w/ modifier): 5

> Records generation text and per‑token timing for AI model calibration.

#### Purpose
- Holds a generated text and timing metrics for calibration.
- Supplies data to `ai-index:calibrate` goal.

#### Type
- `final class AiGenerationTimings`
- Implements `ConvertToRecord`, `ToString`, `EqualsAndHashCode` via Lombok.

#### Input
- Constructor parameters:
  - `String text`
  - `int promptTokens`
  - `double prefillTokensPerSecond`
  - `int predictedTokens`
  - `double decodeTokensPerSecond`

#### Output
- Accessor methods returning the stored fields.
- `toString()`, `equals()`, `hashCode()` via Lombok.

#### Core logic
- Stores supplied values in final fields.
- Provides simple getters that return those values.

#### Public API
- `AiGenerationTimings(String, int, double, int, double)` – construct instance with timing data  
- `String text()` – return generated text  
- `int promptTokens()` – return prompt token count  
- `double prefillTokensPerSecond()` – return prefill throughput  
- `int predictedTokens()` – return predicted token count  
- `double decodeTokensPerSecond()` – return decode throughput  

#### Dependencies
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- None declared; constructor accepts any values.

#### Concurrency
- Immutable after construction; thread‑safe.
