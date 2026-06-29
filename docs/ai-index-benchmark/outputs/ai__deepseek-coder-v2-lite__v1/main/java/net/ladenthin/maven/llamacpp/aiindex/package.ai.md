### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: 83E61DEB
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:25:46Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Calculates VAT for invoices by configuring AI-driven field generation in a Maven plugin for code assistance.

#### Purpose
- **Configure AI-driven field generation** for specific file types and prompts.
- **Associate prompts with AI model definitions** for generating content based on specified criteria.

#### Responsibilities
- **AiFieldGenerationConfig**: Manages configuration for AI-driven field generation, including prompt keys and AI model definitions.
- **AiFieldGenerationSelector**: Selects the appropriate `AiFieldGenerationConfig` for a given source file based on its file extensions.
- **AiGenerationConfig**: Provides a mutable configuration object for AI generation parameters, supporting default values and access through setters and getters.
- **AiGenerationKind**: Identifies whether an AI generation operates on a single source file or a whole package.
- **AiModelDefinition**: Defines and manages AI model configurations for use in a Maven plugin, allowing customization of parameters such as context size, number of threads, and more.
- **AiModelDefinitionSupport**: Resolves AI model definitions by their key, returning the corresponding generation configurations.

#### Key units
- **AiFieldGenerationConfig**: Manages configuration for AI-driven field generation.
- **AiFieldGenerationSelector**: Selects appropriate configurations based on file extensions.
- **AiGenerationConfig**: Mutable configuration object for AI generation parameters.
- **AiGenerationKind**: Enumerates the scope of AI generation (single file or package).
- **AiModelDefinition**: Defines and manages AI model configurations.
- **AiModelDefinitionSupport**: Resolves AI model definitions by key.

#### Data flow
Inputs are typically passed through constructors, setters, and getters to configure and retrieve parameters for AI field generation and model management.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Cross-cutting
- **Shared configuration parameters**: Many classes share configuration parameters such as model paths, context sizes, and more.
- **Exception handling**: No exceptions are explicitly thrown in the provided summaries, but null values are handled with annotations.
- `java.util.List` is frequently used for collections of configurations or extensions.

#### EOF

> Provides AI-generated text responses based on user prompts using a local GGUF model via the llama.cpp library.

#### Purpose
- To provide AI-generated text responses based on user prompts using a local GGUF model via the llama.cpp library.

#### Responsibilities
- Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- Provides a pluggable AI backend for generating text based on specific requests.
- Selects and instantiates an {@link AiGenerationProvider} implementation by name.
- Calculates VAT for invoices backed by the JNI binding of the llama.cpp library, providing AI-generated responses based on user prompts.
- Defines and encapsulates configuration parameters for the llama.cpp JNI provider.
- To provide a mock implementation of `AiGenerationProvider` for testing purposes.

#### Key units
- `AiCompletionParser`: Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- `AiGenerationProvider`: Provides a pluggable AI backend for generating text based on request specifications.
- `AiGenerationProviderFactory`: Selects and instantiates an {@link AiGenerationProvider} implementation by name.
- `LlamaCppJniAiGenerationProvider`: Calculates VAT for invoices backed by the JNI binding of the llama.cpp library, providing AI-generated responses based on user prompts.
- `LlamaCppJniConfig`: Defines and encapsulates configuration parameters for the llama.cpp JNI provider.
- `MockAiGenerationProvider`: To provide a mock implementation of `AiGenerationProvider` for testing purposes.

#### Data flow
- `AiCompletionParser` consumes raw completion text and produces cleaned answer text.
- `AiGenerationProvider` consumes `AiGenerationRequest` and produces AI-generated text.
- `AiGenerationProviderFactory` consumes provider name and configuration, producing an `AiGenerationProvider` instance.
- `LlamaCppJniAiGenerationProvider` consumes `AiGenerationRequest` and configuration, producing AI-generated text.
- `LlamaCppJniConfig` provides configuration parameters for the JNI provider.
- `MockAiGenerationProvider` consumes an `AiGenerationRequest`, produces a mock summary based on the file name and content.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.InferenceParameters`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.Pair`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `org.jspecify.annotations.Nullable`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations (`@ToString`, `@EqualsAndHashCode`)
- Interface `ConvertToRecord` (annotation)

#### Cross-cutting
- Shared base types/interfaces: `AiGenerationProvider`, `AutoCloseable`
- Common exception/error handling: Throws `IOException` in several methods.
- Threading/concurrency notes: The provider is designed to be thread-safe as long as the underlying `LlamaModel` is thread-safe.
- Configuration: `LlamaCppJniConfig` provides configuration parameters for the JNI provider.
