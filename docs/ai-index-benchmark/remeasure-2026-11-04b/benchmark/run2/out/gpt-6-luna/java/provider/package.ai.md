### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:59:36Z
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
> Generates and normalizes AI text completions through configurable local llama.cpp or OpenAI-compatible backends, with mock fallback and optional timing metrics.

#### Purpose
- Defines provider selection, generation, completion cleanup, and lifecycle behavior for AI text generation.
- Centralizes model, sampling, runtime, hardware, API, reasoning, cache, and stop-sequence configuration.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, optional timings, and close behavior; `AiGenerationProviderFactory` resolves model-level versus run-level provider keys and selects mock, llama.cpp JNI, or OpenAI-compatible implementations.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads a GGUF model, applies configured inference and hardware options, parses completions, reports timing/cache metrics, and closes the native model.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completions JSON, normalizes endpoints, handles API-key retrieval and HTTP safety checks, formats errors, and derives timing metrics.
- **Configuration:** `LlamaCppJniConfig` provides immutable settings with a fluent builder, initialized from `AiGenerationConfig` defaults.
- **Completion cleanup:** `AiCompletionParser` trims responses and removes a leading Gemma-4 thinking block.

#### Key units
- `AiGenerationProvider` — pluggable generation interface with default zero-valued timings and no-op close.
- `AiGenerationProviderFactory` — resolves provider configuration and instantiates mock, `LlamaCppJniAiGenerationProvider`, or OpenAI-compatible providers.
- `LlamaCppJniAiGenerationProvider` — executes local llama.cpp inference and exposes generation timings.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — store and fluently construct immutable generation configuration.
- `OpenAiCompatibleProviderSupport` — prepares remote chat requests and handles endpoint, API-key, error, and timing support.
- `AiCompletionParser` — strips a leading `<|channel>thought` block and returns trimmed completion text.

#### Data flow
- `AiGenerationProviderFactory` selects a backend using the model provider key when nonblank, otherwise the run-level key; absent or blank selection defaults to mock generation.
- Providers receive `AiGenerationRequest`; the local provider builds inference parameters through `LlamaCppJniProviderSupport`, runs `chatComplete`, parses the response, and returns completion text or timing metrics.
- OpenAI-compatible support combines configured model and sampling options with system and user messages from `AiPromptSupport` to form a chat-completions request; response usage and elapsed seconds feed timing calculations.
- Local completion text passes through `AiCompletionParser`, which preserves ordinary text and removes only a leading thinking block.

#### Dependencies
- Internal domain types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `ChatResponseParser`, and `LlamaCppJniProviderSupport`.
- Provider implementations: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- Local inference libraries: `LlamaModel`, `ModelParameters`, `FlashAttn`, and `ReasoningFormat`.
- Remote request support uses Jackson JSON nodes and `ChatResponse` usage data.
- Configuration and compatibility support use `Java8CompatibilityHelper`; annotations and generated methods include JSpecify `Nullable` and Lombok.

#### Cross-cutting
- `AiGenerationProvider` extends `AutoCloseable`; default implementations may omit timing data and resource cleanup.
- `LlamaCppJniConfig` is immutable after construction; its builder is mutable and not thread-safe.
- Local model creation is lazy and cached, with no declared synchronization or thread-safety guarantee.
- Missing local timing data yields zero metrics; the default provider timing wrapper also reports zeros.
- OpenAI-compatible API keys are trimmed and are not sent over HTTP to non-loopback hosts; endpoint configuration must use HTTP or HTTPS.
- A leading thinking block without a closing marker causes `AiCompletionParser` to throw `IOException`.
