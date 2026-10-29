### java/provider
- H: 1.0
- C: 6BB2B0BD
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:22:31Z
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
> Generates AI text through interchangeable mock, local llama.cpp, and OpenAI-compatible backends, with configurable model settings and completion cleanup.

#### Purpose
- Define and select AI generation backends for `AiGenerationRequest` inputs.
- Configure local model inference and OpenAI-compatible request support, with optional generation timing data.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines text generation, optional timing results, and resource closure; `AiGenerationProviderFactory` resolves provider precedence and instantiates mock, llama.cpp JNI, or OpenAI-compatible backends.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a GGUF model, applies inference configuration, and returns parsed completions and available model timings.
- **Configuration:** `LlamaCppJniConfig` and its builder hold immutable model, sampling, hardware, cache, provider, reasoning, and stop-sequence options, using defaults from `AiGenerationConfig`.
- **Completion cleanup:** `AiCompletionParser` strips a leading Gemma-style reasoning block and trims the remaining answer.
- **OpenAI-compatible request support:** `OpenAiCompatibleProviderSupport` builds chat-completion JSON and handles endpoint, API-key safety, HTTP status/error formatting, and response timing conversion.

#### Key units
- `AiGenerationProvider` — backend contract for generating text, reporting optional timings, and closing resources.
- `AiGenerationProviderFactory` — applies model-over-run provider precedence and creates the selected provider.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp chat completion with cached model initialization.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — store and construct immutable generation and provider settings.
- `OpenAiCompatibleProviderSupport` — prepares OpenAI-compatible requests and interprets endpoint, error, and timing data.
- `AiCompletionParser` — removes a leading reasoning block from completion text.
- `AiGenerationTimings` — carries generated text and timing/count data returned by provider operations.

#### Data flow
- `AiGenerationRequest` is passed to the selected `AiGenerationProvider`; the factory chooses a backend from run-level and model-level provider keys, defaulting to mock for a null or blank key.
- The llama.cpp provider uses `AiPromptSupport` and `LlamaCppJniConfig` to build inference, submits it to the loaded model, and parses completion text; timing-aware generation also maps available model token and throughput data.
- OpenAI-compatible support assembles a JSON chat request from prompt content and config, resolves the endpoint and API key, and converts response usage into `AiGenerationTimings`.
- `AiCompletionParser` trims output and removes a leading reasoning block; an unclosed leading block raises `IOException`.

#### Dependencies
- Internal: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, and the OpenAI-compatible provider selected by the factory.
- External: llama.cpp JNI model and chat-response types; Jackson JSON tree/serialization APIs; Java I/O and utility APIs; Lombok and JSpecify annotations.
- The factory uses `Java8CompatibilityHelper`; configuration defaults are supplied by `AiGenerationConfig`.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable after construction; its builder is intended for single-threaded use, and list accessors expose unmodifiable views.
- The llama.cpp provider caches a lazily initialized model without synchronization; concurrent first calls may initialize multiple models.
- Provider generation methods declare `IOException` where applicable; the default timing method delegates to `generate` and supplies zero timings.
- OpenAI-compatible support rejects invalid URL schemes and disallows sending configured API keys over cleartext HTTP to non-loopback hosts.
