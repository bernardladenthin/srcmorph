### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:59:47Z
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
> Selects and configures AI text-generation backends, producing cleaned completions and optional inference timings for source-file requests.

#### Purpose
- Defines the provider contract and factory for mock, local llama.cpp JNI, and OpenAI-compatible generation.
- Supplies model-generation configuration, completion cleanup, and support for constructing and interpreting OpenAI-compatible chat requests.

#### Responsibilities
- **Provider selection and lifecycle:** Resolve model-specific versus run-level provider choice, instantiate a backend, and define generation, timing, and close behavior.
- **Local inference:** Lazily load and reuse a llama.cpp GGUF model, apply configured inference options, parse completions, and report model timings.
- **Configuration:** Build immutable per-model settings for sampling, compute, KV cache, chat reasoning, API endpoints, and stop sequences.
- **Completion cleanup:** Remove a leading Gemma thinking block while preserving other completion text.
- **OpenAI-compatible request support:** Build chat-completion JSON, validate endpoint and API-key transport, and derive error messages and generation timings; this support does not itself perform network requests.

#### Key units
- `AiGenerationProvider` — backend interface for text generation, optional timing results, and resource closure.
- `AiGenerationProviderFactory` — selects `mock`, `llamacpp-jni`, or `openai-compatible`, defaulting to mock for a null or blank provider key.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp inference and returns parsed completion text or completion timings.
- `LlamaCppJniConfig` — immutable configuration value with a builder for model, inference, provider, and stop-sequence settings.
- `AiCompletionParser` — strips an initial Gemma thinking block and errors if its closing marker is absent.
- `OpenAiCompatibleProviderSupport` — package-private helper for request bodies, endpoint and API-key checks, HTTP error descriptions, and timings.

#### Data flow
- The factory resolves the effective provider key and creates the selected `AiGenerationProvider`, passing configuration and prompt support to the llama.cpp JNI and OpenAI-compatible providers.
- A provider receives an `AiGenerationRequest` and produces text; `generateWithTimings` may additionally return timing data, with the interface defaulting to zero timings.
- The llama.cpp provider lazily initializes its model, builds inference parameters, parses the chat response, and passes completion content through `AiCompletionParser`; timing generation uses response counts and throughput when available.
- OpenAI-compatible support turns a request and configuration into serialized chat-completion JSON and can translate response usage and elapsed time into `AiGenerationTimings`.

#### Dependencies
- Shared request, prompt, and result types: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`.
- Local inference: `LlamaCppJniProviderSupport`, llama JNI model and parameter types, and chat-response parsing types.
- OpenAI-compatible request support: Jackson JSON nodes, chat response usage, and Java URL parsing.
- Provider selection also depends on `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, and `Java8CompatibilityHelper`.

#### Cross-cutting
- `AiGenerationProvider` extends `AutoCloseable`; its default timing implementation wraps generated text with zero timing values, and its default `close()` does nothing.
- `LlamaCppJniConfig` is immutable after building; its builder is not thread-safe. The llama.cpp provider caches its native model, but lazy initialization and model use are not synchronized.
- OpenAI-compatible support injects environment-variable lookup for API keys; it rejects sending configured keys over plain HTTP except to loopback hosts.
- Provider failures use `IOException` where declared; an unknown factory provider key produces `IllegalArgumentException`.
