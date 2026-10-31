### java
- H: 1.0
- C: A0452664
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:25:02Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> AI-driven source-file documentation generation with configurable file selection and pluggable local/remote LLM inference.
#### Purpose
- Configure AI documentation runs: file scope, LLM sampling/routing, prompts/facts, oversize handling.
- Generate storable header text via pluggable local GGUF and OpenAI-compatible backends with timing and shutdown.
#### Responsibilities
- File selection and validation: `and/or/not/extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob` evaluation over file context, fail-fast validation, line-count optimization flag.
- Deterministic facts and oversize fallback: regex-count facts block prepended to AI body; `FAIL/SAMPLE/MAP_REDUCE/DETERMINISTIC` oversize policy.
- Generation tuning and run root: 45-field LLM knobs, base/output dirs, subtrees/excludes/extensions, providers, prompt/model/field/fact definitions, llama.cpp fallback, provenance versions.
- Provider dispatch and inference: `mock` vs `llamacpp-jni` vs `openai-compatible` resolution, local GGUF `chatComplete` execution, remote chat-completions JSON building, thinking-block stripping, timings parsing.
#### Key units
- `SrcMorphConfiguration` — run-root bean with base/output dirs, scope, providers, prompt/model/fact definitions.
- `AiGenerationConfig` — mutable 45-field LLM knobs with `DEFAULT_*` constants, tri-state thinking flag.
- `AiConditionEvaluator` — matches/validates condition trees, `usesLines` flag.
- `AiConditionGroup` — `List<AiCondition>` wrapper for Maven `<conditions><condition>` binding.
- `AiFactExtractor` — `factsBlock`, `validate`, `countMatches` with `FACTS_HEADER/SUFFIX` formatting.
- `AiOversizeStrategy` — `FAIL/SAMPLE/MAP_REDUCE/DETERMINISTIC` parse via `configValue/fromConfig`.
- `AiGenerationProvider` — `generate/generateWithTimings/close` contract over `AiGenerationRequest` to `String/AiGenerationTimings`.
- `AiGenerationProviderFactory` — `create/effectiveProvider` dispatch with `PROVIDER_MOCK/PROVIDER_LLAMACPP_JNI/PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider` — lazy GGUF load, `model/chatResponse/generate/generateWithTimings/close`.
- `LlamaCppJniConfig` — immutable 41-field sampling/KV-cache/batch/GPU/DRY/reasoning/remote snapshot with `builder(modelPath)`.
- `OpenAiCompatibleProviderSupport` — `requestBody/endpoint/apiKeyFor/isLoopbackHost/isSuccess/errorMessage/timings`.
- `AiCompletionParser` — strips Gemma-4 `<|channel>thought` block via `parseCompletion`.
#### Data flow
- `AiFileContext(fileName,sizeBytes,lineCount,lastModifiedEpochMilli,relativePath)` + `AiCondition` -> `matches` -> selection; `usesLines` gates line counting.
- `List<AiFactCounter(label,pattern)>` + full `source:String` -> `Pattern.compile` + `Matcher.find()` -> `label: count` facts block prepended to AI body.
- `SrcMorphConfiguration` + `AiGenerationConfig/AiModelDefinition/AiPromptDefinition` -> routing plan and model resolution -> `.ai.md` generation; oversized source vs context window -> `fromConfig` -> fail/head-sample/chunk map-reduce/deterministic fallback.
- `AiGenerationRequest` -> `Factory.create` -> selected provider -> inference params/chat-template kwargs or chat-completions body -> `LlamaModel.chatComplete` or remote endpoint -> `ChatResponseParser` + `AiCompletionParser` -> `String` + `AiGenerationTimings` with truncation/cache logging.
#### Dependencies
- Internal: `AiCondition/AiRangeCondition/AiFileContext`, `AiFactCounter/AiCalibration/AiModelDefinition/AiFieldGenerationConfig/AiFactDefinition/AiPromptDefinition/AiSourceExcludeFilter`, `AiGenerationRequest/AiGenerationTimings/AiPromptSupport/LlamaCppJniProviderSupport/MockAiGenerationProvider/OpenAiCompatibleAiGenerationProvider`.
- External: `java.time.Instant`, `java.util.regex.Pattern/Matcher`, `java.util.List`, `java.io.File`, `net.ladenthin.llama` — `LlamaModel/ModelParameters/FlashAttn/ReasoningFormat/ChatResponseParser/ChatResponse/Timings/Usage`, `com.fasterxml.jackson.databind ObjectMapper/ArrayNode/ObjectNode`, `lombok.ToString/EqualsAndHashCode`, `org.jspecify.annotations.Nullable`.
#### Cross-cutting
- Mutable JavaBeans with defaults, null-tolerant setters, defensive-copy/unmodifiable `List` views; immutable shareable `LlamaCppJniConfig` with single-threaded `Builder`.
- Fail-fast validation: `IllegalArgumentException` for bad conditions/extensions/bounds/dates/globs/patterns/strategies/providers/URLs; `IllegalStateException` for branchless match; `IOException` for truncated thinking block, provider failure, insecure key, serialization failure.
- Null/blank-tolerant factory and parsing: blank provider to mock, null completion to empty string.
- Secure key routing: API key only over https or http-to-loopback (`localhost`, `127.x`, `[::1]`).
- Concurrency split: stateless thread-safe `AiConditionEvaluator/AiFactExtractor` vs non-synchronized mutable configs and unsynchronized lazy `LlamaModel` init.
