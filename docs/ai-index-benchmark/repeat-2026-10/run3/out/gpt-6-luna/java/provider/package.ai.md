### java/provider
- H: 1.0
- C: 7F4A1655
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:23:30Z
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
> Generates and cleans AI-produced source-editing text through selectable local or OpenAI-compatible model backends, with configurable inference and request safety controls.

#### Purpose
- Defines a common generation contract and selects mock, llama.cpp JNI, or OpenAI-compatible backends.
- Centralizes model settings, completion cleanup, and supporting request, endpoint, and timing calculations.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` standardizes generation, optional timing results, and resource closure; `AiGenerationProviderFactory` applies model-over-run provider precedence and defaults to mock generation.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and reuses a llama.cpp model, applies configured inference options, and returns completion text and available metrics.
- **Model configuration:** `LlamaCppJniConfig` and its builder hold sampling, token, threading, hardware, cache, reasoning, DRY, stop-string, and provider/API settings.
- **Completion cleanup:** `AiCompletionParser` strips a leading Gemma-compatible thinking block and returns trimmed answer text.
- **OpenAI-compatible support:** `OpenAiCompatibleProviderSupport` builds chat-completion JSON, resolves endpoints and API keys, enforces cleartext-key safety, formats HTTP errors, and calculates timing results.

#### Key units
- `AiGenerationProvider` — backend interface for text generation, generation timings, and closure.
- `AiGenerationProviderFactory` — resolves provider keys and constructs mock, llama.cpp JNI, or OpenAI-compatible implementations.
- `LlamaCppJniAiGenerationProvider` — performs local model inference and exposes prompt-cache and generation metrics.
- `LlamaCppJniConfig` — immutable configuration for local inference and OpenAI-compatible request settings.
- `AiCompletionParser` — removes a leading reasoning block from completion text.
- `OpenAiCompatibleProviderSupport` — prepares and validates OpenAI-compatible request and response-related values.

#### Data flow
- Run-level and model-level provider settings are resolved by `AiGenerationProviderFactory`; the selected provider receives `AiGenerationRequest` and configuration.
- The llama.cpp provider builds model and inference parameters, calls `chatComplete`, and parses completion content through `AiCompletionParser`; its timing path also returns available token counts and rates.
- OpenAI-compatible support combines request data, prompt definitions, and configured sampling values into JSON, while deriving endpoint and API-key values and calculating response error text or timings.

#### Dependencies
- Shared generation types: `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Configuration defaults from `AiGenerationConfig`; compatibility support from `Java8CompatibilityHelper`.
- Local inference uses the `net.ladenthin:llama` JNI model, response, timing, and parameter types.
- OpenAI-compatible request construction uses Jackson; endpoint checks use Java URL and function utilities.
- Provider selection references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`; their implementation details are not covered by the provided summaries.

#### Cross-cutting
- Provider operations declare `IOException` where specified; the provider contract’s default timing method wraps generated text with zero timings.
- `LlamaCppJniConfig` is immutable after building; its builder is not thread-safe, and provider model initialization and closure are unsynchronized.
- OpenAI-compatible support permits API keys over HTTPS or plain HTTP only for recognized loopback hosts; errors include at most 500 response-body characters.
- Completion parsing maps null input to an empty string and throws `IOException` for an unterminated leading thinking block.
