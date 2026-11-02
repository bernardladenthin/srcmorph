### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:02:34Z
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
> Generates AI completions for source-generation requests through selectable local llama.cpp or OpenAI-compatible backends, with prompt cleanup and optional timing metrics.

#### Purpose
- Defines the provider contract, configuration, and selection path for AI text generation.
- Supports local GGUF inference and OpenAI-compatible chat-completion request preparation.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProvider` standardizes generation, timing, and resource closing; `AiGenerationProviderFactory` resolves model-level versus run-level provider settings and selects mock, llama.cpp JNI, or OpenAI-compatible implementations.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a llama.cpp model, applies configured inference options, and returns completion text and model timings.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completion request JSON and handles endpoint construction, API-key transport checks, HTTP status interpretation, error formatting, and timing conversion.
- **Settings and output cleanup:** `LlamaCppJniConfig` holds immutable model, sampling, hardware, caching, provider, reasoning, and stop settings; `AiCompletionParser` removes a leading model reasoning block from completion text.

#### Key units
- `AiGenerationProvider` — interchangeable generation interface with default zero-valued timings and no-op `close`.
- `AiGenerationProviderFactory` — applies provider precedence and dispatches to mock, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniConfig` — immutable builder-created settings shared by local and compatible remote generation.
- `LlamaCppJniAiGenerationProvider` — configures, invokes, and closes the lazily initialized llama.cpp `LlamaModel`.
- `OpenAiCompatibleProviderSupport` — prepares non-streaming chat-completion requests and validates endpoint, API-key, response, and timing details.
- `AiCompletionParser` — strips a leading `THINKING_BLOCK_START_MARKER`…`THINKING_BLOCK_END_MARKER` block and returns trimmed answer text.

#### Data flow
- `AiGenerationProviderFactory` chooses a backend using the model provider when nonblank, otherwise the run provider; a missing key defaults to mock.
- A provider receives `AiGenerationRequest` and uses `AiPromptSupport` to render prompts. The local provider passes inference settings to llama.cpp; compatible-provider support serializes system and user messages with configured generation options.
- Completion text is returned directly or with `AiGenerationTimings`; `AiCompletionParser` removes leading reasoning blocks where used.
- Local model loading is deferred until generation and the retained model is closed through `close()`.

#### Dependencies
- Project types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, and `Java8CompatibilityHelper`.
- Local inference: llama.cpp JNI model, parameter, response, parser, timing, and attention types.
- Compatible request construction: Jackson JSON nodes and `ObjectMapper`, plus `ChatResponse` and `Usage`.
- Java APIs include `AutoCloseable`, `IOException`, collections, URL parsing, and environment lookup via `Function`.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable and exposes unmodifiable list views; its builder is not thread-safe and does not validate numeric ranges.
- Provider generation methods declare `IOException`; the factory rejects unknown provider keys with `IllegalArgumentException`.
- Local model initialization is lazy, but its mutable model reference has no synchronization or explicit coordination with `close()`.
- Default provider timing values are zero; local and compatible support can report model or response-derived token counts and rates.
- Plain-HTTP API keys are permitted only for loopback hosts; HTTPS endpoints may use configured keys.
