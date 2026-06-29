### main/java/net/ladenthin
- H: 1.0
- C: 1F224605
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:21:18Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [maven/](maven/package.ai.md)
---
> A package for calculating VAT for invoices through AI field generation, selection, and AI model interactions.

#### Purpose
- Provides configuration and selection mechanisms for AI field generation based on file extensions and model definitions.
- Parses raw LLM completion text to extract the model answer by stripping any model-internal thinking block before the result is stored in an AI index file.
- Provides AI text generation for prompts using specified or default sampling parameters.

#### Responsibilities
- `AiFieldGenerationConfig`: Associates prompt and AI model definition keys with file extensions for field generation.
- `AiFieldGenerationSelector`: Selects the appropriate AI field generation configuration based on source file extensions.
- `AiGenerationConfig`: Configures AI generation parameters for Maven-based AI model interactions.
- `AiGenerationKind`: Identifies whether AI generation operates on a single source file or a whole package.
- `AiModelDefinition`: Provides a configuration POJO for pairing a lookup key with AI model parameters.
- `AiModelDefinitionSupport`: Resolves AI model definition entries by their key, returning the corresponding AI generation configuration.
- `AiCompletionParser`: Parses raw LLM completion text to extract the model answer.
- `AiGenerationProvider`: Pluggable AI backend for generating text based on prompts.
- `AiGenerationProviderFactory`: Calculates VAT for invoices using the llama.cpp JNI binding and GGUF models locally.
- `LlamaCppJniAiGenerationProvider`: Provides a service for generating AI-based completion based on llama.cpp configurations.
- `LlamaCppJniConfig`: Provides configuration for the llama.cpp JNI provider.
- `MockAiGenerationProvider`: Provides deterministic mock summaries for AI generation requests in testing.

#### Key Units
- `AiFieldGenerationConfig`: Associates prompt template keys with AI model definition keys for field generation.
- `AiFieldGenerationSelector`: Selects the appropriate AI field generation configuration based on source file extensions.
- `AiGenerationConfig`: Carries parameters between Maven configuration and AI provider implementations.
- `AiGenerationKind`: Identifies AI generation operation scope: single file or package.
- `AiModelDefinition`: Provides a configuration POJO for pairing a lookup key with AI model parameters.
- `AiModelDefinitionSupport`: Resolves AI model definition entries by their key, returning the corresponding AI generation configuration.
- `AiCompletionParser`: Parses raw LLM completion text to extract the model answer.
- `AiGenerationProvider`: Generates text based on `AiGenerationRequest`.
- `AiGenerationProviderFactory`: Selects and instantiates an `AiGenerationProvider` implementation by name.
- `LlamaCppJniAiGenerationProvider`: Uses llama.cpp JNI bindings to generate text.
- `LlamaCppJniConfig`: Configures the llama.cpp JNI provider.
- `MockAiGenerationProvider`: Provides deterministic mock summaries for AI generation requests in testing.

#### Data Flow
- `AiFieldGenerationConfig` is instantiated with prompt and AI model definition keys and file extensions.
- `AiFieldGenerationSelector` iterates over configurations to select one based on source file extensions.
- `AiGenerationConfig` is used to configure AI generation parameters.
- `AiGenerationKind` determines the scope of AI generation (single file or package).
- `AiModelDefinition` provides configuration parameters for AI model interactions.
- `AiModelDefinitionSupport` resolves AI model definition entries by their key, returning the corresponding AI generation configuration.
- `AiCompletionParser` processes raw completion text to extract the model answer.
- `AiGenerationProvider` generates text based on `AiGenerationRequest`.
- `AiGenerationProviderFactory` selects and instantiates an `AiGenerationProvider` implementation by name.
- `LlamaCppJniAiGenerationProvider` uses llama.cpp JNI bindings to generate text.
- `LlamaCppJniConfig` configures the llama.cpp JNI provider.

#### Dependencies
- `AiFieldGenerationConfig` imports `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiFieldGenerationSelector` imports `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiGenerationConfig` imports `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiGenerationKind` has no imports.
- `AiModelDefinition` imports `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`, `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`.
- `AiModelDefinitionSupport` imports `java.util.HashMap`, `java.util.List`, `java.util.Map`, `java.util.Objects`, `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`, `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`.
- `AiCompletionParser` imports `java.io.IOException`, `java.util.ArrayList`, `java.util.Collections`, `java.util.List`, `java.util.Objects`, `lombok.ToString`, `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.parameters.InferenceParameters`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.Pair`, `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`, `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`.

#### Cross-cutting
- Thread-safe due to lazy initialization of the model and use of immutable parameters.
- Immutable configuration with value semantics.

#### Summary
This package provides a comprehensive set of classes and configurations for calculating VAT for invoices through AI field generation, selection, and AI model interactions, with clear dependencies and responsibilities structured around specific functional roles. Additionally, it includes tools for parsing LLM completion text and generating AI text using llama.cpp JNI bindings, emphasizing modularity, thread safety, and configurability.
