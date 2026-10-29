### java/provider
- H: 1.0
- C: F0444EC4
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T04:27:29Z
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
> This package provides a pluggable infrastructure for executing AI model inference, supporting both local GGUF models via llama.cpp JNI and remote OpenAI-compatible APIs.

#### Purpose
* Provides a standardized interface for generating text from various AI backends.
* Manages the lifecycle, configuration, and response parsing for local and remote LLM providers.

#### Responsibilities
* **Provider Abstraction**: Defines the `AiGenerationProvider` contract for unified generation and timing metrics.
* **Local Inference (llama.cpp)**: Manages JNI bindings, GPU/CPU layer offloading, KV cache configurations, and lazy model loading.
* **Remote Inference (OpenAI Compatible)**: Handles JSON request construction, endpoint resolution, and security validation for remote APIs.
* **Response Processing**: Parses raw model outputs to strip internal reasoning blocks (e.g., Gemma-4 thinking blocks).
* **Factory & Configuration**: Resolves provider precedence and manages complex hardware/sampling configurations.

#### Key units
* `AiGenerationProvider`: The core interface for generating text and performance timings.
* `LlamaCppJniAiGenerationProvider`: Implementation for local GGUF model inference using native JNI bindings.
* `AiGenerationProviderFactory`: Factory for instantiating providers based on model-specific or run-time configurations.
* `LlamaCppJniConfig`: Immutable configuration object for hardware offloading, sampling, and KV cache settings.
* `AiCompletionParser`: Utility to extract final answers from completions by stripping reasoning markers.
* `OpenAiCompatibleProviderSupport`: Logic for preparing JSON payloads and validating API key security for remote endpoints.

#### Data flow
1. **Request**: An `AiGenerationRequest` (containing prompts and source text) is passed to a provider.
2. **Execution**: The provider (Local JNI or Remote API) executes the inference.
3. **Parsing**: Raw completion text is passed through `AiCompletionParser` to remove thinking blocks.
4. **Output**: The system returns a `String` (cleaned text) or an `AiGenerationTimings` object containing performance metrics.

#### Dependencies
* **Internal**: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`.
* **External**: `llama.cpp` JNI bindings, Jackson (JSON serialization), and `net.ladenthin.llama` models.

#### Cross-cutting
* **Resource Management**: Providers implement `AutoCloseable` to release native handles or network resources.
* **Configuration Pattern**: Uses a fluent `Builder` pattern for complex `LlamaCppJniConfig` objects.
* **Security**: Enforces HTTPS or loopback host checks for API keys in `OpenAiCompatibleProviderSupport`.
* **Error Handling**: Standardized `IOException` for truncated responses or native communication failures.
