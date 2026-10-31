### java
- H: 1.0
- C: 2FB01B39
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:41:54Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑plugin‑friendly framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation.

#### Purpose
- Enable AI‑driven source‑code processing via a single, configurable Maven plugin.
- Provide deterministic fact blocks and robust file‑selection logic for AI prompts.
- Support multiple AI back‑ends and runtime‑adjustable generation parameters.

#### Responsibilities
- **File‑selection & validation** – `AiConditionEvaluator` and `AiConditionGroup` parse and apply complex inclusion rules.
- **Oversize handling** – `AiOversizeStrategy` defines how to treat files exceeding size limits.
- **Fact extraction** – `AiFactExtractor` builds a header‑prefixed facts string from regex counters.
- **Configuration aggregation** – `SrcMorphConfiguration` holds all runtime options (filters, provider, model paths, prompt definitions, etc.).
- **AI generation orchestration** – `AiGenerationProviderFactory` selects a provider, and `AiGenerationProvider` implementations (mock, llama.cpp JNI, OpenAI‑compatible) perform the actual text generation.
- **Request/response utilities** – `OpenAiCompatibleProviderSupport` builds chat requests, validates endpoints, and parses responses; `AiCompletionParser` strips internal reasoning blocks.
- **Metrics & timing** – Providers optionally return `AiGenerationTimings` with token counts and throughput.

#### Key units
- `SrcMorphConfiguration` – mutable bean exposing all srcmorph runtime options.
- `AiConditionEvaluator` – stateless evaluator for `AiCondition` trees.
- `AiFactExtractor` – stateless builder of deterministic fact headers.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized file handling.
- `AiGenerationProviderFactory` – resolves provider keys to concrete `AiGenerationProvider` instances.
- `LlamaCppJniAiGenerationProvider` – llama.cpp native inference provider with lazy model loading.
- `OpenAiCompatibleProviderSupport` – helper for building OpenAI‑compatible chat requests and parsing responses.
- `AiCompletionParser` – utility to strip leading reasoning blocks from completions.

#### Data flow
1. **Configuration load** – `SrcMorphConfiguration` is populated via Maven plugin or JSON/YAML.
2. **File routing** – `AiConditionEvaluator` checks each file against conditions; `AiOversizeStrategy` decides handling for oversized files.
3. **Fact generation** – `AiFactExtractor` produces a facts header for AI prompts.
4. **Provider selection** – `AiGenerationProviderFactory` creates the appropriate `AiGenerationProvider`.
5. **Request construction** – `OpenAiCompatibleProviderSupport.requestBody(...)` builds the JSON payload from an `AiGenerationRequest`.
6. **Generation** – The provider’s `generate(...)` or `generateWithTimings(...)` is invoked, producing a raw completion string.
7. **Post‑processing** – `AiCompletionParser.parseCompletion(...)` removes any leading reasoning block, yielding the final answer.
8. **Result integration** – AI output is written back to source files or aggregated indices.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`, `@EqualsAndHashCode`), `net.ladenthin.llama` (native bindings), `com.fasterxml.jackson.databind` (JSON), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross‑cutting
- **Stateless utilities** – `AiConditionEvaluator` and `AiFactExtractor` are thread‑safe.
- **Defensive copying** – `AiConditionGroup` copies input collections to avoid external mutation.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness.
- **Immutability** – `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are immutable after construction.
- **Thread safety** – Providers are stateless or lazily initialized; factory and config are thread‑safe, though `LlamaCppJniAiGenerationProvider` may load multiple models concurrently.
- **Error handling** – `IOException` signals provider or request failures; `IllegalArgumentException` indicates misconfiguration.
- **Logging** – Providers log truncation warnings, cache reuse, and timing metrics.
