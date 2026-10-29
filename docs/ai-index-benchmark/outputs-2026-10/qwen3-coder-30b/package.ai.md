### ai
- H: 1.0
- C: F0404DDE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:23:29Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Configures and evaluates AI-driven code generation and analysis workflows, managing conditions, facts, and generation parameters across source files.

#### Purpose
- Defines and evaluates AI conditions for file inclusion and processing.
- Configures AI generation parameters and oversize handling strategies.
- Extracts structured facts from source code for deterministic analysis.

#### Responsibilities
- **Condition Evaluation**: Evaluates AI conditions against file metadata to determine inclusion in processing.
- **Configuration Management**: Holds and manages AI generation settings and model parameters.
- **Fact Extraction**: Counts regex-patterned elements in source code for structured analysis.
- **Oversize Handling**: Defines strategies for managing source files exceeding context limits.
- **Configuration Binding**: Supports Maven, JSON/YAML, and Java binding for AI code generation parameters.
- **AI Model Provisioning**: Provides pluggable local and remote AI text generation for source code transformation.
- **Text Generation**: Enables AI-powered source code morphing through configurable text generation backends.

#### Key units
- `AiConditionEvaluator`: Evaluates AI conditions against file metadata.
- `AiConditionGroup`: Groups child AI conditions for logical operations.
- `AiFactExtractor`: Extracts structured facts from source code using regex patterns.
- `AiGenerationConfig`: Configures AI generation parameters for model inference.
- `AiOversizeStrategy`: Defines strategies for oversized source file handling.
- `SrcMorphConfiguration`: Central configuration object for srcmorph AI workflows.
- `AiCompletionParser`: Extracts clean model answers from LLM responses by stripping thinking blocks.
- `AiGenerationProvider`: Defines the contract for AI text generation in source code workflows.
- `AiGenerationProviderFactory`: Selects and creates AI generation providers based on configuration.
- `LlamaCppJniAiGenerationProvider`: Wraps JNI-based llama.cpp for local GGUF model inference.
- `LlamaCppJniConfig`: Encapsulates 37 parameters for llama.cpp model behavior and inference settings.
- `OpenAiCompatibleProviderSupport`: Handles OpenAI-compatible API request construction and response processing.

#### Data flow
- AI conditions are evaluated against file metadata to filter source files.
- Configuration parameters guide AI model behavior during code generation.
- Source code is analyzed using regex patterns to extract structured facts.
- Oversize strategies determine how to handle files exceeding context limits.
- Configuration data flows from Maven/YAML/Java into `SrcMorphConfiguration` for unified processing.
- Input `AiGenerationRequest` objects are processed by providers to generate text responses, with timing data returned from `generateWithTimings` methods.
- Responses are parsed by `AiCompletionParser` before being stored in AI index files.

#### Dependencies
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`: For glob pattern matching.
- `java.time.Instant`, `java.time.format.DateTimeParseException`: For timestamp handling.
- `java.util.regex.Pattern`, `java.util.regex.Matcher`: For regex-based fact counting.
- `lombok.ToString`: For configuration object string representations.
- `org.jspecify.annotations.Nullable`: For nullability annotations.
- `net.ladenthin.llama`: For JNI-based model interactions.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`: For input prompts.
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`: For prompt handling.
- `net.ladenthin.srcmorph.config.AiGenerationConfig`: For default parameter values.
- `com.fasterxml.jackson.databind.ObjectMapper`: For JSON processing.
- `java.io.IOException`: For I/O operations.

#### Cross-cutting
- Null safety via `@Nullable` annotations and defensive copying.
- Immutable enums (`AiOversizeStrategy`) for thread-safe strategy definitions.
- Static, stateless methods for utility operations like regex matching and validation.
- Default values and validation in configuration classes to ensure safe usage.
- Lombok-generated `@ToString` for readable configuration objects.
- All providers implement `AiGenerationProvider` interface for consistent API.
- Configuration objects use builder pattern with immutable value semantics.
- Error handling includes actionable messages for incomplete thinking blocks and API validation.
- Thread safety considerations: `AiCompletionParser` and `LlamaCppJniConfig` are immutable; `LlamaCppJniAiGenerationProvider` is not thread-safe due to native model handle.
- Environment variable lookup for API keys via `Function<String, @Nullable String>` parameter.
- Null handling with `@Nullable` annotations and `Objects.requireNonNull` checks.
