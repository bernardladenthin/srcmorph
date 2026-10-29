### java/provider
- H: 1.0
- C: 8540E203
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T02:40:28Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Calculates VAT for invoices by providing a Java implementation for generating AI-driven content using a local llama.cpp JNI binding, supporting various model configurations and parameters for efficient AI generation.

#### Purpose
- Provide a Java implementation for generating AI-driven content using a local llama.cpp JNI binding.
- Support various model configurations and parameters for efficient AI generation.

#### Responsibilities
- Parses raw LLM completion text to extract the model answer.
- Provides a method for generating text based on an AI generation request.
- Selects and instantiates an AI generation provider based on the given provider name.
- Calculates VAT for invoices by processing request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Key units
- `AiCompletionParser`: Parses raw LLM completion text to extract the model's actual answer from raw completion text by stripping internal reasoning blocks.
- `AiGenerationProvider`: Interface for generating text based on an AI generation request.
- `AiGenerationProviderFactory`: Selects and instantiates an AI generation provider based on the given provider name.
- `OpenAiCompatibleProviderSupport`: Provides support for configuring and interacting with OpenAI-compatible AI generation providers.

#### Data flow
- `AiCompletionParser` processes raw completion text to extract the model's actual answer.
- `AiGenerationProvider` generates text based on an AI generation request.
- `AiGenerationProviderFactory` selects and instantiates an AI generation provider based on the given provider name.
- `OpenAiCompatibleProviderSupport` processes request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Cross-cutting
- Shared base types/interfaces: `AiGenerationProvider`, `AutoCloseable`
- Common exception/error handling: `IOException`
- Threading/concurrency notes: The class is not thread-safe as it does not modify any shared state.
- Configuration: `LlamaCppJniConfig`, `AiGenerationConfig`
