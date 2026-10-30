### java/provider
- H: 1.0
- C: C381D24F
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:37:13Z
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
> Generates AI text for application requests through selectable local or OpenAI-compatible backends, with configurable inference and completion cleanup.

#### Purpose
- Provides a pluggable text-generation contract and selects a backend using run-level and model-level provider settings.
- Configures generation behavior and supports local llama.cpp inference and OpenAI-compatible request handling.

#### Responsibilities
- **Provider selection and contract:** `AiGenerationProviderFactory` resolves provider overrides and creates mock, llama.cpp JNI, or OpenAI-compatible providers; `AiGenerationProvider` defines generation and optional timing APIs.
- **Local inference:** `LlamaCppJniAiGenerationProvider` loads and reuses a llama.cpp model, applies inference settings, and returns completions and available timings.
- **Configuration:** `LlamaCppJniConfig` and its builder hold model, sampling, hardware, cache, reasoning, provider, and stop-string settings.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completion requests and handles endpoint validation, API keys, HTTP errors, and timing estimates.
- **Completion cleanup:** `AiCompletionParser` removes a leading Gemma-4 reasoning block and trims the answer.

#### Key units
- `AiGenerationProvider` — backend interface for text generation, optional timings, and closing.
- `AiGenerationProviderFactory` — resolves the effective provider and instantiates a supported backend.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp chat completion using configured model and request parameters.
- `LlamaCppJniConfig` — immutable provider and inference settings with a fluent builder.
- `OpenAiCompatibleProviderSupport` — constructs and validates OpenAI-compatible chat-completion data.
- `AiCompletionParser` — strips a leading reasoning block from completion text.

#### Data flow
- The factory resolves the provider from run and model settings; blank selection defaults to the mock provider.
- A provider receives `AiGenerationRequest`; the local backend builds inference parameters, runs chat completion, and parses the response.
- OpenAI-compatible support builds request JSON from prompt definitions and configuration, and derives endpoint, key, error, and timing data.
- Generation results may include `AiGenerationTimings`; the default interface implementation reports zero metrics.

#### Dependencies
- Internal types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `Java8CompatibilityHelper`, and `LlamaCppJniProviderSupport`.
- Local inference: llama.cpp JNI model, parameter, response, and timing types.
- Remote request support: Jackson JSON types and OpenAI-compatible chat response and usage types.
- Mock, llama.cpp JNI, and OpenAI-compatible provider implementations are selected by the factory.

#### Cross-cutting
- Generation methods declare `IOException`; the completion parser also throws it for an unterminated leading reasoning block.
- The provider contract is `AutoCloseable`; the llama.cpp provider closes its loaded model and lazily caches it, with unsynchronized initialization.
- Configuration fields are final; the builder is not thread-safe. API keys may be sent over HTTPS or to recognized loopback hosts over HTTP.
