### java
- H: 1.0
- C: 64C7F162
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:32:55Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Java package that configures and runs AI-driven source-file summarisation, selecting files, sizing oversize inputs, and generating per-file text via pluggable local or remote model providers.

#### Purpose
- Controls how srcmorph selects, sizes, and summarises Java source files for AI-generated indexes.
- Makes AI text generation pluggable across mock, local llama.cpp JNI, and OpenAI-compatible backends.

#### Responsibilities
- Configuration model (`config/`): root run settings in `SrcMorphConfiguration`, per-step model parameters in `AiGenerationConfig`, with field names mirroring Maven `@Parameter` names.
- File selection and facts (`config/`): nested `and`/`or`/`not` condition trees evaluated by `AiConditionEvaluator`; regex-based `label: count` facts from `AiFactExtractor` prepended to oversize summaries.
- Oversize policy (`config/`): `AiOversizeStrategy` maps `fail`, `sample`, `mapReduce`, `deterministic` tokens; default `FAIL`.
- Provider contract and selection (`provider/`): `AiGenerationProvider` interface with `generate`, `generateWithTimings`, `close`; `AiGenerationProviderFactory` resolves `mock`, `llamacpp-jni`, `openai-compatible`.
- Local inference (`provider/`): `LlamaCppJniAiGenerationProvider` lazily loads a GGUF model via `LlamaModel` and maps `Timings` to `AiGenerationTimings`.
- Remote inference (`provider/`): `OpenAiCompatibleProviderSupport` builds chat-completions JSON, endpoint URLs, API key lookup, and HTTP error messages; socket code is in `OpenAiCompatibleAiGenerationProvider`, outside this summary set.
- Output cleanup (`provider/`): `AiCompletionParser` strips Gemma-4 `<|channel>thought` ... `<channel|>` thinking blocks.

#### Key units
- `SrcMorphConfiguration`: root mutable JavaBean for all run settings.
- `AiGenerationConfig`: per-step generation parameters with `DEFAULT_*` constants and `-1`/empty "do not send" sentinels.
- `AiConditionEvaluator`: stateless `matches`, `validate`, `usesLines` over condition trees.
- `AiConditionGroup`: child-condition list for `and`/`or` combinators.
- `AiFactExtractor`: static `factsBlock`, `validate`, `countMatches`.
- `AiOversizeStrategy`: enum with `configValue()` and `fromConfig(String)`.
- `AiGenerationProvider`: backend interface extending `AutoCloseable`.
- `AiGenerationProviderFactory`: `create(...)` and `effectiveProvider(...)`; `PROVIDER_*` key constants.
- `LlamaCppJniAiGenerationProvider`: local GGUF generation over `LlamaModel`.
- `OpenAiCompatibleProviderSupport`: package-private offline helper for request body, endpoint, and error handling.
- `LlamaCppJniConfig`: immutable settings value with nested `Builder`, `builder(modelPath)`.
- `AiCompletionParser`: `parseCompletion(String)` with thinking-block markers.

#### Data flow
- Maven/YAML/code → `SrcMorphConfiguration` (`fieldGenerations`, `factDefinitions`, `aiDefinitions`).
- `AiConditionEvaluator.validate` → `matches` over `AiFileContext` facts; `usesLines` gates line counting.
- Oversize file → `AiFactExtractor.factsBlock` facts header → `AiOversizeStrategy` handling.
- `AiGenerationConfig` values → `AiGenerationProviderFactory.effectiveProvider` → `create(...)` → concrete provider.
- Local: `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser.parseCompletion`; `Timings` → `AiGenerationTimings`.
- Remote: `requestBody` JSON → HTTP POST to `endpoint()` → `ChatResponse`/`Usage` → `timings(...)`.

#### Dependencies
- Internal: `AiSourceExcludeFilter`, `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ConvertToRecord`.
- External: `java.time.Instant`, `java.util.regex`, `java.io.File`, `net.ladenthin.llama` (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `Usage`, `ChatResponseParser`, `FlashAttn`, `ReasoningFormat`), Jackson `ObjectMapper`/`ObjectNode`/`ArrayNode`, Lombok, `org.jspecify.annotations.Nullable`.
- Referenced, not summarised: `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`, `AiGenerationTimings`.

#### Cross-cutting
- Config beans: mutable JavaBeans with no-arg constructors, setters, Lombok `@ToString`; no `equals`/`hashCode`; `DEFAULT_*` constants centralised.
- Validation: `IllegalArgumentException` for bad conditions, fact regexes, oversize tokens, unknown provider keys, non-http(s) `baseUrl`; `IllegalStateException` for malformed condition nodes; `IOException` for provider failures and cleartext-key rejection; `NullPointerException` via `Objects.requireNonNull`.
- Null handling: jspecify `@Nullable`; null strings fall back to defaults; null lists become empty.
- Security: API keys read from environment variables named in `apiKeyEnv`; plain `http` only for loopback hosts.
- Concurrency: config beans not synchronized; `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiCompletionParser` are stateless or immutable; `LlamaCppJniAiGenerationProvider.model()` lazy init is not thread-safe; `LlamaCppJniConfig.Builder` is single-thread only.
