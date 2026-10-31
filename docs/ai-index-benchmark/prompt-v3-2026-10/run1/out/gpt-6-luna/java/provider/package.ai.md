### java/provider
- H: 1.0
- C: 748BAA0F
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:51:11Z
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
> Selects and runs configurable AI text-generation backends for document completions, cleaning generated answers and optionally reporting inference timings.

#### Purpose
- Defines the provider contract and configuration-driven selection for mock, llama.cpp JNI, and OpenAI-compatible generation.
- Supports local or compatible chat-completion generation, with response cleanup, safe API-key handling, and timing extraction.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation and lifecycle operations; `AiGenerationProviderFactory` resolves model-versus-run provider settings and instantiates a backend.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a GGUF model, runs chat completion, parses results, and reports model timing metrics.
- **Provider configuration:** `LlamaCppJniConfig` holds immutable model, sampling, hardware, cache, API, reasoning, and stop-sequence settings, with defaults from `AiGenerationConfig`.
- **OpenAI-compatible request support:** `OpenAiCompatibleProviderSupport` builds chat-completion payloads and endpoints, restricts API-key use over plain HTTP, and derives errors and usage timings.
- **Completion cleanup:** `AiCompletionParser` strips a leading Gemma-4 thinking block and rejects an unterminated block.

#### Key units
- `AiGenerationProvider` — pluggable `AutoCloseable` contract for text generation, optional timings, and provider failures.
- `AiGenerationProviderFactory` — applies provider precedence and constructs mock, llama.cpp JNI, or OpenAI-compatible providers.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable backend and inference settings with fluent construction.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp chat inference and returns parsed text and available metrics.
- `OpenAiCompatibleProviderSupport` — prepares compatible chat requests and interprets endpoint, key, error, and usage data.
- `AiCompletionParser` — removes a leading `<|channel>thought` block, preserving markers elsewhere in the answer.

#### Data flow
- Provider selection uses the model-level provider when nonblank, otherwise the run-level setting; absent or `"mock"` selection falls back to mock generation.
- A selected provider uses `AiPromptSupport` and an `AiGenerationRequest` to generate text; the local backend parses completion content and can return token counts and rates.
- OpenAI-compatible support renders system and user messages into a non-streaming request body; response usage can be converted into `AiGenerationTimings`.
- Leading thinking content is removed by `AiCompletionParser`; an unterminated leading block produces an `IOException`.

#### Dependencies
- Internal collaborators include `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, and the OpenAI-compatible provider referenced by the factory.
- Local inference uses the `net.ladenthin:llama` JNI types, including `LlamaModel`, `ModelParameters`, and chat response/timing types.
- OpenAI-compatible request support uses Jackson JSON nodes and response `ChatResponse`/`Usage` data.

#### Cross-cutting
- `AiGenerationProvider` uses `IOException` for generation and resource-close failures; its default timing method wraps generated text with zero metrics.
- `LlamaCppJniConfig` is immutable with final fields and unmodifiable list accessors; its builder is intended for single-threaded use.
- The local provider lazily initializes and caches its model without synchronization; concurrent first calls may race.
- OpenAI-compatible support performs no network calls itself and allows API keys over HTTP only for loopback hosts; HTTPS is permitted.
