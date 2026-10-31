### java/provider
- H: 1.0
- C: 6003833C
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:37:58Z
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
> Provides pluggable LLM text generation for source-document AI requests, supporting local llama.cpp models and OpenAI-compatible providers.

#### Purpose
- Supplies AI text generation backends for the srcmorph project, including local inference and remote chat-completions providers.
- Parses raw LLM completions to extract final model answers for storage.

#### Responsibilities
- Defines the `AiGenerationProvider` contract for text generation backends.
- Instantiates and selects providers via `AiGenerationProviderFactory`.
- Runs GGUF models locally through llama.cpp JNI with configurable inference parameters.
- Handles OpenAI-compatible chat-completions request construction, API key handling, and response parsing.
- Strips Gemma-4 thinking blocks from LLM completions to extract the final model answer.

#### Key units
- `AiGenerationProvider`: interface defining `generate` and `generateWithTimings` methods for AI text generation.
- `AiGenerationRequest`: request object containing prompt, source file, source text, and current header.
- `AiGenerationTimings`: timings object reporting token and rate metrics for generation.
- `AiCompletionParser`: class stripping Gemma-4 thinking blocks from LLM completions to extract the final answer.
- `LlamaCppJniAiGenerationProvider`: class implementing local LLM inference via llama.cpp JNI.
- `LlamaCppJniConfig`: immutable builder-based configuration for llama.cpp JNI parameters.
- `OpenAiCompatibleProviderSupport`: class handling request body, endpoint, API key, status check, error message, and timings for OpenAI-compatible providers.
- `AiGenerationProviderFactory`: class selecting and instantiating AI generation providers by name.

#### Data flow
- `AiGenerationProviderFactory` resolves the effective provider key between run-level and model-level configuration, then instantiates the matching provider.
- `LlamaCppJniAiGenerationProvider` lazily loads the model, builds inference parameters from `LlamaCppJniConfig`, calls the model, and parses the response through `AiCompletionParser` and `ChatResponseParser`.
- `OpenAiCompatibleProviderSupport` builds the chat-completions JSON body, resolves the API key, checks the host, and computes timings from the response.
- `AiCompletionParser` strips thinking blocks from raw completion text to extract the final model answer for storage.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `ChatResponseParser`, `ModelParameters`, `ReasoningFormat`, `FlashAttn`, `ChatResponse`, `Timings`, `MockAiGenerationProvider`.
- External: `com.fasterxml.jackson.databind.ObjectMapper`, `com.fasterxml.jackson.databind.node.ArrayNode`, `com.fasterxml.jackson.databind.node.ObjectNode`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`, `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Lombok `@ToString` used across multiple classes for debugging output.
- jspecify `@Nullable` annotations for optional values.
- `IOException` thrown by generation and parsing methods on underlying failures.
- Immutable value objects (`LlamaCppJniConfig`) for thread-safe configuration.
- Builder pattern for complex configuration (`LlamaCppJniConfig.Builder`).
- Stateless classes after construction for testability.
