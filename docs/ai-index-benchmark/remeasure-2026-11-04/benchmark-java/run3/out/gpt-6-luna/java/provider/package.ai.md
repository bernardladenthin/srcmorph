### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:46:20Z
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
> Generates source-aware text completions through configurable mock, local llama.cpp, or OpenAI-compatible backends, with response cleanup and optional inference metrics.

#### Purpose
- Define and construct AI generation backends for requests containing a prompt key and source context.
- Configure model and transport behavior, normalize completion text, and expose timing data where available.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, optional timing results, and resource cleanup; `AiGenerationProviderFactory` resolves model-level versus run-level provider settings and dispatches to mock, llama.cpp JNI, or OpenAI-compatible implementations.
- **Local model inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a native GGUF model, performs chat inference, parses responses, and reports token and throughput metrics.
- **Provider configuration:** `LlamaCppJniConfig` captures immutable model, sampling, hardware, cache, reasoning, provider, and stop-sequence settings.
- **Completion and remote-request support:** `AiCompletionParser` strips a leading Gemma 4 thinking block; `OpenAiCompatibleProviderSupport` builds chat-completion requests and handles endpoint, API-key, HTTP-status, error-message, and usage-to-timing utilities.

#### Key units
- `AiGenerationProvider` — backend interface for generating text, optionally with timings, and closing resources.
- `AiGenerationProviderFactory` — chooses `mock`, `llamacpp-jni`, or `openai-compatible` using effective provider settings.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp JNI inference and exposes response text and native timing metrics.
- `LlamaCppJniConfig` — immutable configuration value with a fluent builder for local inference and compatible-provider options.
- `AiCompletionParser` — removes an initial Gemma 4 thinking block while preserving answer text.
- `OpenAiCompatibleProviderSupport` — package-private helpers for request JSON, endpoint and API-key handling, HTTP checks, error formatting, and timing conversion.

#### Data flow
- Provider settings and `LlamaCppJniConfig` flow through `AiGenerationProviderFactory` to a selected backend; missing or blank effective settings select the mock provider.
- A backend consumes `AiGenerationRequest` and `AiPromptSupport`; the llama.cpp provider builds inference parameters, invokes chat completion, parses the response, and applies `AiCompletionParser` to its first response content.
- OpenAI-compatible support builds a non-streaming chat-completions request from system and user prompts and configuration; response usage and elapsed time can be converted to `AiGenerationTimings`.

#### Dependencies
- Internal request and prompt types: `AiGenerationRequest`, `AiPromptSupport`; configuration defaults: `AiGenerationConfig`.
- Generation support types: `AiGenerationTimings`, `ChatResponseParser`, `ChatResponse`, and `Usage`.
- Local inference: llama.cpp JNI types including `LlamaModel`, `ModelParameters`, `FlashAttn`, and `ReasoningFormat`, plus `LlamaCppJniProviderSupport`.
- OpenAI-compatible request serialization uses Jackson; API-key resolution uses a supplied environment lookup function.

#### Cross-cutting
- Providers implement `AutoCloseable`; the interface’s default timing method delegates to generation and reports zero timings, while the llama.cpp provider closes its cached native model.
- `LlamaCppJniConfig` is immutable after construction; its builder is mutable and not thread-safe. The llama.cpp provider’s lazy model initialization and close/generation operations are unsynchronized.
- OpenAI-compatible support allows API keys over HTTPS or HTTP loopback only; it rejects cleartext key transmission to non-loopback hosts.
