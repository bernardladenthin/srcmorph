### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:11:44Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that configures, selects, and generates source‑code artifacts using AI models, with pluggable local or remote inference back‑ends.

#### Purpose
- Centralize AI‑driven source‑generation configuration and orchestration.
- Provide a pluggable provider layer for local llama.cpp, OpenAI‑compatible APIs, or mocks.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`, `AiConditionEvaluator`.
- **File Selection** – recursive condition trees (`AiCondition`, `AiConditionGroup`, `AiRangeCondition`) applied to `AiFileContext`.
- **Fact Extraction** – deterministic fact blocks via `AiFactExtractor` and regex counters.
- **Provider Abstraction** – `AiGenerationProvider`, factory, and concrete implementations (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, mock).
- **Request/Response Handling** – `AiPromptSupport`, `AiCompletionParser`, timing DTOs.

#### Key units
- **`SrcMorphConfiguration`** – mutable bean aggregating directories, filters, provider key, model paths, prompt/fact definitions.
- **`AiGenerationConfig`** – immutable bean with model path, context size, temperature, retries, stop strings, GPU/CPU flags.
- **`AiOversizeStrategy`** – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with `fromConfig`.
- **`AiConditionEvaluator`** – stateless tree evaluator that flags line‑count usage.
- **`AiFactExtractor`** – builds deterministic fact strings from regex counters.
- **`AiGenerationProvider`** – interface for `generate` / `generateWithTimings`.
- **`AiGenerationProviderFactory`** – resolves provider key to concrete implementation.
- **`LlamaCppJniAiGenerationProvider`** – JNI wrapper for llama.cpp, lazy model loading, chat completion, timing extraction.
- **`OpenAiCompatibleProviderSupport`** – builds HTTP requests, validates URLs, handles API keys, parses responses.
- **`AiCompletionParser`** – strips Gemma‑4 thinking blocks from raw completions.
- **`AiGenerationTimings`** – DTO with text, token counts, cache usage, throughput.

#### Data flow
1. **Setup** – `SrcMorphConfiguration` is populated (Maven plugin, CLI, or code).
2. **Condition & Fact** – `AiConditionEvaluator` filters files; `AiFactExtractor` produces a facts block.
3. **Provider Selection** – `AiGenerationProviderFactory` creates a provider based on the key in the configuration.
4. **Generation** – Client calls `generate` or `generateWithTimings` with an `AiGenerationRequest` (prompt + facts).
5. **Execution** –  
   *Local*: `LlamaCppJniAiGenerationProvider` invokes native chat completion, parses JSON, removes thinking blocks.  
   *Remote*: `OpenAiCompatibleProviderSupport` sends HTTP request, parses `ChatResponse`, builds timings.  
6. **Result** – Text (and optional timings) returned to the caller for further processing or file emission.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External** – `java.time`, `java.util`, `java.io`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations`, Lombok `@ToString`, `net.ladenthin:llama` JNI, Jackson `ObjectMapper`.

#### Cross-cutting
- Defensive copying in `AiConditionGroup`; stateless evaluators for thread safety.
- Validation throws `IllegalArgumentException`/`IllegalStateException` on misconfiguration.
- Immutability of provider configs and request/response DTOs.
- Timing support via `generateWithTimings`; includes prompt token counts, cache usage, throughput.
- Environment‑based API key handling; loopback URL checks for security.
