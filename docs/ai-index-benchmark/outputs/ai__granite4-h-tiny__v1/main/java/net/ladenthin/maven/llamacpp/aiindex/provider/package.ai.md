### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:15:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
---
> A package for parsing LLM completion text and providing AI text generation services using llama.cpp JNI bindings.

#### Purpose
- Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- Provides AI text generation for prompts using specified or default sampling parameters.

#### Responsibilities
- Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- Pluggable AI backend for generating text based on prompts.
- Calculates VAT for invoices using the llama.cpp JNI binding and GGUF models locally.

#### Key Units
- `AiCompletionParser`: Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- `AiGenerationProvider`: Pluggable AI backend for generating text based on prompts.
- `AiGenerationProviderFactory`: Calculates VAT for invoices using the llama.cpp JNI binding and GGUF models locally.
- `LlamaCppJniAiGenerationProvider`: Provides a service for generating AI-based completion based on llama.cpp configurations.
- `LlamaCppJniConfig`: Provides configuration for the llama.cpp JNI provider.
- `MockAiGenerationProvider`: Provides deterministic mock summaries for AI generation requests in testing.

#### Data Flow
- `AiCompletionParser` processes raw completion text to extract the model answer.
- `AiGenerationProvider` generates text based on `AiGenerationRequest`.
- `AiGenerationProviderFactory` selects and instantiates an `AiGenerationProvider` implementation by name.
- `LlamaCppJniAiGenerationProvider` uses llama.cpp JNI bindings to generate text.
- `LlamaCppJniConfig` configures the llama.cpp JNI provider.

#### Dependencies
- `java.io.IOException`: For handling I/O exceptions.
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`: For data structures.
- `java.util.Objects`: For object utility methods.
- `lombok.ToString`: For generating toString method.
- `net.ladenthin.llama.LlamaModel`: For llama.cpp model handling.
- `net.ladenthin.llama.parameters.InferenceParameters`: For inference parameters.
- `net.ladenthin.llama.parameters.ModelParameters`: For model parameters.
- `net.ladenthin.llama.value.Pair`: For pairing strings.
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`: For generation request.
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`: For prompt support.

#### Cross-cutting
- Thread-safe due to lazy initialization of the model and use of immutable parameters.
- Immutable configuration with value semantics.

#### Summary
This package provides comprehensive tools for parsing LLM completion text and generating AI text using llama.cpp JNI bindings, with a focus on modularity, thread safety, and configurability.
