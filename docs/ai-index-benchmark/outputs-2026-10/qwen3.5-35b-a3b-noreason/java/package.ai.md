### java
- H: 1.0
- C: 7EEE2668
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:57:51Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Orchestrates AI-driven source code analysis and generation by managing configuration validation, file filtering logic, and pluggable backend execution for local or remote language models.

#### Purpose
- Aggregates and validates configuration for AI model routing, file filtering, and generation parameters within the srcmorph build tool.
- Provides a pluggable architecture for executing text generation via local (llama.cpp) or remote (OpenAI-compatible) backends with response sanitization.

#### Responsibilities
- **Condition Evaluation**: Recursively evaluates logical condition trees (`and`, `or`, `not`) against file attributes to determine inclusion.
- **Configuration Management**: Serves as the central mutable JavaBean for passing AI generation parameters, model definitions, and file scope settings.
- **Fact Extraction**: Computes exact, whole-file regex-based statistics (label: count pairs) from source code for AI summaries.
- **Strategy Handling**: Defines and parses strategies for handling source files exceeding the model's context window.
- **Provider Orchestration**: Factory logic to resolve configuration keys to specific backend implementations (Mock, LlamaCpp, OpenAI).
- **Local Model Execution**: Manages lazy loading, GPU/CPU offloading, and KV-cache reuse for `llama.cpp` native bindings.
- **Remote Request Construction**: Serializes prompts to JSON, validates endpoint security, and handles API key transmission.
- **Response Sanitization**: Parses raw completion text to remove chain-of-thought markers and extract final answers.

#### Key units
- `AiConditionEvaluator`: Evaluates `AiCondition` trees against `AiFileContext` to determine matches and validates configuration integrity.
- `AiConditionGroup`: Container for child `AiCondition` instances enabling `<and>` and `<or>` combinator elements in Maven configuration.
- `AiFactExtractor`: Utility class generating deterministic "facts" blocks by counting non-overlapping regex pattern matches in source code.
- `AiGenerationConfig`: Mutable JavaBean carrying AI generation parameters including model paths, sampling strategies, and hardware offloading settings.
- `AiOversizeStrategy`: Enum defining behaviors for files exceeding context windows (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `SrcMorphConfiguration`: Central configuration bean aggregating file filtering rules, AI provider settings, and model definitions.
- `AiFileContext`: Context object holding file facts (name, size, line count, modification time) used for condition evaluation.
- `AiRangeCondition`: Component defining bounds for `size` and `lines` fields within condition trees.
- `AiGenerationProvider`: Interface defining `generate` and `generateWithTimings` contracts for pluggable backends.
- `AiGenerationProviderFactory`: Resolves provider names to `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider`: Implements local generation using `LlamaModel` with lazy initialization and parameter mapping.
- `LlamaCppJniConfig`: Immutable value object holding 37 configuration options for model loading and sampling.
- `OpenAiCompatibleProviderSupport`: Stateless utility for JSON request building, endpoint validation, and secure API key routing.
- `AiCompletionParser`: Strips `<|channel>thought` blocks from raw text to prevent reasoning data leakage.
- `AiGenerationTimings`: Data carrier for performance metrics (tokens/sec, cache hits) returned by providers.

#### Data flow
- **Configuration Ingestion**: `SrcMorphConfiguration` and `AiGenerationConfig` are populated via Maven plugin setters or external files, establishing defaults for models, prompts, and file filters.
- **File Filtering**: `AiConditionEvaluator` traverses `AiCondition` trees using `AiFileContext` data to determine if a file matches inclusion criteria or requires line counting.
- **Fact Generation**: `AiFactExtractor` processes source strings against `AiFactCounter` definitions to produce formatted statistics blocks.
- **Strategy Resolution**: `AiOversizeStrategy.fromConfig` parses configuration tokens to select handling logic for oversized files.
- **Provider Resolution**: `AiGenerationProviderFactory` selects the implementation based on `runProvider` or `modelProvider` keys.
- **Execution**:
    - **Local**: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, maps config to `ModelParameters`, and executes `generate()`.
    - **Remote**: `OpenAiCompatibleProviderSupport` builds JSON payload, validates HTTPS for API keys, and constructs the request.
- **Sanitization**: Raw `String` response from the backend is passed to `AiCompletionParser.parseCompletion()` to remove thinking blocks.
- **Output**: Clean text and `AiGenerationTimings` are returned to the caller.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`.
- **External**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for path glob evaluation; `java.time` for date parsing; `java.util.regex` for pattern matching; `lombok` for boilerplate reduction; `org.jspecify.annotations` for null safety; `com.fasterxml.jackson.databind.ObjectMapper` for JSON serialization.
- **Native**: `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`.

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive copying (e.g., `AiConditionGroup.setConditions`) to handle optional configuration fields.
- **Validation**: `AiConditionEvaluator` and `AiFactExtractor` enforce strict configuration integrity, throwing `IllegalArgumentException` for missing branches, invalid regex, or malformed dates.
- **State Management**: Configuration classes (`AiGenerationConfig`, `SrcMorphConfiguration`) are mutable JavaBeans intended for single-threaded population, lacking thread-safety guarantees.
- **Immutability**: `LlamaCppJniConfig` and `AiGenerationTimings` are immutable value types; `OpenAiCompatibleProviderSupport` is stateless.
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS for API keys on non-loopback hosts, throwing `IOException` on insecure transmission.
- **Error Handling**: Consistent wrapping of `DateTimeParseException` and `PatternSyntaxException` into `IllegalArgumentException`; `AiCompletionParser` throws `IOException` on truncated reasoning blocks; Factory throws `IllegalArgumentException` for unknown providers.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable` to ensure native `LlamaModel` handles are closed.
