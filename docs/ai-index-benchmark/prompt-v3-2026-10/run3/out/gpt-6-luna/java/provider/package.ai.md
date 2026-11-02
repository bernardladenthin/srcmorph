### java/provider
- H: 1.0
- C: 9D08F4C5
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:38:04Z
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
> Provides configurable AI text generation through mock, local llama.cpp, and OpenAI-compatible backends, with completion cleanup and optional performance metrics.

#### Purpose
- Define and select AI generation backends for `AiGenerationRequest` inputs.
- Configure model and inference behavior, then return generated text with optional timing and token metrics.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, timing, and resource-close operations; `AiGenerationProviderFactory` selects mock, llama.cpp JNI, or OpenAI-compatible implementations.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily initializes a GGUF-backed native model, applies configured inference options, and reports prompt/cache/decode metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completions JSON, resolves endpoints and API keys, and formats HTTP errors and timing results.
- **Configuration and response cleanup:** `LlamaCppJniConfig` collects model, sampling, compute, cache, reasoning, and stop-sequence settings; `AiCompletionParser` strips a leading model-internal thinking block.

#### Key units
- `AiGenerationProvider` — pluggable generation interface with default timing wrapper and no-op `close`.
- `AiGenerationProviderFactory` — resolves model/run provider keys and constructs the selected backend, defaulting to mock when unset.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp inference and parses completion responses.
- `LlamaCppJniConfig` — immutable configuration value with a fluent builder for local and OpenAI-compatible generation options.
- `OpenAiCompatibleProviderSupport` — package-private helper for request bodies, endpoint and API-key handling, HTTP errors, and timings.
- `AiCompletionParser` — removes a leading Gemma-compatible reasoning block and trims the remaining answer.

#### Data flow
- The factory resolves provider configuration and creates an `AiGenerationProvider`; unset or blank selection yields the mock provider.
- Generation receives an `AiGenerationRequest`. The llama.cpp provider builds model parameters and inference settings from `LlamaCppJniConfig`, invokes chat completion, parses the response, and strips a leading thinking block.
- OpenAI-compatible support builds a non-streaming chat request from prompt support and configuration, and provides endpoint, key-safety, error, and timing utilities.
- `generateWithTimings` returns backend metrics where available; the interface default wraps generated text with zero timing values.

#### Dependencies
- Internal: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- External: llama.cpp JNI model and chat-response types; Jackson JSON nodes and `ObjectMapper`; Java I/O, URL, collection, and functional APIs; Lombok and JSpecify annotations.

#### Cross-cutting
- Providers declare `IOException` for generation and lifecycle failures; completion parsing also reports an unterminated leading thinking block with token-budget guidance.
- Configuration uses defaults from `AiGenerationConfig`; null string and list builder inputs restore defaults, while nullable thinking mode distinguishes unset from false.
- `LlamaCppJniConfig` fields are final, but builder-supplied lists are not copied; returned list views are unmodifiable.
- llama.cpp model initialization and cached-handle access are unsynchronized; no provider thread-safety guarantee is stated.
- OpenAI-compatible API keys are allowed over HTTPS or to recognized loopback hosts, and rejected for other cleartext endpoints.
