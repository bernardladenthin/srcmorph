### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:31:16Z
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
> Generates source-aware AI completions through selectable mock, local llama.cpp, or OpenAI-compatible backends, with completion cleanup and optional performance metrics.

#### Purpose
- Defines and configures AI text-generation providers for requests built from prompt keys, source files, source text, and current headers.
- Supports local-model inference and OpenAI-compatible request preparation, plus completion parsing and generation timing data.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProviderFactory` resolves model-level overrides and creates mock, llama.cpp JNI, or OpenAI-compatible providers; `AiGenerationProvider` defines generation, optional timings, and lifecycle closure.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a configured GGUF model, runs chat completion, parses the answer, and reports available token and throughput metrics.
- **Provider configuration:** `LlamaCppJniConfig` immutably collects model, sampling, hardware, caching, reasoning, provider-connection, DRY, and stop-string settings.
- **Completion cleanup:** `AiCompletionParser` strips a leading Gemma-4 thinking block and returns trimmed answer text.
- **OpenAI-compatible request support:** `OpenAiCompatibleProviderSupport` builds chat-completion request JSON, normalizes endpoints, applies API-key transport safeguards, formats HTTP errors, and derives timing data.

#### Key units
- `AiGenerationProvider` — pluggable `AutoCloseable` generation interface with a default zero-timing implementation.
- `AiGenerationProviderFactory` — selects a provider using run configuration and an optional model-level provider override.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp JNI chat inference and parses generated completions.
- `LlamaCppJniConfig` — immutable configuration with a fluent builder for inference and provider settings.
- `AiCompletionParser` — removes a leading reasoning block, throwing `IOException` if it is unterminated.
- `OpenAiCompatibleProviderSupport` — package-private helper for OpenAI-compatible request data, API-key policy, errors, and timings.

#### Data flow
- `AiGenerationProviderFactory` resolves the effective provider; null or blank selection defaults to `MockAiGenerationProvider`, while `llamacpp-jni` selects `LlamaCppJniAiGenerationProvider` and `openai-compatible` selects the OpenAI-compatible provider.
- The local provider combines `AiGenerationRequest`, `LlamaCppJniConfig`, and `AiPromptSupport` to build inference parameters, invokes `chatComplete`, parses the response, and returns completion text or timing data.
- OpenAI-compatible support combines request and configuration data into non-streaming chat-completions JSON; it also prepares endpoint and API-key information and maps response metadata into `AiGenerationTimings`.
- `AiCompletionParser` trims generated text and removes a leading Gemma-4 thinking block before the answer is returned.

#### Dependencies
- Request and result types: `AiGenerationRequest`, `AiGenerationTimings`; prompt construction: `AiPromptSupport`.
- Local inference: `LlamaCppJniProviderSupport`, llama.cpp `LlamaModel` and parameter types, `ChatResponseParser`, and response timing data.
- OpenAI-compatible support: Jackson JSON nodes, `ChatResponse` and `Usage`, and an injected environment-variable lookup.
- Configuration defaults and compatibility checks use `AiGenerationConfig` and `Java8CompatibilityHelper`.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable after construction; its builder is intended for single-threaded use, and numeric values are not validated.
- The llama.cpp model is initialized lazily and cached without synchronization; concurrent first calls may race.
- Provider generation methods declare `IOException` in the interface; the completion parser also throws `IOException` for an unterminated leading thinking block.
- OpenAI-compatible support accepts only HTTP(S) base URLs and prevents configured API keys from being sent over plain HTTP to non-loopback hosts.
