### java/provider
- H: 1.0
- C: 1E5D7C55
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:00:26Z
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
> Provides configurable AI text generation through local llama.cpp JNI or OpenAI-compatible backends, with provider selection, prompt handling, and optional completion timing data.

#### Purpose
- Defines and selects pluggable providers that turn `AiGenerationRequest` inputs into generated text.
- Centralizes model-generation settings and shared completion parsing and OpenAI-compatible request support.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, optional timing results, and resource closure; `AiGenerationProviderFactory` resolves model-level versus run-level provider keys and defaults to mock generation.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a llama.cpp model, applies configured inference settings, and returns completion text and available timing/cache metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completions JSON and handles endpoint validation, API-key policy, HTTP errors, and timing extraction for the compatible provider.
- **Configuration and response cleanup:** `LlamaCppJniConfig` collects generation, sampling, hardware, cache, reasoning, and stop-sequence options; `AiCompletionParser` strips a leading Gemma-4 reasoning block.

#### Key units
- `AiGenerationProvider` — backend interface with `generate`, optional `generateWithTimings`, and `close`.
- `AiGenerationProviderFactory` — maps `mock`, `llamacpp-jni`, and `openai-compatible` keys to provider implementations.
- `LlamaCppJniAiGenerationProvider` — local llama.cpp JNI inference provider with model reuse and timing extraction.
- `OpenAiCompatibleProviderSupport` — package-private helper for remote chat request formatting and response-related utilities.
- `LlamaCppJniConfig` — immutable model-generation settings with a fluent `Builder`.
- `AiCompletionParser` — removes a leading thinking block and reports an unclosed block with `IOException`.

#### Data flow
- The factory chooses an effective provider using model and run configuration; absent or blank keys select `MockAiGenerationProvider`.
- A provider consumes `AiGenerationRequest`; the local backend builds llama inference parameters, runs chat completion, and parses the response, while shared OpenAI-compatible support builds a chat-completions request.
- `generateWithTimings` returns `AiGenerationTimings`; the interface default wraps generated text with zero metrics, while the local backend and remote support derive available token and rate data.

#### Dependencies
- Project types: `AiGenerationRequest`, `AiGenerationTimings`, `AiGenerationConfig`, `AiPromptSupport`, `MockAiGenerationProvider`, and `Java8CompatibilityHelper`.
- Local inference uses the `net.ladenthin:llama` JNI binding (`LlamaModel`, `ModelParameters`, chat response and timing types) and `LlamaCppJniProviderSupport`.
- OpenAI-compatible request support uses Jackson JSON nodes and configured environment-variable lookup for API keys.
- Provider factory references `OpenAiCompatibleAiGenerationProvider`; its implementation is not summarized in the supplied files.

#### Cross-cutting
- Generation methods and provider closure use `IOException` in the common interface; unsupported provider keys raise `IllegalArgumentException`.
- `LlamaCppJniConfig` is immutable after construction; its builder is intended for single-threaded use.
- The local provider caches a mutable model reference without synchronization, so concurrent initialization or closing is not established as thread-safe.
- OpenAI-compatible support restricts API-key transmission over plain HTTP to recognized loopback hosts and truncates long HTTP error bodies.
