### java
- H: 1.0
- C: 5BF387FF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:16:44Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A configuration‑driven AI‑powered source‑code morphing engine that orchestrates file selection, condition evaluation, fact extraction, and AI generation for code transformations.

#### Purpose
- Central configuration and AI generation orchestration for source‑morph runs.

#### Responsibilities
- **Configuration Management** – load and expose runtime settings (`SrcMorphConfiguration`, `AiGenerationConfig`).
- **File Selection & Filtering** – apply extension, size, and exclude globs.
- **Condition Evaluation** – validate `AiCondition` trees against file metadata (`AiConditionEvaluator`).
- **Fact Extraction** – build deterministic fact blocks from regex counters (`AiFactExtractor`).
- **Oversize Handling** – decide on failure, sampling, or split strategies (`AiOversizeStrategy`).
- **AI Generation Abstraction** – provide a uniform `AiGenerationProvider` API for local and remote LLMs.
- **Provider Selection & Instantiation** – resolve provider keys via `AiGenerationProviderFactory`.
- **Local Inference** – load GGUF models via llama.cpp JNI (`LlamaCppJniAiGenerationProvider`).
- **Remote API Support** – construct and send OpenAI‑compatible requests (`OpenAiCompatibleProviderSupport`).
- **Response Parsing** – strip internal reasoning blocks (`AiCompletionParser`).

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for run‑time settings.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – deterministic fact counter utility.
- `AiGenerationConfig` – bean for AI model and inference parameters.
- `AiOversizeStrategy` – enum for oversized‑file handling.
- `AiGenerationProvider` – interface for generating text and timings.
- `AiGenerationProviderFactory` – resolves provider keys.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference provider.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible APIs.
- `LlamaCppJniConfig` – immutable config record for llama.cpp settings.
- `AiCompletionParser` – removes internal “thinking” blocks from completions.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` populated from Maven/JSON/YAML.
2. **File Selection** – filter by extensions, size, exclude globs.
3. **Condition Evaluation** – `AiConditionEvaluator.matches` checks `AiCondition` trees; `usesLines` decides if source text is needed.
4. **Fact Extraction** – `AiFactExtractor.factsBlock` generates a facts header if required.
5. **AI Generation Request** – build `AiGenerationRequest` (prompt key, source file, text, header).
6. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` chooses key; `create` returns matching provider.
7. **Generation** – provider’s `generate`/`generateWithTimings` runs inference (local or remote).
8. **Post‑processing** – `AiCompletionParser.parseCompletion` strips reasoning blocks.
9. **Oversize Handling** – `AiOversizeStrategy` dictates action for files exceeding context windows.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External** – Java standard libraries (`java.time`, `java.util`, `java.io`, `java.util.regex`), Lombok (`@ToString`), JSPECIFY annotations, `net.ladenthin.llama` (model, parameters, JSON parsing), Jackson (`com.fasterxml.jackson`), `java.net.URL`, `java.io.IOException`.

#### Cross‑cutting
- **Thread Safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless; providers are generally stateless after construction; local provider reuses a single `LlamaModel` instance (single‑threaded unless externally synchronized).
- **Immutability** – `LlamaCppJniConfig` is a record‑style immutable holder; provider factories are stateless.
- **Error Handling** – `IllegalArgumentException` for misconfiguration or unknown provider keys; `IOException` propagates from provider failures.
- **Timing Metrics** – `AiGenerationTimings` captures generated text, token counts, and throughput; local provider parses `Timings` from llama.cpp JSON, remote provider derives usage from OpenAI’s `usage` field.
