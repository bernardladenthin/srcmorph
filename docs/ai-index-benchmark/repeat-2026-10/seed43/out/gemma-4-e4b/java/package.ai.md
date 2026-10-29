### java
- H: 1.0
- C: D3B3E611
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T14:00:22Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides the infrastructure for configuring, evaluating, and executing AI-driven analysis and generation tasks against source code contexts.

#### Purpose
* Centralizes parameters and logic for running AI-driven source code analysis and generation.
* Provides a pluggable framework for interfacing with diverse Large Language Model inference backends.

#### Responsibilities
* Configuration Management: Defining and managing all parameters for AI runs, including file filtering, AI provider settings, and generation rules (`SrcMorphConfiguration`, `AiGenerationConfig`).
* Condition Evaluation: Determining if source file contexts meet complex, defined criteria (`AiConditionEvaluator`, `AiConditionGroup`).
* Fact Extraction: Calculating deterministic structural metrics from source code based on configured patterns (`AiFactExtractor`).
* Provider Abstraction: Defining the contract and implementation for interacting with LLM backends, supporting both local (llama.cpp) and external (OpenAI) APIs (`AiGenerationProvider`, `LlamaCppJniAiGenerationProvider`).
* Response Processing: Cleaning raw LLM outputs by stripping internal reasoning blocks (`AiCompletionParser`).

#### Key units
* `SrcMorphConfiguration`: Central hub for all run parameters, including file filters and AI definitions.
* `AiGenerationProvider`: Interface defining the contract for AI text generation across different backends.
* `AiGenerationProviderFactory`: Factory responsible for instantiating the correct `AiGenerationProvider` based on configuration.
* `AiConditionEvaluator`: Recursively matches `AiFileContext` against defined `AiCondition` trees.
* `AiFactExtractor`: Computes structured "facts" from source text using configured `AiFactCounter` patterns.
* `LlamaCppJniAiGenerationProvider`: Concrete implementation for local GGUF model inference via JNI.
* `AiCompletionParser`: Utility for cleaning raw LLM responses by removing thinking blocks.

#### Data flow
`SrcMorphConfiguration` dictates the run parameters, which are used by `AiConditionEvaluator` to filter `AiFileContext` objects. Matched contexts are processed by `AiFactExtractor` to generate facts. The overall process then utilizes `AiGenerationProviderFactory` to select and instantiate a provider (e.g., `LlamaCppJniAiGenerationProvider`), which accepts an `AiGenerationRequest` and returns a result processed by `AiCompletionParser`.

#### Dependencies
* Internal: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationRequest`, `AiGenerationTimings`.
* External: `java.time.Instant`, `lombok.ToString`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `ObjectMapper`, `LlamaModel` (JNI dependency).

#### Cross-cutting
* Configuration: `SrcMorphConfiguration` serves as the primary configuration source, feeding into provider-specific immutable configurations like `LlamaCppJniConfig`.
* Pluggability: The `AiGenerationProvider` interface allows swapping backend implementations (e.g., `LlamaCppJniAiGenerationProvider` vs. OpenAI support).
* Error Handling: Common exceptions include `IOException` during generation calls and `IllegalStateException` within condition tree evaluation.
* Immutability: Provider configurations (`LlamaCppJniConfig`) are designed to be immutable for thread safety.
