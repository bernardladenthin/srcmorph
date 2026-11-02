### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:49:24Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that selects, filters, and AI‑generates source‑code transformations, driven by a rich configuration and pluggable provider back‑ends.

#### Purpose
- Central configuration and validation for the srcmorph tool.
- AI‑powered generation of code snippets or transformations with interchangeable back‑ends.

#### Responsibilities
- **Configuration aggregation** – `SrcMorphConfiguration` holds all runtime settings.
- **Condition evaluation** – `AiConditionEvaluator` validates file‑selection trees.
- **Fact extraction** – `AiFactExtractor` builds deterministic fact blocks for AI prompts.
- **Oversize handling** – `AiOversizeStrategy` routes files that exceed context limits.
- **Provider abstraction** – `AiGenerationProvider` interface and `AiGenerationProviderFactory` create concrete providers.
- **Local inference** – `LlamaCppJniAiGenerationProvider` wraps llama.cpp JNI.
- **Remote inference** – `OpenAiCompatibleProviderSupport` handles OpenAI‑compatible HTTP calls.
- **Mock support** – `MockAiGenerationProvider` supplies deterministic responses for tests.
- **Post‑processing** – `AiCompletionParser` cleans raw LLM output.

#### Key units
- `SrcMorphConfiguration` – mutable bean of all srcmorph settings.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – stateless utility for fact block creation.
- `AiOversizeStrategy` – enum (`FAIL`, `HEAD`, `CHUNK`, `METADATA`).
- `AiGenerationProvider` – interface for text generation.
- `AiGenerationProviderFactory` – resolves provider key and instantiates provider.
- `LlamaCppJniConfig` – immutable config for llama.cpp JNI provider.
- `LlamaCppJniAiGenerationProvider` – local GGUF inference implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible chat‑completion.
- `AiCompletionParser` – strips thinking blocks from completions.

#### Data flow
1. **Load configuration** – Maven plugin or CLI populates `SrcMorphConfiguration`.
2. **Build condition tree** – `AiConditionGroup`/`AiCondition` objects from XML/YAML.
3. **Filter files** – `AiConditionEvaluator` checks each file’s `AiFileContext`.
4. **Handle oversize** – `AiOversizeStrategy` decides routing for large files.
5. **Generate AI prompt** – `AiFactExtractor` prepends fact block to prompt.
6. **Select provider** – `AiGenerationProviderFactory.effectiveProvider` chooses backend.
7. **Generate text** – provider’s `generate`/`generateWithTimings` produces output.
8. **Post‑process** – `AiCompletionParser.parseCompletion` cleans the result.
9. **Persist or apply** – generated code is written back or used for transformation.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `MockAiGenerationProvider`.
- **External** – `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (model, parameters, JSON), `com.fasterxml.jackson` (JSON), `java.time.Instant`, `java.util.regex.Pattern`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), `java.net`, `java.io`.

#### Cross‑cutting
- **Thread safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless; `LlamaCppJniConfig` is immutable.
- **Validation** – `AiConditionEvaluator.validate()` and `AiFactExtractor.validate()` enforce well‑formed trees and counter definitions.
- **Error handling** – Validation throws `IllegalArgumentException`; I/O errors propagate as `IOException`; unknown provider names trigger `IllegalArgumentException`.
- **Timing & metrics** – `generateWithTimings` returns `AiGenerationTimings` with token counts, decode rates, and optional native timings.
- **Security** – `OpenAiCompatibleProviderSupport` requires HTTPS or loopback hosts for API keys.
- **Logging** – providers log truncation, cache reuse, and native model disposal.
