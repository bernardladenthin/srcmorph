### java
- H: 1.0
- C: 5BF387FF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:53:22Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides the infrastructure for configuring, evaluating, and executing AI-driven source code analysis and modification tasks using pluggable model backends.

#### Purpose
* To centralize and manage all parameters governing an AI-driven source code modification run (`SrcMorphConfiguration`).
* To provide a standardized, pluggable framework for interfacing with diverse AI model backends (`AiGenerationProvider`).

#### Responsibilities
* **Configuration Management:** Holding and providing access to all mutable parameters for AI generation and defining evaluation constraints (`SrcMorphConfiguration`, `AiGenerationConfig`).
* **Condition Logic & Fact Extraction:** Evaluating file context against structured criteria and extracting structured data from source text (`AiConditionEvaluator`, `AiFactExtractor`).
* **AI Backend Abstraction:** Defining the contract and managing the instantiation of various AI providers (local GGUF, OpenAI-compatible) (`AiGenerationProvider`, `AiGenerationProviderFactory`).
* **Response Handling:** Post-processing raw LLM outputs to extract clean results (`AiCompletionParser`).

#### Key units
* `SrcMorphConfiguration`: Central mutable object holding all run parameters.
* `AiGenerationProvider`: Interface defining the contract for text generation across different AI sources.
* `AiGenerationProviderFactory`: Factory responsible for creating the correct `AiGenerationProvider` instance.
* `AiConditionEvaluator`: Recursively validates `AiCondition` trees against an `AiFileContext`.
* `AiFactExtractor`: Computes a structured facts block from source text using configured `AiFactCounter`s.
* `LlamaCppJniAiGenerationProvider`: Concrete implementation for local GGUF model inference.
* `OpenAiCompatibleProviderSupport`: Utility for constructing OpenAI-formatted JSON requests.

#### Data flow
Configuration parameters flow from `SrcMorphConfiguration` into specialized objects like `AiGenerationConfig` or are used to define constraints in `AiConditionGroup` structures. `AiConditionEvaluator` consumes an `AiFileContext` and an `AiCondition` tree to produce a boolean match result. The `AiGenerationProviderFactory` uses configuration to select a provider, which then uses an `AiGenerationRequest` to generate text, which is finally cleaned by `AiCompletionParser`.

#### Dependencies
* **Internal:** `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiGenerationRequest`, `ChatResponse`.
* **External/System:** `java.io.File`, `java.time.Instant`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
* **Configuration:** Heavy reliance on mutable configuration objects (`SrcMorphConfiguration`) and immutable provider configurations (`LlamaCppJniConfig`) managed via Builder patterns.
* **Error Handling:** Uses `IllegalArgumentException` for configuration validation and `IOException` for I/O failures across providers.
* **Resource Management:** Providers implement `AutoCloseable` to ensure native resources are released.
* **Pattern:** Factory pattern (`AiGenerationProviderFactory`) is used for provider instantiation.
