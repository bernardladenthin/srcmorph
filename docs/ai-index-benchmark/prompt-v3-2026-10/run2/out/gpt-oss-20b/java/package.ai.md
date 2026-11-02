### java
- H: 1.0
- C: 0361907E
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:21:39Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework for configuring, evaluating, and executing AI‑driven source‑code generation and summarisation within the srcmorph toolchain.

#### Purpose
- Centralised AI configuration and orchestration for srcmorph.
- Runtime evaluation of file‑selection rules and deterministic fact extraction.
- Unified provider abstraction for local and remote LLM inference.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings.
- **Condition Evaluation** – `AiConditionEvaluator`/`AiConditionGroup` validate file‑selection trees.
- **Fact Extraction** – `AiFactExtractor` produces deterministic fact blocks.
- **Oversize Handling** – `AiOversizeStrategy` defines policies for files exceeding context limits.
- **Provider Abstraction** – `AiGenerationProvider` interface and concrete providers (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`).
- **Factory & Resolution** – `AiGenerationProviderFactory` selects the appropriate provider.
- **Prompt & Request Building** – `AiPromptSupport` and `OpenAiCompatibleProviderSupport` assemble chat requests.
- **Response Handling** – `AiCompletionParser` and `ChatResponseParser` clean and parse LLM outputs.
- **Timing & Metrics** – `AiGenerationTimings` captures token counts and decode rates.

#### Key units
- `SrcMorphConfiguration` – mutable bean for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – deterministic fact counter and formatter.
- `AiOversizeStrategy` – enum of oversize policies (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiGenerationProvider` – interface for generating text and timings.
- `AiGenerationProviderFactory` – resolves provider key and instantiates the correct provider.
- `LlamaCppJniConfig` – immutable config for llama.cpp and remote‑model options.
- `LlamaCppJniAiGenerationProvider` – local inference provider using llama.cpp JNI.
- `OpenAiCompatibleProviderSupport` – builds and validates OpenAI‑compatible chat requests.
- `AiCompletionParser` – removes internal “thinking” blocks from raw completions.
- `AiGenerationTimings` – DTO for token counts and decode rate.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated via binding mechanisms.
2. **File Routing** – `AiConditionEvaluator` checks each source file against conditions.
3. **Fact Generation** – `AiFactExtractor` produces a facts block for deterministic prompts.
4. **Oversize Handling** – `AiOversizeStrategy` decides action for oversized files.
5. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` chooses the provider key.
6. **Prompt Assembly** – provider uses `AiPromptSupport` to fetch and inject prompt templates.
7. **Chat Request** – local provider calls `LlamaModel.chatComplete`; remote provider posts to OpenAI‑compatible endpoint.
8. **Response Parsing** – `ChatResponseParser` extracts raw chat JSON; `AiCompletionParser` trims reasoning blocks.
9. **Timing Extraction** – provider records prompt/cache token counts and decode rate into `AiGenerationTimings`.
10. **Output** – caller receives cleaned text (and optional timings).

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ChatResponseParser`.
- **External** – `java.time`, `java.util.regex`, `java.util`, `lombok`, `org.jspecify.annotations`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (JNI bindings), `com.fasterxml.jackson.databind`, `org.jspecify.annotations.Nullable`.
- **Remote** – OpenAI‑compatible HTTP endpoints, environment variables for API keys.

#### Cross-cutting
- **Thread‑Safety** – Evaluator, extractor, and provider support classes are stateless/immutable; configuration classes are mutable but intended for single‑threaded setup.
- **Validation** – Early checks in `AiConditionEvaluator.validate` and `AiFactExtractor.validate`.
- **Error Handling** – `IllegalArgumentException` for config errors; `IllegalStateException` for malformed trees; `IOException` for provider communication failures.
- **Immutability** – `AiOversizeStrategy` enum and `LlamaCppJniConfig` are immutable.
- **Timing Metrics** – All providers expose `generateWithTimings` to capture token counts and decode rates.
- **Resource Cleanup** – `LlamaCppJniAiGenerationProvider` implements `AutoCloseable` to release native handles.
