### java/provider
- H: 1.0
- C: D2D2268E
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:57:47Z
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
> Provides a pluggable architecture for local and remote AI text generation, managing model lifecycle, configuration, and response sanitization.

#### Purpose
- Defines a provider abstraction for generating text via local (llama.cpp) or remote (OpenAI-compatible) backends.
- Ensures clean output by stripping internal reasoning blocks from model responses before persistence.

#### Responsibilities
- **Provider Orchestration**: Factory logic to resolve configuration keys to specific backend implementations (Mock, LlamaCpp, OpenAI).
- **Local Model Execution**: Manages lazy loading, GPU/CPU offloading, and KV-cache reuse for `llama.cpp` native bindings.
- **Remote Request Construction**: Serializes prompts to JSON, validates endpoint security, and handles API key transmission.
- **Response Sanitization**: Parses raw completion text to remove chain-of-thought markers and extract final answers.
- **Configuration Management**: Enforces immutable, builder-pattern configuration for 37+ model parameters.

#### Key units
- `AiGenerationProvider`: Interface defining `generate` and `generateWithTimings` contracts for pluggable backends.
- `AiGenerationProviderFactory`: Resolves provider names to `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider`: Implements local generation using `LlamaModel` with lazy initialization and parameter mapping.
- `LlamaCppJniConfig`: Immutable value object holding 37 configuration options for model loading and sampling.
- `OpenAiCompatibleProviderSupport`: Stateless utility for JSON request building, endpoint validation, and secure API key routing.
- `AiCompletionParser`: Strips `<|channel>thought` blocks from raw text to prevent reasoning data leakage.
- `AiGenerationTimings`: Data carrier for performance metrics (tokens/sec, cache hits) returned by providers.

#### Data flow
1.  **Configuration**: `LlamaCppJniConfig` is constructed via `Builder` with model path and sampling parameters.
2.  **Resolution**: `AiGenerationProviderFactory` selects the implementation based on `runProvider` or `modelProvider` keys.
3.  **Execution**:
    *   **Local**: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, maps config to `ModelParameters`, and executes `generate()`.
    *   **Remote**: `OpenAiCompatibleProviderSupport` builds JSON payload, validates HTTPS for API keys, and constructs the request.
4.  **Sanitization**: Raw `String` response from the backend is passed to `AiCompletionParser.parseCompletion()` to remove thinking blocks.
5.  **Output**: Clean text and `AiGenerationTimings` are returned to the caller.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig`.
- **Native/External**: `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`.
- **Serialization**: `com.fasterxml.jackson.databind.ObjectMapper` for JSON request construction.
- **Utilities**: `lombok` (ToString, Builder), `org.jspecify` (Nullable), `java.io.IOException`.

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and `AiGenerationTimings` are immutable value types; `OpenAiCompatibleProviderSupport` is stateless.
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS for API keys on non-loopback hosts, throwing `IOException` on insecure transmission.
- **Error Handling**: `AiCompletionParser` throws `IOException` on truncated reasoning blocks; Factory throws `IllegalArgumentException` for unknown providers.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable` to ensure native `LlamaModel` handles are closed.
