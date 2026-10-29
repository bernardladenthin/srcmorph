### java
- H: 1.0
- C: 2170A0D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:31:49Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Orchestrates AI-driven source code analysis and transformation by combining configurable file filtering and deterministic fact extraction with a pluggable LLM provider framework.

#### Purpose
* Automate the extraction of code metadata and the generation of summaries using Large Language Models (LLMs).
* Provide a flexible pipeline that supports both local (GGUF/llama.cpp) and remote (OpenAI-compatible) inference backends.

#### Responsibilities
* **Configuration & Filtering**: Manages global settings, model parameters, and recursive boolean logic to determine which source files enter the AI pipeline.
* **Deterministic Analysis**: Uses regular expressions to count specific code patterns, supplementing AI-generated summaries with exact metrics.
* **LLM Provider Abstraction**: Decouples the generation request from the underlying engine, supporting JNI-based local inference and HTTP-based remote APIs.
* **Output Post-processing**: Cleans raw LLM responses by stripping internal reasoning or "thinking" blocks.

#### Key units
* `SrcMorphConfiguration`: Root JavaBean for global paths, constraints, and AI definitions.
* `AiConditionEvaluator`: Engine for evaluating `AiCondition` trees against `AiFileContext`.
* `AiFactExtractor`: Utility for generating deterministic "facts" blocks via regex matching.
* `AiGenerationProvider`: Interface defining the contract for text generation and resource cleanup.
* `LlamaCppJniAiGenerationProvider`: Local inference implementation utilizing `LlamaModel` and `ModelParameters`.
* `OpenAiCompatibleProviderSupport`: Utility for managing JSON requests and secure API key transmission for remote LLMs.
* `AiGenerationProviderFactory`: Resolver that instantiates the appropriate provider based on configuration.
* `AiCompletionParser`: Logic for removing `<thought>` blocks from LLM output.

#### Data flow
1. **Setup**: `SrcMorphConfiguration` is loaded (Maven/JSON/YAML) and passed to the `AiGenerationProviderFactory` to initialize the chosen `AiGenerationProvider`.
2. **Selection**: `AiConditionEvaluator` filters files based on `AiCondition` trees.
3. **Augmentation**: `AiFactExtractor` scans selected files to produce a deterministic facts summary.
4. **Inference**: An `AiGenerationRequest` is sent to the provider; local providers use `LlamaCppJniConfig` for GPU/sampling settings, while remote providers use `OpenAiCompatibleProviderSupport` for HTTP communication.
5. **Refinement**: The resulting text is processed by `AiCompletionParser` to remove reasoning markers before returning the final string.

#### Dependencies
* **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`.
* **External**: `net.ladenthin.llama` (llama.cpp JNI), `com.fasterxml.jackson` (JSON), `java.util.regex`, `java.time`.

#### Cross-cutting
* **Resource Lifecycle**: Providers implement `AutoCloseable` to manage native model handles.
* **Security**: API keys are blocked from transmission over plain HTTP (except for loopback addresses).
* **Configuration Patterns**: Uses a mix of mutable JavaBeans for root configuration and the Builder pattern for immutable provider settings (`LlamaCppJniConfig`).
* **Error Handling**: Standardized use of `IllegalArgumentException` for configuration validation and `IOException` for generation failures.
