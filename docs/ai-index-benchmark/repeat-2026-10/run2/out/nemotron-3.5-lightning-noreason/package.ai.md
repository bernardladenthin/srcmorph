### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:10Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> ai-assisted source code transformation pipelines, configuring condition-based file selection, generation parameters, and oversize handling across pluggable AI backends.

#### Purpose
The `net.ladenthin.srcmorph` package orchestrates AI-assisted source code transformation, integrating condition-based file filtering, generation configuration, and pluggable AI provider backends (mock, llama.cpp JNI, OpenAI-compatible) to enable automated source code transformation workflows.

#### Responsibilities
* **AI-assisted source transformation pipeline**: Coordinates the end-to-end workflow from file selection via AI conditions to generation output via configured providers.
* **Condition-based file filtering**: Evaluates `AiCondition` trees to select files for AI processing based on extensions, size, modification time, path patterns, and line counts.
* **Generation configuration and parameter distribution**: Holds `AiGenerationConfig` and `AiOversizeStrategy` to define how AI generation steps execute, including model selection, sampling parameters, and context-window overflow handling.
* **Pluggable AI provider execution**: Selects and instantiates `AiGenerationProvider` implementations (mock, llama.cpp JNI, OpenAI-compatible) to produce text outputs from model inference.
* **Root configuration aggregation**: Aggregates all package-level configuration into `SrcMorphConfiguration`, the single shared object bindable from Maven plugins, JSON/YAML, or direct Java construction.

#### Key units
* **`SrcMorphConfiguration`**: Root mutable JavaBean holding every parameter a `srcmorph` run needs; the central config object bindable from every surface (Maven, JSON/YAML, direct Java).
* **`AiConditionEvaluator`**: Walks `and`/`or`/`not` condition trees to determine file inclusion based on file contexts (path, size, extensions, modification time, line count).
* **`AiGenerationConfig`**: Configuration object carrying all parameters for a single AI generation step (model path, sampling parameters, retry policy, trimming limits, stop strings).
* **`AiOversizeStrategy`**: Enum defining strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling source files exceeding a model's context window.
* **`AiGenerationProvider` (interface)**: Pluggable backend producing text for generation requests; extended by `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
* **`AiGenerationProviderFactory`**: Selects and instantiates an `AiGenerationProvider` implementation by name; defaults to mock when provider name is blank or null.
* **`LlamaCppJniAiGenerationProvider`**: Runs local GGUF models through the llama.cpp JNI bridge; lazily initializes native LlamaModel; builds chat template kwargs and model parameters; executes inference; parses JSON response.
* **`OpenAiCompatibleAiGenerationProvider`**: Constructs JSON request bodies and endpoints for OpenAI-compatible model calls; validates API key transmission; formats error messages.
* **`AiCompletionParser`**: Strips leading Gemma-4 thinking blocks from raw completion text to extract the model answer.

#### Data flow
1. **File selection**: `SrcMorphConfiguration` provides file filters and AI settings; `AiConditionEvaluator.matches()` walks condition trees to accept or reject files based on their context (path, size, extensions, modification time, line count).
2. **Oversize determination**: For files passing the condition filter, `AiOversizeStrategy` determines the strategy (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) if the file's size or line count exceeds the model's context window defined by `AiGenerationConfig` limits.
3. **Provider instantiation**: `AiGenerationProviderFactory.create(providerName, llamaConfig, promptSupport)` selects and instantiates the requested `AiGenerationProvider` implementation.
4. **Generation execution**: The selected provider's `generate(AiGenerationRequest)` or `generateWithTimings(AiGenerationRequest)` method is called. For llama.cpp, this lazily initializes a native model, builds chat/template parameters, executes inference via `model().chatComplete(...)`, and parses the JSON response via `ChatResponseParser`, ultimately cleaning text via `AiCompletionParser`. For OpenAI-compatible, `OpenAiCompatibleAiGenerationProvider` constructs the JSON body and endpoint, validates the API key, and executes the request.

#### Dependencies
* **Internal collaborations**: `AiConditionEvaluator` depends on `AiFileContext` and `AiSourceExcludeFilter`; `AiGenerationConfig` references `AiCalibration`; `SrcMorphConfiguration` references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`; `AiGenerationProviderFactory` depends on `AiGenerationProvider` implementations and `LlamaCppJniConfig`; `LlamaCppJniAiGenerationProvider` depends on `net.ladenthin.llama.*` native bindings and `AiGenerationRequest`/`AiGenerationTimings`.
* **External modules/packages**: `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `org.jspecify.annotations.Nullable`, `lombok.ToString`, `lombok.EqualsAndHashCode`; Jackson `ObjectMapper` for JSON serialization; `org.jspecify` for null annotations.
* **Configuration binding**: `SrcMorphConfiguration` intentionally mirrors Maven `@Parameter` field names to enable binding from Maven plexus reflection, Jackson ObjectMapper/YAMLMapper, or direct Java construction.

#### Cross-cutting
* **Defensive copying**: `AiConditionGroup.setConditions()` defensively copies input collections; `AiGenerationConfig` resets list setters to empty on null input and returns unmodifiable views.
* **Null-safety**: `@Nullable` annotations are prevalent on getters/parameters for model paths, chat template thinking, cache types, devices, provider, remote model, base URL, API key env, and sequence breakers/stops lists. `AiGenerationConfig` setters null-safe replace nulls with defaults for lazy mode, cache type K/V, devices, provider, remote model, base URL, and API key env.
* **Statelessness**: `AiConditionEvaluator` and `AiFactExtractor` methods are stateless and side-effect free; `AiOversizeStrategy` is a pure enum with no mutable state.
* **Immutability patterns**: `AiConditionGroup` is final with defensive copies; `AiFactExtractor` is a utility class with a private constructor; `AiOversizeStrategy` is an enum with constants; `LlamaCppJniConfig` is immutable after `build()`.
* **Builder pattern**: `LlamaCppJniConfig` uses a fluent builder with thirty-seven setters; builder is not thread-safe, config is immutable after `build()`.
