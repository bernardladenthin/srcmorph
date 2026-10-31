### java/provider
- H: 1.0
- C: 87119406
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:47:17Z
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
> Generates and routes source-processing prompts to local llama.cpp or OpenAI-compatible models, with completion cleanup and optional generation metrics.

#### Purpose
- Provides a pluggable AI generation boundary, provider selection, and configuration for local and remote model backends.
- Builds provider-specific requests and normalizes completion text and timing results.

#### Responsibilities
- **Provider abstraction and selection:** Define generation and lifecycle operations; resolve run-level versus model-level provider settings and dispatch to mock, llama.cpp JNI, or OpenAI-compatible implementations.
- **Local inference:** Lazily load and reuse a llama.cpp model, construct inference settings from configuration, parse completions, report timing/cache metrics, and release the model.
- **Remote request support:** Build OpenAI-compatible chat-completions JSON, form and validate endpoints, protect API keys on plain HTTP, and interpret status, errors, and timing data.
- **Configuration and completion cleanup:** Expose fluent `LlamaCppJniConfig` settings and strip a leading Gemma thinking block from model responses.

#### Key units
- `AiGenerationProvider` — generation interface with default zero-valued timing results and no-op `close`.
- `AiGenerationProviderFactory` — resolves effective provider keys and creates the corresponding backend.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp inference with lazy model loading, completion parsing, and optional metrics.
- `LlamaCppJniConfig` — immutable provider and generation settings, constructed through a fluent builder.
- `OpenAiCompatibleProviderSupport` — package-private helper for remote request bodies, endpoint/API-key validation, HTTP errors, and timings.
- `AiCompletionParser` — removes a leading Gemma thinking block while preserving marker text elsewhere.

#### Data flow
- `AiGenerationRequest` and effective provider configuration enter through the selected `AiGenerationProvider`.
- The local provider uses `AiPromptSupport` and `LlamaCppJniProviderSupport` to prepare inference, runs llama.cpp chat completion, then parses response text and exposes timing metrics when requested.
- OpenAI-compatible support combines prompt data and configured sampling/model options into a non-streaming chat-completions request; response status, error body, and usage data are converted to success/error information and `AiGenerationTimings`.
- `AiCompletionParser` trims results and strips a leading thinking block; an unterminated block raises `IOException`.

#### Dependencies
- Application types: `AiGenerationRequest`, `AiGenerationTimings`, `AiGenerationConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniProviderSupport`.
- Provider implementations: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- Local inference: llama.cpp JNI `LlamaModel`, `ModelParameters`, `ChatResponse`, `ChatResponseParser`, and `Timings`.
- Remote request support: Jackson JSON nodes and `ObjectMapper`; Java URL and HTTP-related utilities.

#### Cross-cutting
- `AiGenerationProvider` extends `AutoCloseable`; generation operations declare `IOException`, while default timing support delegates to `generate` with zero metrics.
- `LlamaCppJniConfig` fields are final, but its builder is not thread-safe; configured lists are retained by reference, despite unmodifiable accessor views.
- Local model loading is lazy and cached; `close` releases the model only if initialized.
- OpenAI-compatible support uses an injected environment lookup for API keys and permits plain HTTP API-key transmission only to recognized loopback hosts.
