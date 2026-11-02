### java
- H: 1.0
- C: 68579582
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:06:14Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑based code‑generation plugin that orchestrates AI‑powered source‑code transformation, from configuration and file selection to provider‑agnostic text generation and post‑processing.

#### Purpose
- Central AI configuration and orchestration for source‑code generation.
- Abstracts multiple AI back‑ends and provides a unified generation API.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`, `AiConditionEvaluator`.
- **File Selection & Fact Extraction** – `AiConditionGroup`, `AiFactExtractor`, `AiFactCounter`.
- **Provider Abstraction & Generation** – `AiGenerationProvider`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`.
- **Response Handling & Timing** – `AiCompletionParser`, `AiGenerationTimings`.

#### Key units
- **`SrcMorphConfiguration`** – mutable bean holding directories, filters, AI provider, prompts, models, field rules, facts.
- **`AiGenerationConfig`** – bean exposing AI model knobs (context size, tokens, temperature, GPU/CPU layers, caching).
- **`AiOversizeStrategy`** – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized‑file handling.
- **`AiConditionEvaluator`** – stateless validator for `AiCondition` trees against `AiFileContext`.
- **`AiFactExtractor`** – static utility producing deterministic facts blocks from regex counters.
- **`AiGenerationProvider`** – interface for `generate` and `generateWithTimings`.
- **`AiGenerationProviderFactory`** – resolves provider key and instantiates concrete provider.
- **`LlamaCppJniAiGenerationProvider`** – local llama.cpp inference with lazy model loading and JSON parsing.
- **`OpenAiCompatibleProviderSupport`** – builds requests, resolves endpoints, validates API keys, extracts timings.
- **`AiCompletionParser`** – strips reasoning blocks from LLM outputs.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` populated via Maven/JSON/YAML.
2. **Condition Setup** – `AiConditionGroup` built from config; `AiConditionEvaluator` validates.
3. **File Evaluation** – `AiConditionEvaluator.matches()` selects files; `usesLines()` flags line‑count needs.
4. **Fact Extraction** – `AiFactExtractor.factsBlock()` generates facts header for selected files.
5. **Oversize Handling** – `AiOversizeStrategy` determines processing (fail, sample, map‑reduce, deterministic).
6. **Generation Request** – `AiGenerationRequest` (prompt key, source text, header) created.
7. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` chooses provider; `create` returns concrete provider.
8. **Text Generation** – Provider’s `generate`/`generateWithTimings` invoked; llama.cpp or OpenAI‑compatible path executed.
9. **Post‑processing** – `AiCompletionParser` extracts final answer; `AiGenerationTimings` records token counts and decode rate.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`.
- **External** – `java.time`, `java.util.regex`, Lombok, `org.jspecify.annotations`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (native bindings), Jackson `ObjectMapper`, environment variables for API keys.

#### Cross‑cutting
- **Thread safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless; provider configs are immutable.
- **Validation & Error handling** – configuration errors surface as unchecked `IllegalArgumentException`; I/O errors as `IOException`.
- **Immutability** – `LlamaCppJniConfig`, `AiOversizeStrategy`, and most support classes are immutable.
- **Timing measurement** – `AiGenerationTimings` captures prompt, cached, and completion token counts and decode rate.
- **Configuration defaults** – `AiGenerationConfig` and `SrcMorphConfiguration` provide sensible defaults and preserve them when `null` or empty values are supplied.
