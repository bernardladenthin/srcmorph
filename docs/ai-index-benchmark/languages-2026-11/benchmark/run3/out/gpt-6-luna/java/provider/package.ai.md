### java/provider
- H: 1.0
- C: 038F6632
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:00:58Z
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
> Provides configurable AI text generation for source-analysis workflows, including local llama.cpp inference and OpenAI-compatible chat completions.

#### Purpose
- Defines provider selection and a common generation contract for AI completion requests.
- Supplies llama.cpp JNI configuration, completion cleanup, and support for preparing OpenAI-compatible requests.

#### Responsibilities
- **Provider abstraction and selection:** `AiGenerationProvider` exposes generation, optional timing results, and resource cleanup; `AiGenerationProviderFactory` resolves run-level versus model-level provider settings and selects mock, llama.cpp JNI, or OpenAI-compatible backends.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily initializes a native GGUF model, generates completions, reports inference and cache metrics, and closes the model.
- **Provider configuration:** `LlamaCppJniConfig` captures model, sampling, hardware, cache, reasoning, remote-provider, and stop-sequence settings through a builder.
- **Completion and request support:** `AiCompletionParser` strips a leading Gemma-style thinking block; `OpenAiCompatibleProviderSupport` builds chat-completion JSON and handles endpoint, API-key, error, and timing calculations.

#### Key units
- `AiGenerationProvider` — shared `generate` and `generateWithTimings` contract with default zero-valued timings and no-op `close`.
- `AiGenerationProviderFactory` — provider-key precedence and construction for `mock`, `llamacpp-jni`, and `openai-compatible`.
- `LlamaCppJniAiGenerationProvider` — llama.cpp JNI generation, response parsing, timing extraction, and native model lifecycle.
- `LlamaCppJniConfig` — immutable, builder-created model and generation settings.
- `AiCompletionParser` — removes a leading thinking block and errors if its closing marker is missing.
- `OpenAiCompatibleProviderSupport` — assembles request bodies and validates endpoints and credential delivery without performing network I/O.

#### Data flow
- Run-level and model-level provider keys pass through `AiGenerationProviderFactory`; a nonblank model key takes precedence, and an absent key defaults to mock generation.
- An `AiGenerationRequest` is passed to the selected provider. The JNI provider builds inference parameters, calls `chatComplete`, parses response text, and can return native timing and cache metrics.
- `OpenAiCompatibleProviderSupport` combines request data, prompt support, and configured sampling options into chat-completion JSON; it also normalizes the endpoint and prepares status, error, and timing results.
- Completion text is cleaned by `AiCompletionParser` when a leading thinking block is present.

#### Dependencies
- Project types: `AiGenerationRequest`, `AiGenerationTimings`, `AiGenerationConfig`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, `Java8CompatibilityHelper`.
- Local inference: `net.ladenthin:llama` JNI model, chat-response, timing, and parameter types.
- OpenAI-compatible request support: Jackson JSON nodes and `ObjectMapper`; environment lookup is supplied as a function.
- Lombok, JSpecify, and `ConvertToRecord` support diagnostics, nullability, and configuration conventions.

#### Cross-cutting
- Provider failures use `IOException` where declared; unsupported provider keys and invalid endpoint schemes use `IllegalArgumentException`.
- `AiGenerationTimings` provides a common result shape; default provider timings are zero, and the JNI provider substitutes zero metrics when native timing data is absent.
- `LlamaCppJniConfig` is immutable after construction; its builder is not thread-safe. JNI model initialization and cached model access are unsynchronized.
- API-key handling trims configured credentials and prevents sending them over plain HTTP except to recognized loopback hosts.
