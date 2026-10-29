### java/provider
- H: 1.0
- C: 7F4A1655
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:31:36Z
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
> Provides a pluggable framework for integrating Large Language Model (LLM) backends, supporting local GGUF models via JNI and remote OpenAI-compatible APIs to generate source code transformations.

#### Purpose
* Abstract AI text generation to allow switching between local, remote, and mock providers.
* Manage the lifecycle and hardware configuration of native LLM inference engines.

#### Responsibilities
* **Provider Orchestration**: Managing the instantiation and resolution of AI backends based on configuration priority.
* **Local Inference**: Interfacing with `llama.cpp` via JNI to handle GGUF model loading, GPU offloading, and token generation.
* **Remote API Integration**: Constructing OpenAI-compatible JSON requests, managing secure API key transmission, and parsing HTTP responses.
* **Response Processing**: Cleaning raw LLM output by stripping chain-of-thought "thinking" blocks.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for text generation and resource cleanup.
* `AiGenerationProviderFactory`: Factory that resolves the effective provider key and instantiates the corresponding implementation.
* `LlamaCppJniAiGenerationProvider`: Implementation for local inference using `LlamaModel` and `ModelParameters`.
* `LlamaCppJniConfig`: Immutable configuration record for model paths, sampling parameters (temperature, top-p), and hardware settings (GPU layers, Flash Attention).
* `OpenAiCompatibleProviderSupport`: Utility for JSON serialization, endpoint formatting, and security validation for remote APIs.
* `AiCompletionParser`: Logic for extracting final answers by removing `<thought>` blocks.

#### Data flow
1. `AiGenerationProviderFactory` selects a provider based on `LlamaCppJniConfig` and run-level settings.
2. `AiGenerationRequest` is passed to the provider's `generate` or `generateWithTimings` method.
3. **For Local**: `LlamaCppJniAiGenerationProvider` configures the native `LlamaModel` $\rightarrow$ executes `chatComplete` $\rightarrow$ parses JSON response.
4. **For Remote**: `OpenAiCompatibleProviderSupport` builds a JSON body $\rightarrow$ transmits via HTTP $\rightarrow$ calculates timings from `ChatResponse`.
5. `AiCompletionParser` strips reasoning markers from the resulting text.
6. Final cleaned `String` or `AiGenerationTimings` is returned to the caller.

#### Dependencies
* **Native/External**: `net.ladenthin.llama` (JNI bindings for llama.cpp), `com.fasterxml.jackson` (JSON processing).
* **Internal**: `AiPromptSupport` (prompt templates), `AiGenerationRequest`/`AiGenerationTimings` (data transfer objects).

#### Cross-cutting
* **Immutability**: Configuration is handled via the Builder pattern in `LlamaCppJniConfig` to ensure thread-safe, read-only settings.
* **Security**: `OpenAiCompatibleProviderSupport` explicitly blocks API key transmission over plain HTTP unless the destination is a loopback address.
* **Resource Management**: Providers implement `AutoCloseable` to ensure native model handles are released.
* **Error Handling**: Uses `IOException` for generation failures and `IllegalArgumentException` for configuration mismatches.
