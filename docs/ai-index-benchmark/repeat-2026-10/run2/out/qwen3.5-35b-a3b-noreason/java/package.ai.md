### java
- H: 1.0
- C: 5BF387FF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:05:35Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Orchestrates AI-driven source code summarization by managing configuration validation, file filtering, and pluggable text generation across local and remote backends.

#### Purpose
- Aggregates Maven plugin bindings and runtime settings to control AI model behavior, file selection criteria, and output formatting.
- Provides a pluggable architecture for local and remote AI text generation, managing model lifecycle and response parsing.

#### Responsibilities
- **Condition Evaluation**: Recursively validates and matches `AiCondition` trees against file metadata (size, lines, paths) using logical operators.
- **Configuration Management**: Serves as a mutable JavaBean carrier for global project settings, fallback Llama.cpp parameters, and provider-specific options.
- **Fact Extraction**: Computes deterministic counts of structural elements (e.g., functions, rows) via regex patterns to prepend to AI summaries.
- **Strategy Handling**: Defines behaviors for processing files that exceed model context windows (fail, sample, map-reduce, deterministic).
- **Provider Abstraction**: Defines the `AiGenerationProvider` contract for text generation with optional timing metrics.
- **Factory Resolution**: Instantiates specific providers (`Mock`, `LlamaCppJni`, `OpenAiCompatible`) based on effective configuration keys.
- **Local Inference**: Manages native `llama.cpp` model loading, GPU offloading, KV-cache reuse, and parameter configuration.
- **Remote Compatibility**: Constructs JSON payloads, validates API key security, and parses HTTP responses for OpenAI-compatible endpoints.
- **Response Sanitization**: Parses raw completion text to remove chain-of-thought markers and validate block closure.

#### Key units
- `AiConditionEvaluator`: Evaluates `AiCondition` trees against `AiFileContext` to determine file matches and validates tree structure.
- `AiConditionGroup`: Container for child `AiCondition` instances enabling `<and>` and `<or>` logical combinators in configuration.
- `AiFactExtractor`: Static utility generating formatted facts blocks by counting regex matches in source text.
- `AiGenerationConfig`: Mutable JavaBean holding detailed AI sampling parameters, GPU/CPU offloading settings, and retry policies.
- `AiOversizeStrategy`: Enum defining handling modes (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files.
- `SrcMorphConfiguration`: Central aggregator for file filters, prompt definitions, model definitions, and fallback Llama.cpp defaults.
- `AiFileContext`: Data object containing file name, size, line count, modification time, and path used during evaluation.
- `AiRangeCondition`: Component within condition trees defining bounds for size or line count comparisons.
- `AiSourceExcludeFilter`: Utility delegated for glob pattern matching on file paths.
- `AiGenerationProvider`: Interface defining `generate`, `generateWithTimings`, and `close` for pluggable backends.
- `AiGenerationProviderFactory`: Resolves effective provider names and instantiates concrete implementations.
- `LlamaCppJniAiGenerationProvider`: Native JNI wrapper for `LlamaModel` with lazy loading and hardware offloading.
- `LlamaCppJniConfig`: Immutable builder-based configuration for model paths, sampling, and GPU/CPU layer settings.
- `OpenAiCompatibleProviderSupport`: Stateless utility for JSON request construction, security validation, and timing calculation.
- `AiCompletionParser`: Utility to strip `<|channel>thought` blocks and extract clean answers from raw LLM responses.
- `AiGenerationRequest`: Input object containing prompt keys, source files, and context headers.
- `AiGenerationTimings`: Wrapper for generated text and performance metrics (tokens/sec, cache hits).

#### Data flow
1. **Initialization**: `SrcMorphConfiguration` aggregates user inputs (Maven params, files) and populates `AiGenerationConfig` with model/sampling details.
2. **Filtering**: `AiConditionEvaluator` traverses the configured `AiCondition` tree, querying `AiFileContext` properties (via `matches`, `usesLines`).
3. **Validation**: `validate` ensures condition trees are structurally sound before execution; `AiFactExtractor.validate` checks regex syntax.
4. **Extraction**: If `usesLines` is true, content is read; `AiFactExtractor.factsBlock` computes counts using `AiFactCounter` patterns.
5. **Routing**: Oversized files trigger `AiOversizeStrategy` logic; valid files proceed to generation with `AiGenerationConfig` parameters.
6. **Resolution**: `AiGenerationProviderFactory` resolves the effective provider key (model vs. run config) and instantiates the provider.
7. **Execution**:
   - **Local**: `LlamaCppJniAiGenerationProvider` initializes `LlamaModel` (lazy), applies `ModelParameters`, and calls `chatComplete`.
   - **Remote**: `OpenAiCompatibleProviderSupport` builds a JSON payload, validates API key security, and prepares the request.
8. **Parsing**: Raw response text is passed to `AiCompletionParser` to remove thinking blocks.
9. **Metrics**: `AiGenerationTimings` is populated with token counts and rates from `ChatResponse` or `Usage` objects.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.document`, `net.ladenthin.srcmorph.prompt`.
- **External Support**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- **External Libraries**: `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (annotations), `org.jspecify` (null safety).
- **Native**: `net.ladenthin.llama` (native bindings, `LlamaModel`, `ChatResponse`), `llama.cpp` JNI bindings for model inference.
- **Java Standard**: `java.util.regex`, `java.time`, `java.io.File`, `java.io`.

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations; setters often reset to defaults if null inputs are provided.
- **Immutability Patterns**: `AiConditionGroup` uses defensive copying; `AiGenerationConfig` returns unmodifiable views for list fields; `LlamaCppJniConfig` and `AiGenerationTimings` are immutable value objects.
- **Error Handling**: Validation methods throw `IllegalArgumentException` for structural violations (empty lists, invalid ranges, bad regex); `DateTimeParseException` wrapped internally; `IOException` used for generation failures and serialization errors.
- **Thread Safety**: `AiFactExtractor` is stateless and thread-safe; other classes are mutable JavaBeans not designed for concurrent access; `LlamaModel` lifecycle is single-threaded per provider instance.
- **Tri-State Logic**: `AiGenerationConfig` implements tri-state flags for optional features (e.g., thinking mode, GPU layers, DRY penalty) using specific sentinel values (-1, null, 0).
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS for non-loopback hosts when transmitting API keys.
- **Lazy Loading**: `LlamaCppJniAiGenerationProvider` defers `LlamaModel` instantiation until the first generation request.
