### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:29:18Z
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
> Generates AI text for source-aware documentation or indexing workflows through configurable local llama.cpp, OpenAI-compatible, or mock backends.

#### Purpose
- Defines provider selection and generation contracts for `AiGenerationRequest` inputs, with optional timing results.
- Centralizes model/provider configuration and completion cleanup, including removal of leading Gemma-style reasoning blocks.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProviderFactory` resolves model-level versus run-level provider keys and selects mock, llama.cpp, or OpenAI-compatible backends; `AiGenerationProvider` defines generation, timing, and resource-closing operations.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads a llama.cpp model, applies configured inference and hardware options, parses responses, and reports timing and cache data.
- **Remote request support:** `OpenAiCompatibleProviderSupport` formats chat-completion requests, builds endpoints, handles API-key safety checks, and derives response status, error messages, and timings.
- **Configuration and response cleanup:** `LlamaCppJniConfig` holds generation, hardware, cache, remote-provider, reasoning, and stop settings; `AiCompletionParser` strips a leading thinking block and rejects an unterminated one.

#### Key units
- `AiGenerationProvider` — backend interface for generating text, optionally with timing data, and closing resources.
- `AiGenerationProviderFactory` — selects `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider` from provider keys.
- `LlamaCppJniAiGenerationProvider` — performs local model inference and exposes completion text, model timings, and cache statistics.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable model and inference settings with a fluent, mutable builder.
- `OpenAiCompatibleProviderSupport` — package-private support for remote chat request construction, endpoint and authentication handling, and response helpers.
- `AiCompletionParser` — removes a leading Gemma reasoning block while preserving markers elsewhere in the answer.

#### Data flow
- `AiGenerationProviderFactory` resolves the effective provider key, then constructs the selected backend using `LlamaCppJniConfig` and `AiPromptSupport` as applicable.
- The backend consumes an `AiGenerationRequest`; the llama.cpp provider builds inference parameters, invokes `LlamaModel.chatComplete`, parses the response, and cleans the first-content text with `AiCompletionParser`.
- Timing-aware generation returns `AiGenerationTimings`; the provider interface supplies zero-valued timing defaults when an implementation does not report metrics.
- OpenAI-compatible support builds a serialized chat-completions request from prompt definitions and configuration, and provides response-status, error, and timing helpers.

#### Dependencies
- Internal request and prompt types: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationConfig`, and `Java8CompatibilityHelper`.
- Provider implementations: `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider` are factory dependencies; local inference uses `LlamaCppJniProviderSupport`.
- Local inference libraries: `LlamaModel`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, `Timings`, `FlashAttn`, and `ReasoningFormat`.
- Remote request support uses Jackson JSON nodes and `ObjectMapper`; provider configuration includes remote model, base URL, and API-key environment-variable settings.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable after building; its builder is mutable and not thread-safe.
- Local model initialization is lazy and unsynchronized; concurrent first use or close is not guarded.
- Provider operations declare `IOException` where applicable; unsupported provider keys and invalid remote endpoint or unsafe API-key delivery produce errors.
- OpenAI-compatible API keys are permitted over HTTPS or plain HTTP to recognized loopback hosts; other plain-HTTP key delivery is rejected.
