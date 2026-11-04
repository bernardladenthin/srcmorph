### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:50:10Z
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
> Generates source-aware AI completions through selectable mock, local llama.cpp, or OpenAI-compatible backends, with configurable inference and response cleanup.

#### Purpose
- Provide a pluggable contract and factory for AI text generation, with optional inference timing data.
- Configure local GGUF inference and OpenAI-compatible requests, including model parameters, provider settings, and safe API-key handling.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProvider` defines generation, timing, and close behavior; `AiGenerationProviderFactory` resolves model overrides and selects mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads a llama.cpp model, builds inference parameters, parses responses, logs truncation and cache reuse, and reports available timings.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completion JSON, resolves endpoints and API keys, checks HTTP transport safety, formats errors, and derives timing metrics.
- **Configuration and cleanup:** `LlamaCppJniConfig` captures model, sampling, hardware, caching, provider, reasoning, DRY, and stop-string options; `AiCompletionParser` strips a leading Gemma-4 thinking block from completion text.

#### Key units
- `AiGenerationProvider` — backend contract for generation, optional timing results, and resource closure.
- `AiGenerationProviderFactory` — provider-key resolution and backend construction.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable generation settings and fluent configuration.
- `LlamaCppJniAiGenerationProvider` — local llama.cpp JNI model loading and completion generation.
- `OpenAiCompatibleProviderSupport` — OpenAI-compatible request construction, HTTP/key safety checks, and timing conversion.
- `AiCompletionParser` — trims completion text and removes a leading thinking block, throwing `IOException` if it is unterminated.

#### Data flow
- `AiGenerationRequest` and `LlamaCppJniConfig` flow through provider selection into a backend.
- The llama.cpp provider builds inference parameters, calls `LlamaModel.chatComplete`, parses the response, and returns cleaned text or `AiGenerationTimings`.
- OpenAI-compatible support combines request and config values into serialized chat JSON; response usage and elapsed time feed timing results.
- Provider selection prefers a nonblank model-level provider override, then the run-level key; absent keys select the mock provider.

#### Dependencies
- Internal: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, and the mock, llama.cpp JNI, and OpenAI-compatible provider types.
- External: llama.cpp JNI model and response APIs; Jackson JSON nodes and mapper; Java I/O, URL, collections, and functional APIs; Lombok and JSpecify annotations.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable and defaulted; its builder is documented as not thread-safe.
- The llama.cpp provider caches a lazily created model without synchronization; generation and `close()` are not coordinated.
- `AiGenerationProvider` supplies zero-valued default timings and a no-op `close()` unless providers override them.
- OpenAI-compatible support permits API keys over HTTPS or HTTP to recognized loopback hosts, and rejects keyed plain-HTTP requests to other hosts.
