### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:58:57Z
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
> Provides a pluggable framework for integrating Large Language Model (LLM) backends to perform source code transformations, supporting local GGUF models via JNI and remote OpenAI-compatible APIs.

#### Purpose
* Abstract AI text generation to allow switching between local, remote, and mock providers.
* Manage the lifecycle and hardware-specific configuration of LLM inference.

#### Responsibilities
* **Provider Orchestration**: Managing the instantiation and resolution of AI backends based on configuration keys.
* **Local Inference**: Interfacing with native `llama.cpp` bindings for GGUF model execution, including GPU offloading and memory optimization.
* **Remote API Integration**: Handling JSON serialization, endpoint management, and secure API key transmission for OpenAI-compatible services.
* **Response Processing**: Cleaning raw model output by stripping chain-of-thought "thinking" blocks.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for generating text and performance timings.
* `AiGenerationProviderFactory`: Factory that resolves the effective provider (model vs. run settings) and instantiates the implementation.
* `LlamaCppJniAiGenerationProvider`: Implementation for local inference using `LlamaModel` JNI bindings.
* `LlamaCppJniConfig`: Immutable configuration object for model paths, sampling parameters, and hardware settings (e.g., `gpuLayers`, `contextSize`).
* `OpenAiCompatibleProviderSupport`: Utility for constructing OpenAI-style JSON requests and validating secure transmission of API keys.
* `AiCompletionParser`: Logic to extract final answers by removing `<thought>` blocks from responses.

#### Data flow
1. `AiGenerationProviderFactory` receives a request and resolves the provider implementation.
2. An `AiGenerationRequest` (containing prompts and source text) is passed to the `AiGenerationProvider`.
3. The provider (e.g., `LlamaCppJniAiGenerationProvider`) uses `AiPromptSupport` to render the prompt and sends it to the model.
4. The raw response is processed by `AiCompletionParser` to remove reasoning blocks.
5. The final cleaned text and `AiGenerationTimings` (token counts/throughput) are returned to the caller.

#### Dependencies
* **Native/External**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`, `ChatResponse`), `com.fasterxml.jackson` (JSON serialization).
* **Internal**: `AiPromptSupport` (prompt rendering), `AiGenerationConfig` (default settings).

#### Cross-cutting
* **Immutability**: Configuration is handled via the Builder pattern in `LlamaCppJniConfig` to ensure thread-safe, read-only settings.
* **Security**: `OpenAiCompatibleProviderSupport` enforces a policy preventing API key transmission over plain HTTP unless the destination is a loopback address.
* **Performance Tracking**: The `AiGenerationTimings` object is used across providers to standardize the measurement of token throughput and latency.
* **Error Handling**: Custom `IOException` usage for token budget exhaustion (truncated thinking blocks) and API communication failures.
