### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:45:50Z
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
> Generates AI text for application requests through mock, local llama.cpp, or OpenAI-compatible backends, with completion cleanup and optional inference metrics.

#### Purpose
- Selects and configures an AI generation backend for `AiGenerationRequest` inputs.
- Normalizes model output and exposes timing data where supported.

#### Responsibilities
- **Backend contract and selection:** `AiGenerationProvider` defines generation and lifecycle operations; `AiGenerationProviderFactory` applies run/model provider precedence and selects mock, `llamacpp-jni`, or `openai-compatible`.
- **Local inference:** `LlamaCppJniAiGenerationProvider` configures and reuses a llama.cpp model, generates chat completions, and reports token/cache timing metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` constructs chat-completion requests, resolves endpoints and API keys, checks HTTP outcomes, and calculates timings.
- **Generation settings:** `LlamaCppJniConfig` holds immutable model, sampling, caching, hardware, provider, reasoning, and stop-sequence options.
- **Completion cleanup:** `AiCompletionParser` removes a leading Gemma-4 thinking block while preserving markers appearing within answer text.

#### Key units
- `AiGenerationProvider` — pluggable `generate` / `generateWithTimings` contract with default zero-timing and no-op close behavior.
- `AiGenerationProviderFactory` — resolves provider precedence and instantiates the selected backend.
- `LlamaCppJniAiGenerationProvider` — lazily initializes a `LlamaModel`, generates local completions, and exposes timing and cache-reuse information.
- `LlamaCppJniConfig` — immutable settings object with a fluent builder and `AiGenerationConfig` defaults.
- `OpenAiCompatibleProviderSupport` — package-private utilities for OpenAI-compatible request construction, credential safety, HTTP errors, and timing conversion.
- `AiCompletionParser` — strips leading reasoning blocks and raises `IOException` for an unterminated block.

#### Data flow
- `AiGenerationProviderFactory` resolves run-level and model-level provider settings, then constructs the selected provider using model configuration and `AiPromptSupport`.
- A provider consumes `AiGenerationRequest`; the local backend builds inference parameters through `LlamaCppJniProviderSupport`, calls `LlamaModel`, parses the chat response, and cleans completion text with `AiCompletionParser`.
- The OpenAI-compatible support builds chat-completion JSON from prompt support and configuration, resolves endpoint and safe API-key usage, and converts response usage into `AiGenerationTimings`.
- `generateWithTimings` returns generated text with metrics; providers without timing support use the interface’s zero-timing default.

#### Dependencies
- Shared generation types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Local inference: llama.cpp JNI types including `LlamaModel`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, and `Timings`.
- Remote request support: Jackson JSON nodes and mapper, `ChatResponse`, and `Usage`.
- Provider and configuration utilities: `LlamaCppJniProviderSupport` and `Java8CompatibilityHelper`.

#### Cross-cutting
- Provider failures use `IOException`; unsupported provider keys produce `IllegalArgumentException`.
- `LlamaCppJniConfig` is immutable, with unmodifiable list views; its builder is intended for single-threaded use.
- Local model initialization is lazy and cached, but concurrent first access is not explicitly synchronized.
- OpenAI-compatible API keys are permitted over HTTPS or to loopback hosts over HTTP; non-loopback cleartext transmission is rejected.
- Providers implement `AutoCloseable`; the interface’s default `close` is a no-op, while the local provider closes an initialized model.
