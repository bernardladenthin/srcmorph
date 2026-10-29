### java
- H: 1.0
- C: 726884C5
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:07:27Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides the infrastructure for configuring, evaluating, and executing AI-driven source code analysis and generation across various LLM backends.

#### Purpose
* To centralize configuration and logic for AI-driven source code analysis and generation.
* To provide a standardized, pluggable interface for interacting with diverse Large Language Model backends.

#### Responsibilities
* Configuration Management: Holding and setting all parameters for AI generation, including model paths, context sizes, and sampling parameters (`SrcMorphConfiguration`, `AiGenerationConfig`).
* File Filtering and Context Evaluation: Determining if a file context meets specific criteria using condition trees (`AiConditionEvaluator`, `AiConditionGroup`).
* Data Extraction: Calculating structural metrics (facts) from source code using regular expressions (`AiFactExtractor`).
* Provider Abstraction: Defining the `AiGenerationProvider` contract for text generation across different backends.
* Provider Selection: Using `AiGenerationProviderFactory` to select and instantiate the correct provider based on configuration.
* Local Inference: Implementing `LlamaCppJniAiGenerationProvider` for local LLM interaction via JNI bindings.
* API Compatibility: Providing support logic in `OpenAiCompatibleProviderSupport` for OpenAI-compatible API interactions.

#### Key units
* `SrcMorphConfiguration`: Central mutable object holding all run parameters for AI morphing.
* `AiGenerationProvider`: Interface defining the contract for AI text generation.
* `AiGenerationProviderFactory`: Factory responsible for creating specific `AiGenerationProvider` instances.
* `AiConditionEvaluator`: Class responsible for recursively validating if an `AiFileContext` matches an `AiCondition` tree.
* `LlamaCppJniAiGenerationProvider`: Concrete implementation for local LLM inference using llama.cpp JNI.
* `AiFactExtractor`: Computes structured "facts" from source text based on configured `AiFactCounter` regexes.
* `LlamaCppJniConfig`: Immutable configuration object controlling Llama.cpp parameters.
* `AiCompletionParser`: Utility class to extract clean answers from raw model responses.

#### Data flow
Configuration flows from `SrcMorphConfiguration` into specialized objects like `AiGenerationConfig` and `AiConditionEvaluator`. File contexts (`AiFileContext`) are passed to `AiConditionEvaluator` for validation, and source text is passed to `AiFactExtractor` to generate structured data blocks. Generation requests flow through the `AiGenerationProviderFactory` to an instantiated provider, which uses its configuration (`LlamaCppJniConfig`) to generate text, which is then cleaned by `AiCompletionParser`.

#### Dependencies
* Internal: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationRequest`, `AiGenerationTimings`, `LlamaModel`, `ChatResponseParser`, `ChatResponse`.
* External: `java.time.Instant`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `lombok.ToString`, `ObjectMapper`.

#### Cross-cutting
* Configuration: `SrcMorphConfiguration` acts as the primary configuration hub, referencing `AiGenerationConfig` for generation specifics, while `LlamaCppJniConfig` provides immutable parameters for providers.
* Error Handling: `AiConditionEvaluator` throws `IllegalArgumentException` or `IllegalStateException`; generation calls commonly throw `IOException`.
* Mutability: `SrcMorphConfiguration` and `AiGenerationConfig` are mutable JavaBeans.
* Patterns: The Builder pattern is used for safe construction of configuration objects like `LlamaCppJniConfig`.
