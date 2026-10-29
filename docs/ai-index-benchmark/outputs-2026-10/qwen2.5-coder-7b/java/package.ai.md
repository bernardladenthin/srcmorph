### java
- H: 1.0
- C: BBEDBCD3
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:18:18Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Manages AI-related configurations and provides a pluggable interface for generating text using AI.

#### Purpose
- Manages AI-related configurations.
- Handles condition evaluation and validation.
- Extracts facts from source code.
- Configures AI generation parameters.
- Provides a pluggable interface for generating text using AI.
- Supports both local and mock implementations.
- Manages configuration and request handling for AI generation providers.
- Parses raw LLM completion text to extract the model's answer.
- Implements an AI generation service using the Llama.cpp JNI binding.
- Caches the GGUF model for subsequent generation calls.
- Parses chat responses and extracts completion text.
- Supports logging and warning on truncated answers.
- Measures and returns generation timings.

#### Responsibilities
- **Configuration Management**: Manages AI-related configurations, including condition evaluation, fact extraction, and generation settings.
- **Fact Extraction**: Extracts facts from source code.
- **AI Generation Interface**: Defines a contract for AI backend providers that can generate text based on an `AiGenerationRequest`.
- **Provider Factory**: Selects and instantiates an `AiGenerationProvider` implementation by name.
- **Completion Parsing**: Parses raw LLM completion text to isolate the model's answer and strip internal reasoning blocks.
- **Thread Safety**: Ensures the provider is designed to be thread-safe with lazy initialization of the native model handle.

#### Key units
- `AiConditionEvaluator` - Evaluates and validates `AiCondition` trees.
- `AiFactExtractor` - Computes a deterministic "facts" block from `AiFactCounter`s.
- `AiGenerationConfig` - Manages configuration parameters for AI generation.
- `AiOversizeStrategy` - Configures handling strategies for oversized source files.
- `SrcMorphConfiguration` - Holds configuration parameters for `srcmorph` runs.
- `AiGenerationProvider`: Interface for generating text using AI.
- `LlamaCppJniAiGenerationProvider`: Class implementing AI generation using the Llama.cpp JNI binding.
- `LlamaCppJniConfig`: Immutable configuration class for the llama.cpp JNI provider.
- `AiCompletionParser`: Class for parsing raw LLM completion text to extract the model's answer.
- `AiGenerationProviderFactory`: Factory class for creating `AiGenerationProvider` instances based on a provider name.

#### Data flow
- **Inputs**: `AiCondition` trees, `AiFileContext`, `AiFactCounter`s, and configuration settings.
- **Outputs**: `boolean` for condition matches, `String` for facts block, and configuration parameters.
- **Process**:
  - The `AiGenerationProviderFactory` determines the effective provider name and creates an instance.
  - The `LlamaCppJniAiGenerationProvider` generates AI completion text using the Llama.cpp JNI binding, caches the model, and handles logging and warnings.
  - The `AiCompletionParser` parses the raw completion text to extract the model's answer and strip internal reasoning blocks.

#### Dependencies
- **Internal Collaborations**:
  - `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `File`, `List`, `String`, `int`, `float`, `long`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
  - `AiPromptSupport`: For prompt lookup support.
  - `LlamaCppJniConfig`: For configuration settings.
  - `AiGenerationRequest`: For generation requests.
- **External Modules/Packages/Tables**:
  - `net.ladenthin.llama.LlamaModel`: For the native LlamaModel handle.
  - `com.fasterxml.jackson.databind.ObjectMapper`: For JSON processing.
  - `java.io.IOException`: For handling I/O exceptions.
  - `java.util.List`: For handling lists of stop strings and sequence breakers.
  - `java.util.function.Function`: For environment variable lookup.
  - `java.util.regex.Pattern`: For pattern matching in error messages.

#### Cross-cutting
- `@Nullable` annotations for handling potential null values.
- Defensive copying of input collections to prevent null pointer exceptions.
- No explicit concurrency handling.
- **Shared Base Types/Interfaces**: `AiGenerationProvider`, `AutoCloseable`.
- **Common Exception/Handling**: Throws `IOException` and `IllegalArgumentException`.
- **Concurrency**: The provider is designed to be thread-safe with lazy initialization of the native model handle.
- **Configuration**: Managed through the `LlamaCppJniConfig` class with a fluent builder pattern.
