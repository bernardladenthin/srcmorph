### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:13:15Z
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
> Generates AI text through selectable mock, local llama.cpp, or OpenAI-compatible backends, with shared configuration, completion cleanup, and optional generation metrics.

#### Purpose
- Defines a pluggable text-generation contract and selects a backend using model- and run-level provider settings.
- Configures local and remote generation options and provides completion parsing and OpenAI-compatible request policies.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, optional timing results, and cleanup; `AiGenerationProviderFactory` resolves provider precedence and creates mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a GGUF model, runs chat completion, parses results, and reports available token and throughput metrics.
- **Configuration:** `LlamaCppJniConfig` collects model, sampling, runtime, remote API, reasoning, and stop-control settings in an immutable builder-produced object.
- **Completion cleanup:** `AiCompletionParser` strips a leading Gemma-4 reasoning block while retaining markers elsewhere in answer text.
- **OpenAI-compatible request policy:** `OpenAiCompatibleProviderSupport` constructs chat-completion JSON, validates endpoints and API-key transport, formats HTTP failures, and derives timing data.

#### Key units
- `AiGenerationProvider` — backend interface for generating text, optionally with `AiGenerationTimings`, and closing resources.
- `AiGenerationProviderFactory` — prefers a nonblank model provider over the run provider and maps provider keys to implementations.
- `LlamaCppJniAiGenerationProvider` — performs lazy, cached llama.cpp JNI inference and parses completion content.
- `LlamaCppJniConfig` — immutable configuration for local model runtime, generation sampling, and remote-provider options.
- `OpenAiCompatibleProviderSupport` — builds OpenAI-compatible requests and applies endpoint, API-key, error, and timing policies.
- `AiCompletionParser` — removes a leading reasoning block or raises `IOException` if it is unclosed.

#### Data flow
- The factory resolves the effective provider from model and run settings, then creates the corresponding provider; absent or blank settings select the mock provider.
- The llama.cpp provider combines `AiGenerationRequest`, `LlamaCppJniConfig`, and `AiPromptSupport` to build inference inputs, obtains a chat response, and returns parsed completion text or text with timing metrics.
- OpenAI-compatible request support uses the request’s prompt key and source content plus configured sampling options to serialize chat messages; response usage and elapsed time feed `AiGenerationTimings`.
- `AiCompletionParser` trims completions and, when they begin with a thinking-block marker, returns the text following the closing marker.

#### Dependencies
- Shared generation types: `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Local inference: `LlamaCppJniProviderSupport`, `LlamaModel`, llama.cpp model and response types, and `ChatResponseParser`.
- Provider selection: `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`, and `Java8CompatibilityHelper`.
- OpenAI-compatible request handling: Jackson JSON nodes, `ChatResponse`/`Usage`, and Java URL and environment-lookup utilities.
- Configuration defaults come from `AiGenerationConfig`; completion parsing uses `IOException`.

#### Cross-cutting
- `AiGenerationProvider` extends `AutoCloseable`; its default timing method wraps ordinary generation with zero metrics, and its default `close()` is a no-op.
- `LlamaCppJniAiGenerationProvider` loads its native model on first use and closes it when present; lazy initialization and access are unsynchronized.
- `LlamaCppJniConfig` has final fields and unmodifiable list accessors; its mutable builder is not thread-safe.
- API keys are resolved from a configured environment variable and are rejected for plain-HTTP endpoints outside the supported loopback hosts.
