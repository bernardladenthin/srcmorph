### java
- H: 1.0
- C: 2170A0D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:45:13Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Orchestrates deterministic AI-driven source code summarization and generation by managing configuration, file filtering, and pluggable inference backends.

#### Purpose
- Aggregates and validates all parameters for AI model inference, file filtering, and generation strategies.
- Orchestrates the selection, instantiation, and execution of diverse AI generation providers (local, remote, or mock) while sanitizing outputs.

#### Responsibilities
- **Configuration Aggregation**: Centralizes Maven plugin parameters, model paths, and inference settings in `SrcMorphConfiguration` and `AiGenerationConfig`.
- **Condition Evaluation**: Recursively matches file attributes (size, path, extensions) against logical trees in `AiConditionEvaluator`.
- **Metric Extraction**: Generates deterministic regex-based counts of code elements (functions, tables) for AI context augmentation.
- **Strategy Resolution**: Maps configuration strings to specific handling behaviors for oversized files via `AiOversizeStrategy`.
- **Provider Orchestration**: Factory logic resolves effective provider keys and instantiates concrete implementations (`Mock`, `LlamaCppJni`, `OpenAiCompatible`).
- **Local Inference Execution**: Manages native llama.cpp model loading, parameter configuration, and inference execution with timing metrics.
- **Remote API Integration**: Constructs JSON payloads for OpenAI-compatible endpoints, validates HTTPS/loopback security, and parses HTTP responses.
- **Response Sanitization**: Parses raw completion strings to remove specific chain-of-thought markers (e.g., Gemma-4 thinking blocks) before persisting results.

#### Key units
- `SrcMorphConfiguration`: Central mutable JavaBean aggregating global project settings, model paths, and generation parameters.
- `AiGenerationConfig`: Mutable carrier for AI inference parameters including temperature, context size, GPU layers, and sampling strategies.
- `AiConditionEvaluator`: Recursive engine evaluating `AiCondition` trees against `AiFileContext` to determine file matches.
- `AiFactExtractor`: Static utility computing exact regex-based counts of code elements to prepend as facts blocks.
- `AiOversizeStrategy`: Enum defining fallback behaviors (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for files exceeding context limits.
- `AiGenerationProviderFactory`: Resolves provider priority and dispatches instantiation to concrete classes based on string constants.
- `LlamaCppJniAiGenerationProvider`: Implements the provider interface using native JNI handles for GGUF models with lazy initialization.
- `OpenAiCompatibleProviderSupport`: Serializes requests to JSON, validates endpoint security schemes, and formats error messages.
- `AiCompletionParser`: Strips `<|channel>thought` blocks from raw text to prevent reasoning leakage in persisted data.
- `LlamaCppJniConfig`: Immutable value object holding 37 configuration fields for local model tuning via a strict builder pattern.
- `AiGenerationProvider`: Interface contract defining `generate`, `generateWithTimings`, and resource cleanup methods.

#### Data flow
1.  **Configuration Ingestion**: Maven plugin or external source populates `SrcMorphConfiguration` and `AiGenerationConfig` with project paths, model definitions, and inference parameters.
2.  **File Filtering**: `AiConditionEvaluator` traverses `AiCondition` trees using `AiFileContext` data to validate file inclusion based on size, path globs, and extensions.
3.  **Metric Calculation**: For included files, `AiFactExtractor` applies `AiFactCounter` regex patterns to source text to generate deterministic fact strings.
4.  **Strategy Application**: If file size exceeds `AiGenerationConfig` context limits, `AiOversizeStrategy` determines whether to fail, sample, or map-reduce the input.
5.  **Provider Resolution**: `AiGenerationProviderFactory` receives run-level and model-level provider keys, resolving them to an effective name (model overrides run).
6.  **Inference Execution**:
    - For local: `LlamaCppJniAiGenerationProvider` lazily loads the `LlamaModel`, constructs `ModelParameters` from config, and executes `chatComplete()`.
    - For remote: `OpenAiCompatibleProviderSupport` builds a JSON payload, validates the URL scheme and API key security, and sends the request.
7.  **Response Handling**: Both paths return raw text or `ChatResponse` objects; `generateWithTimings` extracts token counts and calculates throughput rates.
8.  **Sanitization**: Raw completion strings are passed to `AiCompletionParser.parseCompletion()` to strip thinking blocks before returning the final answer.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.document.AiGenerationRequest`.
- **External**: `java.time.Instant`, `java.util.regex`, `java.io.File`, `lombok.ToString`, `org.jspecify.annotations.Nullable`, `com.fasterxml.jackson.databind`, `java.io.IOException`.
- **Native**: `llama.cpp` library accessed via JNI for model inference (`net.ladenthin.llama.*`).
- **Infrastructure**: Maven Plexus (implied via configuration binding), Jackson/YAML (implied via JSON/YAML support).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive copying (e.g., `AiConditionGroup`) to prevent `NullPointerException` during configuration binding.
- **Validation**: `AiConditionEvaluator` and `AiFactExtractor` perform strict validation (e.g., regex syntax, non-empty lists) to fail fast on misconfiguration.
- **Thread Safety**: `AiFactExtractor` and `AiOversizeStrategy` are stateless and thread-safe; `SrcMorphConfiguration` and `AiGenerationConfig` are mutable and not thread-safe.
- **Immutability**: `LlamaCppJniConfig` is fully immutable after construction; `AiGenerationConfig` returns unmodifiable list views for `drySequenceBreakers` and `stopStrings`.
- **Security**: `OpenAiCompatibleProviderSupport` enforces strict checks preventing API key transmission over unencrypted HTTP unless the host is loopback.
- **Resource Management**: Providers implement `AutoCloseable` to ensure native `LlamaModel` handles are released safely.
