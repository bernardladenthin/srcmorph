### java/provider
- H: 1.0
- C: CDF7F2E3
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:13:25Z
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
> Generates text for source-document workflows through selectable mock, local llama.cpp, or OpenAI-compatible AI backends, with configurable inference and optional timing metrics.

#### Purpose
- Define a pluggable `AiGenerationProvider` contract and select a backend from run- or model-level provider configuration.
- Configure model and request behavior, produce completions, and remove leading Gemma-style reasoning from answers.

#### Responsibilities
- **Provider selection:** `AiGenerationProviderFactory` resolves model provider overrides, defaults blank selections to mock, and dispatches supported provider keys.
- **Generation backends:** `LlamaCppJniAiGenerationProvider` runs local GGUF inference with lazy model loading, configurable placement and caching, and timing extraction; OpenAI-compatible request support prepares remote chat-completions data and validates API-key transport.
- **Generation configuration:** `LlamaCppJniConfig` provides immutable model, sampling, hardware, caching, provider, reasoning, DRY, and stop settings through a fluent builder.
- **Completion handling:** `AiCompletionParser` strips an initial thinking block, preserves later marker text, and rejects an unterminated leading block.
- **Provider contract and metrics:** `AiGenerationProvider` defines generation, optional timing results, and resource closing; its default timing method reports zero metrics.

#### Key units
- `AiGenerationProvider` — backend interface for `AiGenerationRequest` generation, timing results, and lifecycle.
- `AiGenerationProviderFactory` — resolves provider precedence and constructs mock, llama.cpp JNI, or OpenAI-compatible providers.
- `LlamaCppJniAiGenerationProvider` — lazily initializes and reuses `LlamaModel` for local chat inference.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable generation settings and fluent configuration API.
- `OpenAiCompatibleProviderSupport` — builds chat-completions JSON and endpoint data, handles API-key transport rules and response error/timing calculations.
- `AiCompletionParser` — extracts the answer following a leading Gemma thinking block.

#### Data flow
- `AiGenerationProviderFactory` chooses a provider using the model provider key when nonblank, otherwise the run provider key; absent keys select the mock backend.
- Providers consume `AiGenerationRequest`; the llama.cpp provider uses `AiPromptSupport` and `LlamaCppJniProviderSupport` to prepare inference, then parses completion content and may return timing counters.
- OpenAI-compatible support combines request and `LlamaCppJniConfig` settings into a chat-completions body and endpoint data, and converts response usage plus elapsed time into timing metrics.
- `AiCompletionParser` trims output and removes a leading thinking block; missing its closing marker raises `IOException`.

#### Dependencies
- Shared request and result types: `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Local inference: `LlamaCppJniProviderSupport` and llama.cpp JNI types including `LlamaModel`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, and `Timings`.
- Configuration defaults and utilities: `AiGenerationConfig` and `Java8CompatibilityHelper`.
- Remote request support: Jackson JSON nodes and mapper, plus `ChatResponse` / `Usage` data.
- Provider lifecycle uses Java `AutoCloseable`; configuration and provider code use Lombok and JSpecify annotations.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable after construction; its builder is not thread-safe, and list accessors expose unmodifiable views.
- The llama.cpp provider caches its native model, but initialization and access are unsynchronized; `close()` closes the current model without clearing the cached field.
- Provider operations declare `IOException` for applicable generation and lifecycle failures; unsupported nonblank provider keys raise `IllegalArgumentException`.
- OpenAI-compatible support rejects non-HTTP(S) endpoints and blocks API-key transmission over cleartext HTTP except for recognized loopback hosts.
