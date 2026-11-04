### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:41:44Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A modular AI‑driven source‑code transformation engine that selects files, extracts facts, and generates code changes via pluggable local or cloud providers.

#### Purpose
- Orchestrate AI‑based source‑code analysis and generation for the srcmorph tool.
- Provide configurable file selection, fact extraction, and generation pipelines.

#### Responsibilities
- **Configuration & Selection** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact Extraction** – `AiFactExtractor`, `AiFactCounter`.
- **Generation Pipeline** – `AiGenerationConfig`, `AiOversizeStrategy`, `AiGenerationProviderFactory`.
- **Provider Implementation** – `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.

#### Key units
- `SrcMorphConfiguration` – mutable holder for file paths, prompts, and model metadata.
- `AiConditionEvaluator` – validates and evaluates file‑selection trees.
- `AiFactExtractor` – regex‑based fact counter for AI prompts.
- `AiGenerationConfig` – JavaBean for AI model, sampling, caching, and DRY settings.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files.
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider keys to concrete implementations.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI provider with lazy model loading.
- `OpenAiCompatibleProviderSupport` – builds JSON payloads and normalises endpoints for OpenAI‑compatible APIs.
- `AiCompletionParser` – strips internal “thinking” blocks from LLM outputs.
- `LlamaCppJniConfig` – immutable record of 37 llama.cpp tuning knobs.

#### Data flow
1. **Setup** – `SrcMorphConfiguration` supplies file selection rules, prompts, and AI settings.
2. **Selection** – `AiConditionEvaluator` checks each file against `AiConditionGroup`; selected files may trigger line‑count checks.
3. **Fact Extraction** – `AiFactExtractor` scans source text with `AiFactCounter` patterns, producing a facts block for prompts.
4. **Provider Selection** – `AiGenerationProviderFactory` creates the appropriate `AiGenerationProvider`.
5. **Generation** – Provider receives an `AiGenerationRequest`; for OpenAI‑compatible providers, `OpenAiCompatibleProviderSupport` builds the request body; for local llama.cpp, native chat completion is invoked.
6. **Response Handling** – `AiCompletionParser` cleans the raw output; provider returns plain text or `AiGenerationTimings`.
7. **Output** – Generated code is written to the output directory defined in `SrcMorphConfiguration`.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`.
- **External**: `java.time.Instant`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), Jackson `ObjectMapper`, `net.ladenthin.llama` (model, args, JSON parser), `net.ladenthin.srcmorph.support`.
- **Configuration Sources**: Maven plugin parameters or external config files populate `SrcMorphConfiguration` and `AiGenerationConfig`.

#### Cross‑cutting
- Thread‑safe, stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Defensive copying in `AiConditionGroup.setConditions`.
- Immutable configuration objects (`LlamaCppJniConfig`, provider support classes).
- Robust error handling with clear exception messages for invalid configs or missing markers.
- Timing metrics via `generateWithTimings` default implementation.
- Logging of truncation, prompt‑cache reuse, and provider status (details omitted in summaries).
