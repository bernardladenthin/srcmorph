### main
- H: 1.0
- C: 0D4DE298
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:34:35Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
---
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
