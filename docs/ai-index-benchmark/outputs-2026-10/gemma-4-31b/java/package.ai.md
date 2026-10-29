### java
- H: 1.0
- C: DA147B25
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:40:58Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> An AI-driven source code summarization engine that manages file filtering, configuration, and pluggable LLM provider integrations.

#### Purpose
* Centralize configuration and evaluation logic for the `srcmorph` engine.
* Abstract AI text generation to support interchangeable local and remote backends.

#### Responsibilities
* **Configuration & Filtering**: Managing project settings, AI routing, and conditional file inclusion/exclusion logic via `AiConditionEvaluator`.
* **Content Analysis**: Extracting regex-based facts from source code to augment AI summaries using `AiFactExtractor`.
* **Provider Management**: Factory-based instantiation and lifecycle management of AI backends.
* **LLM Integration**: Handling local GGUF models via JNI (llama.cpp) and remote OpenAI-compatible APIs.
* **Response Processing**: Parsing AI completions to remove reasoning blocks and capturing performance metrics.

#### Key units
* `SrcMorphConfiguration`: Central DTO for project paths, AI routing, and model definitions.
* `AiGenerationConfig`: Data carrier for inference parameters like `temperature`, `topP`, and `gpuLayers`.
* `AiConditionEvaluator`: Logic engine matching `AiCondition` trees against `AiFileContext` metadata.
* `AiFactExtractor`: Utility for generating formatted blocks of regex-based pattern counts from source code.
* `AiGenerationProvider`: Interface defining the contract for text generation and performance timing.
* `AiGenerationProviderFactory`: Resolves provider keys to specific implementations (mock, llama-cpp, or openai-compatible).
* `LlamaCppJniAiGenerationProvider`: Local LLM implementation managing `LlamaModel` handles and native hardware settings.
* `OpenAiCompatibleProviderSupport`: Logic for JSON request construction, endpoint resolution, and API key security.
* `AiCompletionParser`: Utility to remove internal thinking blocks from LLM completions.
* `AiOversizeStrategy`: Enum defining behaviors (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files.

#### Data flow
* **Configuration**: Maven/JSON/YAML $\rightarrow$ `SrcMorphConfiguration` $\rightarrow$ `AiGenerationConfig`.
* **Filtering**: `AiFileContext` $\rightarrow$ `AiConditionEvaluator` $\rightarrow$ Boolean match result.
* **Augmentation**: Source Text $\rightarrow$ `AiFactExtractor` $\rightarrow$ Formatted facts string.
* **Generation**: `AiGenerationRequest` $\rightarrow$ `AiGenerationProvider` $\rightarrow$ `AiCompletionParser` $\rightarrow$ Final summary string.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `AiFileContext`.
* **External**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`), `com.fasterxml.jackson.databind.ObjectMapper` (JSON serialization), `lombok`, `org.jspecify.annotations`.

#### Cross-cutting
* **Configuration Patterns**: Use of mutable JavaBeans for Maven population and immutable builders for `LlamaCppJniConfig`.
* **Validation**: Explicit `validate()` methods in `AiConditionEvaluator` and `AiFactExtractor` to ensure regex and logic tree integrity.
* **Security**: `OpenAiCompatibleProviderSupport` prevents API key transmission over plain HTTP unless using loopback addresses.
* **Performance**: Lazy initialization of large GGUF models and capture of `AiGenerationTimings`.
* **Error Handling**: Standardized use of `IOException` for provider failures and `IllegalArgumentException` for invalid configurations.
