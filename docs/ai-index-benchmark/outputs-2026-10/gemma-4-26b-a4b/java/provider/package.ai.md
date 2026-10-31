### java/provider
- H: 1.0
- C: 962F0B37
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T14:02:37Z
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
> Provides a pluggable interface and factory for integrating various AI backends, including local llama.cpp JNI bindings and OpenAI-compatible APIs, for text generation and reasoning.

#### Purpose
* To abstract AI text generation through a unified provider interface.
* To manage complex model configurations and response parsing (including chain-of-thought extraction).

#### Responsibilities
* **Provider Abstraction**: Defines the `AiGenerationProvider` contract for text production and resource management.
* **Implementation Management**: Uses `AiGenerationProviderFactory` to instantiate Mock, LlamaCppJNI, or OpenAI-compatible providers.
* **Local Inference**: Implements `LlamaCppJniAiGenerationProvider` to interface with native GGUF models via JNI.
* **API Compatibility**: Provides `OpenAiCompatibleProviderSupport` to handle JSON serialization, endpoint formatting, and security for OpenAI-style APIs.
* **Response Processing**: Uses `AiCompletionParser` to strip internal reasoning blocks from raw model outputs.
* **Configuration**: Manages extensive model parameters via the immutable `LlamaCppJniConfig`.

#### Key units
* `AiGenerationProvider`: The core interface for generating text and performance metrics via `generate` and `generateWithTimings`.
* `AiGenerationProviderFactory`: A factory that resolves the effective provider based on run-level or model-specific settings.
* `LlamaCppJniAiGenerationProvider`: A JNI-based implementation that manages a native `LlamaModel` lifecycle.
* `LlamaCppJniConfig`: An immutable value object containing 41+ parameters for local model tuning (e.g., `temperature`, `gpuLayers`, `flashAttn`).
* `AiCompletionParser`: A utility to extract clean answers by removing content between `THINKING_BLOCK_START_MARKER` and `THINKING_BLOCK_END_MARKER`.
* `OpenAiCompatibleProviderSupport`: A helper for constructing JSON request bodies and validating secure API key transmission.

#### Data flow
1. **Configuration**: `LlamaCppJniConfig` is built using a fluent builder.
2. **Instantiation**: `AiGenerationProviderFactory` creates a concrete `AiGenerationProvider`.
3. **Request**: An `AiGenerationRequest` is passed to the provider's `generate` method.
4. **Execution**: 
    - For local models, the request is mapped to `ModelParameters` and sent to the native `LlamaModel`.
    - For OpenAI-compatible models, `OpenAiCompatibleProviderSupport` serializes the request to JSON.
5. **Parsing**: Raw text is passed through `AiCompletionParser` to remove reasoning blocks.
6. **Output**: The system returns cleaned text and `AiGenerationTimings` (token counts and throughput).

#### Dependencies
* **Native**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`, `ModelParameters`, and `ChatResponseParser`).
* **External**: Jackson (`ObjectMapper`) for JSON serialization.
* **Internal**: `AiPromptSupport` for prompt rendering and `AiGenerationConfig` for default parameter values.

#### Cross-cutting
* **Immutability**: Configuration objects like `LlamaCppJniConfig` are final and immutable to ensure thread safety.
* **Resource Management**: Providers implement `AutoCloseable` to ensure native handles or network resources are released.
* **Error Handling**: Custom handling for truncated responses (missing thinking block end markers) and security enforcement (preventing API keys over insecure HTTP).
* **Performance Tracking**: Standardized use of `AiGenerationTimings` across different provider types.
