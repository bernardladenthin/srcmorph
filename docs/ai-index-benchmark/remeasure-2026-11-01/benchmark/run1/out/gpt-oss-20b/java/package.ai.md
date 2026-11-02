### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:28:33Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑based AI code‑generation framework that evaluates source files against configurable rules, extracts deterministic facts, and delegates text generation to pluggable local or remote AI back‑ends.

#### Purpose
- Orchestrate AI‑driven source‑code analysis and generation within a Maven build.
- Provide a unified, configurable pipeline from file selection to AI output.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`.
- **Rule Evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact Extraction** – `AiFactExtractor`.
- **AI Generation** – `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`.
- **Prompt & Model Handling** – `AiPromptSupport`, `AiPromptDefinition`, `AiModelDefinition`, `AiFactDefinition`.
- **Output Cleaning & Timing** – `AiCompletionParser`, `AiGenerationTimings`.

#### Key units
- `SrcMorphConfiguration` – runtime bean for directories, providers, prompts, models, field rules.
- `AiGenerationConfig` – per‑generation settings (model path, context, temperature, GPU layers, caching, stop strings, dry‑run).
- `AiOversizeStrategy` – enum (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for oversized files.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees against `AiFileContext`.
- `AiFactExtractor` – builds deterministic fact blocks from regex counters.
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider keys to concrete providers.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI provider with lazy model load and timing extraction.
- `OpenAiCompatibleProviderSupport` – remote API support with endpoint/key validation and timing.
- `AiCompletionParser` – strips internal reasoning blocks from raw completions.
- `AiPromptSupport` – builds chat requests from prompt definitions.

#### Data flow
1. **Setup** – `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.
2. **File Routing** – `AiConditionEvaluator` checks each source file against configured `AiCondition` trees; matched files proceed.
3. **Fact Block** – `AiFactExtractor` counts regex matches and produces a deterministic facts block.
4. **Prompt Construction** – `AiPromptSupport` merges prompt definitions with facts and source context.
5. **Generation Request** – `AiGenerationRequest` is sent to `AiGenerationProviderFactory`.
6. **Provider Selection** – Factory resolves key → `LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport`.
7. **AI Call** – Provider builds chat request, invokes native or HTTP API, parses `ChatResponse`, extracts text and timings.
8. **Post‑processing** – `AiCompletionParser` removes internal reasoning; result returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`, `org.jspecify.annotations`, Lombok (`@ToString`, `@EqualsAndHashCode`).
- **Configuration Sources** – Maven, JSON/YAML, code.

#### Cross‑cutting
- **Immutability & Thread‑Safety** – `LlamaCppJniConfig`, provider support classes, and factory are immutable; provider interfaces are stateless.
- **Error Handling** – Validation throws `IllegalArgumentException`/`IllegalStateException`; providers throw `IOException` for I/O or config errors.
- **Timing Metrics** – `AiGenerationTimings` captures prompt/cache stats and throughput; default zeroed for unsupported providers.
- **Configuration Flexibility** – Builder pattern for `LlamaCppJniConfig`; mutable beans for runtime setup.
- **Stateless Utilities** – `AiConditionEvaluator` and `AiFactExtractor` contain no mutable state.
- **Concurrency Notes** – Local provider’s lazy model load is not synchronized; may load multiple times concurrently; remote provider is stateless and thread‑safe.
