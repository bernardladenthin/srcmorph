### java
- H: 1.0
- C: 17C81C7A
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:04:51Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Evaluates source-file conditions, extracts deterministic facts, and carries mutable AI generation and run configuration for srcmorph processing, while resolving and instantiating pluggable AI text-generation providers for source morphing, supporting local llama.cpp JNI models and OpenAI-compatible backends, and parsing model completions to strip internal thinking blocks.

#### Purpose
- Provides configuration types, validation, and mutable run parameters for matching source files against AI condition trees and generating AI summaries.
- Provides the AI generation backend abstraction, provider factory, local llama.cpp JNI and OpenAI-compatible provider implementations, and completion parsing to strip thinking blocks.

#### Responsibilities
- Condition tree evaluation and validation for file selection (`AiConditionEvaluator`, `AiConditionGroup`).
- Deterministic fact counting and facts-block assembly for AI input (`AiFactExtractor`).
- Mutable JavaBean configuration for a single AI generation step (`AiGenerationConfig`).
- Mutable JavaBean configuration for an entire srcmorph run (`SrcMorphConfiguration`).
- Oversize-file handling strategy parsing (`AiOversizeStrategy`).
- Provider contract and default timing behavior (`AiGenerationProvider`).
- Provider selection and instantiation by provider key (`AiGenerationProviderFactory`).
- Local GGUF model execution via llama.cpp JNI (`LlamaCppJniAiGenerationProvider`).
- Immutable llama.cpp configuration with thirty-seven parameters (`LlamaCppJniConfig`).
- OpenAI-compatible request building, endpoint validation, API key handling, and timing computation without network I/O (`OpenAiCompatibleProviderSupport`).
- Completion text parsing that strips leading thinking blocks (`AiCompletionParser`).

#### Key units
- `AiConditionEvaluator` — recursively evaluates and/or/not condition trees against `AiFileContext` facts; validates tree configuration; detects line-count requirements.
- `AiConditionGroup` — nullable container for child `AiCondition` elements in and/or combinators, with defensive copy on set.
- `AiFactExtractor` — stateless utility that counts regex matches for fact labels and builds a labeled facts block string.
- `AiGenerationConfig` — mutable JavaBean holding model path, sampling, retry, trimming, and stop-string parameters for one AI generation step.
- `SrcMorphConfiguration` — central mutable JavaBean for all srcmorph run parameters, including output paths, filters, provider/model settings, prompts, fields, facts, and header version metadata.
- `AiOversizeStrategy` — enum parsing `<onOversize>` config tokens into `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC`.
- `AiGenerationProvider` — interface defining `generate`, `generateWithTimings`, and `close`; extends `AutoCloseable`.
- `AiGenerationProviderFactory` — stateless factory resolving mock, llama.cpp JNI, or OpenAI-compatible providers.
- `LlamaCppJniAiGenerationProvider` — final provider implementing local GGUF generation with lazy native model loading.
- `LlamaCppJniConfig` — immutable value type holding thirty-seven generation parameters via a fluent Builder.
- `OpenAiCompatibleProviderSupport` — package-private helper for OpenAI-compatible request bodies, endpoints, API keys, and timings.
- `AiCompletionParser` — stateless parser stripping Gemma-4 thinking blocks from raw completion text.

#### Data flow
- `SrcMorphConfiguration` supplies run-level scope (subtrees, excludes, extensions, size bounds) and AI definitions to the engine.
- `AiConditionEvaluator.matches` consumes `AiCondition` trees and `AiFileContext` to decide whether a file is in scope.
- `AiConditionEvaluator.usesLines` signals whether the indexer must read file contents for line counting.
- `AiFactExtractor.factsBlock` consumes `List<AiFactCounter>` and source text to produce a deterministic facts block prepended to AI summaries.
- `AiGenerationConfig` carries per-step generation parameters from Maven configuration to AI provider implementations.
- `AiOversizeStrategy.fromConfig` converts the `<onOversize>` token into a strategy constant for oversized-file handling.
- `AiGenerationProviderFactory.effectiveProvider` selects the provider key from model or run configuration, defaulting to mock when blank.
- `AiGenerationProviderFactory.create` instantiates the chosen provider, injecting `LlamaCppJniConfig` and `AiPromptSupport`.
- `LlamaCppJniAiGenerationProvider.generate` builds model parameters from config, lazily loads the native `LlamaModel`, calls `chatComplete`, and pipes the response through `AiCompletionParser.parseCompletion` to strip thinking blocks.
- `OpenAiCompatibleProviderSupport.requestBody` serializes a Jackson JSON chat-completions body; `apiKeyFor` guards against cleartext keys over non-loopback HTTP; `timings` derives `AiGenerationTimings` from `ChatResponse` usage and wall-clock seconds.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `net.ladenthin.srcmorph.support.ConvertToRecord`.
- External: `java.io.File`, `java.io.IOException`, `java.net.URL`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `java.util.Map`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`, `java.lang.AutoCloseable`, `net.ladenthin.llama.*` (LlamaModel, ModelParameters, ChatResponse, Timings, Usage, ChatResponseParser, FlashAttn, ReasoningFormat), Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok (`@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"}`), `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Mutable JavaBeans (`AiGenerationConfig`, `SrcMorphConfiguration`) are not thread-safe and intended for single-threaded population before use.
- Stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`) are thread-safe with no shared mutable state.
- `AiConditionGroup` and `AiGenerationConfig` defensively copy or null-coerce mutable collections in setters; list getters return unmodifiable views where applicable.
- `validate` methods fail fast with `IllegalArgumentException` on misconfigured condition trees, fact counters, or unrecognized oversize strategy tokens.
- Lombok `@ToString` used across `AiCompletionParser`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, and `OpenAiCompatibleProviderSupport`; `LlamaCppJniConfig` also uses `@EqualsAndHashCode` and `@ConvertToRecord`.
- Nullability expressed via `org.jspecify.annotations.Nullable` and `Java8CompatibilityHelper.isBlank` for blank-string handling.
- `AiGenerationProvider` and `LlamaCppJniAiGenerationProvider` implement `AutoCloseable` for native resource cleanup.
- `LlamaCppJniAiGenerationProvider` is not thread-safe due to unsynchronized lazy model initialization; other units are stateless or effectively immutable and safe for concurrent use.
- `IllegalArgumentException` is thrown by the factory and `OpenAiCompatibleProviderSupport.endpoint` for unrecognized providers or invalid URL schemes; `IOException` surfaces JSON serialization, missing thinking-block end markers, and cleartext API key violations.
