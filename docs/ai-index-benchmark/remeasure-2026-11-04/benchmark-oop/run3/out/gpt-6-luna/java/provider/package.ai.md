### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:53:16Z
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
> Converts source-generation requests into cleaned AI completions through selectable mock, local llama.cpp, or OpenAI-compatible backends.

#### Purpose
- Defines provider selection and generation contracts, with shared configuration and support for local and OpenAI-compatible inference.
- Cleans model output and optionally returns generation timing and token-usage metrics.

#### Responsibilities
- **Provider abstraction and selection:** `AiGenerationProvider` defines text generation, optional timings, and resource cleanup; `AiGenerationProviderFactory` resolves run- and model-level provider settings and dispatches to mock, llama.cpp JNI, or OpenAI-compatible implementations.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily configures and reuses a GGUF `LlamaModel`, generates completions, and exposes model timing and cache metrics.
- **Provider configuration:** `LlamaCppJniConfig` and its builder collect model, sampling, hardware, cache, reasoning, provider, and stop-control settings.
- **Output and remote-request support:** `AiCompletionParser` strips a leading Gemma 4 thinking block; `OpenAiCompatibleProviderSupport` assembles chat-completion request bodies, resolves endpoints and API keys, checks responses, and calculates timings.

#### Key units
- `AiGenerationProvider` — pluggable generation contract with default zero-valued timing support and no-op `close()`.
- `AiGenerationProviderFactory` — selects `mock`, `llamacpp-jni`, or `openai-compatible`, defaulting to mock when no provider key is set.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp chat completions with lazy model loading and timing extraction.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable settings and fluent construction for generation providers.
- `AiCompletionParser` — removes a leading `<|channel>thought` block and rejects an unterminated leading block with `IOException`.
- `OpenAiCompatibleProviderSupport` — package-private request, endpoint, API-key, error-message, and timing utilities for an OpenAI-compatible provider.
- `AiGenerationTimings` — generation result type used by the provider contract and timing-producing support.

#### Data flow
- Provider settings are resolved by `AiGenerationProviderFactory`; `LlamaCppJniConfig` supplies local inference and remote-provider options.
- A provider receives an `AiGenerationRequest`; the llama.cpp path builds inference parameters through `LlamaCppJniProviderSupport`, runs `chatComplete`, and parses the completion with `AiCompletionParser`.
- The OpenAI-compatible support path builds JSON from the request and configuration, resolves the completion endpoint and optional API key, and maps response usage into `AiGenerationTimings`.
- `generateWithTimings` returns generated text with timing data where available; the provider interface supplies zero timing rates by default when an implementation does not report timings.

#### Dependencies
- Internal collaborators: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, and `Java8CompatibilityHelper`.
- Provider implementations selected by the factory: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- External inference and response types: llama.cpp JNI (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`) and Jackson JSON nodes and mapper.
- Configuration defaults come from `AiGenerationConfig`; the OpenAI-compatible support uses `ChatResponse` and `Usage` for response timing data.

#### Cross-cutting
- Providers implement `AutoCloseable`; the llama.cpp provider loads its model lazily and closes it only if initialized.
- `LlamaCppJniConfig` fields are final, but builder list inputs are not defensively copied; the builder is not thread-safe.
- The llama.cpp provider’s lazy model field is unsynchronized, so concurrent first calls are not guarded against duplicate initialization.
- OpenAI-compatible support permits API keys over HTTPS or plain HTTP to recognized loopback hosts; it rejects configured keys for other plain-HTTP hosts.
- A leading but incomplete thinking block raises `IOException`; an unrecognized nonblank provider key raises `IllegalArgumentException`.
