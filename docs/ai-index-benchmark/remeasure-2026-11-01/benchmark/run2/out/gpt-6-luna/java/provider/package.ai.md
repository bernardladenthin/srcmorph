### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:44:46Z
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
> Provides configurable AI text generation for application requests through local llama.cpp models or OpenAI-compatible services, with mock fallback, completion cleanup, and optional timing metrics.

#### Purpose
- Selects and runs an AI generation backend for `AiGenerationRequest` inputs.
- Configures model and sampling behavior, cleans model-internal reasoning from completions, and supports timing results.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, timing, and resource-lifecycle operations; `AiGenerationProviderFactory` resolves provider keys and creates mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Local model generation:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a GGUF model, performs chat completion, parses responses, and reports inference and prompt-cache metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completion JSON and handles endpoint, API-key, status, error-message, and timing logic without performing network I/O.
- **Configuration and parsing:** `LlamaCppJniConfig` holds immutable model and generation settings; `AiCompletionParser` strips a leading Gemma-compatible thinking block.

#### Key units
- `AiGenerationProvider` — backend interface with default timing wrapping and no-op `close`.
- `AiGenerationProviderFactory` — resolves provider configuration and selects `mock`, `llamacpp-jni`, or `openai-compatible`.
- `LlamaCppJniAiGenerationProvider` — generates completions using a lazily initialized llama.cpp JNI model.
- `LlamaCppJniConfig` — builder-created configuration for model loading, sampling, hardware, caching, remote-provider, reasoning, and stop options.
- `OpenAiCompatibleProviderSupport` — constructs OpenAI-compatible requests and applies endpoint, credential, error, and timing rules.
- `AiCompletionParser` — removes a leading thinking block and rejects one without a closing marker.

#### Data flow
- `AiGenerationProviderFactory` resolves the effective provider key from run and model configuration, defaulting to mock when blank.
- The selected provider consumes an `AiGenerationRequest`; local generation uses `LlamaCppJniConfig` and `AiPromptSupport`, while remote request support builds JSON from request, prompt definitions, and configuration.
- Completion text is parsed to remove a leading thinking block; timing-capable paths return text with token counts and throughput metrics where available.

#### Dependencies
- Generation paths use `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`; configuration defaults come from `AiGenerationConfig`.
- Local inference depends on llama.cpp JNI types including `LlamaModel`, `ModelParameters`, `ChatResponse`, and `Timings`.
- OpenAI-compatible request support uses Jackson JSON types and `LlamaCppJniConfig`; it relies on an injected environment lookup for API keys.
- Provider selection also references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
- `AiGenerationProvider` extends `AutoCloseable`; provider failures use `IOException` where declared.
- `LlamaCppJniConfig` is immutable after construction, with unmodifiable list views; its builder is not thread-safe.
- The llama.cpp provider caches its model, but lazy initialization and access are unsynchronized.
- `AiCompletionParser` converts null input to an empty string; a missing thinking-block terminator raises `IOException`.
- OpenAI-compatible support restricts API keys over plain HTTP to loopback hosts and bounds included error-body text.
