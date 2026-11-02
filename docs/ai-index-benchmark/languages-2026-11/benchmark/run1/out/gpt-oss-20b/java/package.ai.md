### java
- H: 1.0
- C: CDD4BECC
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:21:07Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that configures, selects, and generates AI‑powered source‑code transformations, exposing a Maven‑style JavaBean API and pluggable provider back‑ends for local or remote inference.

#### Purpose
- Centralize AI‑generation and file‑selection settings for a source‑code morphing tool.
- Provide a unified, pluggable interface for generating AI text, supporting local llama.cpp, OpenAI‑compatible, or mock providers.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates runtime parameters, AI provider settings, file filters, and output paths.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees to decide file inclusion.
- **Fact Extraction** – `AiFactExtractor` counts regex‑based facts in source files, producing a formatted facts block.
- **AI Generation Settings** – `AiGenerationConfig` holds mutable parameters for AI model invocation (sampling, caching, device, etc.).
- **Oversize Handling** – `AiOversizeStrategy` defines policies for files exceeding model context limits.
- **Provider Abstraction** – `AiGenerationProvider` interface defines generation contracts; concrete providers implement local or remote inference.
- **Provider Factory** – `AiGenerationProviderFactory` resolves provider keys and instantiates the appropriate provider.
- **Local Inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model, runs chat completions, parses responses, and reports metrics.
- **Remote Compatibility** – `OpenAiCompatibleProviderSupport` builds request bodies, resolves endpoints, validates API keys, and interprets responses.
- **Response Cleanup** – `AiCompletionParser` strips internal thinking blocks from completions.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiGenerationConfig` – JavaBean for AI provider configuration.
- `AiOversizeStrategy` – enum of oversize handling policies.
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `create`, `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` – local inference, lazy `LlamaModel`, `generateWithTimings`.
- `OpenAiCompatibleProviderSupport` – request body, endpoint, API key, error handling, `timings`.
- `LlamaCppJniConfig` – immutable config record with builder.
- `AiCompletionParser` – `parseCompletion`.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.
2. **Routing** – `AiConditionEvaluator` receives `AiCondition` trees and evaluates each file’s `AiFileContext` to decide inclusion.
3. **Fact Counting** – For included files, `AiFactExtractor.factsBlock` produces a facts block.
4. **Prompt Construction** – `AiPromptSupport` supplies the prompt template for a given file.
5. **Provider Selection** – `AiGenerationProviderFactory` chooses the concrete provider based on configuration.
6. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls `model.chatComplete`, parses JSON, extracts text and timing.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds a JSON body, resolves endpoint, sends HTTP request, parses `ChatResponse`, extracts text and usage.
7. **Post‑processing** – `AiCompletionParser` removes any internal thinking block from the raw completion.
8. **Metrics** – `AiGenerationTimings` returned with token counts and rates for calibration.

#### Dependencies
- **Internal** – `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `java.time`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (model, args, JSON parser), `com.fasterxml.jackson.databind` (ObjectMapper), HTTP client for remote provider.

#### Cross‑cutting
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`).
- Defensive copying in `AiConditionGroup.setConditions`.
- Immutable configuration (`LlamaCppJniConfig`, `AiOversizeStrategy`).
- Consistent error handling: `IllegalArgumentException` for misconfigurations, `IOException` for I/O or HTTP failures.
- Uniform timing metrics via `AiGenerationTimings`.
- API key validation enforces loopback restrictions for plain HTTP endpoints.
