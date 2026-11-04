### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:38:30Z
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
> Generates text from AI prompts through selectable local llama.cpp or OpenAI-compatible backends, with mock fallback, completion cleanup, and optional generation metrics.

#### Purpose
- Define and configure pluggable AI text-generation providers for `AiGenerationRequest` inputs.
- Support local GGUF inference, OpenAI-compatible chat completions, and mock generation.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProviderFactory` resolves model-level versus run-level provider settings, defaults to mock when unset, and dispatches supported provider keys; `AiGenerationProvider` defines generation, timing, and cleanup operations.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a llama.cpp `LlamaModel`, builds runtime parameters from configuration, parses completions, and reports token and throughput metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` assembles chat-completion JSON, resolves endpoints and API keys, formats HTTP errors, and calculates timing metrics for its provider.
- **Completion cleanup:** `AiCompletionParser` removes a leading Gemma-4 thinking block and trims the remaining answer.
- **Generation configuration:** `LlamaCppJniConfig` stores immutable model, sampling, hardware, caching, provider, reasoning, and stop settings, initialized through a fluent builder.

#### Key units
- `AiGenerationProvider` — provider interface for generation, optional timing results, and resource cleanup.
- `AiGenerationProviderFactory` — selects `mock`, `llamacpp-jni`, or `openai-compatible` implementations.
- `LlamaCppJniAiGenerationProvider` — performs native local-model inference and extracts response and timing data.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable generation settings and their mutable fluent construction API.
- `OpenAiCompatibleProviderSupport` — constructs remote chat-completion requests and handles endpoint, key-safety, HTTP-result, and timing support.
- `AiCompletionParser` — strips leading Gemma-4 chain-of-thought text from completions.
- `AiGenerationTimings` — timing and token-count result used by provider APIs.

#### Data flow
- The factory chooses a backend using run- and model-level provider keys, then constructs it with configuration and prompt support.
- A provider consumes `AiGenerationRequest`; local inference uses `LlamaCppJniProviderSupport` and a cached `LlamaModel`, while remote support builds a chat-completion request from prompt messages and configuration.
- Completion text is parsed to remove a leading thinking block; timing-capable paths return text with available token counts and rates.

#### Dependencies
- Internal: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, and `Java8CompatibilityHelper`.
- Local inference: llama.cpp JNI types including `LlamaModel`, `ModelParameters`, `ChatResponseParser`, and `Timings`.
- Remote request support: Jackson JSON nodes and `ObjectMapper`; response usage data from `ChatResponse` and `Usage`.
- Configuration defaults: `AiGenerationConfig`; completion parsing reports malformed thinking blocks through `IOException`.

#### Cross-cutting
- Provider generation declares `IOException`; `AiGenerationProvider` supplies default zero-valued timing data and a no-op `close()`.
- `LlamaCppJniConfig` is immutable after building; its builder is mutable and not thread-safe, and configuration values receive no range validation.
- The local provider lazily initializes a shared `LlamaModel` without synchronization, so concurrent first calls may race.
- OpenAI-compatible support only returns API keys for HTTPS or loopback HTTP endpoints; other cleartext destinations are rejected.
