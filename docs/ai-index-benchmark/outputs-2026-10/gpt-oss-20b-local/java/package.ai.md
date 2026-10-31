### java
- H: 1.0
- C: C3D438BB
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:21:18Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A runtime‑driven AI source‑code transformation engine that selects files, extracts deterministic facts, and orchestrates AI generation for code generation or analysis.

#### Purpose
- Orchestrates AI‑powered source‑code transformations.
- Validates file eligibility, extracts facts, and routes oversized files.
- Provides a pluggable AI generation layer with timing and parsing support.

#### Responsibilities
- **Configuration & Validation** – load and validate runtime settings, evaluate file‑selection conditions, and enforce oversize strategies.
- **Fact Extraction** – build deterministic fact blocks from regex counters for AI inputs.
- **AI Generation** – abstract provider selection, construct prompts, send requests, parse completions, and expose timing metrics.

#### Key units
- `SrcMorphConfiguration` – mutable bean holding paths, models, prompts, routing rules, and overrides.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static builder for deterministic fact headers.
- `AiOversizeStrategy` – enum of oversize handling modes (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiGenerationProviderFactory` – resolves and creates `AiGenerationProvider` implementations.
- `AiGenerationProvider` (interface) – contract for `generate` and `generateWithTimings`.
- `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider` – concrete providers.
- `LlamaCppJniConfig` – immutable configuration for llama.cpp inference.
- `AiCompletionParser` – strips chain‑of‑thought blocks from raw completions.
- `AiGenerationTimings` – captures prompt/cache tokens, decode rates, and predicted token counts.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven/JSON/YAML.
2. **File Selection** – `AiConditionEvaluator.validate` checks `AiCondition` trees; `matches` decides inclusion.
3. **Fact Generation** – `AiFactExtractor.factsBlock` creates a deterministic facts header for oversized files.
4. **Provider Selection** – `AiGenerationProviderFactory.create` chooses the appropriate provider.
5. **Prompt Construction** – provider builds chat‑completion payload via `AiPromptSupport`.
6. **AI Request** – provider sends request (local llama.cpp or OpenAI‑compatible API) and receives raw completion.
7. **Completion Parsing** – `AiCompletionParser.parseCompletion` removes internal reasoning blocks.
8. **Result Delivery** – plain string or `AiGenerationTimings` is returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (JNI model, parameters, JSON parsing), Jackson (`ObjectMapper`), `org.jspecify.annotations.Nullable`.
- **Runtime** – native llama.cpp library, HTTP client for OpenAI‑compatible endpoints.
- **Java Core** – `java.util`, `java.time`, `java.io`, `java.util.regex`.

#### Cross-cutting
- **Stateless & Thread‑Safe Utilities** – `AiConditionEvaluator`, `AiFactExtractor`, `LlamaCppJniConfig`.
- **Defensive Copying & Immutability** – configuration beans expose unmodifiable lists; provider configs are immutable.
- **Error Handling** – validation throws `IllegalArgumentException`/`IllegalStateException`; IOExceptions for missing reasoning blocks or endpoint issues.
- **Extensibility** – `AiOversizeStrategy` enum allows new strategies; condition nodes support future extensions.
- **Timing & Metrics** – `AiGenerationTimings` captures token counts, decode rates, and predicted tokens for performance analysis.
- **Logging & Warnings** – providers log truncation, cache reuse, and key validation issues.
