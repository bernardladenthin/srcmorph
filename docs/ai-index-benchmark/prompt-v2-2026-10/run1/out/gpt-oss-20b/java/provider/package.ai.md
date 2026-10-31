### java/provider
- H: 1.0
- C: A064EE97
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:41:28Z
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
> A modular AI generation framework that supports multiple back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) and provides utilities for request construction, response parsing, and timing metrics.

#### Purpose
- Offer a pluggable AI text generation service.
- Abstract provider selection and configuration.
- Supply utilities for building chat requests and handling responses.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Factory & configuration** – `AiGenerationProviderFactory` resolves provider keys; `LlamaCppJniConfig` holds llama.cpp runtime knobs.
- **Request/response handling** – `OpenAiCompatibleProviderSupport` builds JSON bodies, validates endpoints, secures API keys, and interprets HTTP responses.
- **Completion parsing** – `AiCompletionParser` strips internal reasoning blocks from LLM outputs.

#### Key units
- `AiGenerationProvider` – interface for generating text and optional timing data.
- `AiGenerationProviderFactory` – creates providers based on run or model config.
- `LlamaCppJniConfig` – immutable record‑style config for llama.cpp JNI provider.
- `LlamaCppJniAiGenerationProvider` – llama.cpp native inference provider with lazy model loading and timing extraction.
- `OpenAiCompatibleProviderSupport` – helper for building OpenAI‑compatible chat requests and parsing responses.
- `AiCompletionParser` – utility to strip leading thinking blocks from completions.

#### Data flow
1. **Provider selection** – `AiGenerationProviderFactory.create(...)` chooses a provider based on `providerName` or model config.
2. **Request construction** – `OpenAiCompatibleProviderSupport.requestBody(...)` builds the JSON payload from an `AiGenerationRequest`.
3. **Generation** – The chosen provider’s `generate(...)` or `generateWithTimings(...)` is invoked, producing a raw completion string.
4. **Post‑processing** – `AiCompletionParser.parseCompletion(...)` removes any leading reasoning block, yielding the final answer.
5. **Metrics** – If timings are requested, the provider returns an `AiGenerationTimings` object containing token counts and throughput.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (native llama.cpp bindings), `com.fasterxml.jackson.databind` (JSON), `org.jspecify.annotations.Nullable`, Lombok (`@ToString`, `@EqualsAndHashCode`).
- **Runtime** – Environment variables for API keys, optional OpenAI‑compatible endpoint URLs.

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are immutable after construction.
- **Thread safety** – Providers are stateless or lazily initialized; factory and config are thread‑safe. `LlamaCppJniAiGenerationProvider` lacks explicit synchronization on model loading, potentially creating multiple models under concurrent use.
- **Error handling** – `IOException` is the primary failure mode for provider creation, request building, and response parsing; `IllegalArgumentException` signals misconfiguration.
- **Logging** – Providers log truncation warnings and cache reuse; timing metrics are computed from `ChatResponse` data.
