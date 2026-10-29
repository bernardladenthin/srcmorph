### java
- H: 1.0
- C: 1DD19FA7
- D: 2026-10-28T23:55:11Z
- T: 2026-10-29T00:00:08Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides the infrastructure for configuring, evaluating, and executing AI-driven source code modification and fact extraction across pluggable backend providers.

#### Purpose
* To manage the complete lifecycle of an AI-driven code modification process, from configuration to execution.
* To provide a pluggable framework for interacting with various AI generation backends (local GGUF or remote APIs).

#### Responsibilities
* **Configuration & Evaluation:** Centralizing AI parameters, defining conditions for file context evaluation, and extracting structured facts from source code.
* **AI Provider Abstraction:** Defining the contract (`AiGenerationProvider`) for interchangeable AI services.
* **Backend Implementation:** Providing concrete implementations for local GGUF models (`LlamaCppJniAiGenerationProvider`) and supporting OpenAI-compatible APIs.
* **Response Processing:** Parsing raw LLM outputs to extract clean, structured answers.

#### Key units
* SrcMorphConfiguration: Central mutable configuration object for the entire AI modification run.
* AiGenerationProvider: Interface defining the contract for AI text generation services.
* AiGenerationProviderFactory: Factory responsible for instantiating the correct AI provider based on configuration.
* AiConditionEvaluator: Class responsible for recursively matching file contexts against structured AI conditions.
* AiFactExtractor: Computes structured facts by counting regex matches within source text.
* LlamaCppJniAiGenerationProvider: Concrete provider interfacing with local GGUF models via JNI.
* AiCompletionParser: Utility to strip internal reasoning blocks from raw LLM responses.

#### Data flow
Configuration (`SrcMorphConfiguration`, `AiGenerationConfig`) dictates the setup, which is used by `AiGenerationProviderFactory` to select a provider. The provider accepts an `AiGenerationRequest`, executes inference (potentially using `OpenAiCompatibleProviderSupport`), and returns a result. File content is simultaneously processed by `AiFactExtractor` and validated by `AiConditionEvaluator` using the configuration context.

#### Dependencies
* **Internal:** Relies on `AiFileContext`, `AiCondition`, `AiPromptDefinition`, and various configuration objects (`AiGenerationConfig`, `LlamaCppJniConfig`).
* **External/Support:** Utilizes `ObjectMapper` and standard Java utilities for serialization and I/O.
* **Providers:** Depends on specific implementations like `LlamaCppJniAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
* **Configuration:** Configuration is highly centralized in `SrcMorphConfiguration` and granularly managed in `AiGenerationConfig` and `LlamaCppJniConfig`.
* **Resource Management:** Providers implement `AutoCloseable` to ensure proper cleanup of native resources.
* **Error Handling:** Uses specific exceptions like `IllegalArgumentException` during factory selection and `IOException` during provider execution.
* **Immutability:** Configuration objects like `LlamaCppJniConfig` are designed to be immutable after creation.
