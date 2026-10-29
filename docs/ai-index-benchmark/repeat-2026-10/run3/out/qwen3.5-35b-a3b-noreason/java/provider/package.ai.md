### java/provider
- H: 1.0
- C: 7F4A1655
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:45:01Z
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
> Provides a pluggable architecture for AI text generation that supports local llama.cpp inference, OpenAI-compatible APIs, and mock backends while handling prompt construction, response parsing, and security validation.

#### Purpose
- Orchestrates the selection, instantiation, and execution of diverse AI generation providers (local, remote, or mock) based on configuration.
- Sanitizes raw LLM outputs by stripping internal reasoning blocks before persisting results.

#### Responsibilities
- **Provider Orchestration**: Factory logic resolves effective provider keys from run/model configurations and instantiates concrete implementations (`Mock`, `LlamaCppJni`, `OpenAiCompatible`).
- **Local Inference Execution**: Manages native llama.cpp model loading, parameter configuration (GPU offloading, KV caching), and inference execution with timing metrics.
- **Remote API Integration**: Constructs JSON payloads for OpenAI-compatible endpoints, validates HTTPS/loopback security for API keys, and parses HTTP responses.
- **Response Sanitization**: Parses raw completion strings to remove specific chain-of-thought markers (e.g., Gemma-4 thinking blocks) and extract clean answers.
- **Configuration Management**: Enforces immutable, builder-based configuration for 37+ parameters controlling model behavior, sampling, and hardware usage.

#### Key units
- `AiGenerationProviderFactory`: Resolves provider priority and dispatches instantiation to concrete classes based on string constants.
- `LlamaCppJniAiGenerationProvider`: Implements the provider interface using native JNI handles for GGUF models with lazy initialization.
- `OpenAiCompatibleProviderSupport`: Serializes requests to JSON, validates endpoint security schemes, and formats error messages.
- `AiCompletionParser`: Strips `<|channel>thought` blocks from raw text to prevent reasoning leakage in persisted data.
- `LlamaCppJniConfig`: Immutable value object holding 37 configuration fields for local model tuning via a strict builder pattern.
- `AiGenerationProvider`: Interface contract defining `generate`, `generateWithTimings`, and resource cleanup methods.

#### Data flow
1.  **Initialization**: `AiGenerationProviderFactory` receives run-level and model-level provider keys, resolving them to an effective name (model overrides run).
2.  **Instantiation**: The factory creates an instance of `LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleAiGenerationProvider` using `LlamaCppJniConfig` and `AiPromptSupport`.
3.  **Request Processing**:
    - For local: `LlamaCppJniAiGenerationProvider` lazily loads the `LlamaModel`, constructs `ModelParameters` from config, and executes `chatComplete()`.
    - For remote: `OpenAiCompatibleProviderSupport` builds a JSON payload, validates the URL scheme and API key security, and sends the request.
4.  **Response Handling**: Both paths return raw text or `ChatResponse` objects; `generateWithTimings` extracts token counts and calculates throughput rates.
5.  **Sanitization**: Raw completion strings are passed to `AiCompletionParser.parseCompletion()` to strip thinking blocks before returning the final answer.

#### Dependencies
- **Internal**: `net.ladenthin.llama.*` (native JNI bindings, `LlamaModel`, `ChatResponse`, `Timings`), `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.document.AiGenerationRequest`.
- **External**: `com.fasterxml.jackson.databind` (JSON serialization), `org.jspecify.annotations.Nullable`, `lombok` (annotations), `java.io.IOException`.
- **Native**: `llama.cpp` library accessed via JNI for model inference.

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` is fully immutable after construction; `AiGenerationProvider` interface ensures consistent contracts.
- **Security**: `OpenAiCompatibleProviderSupport` enforces strict checks preventing API key transmission over unencrypted HTTP unless the host is loopback.
- **Error Handling**: Consistent use of `IOException` for network/native failures; `IllegalArgumentException` for invalid provider names or missing required config fields.
- **Resource Management**: Providers implement `AutoCloseable` to ensure native `LlamaModel` handles are released safely.
