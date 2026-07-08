### AiGenerationTimings.java
- H: 1.0
- C: 1D58E849
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:13:06Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 7; TODO/FIXME: 0; @Override: 0; methods (approx): 6; constructors: 1; field declarations (w/ modifier): 5

> Holds text output with model timing metrics for AI generation.

#### Purpose
- Encapsulates generated text and performance data for calibration.

#### Type
- final class `AiGenerationTimings`  
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`

#### Input
- Constructor parameters: `String text`, `int promptTokens`, `double prefillTokensPerSecond`, `int predictedTokens`, `double decodeTokensPerSecond`

#### Output
- Getter methods returning each field value.

#### Core logic
- Stores supplied values in private final fields.  
- Provides accessor methods that simply return those fields.

#### Public API
- `AiGenerationTimings(String, int, double, int, double) -> AiGenerationTimings` – create instance  
- `text() -> String` – get generated text  
- `promptTokens() -> int` – get prompt token count  
- `prefillTokensPerSecond() -> double` – get prefill throughput  
- `predictedTokens() -> int` – get generated token count  
- `decodeTokensPerSecond() -> double` – get decode throughput

#### Dependencies
- `lombok.EqualsAndHashCode`, `lombok.ToString`, `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- None declared; constructor accepts all parameters directly.

#### Concurrency
- Immutable after construction; thread‑safe.
