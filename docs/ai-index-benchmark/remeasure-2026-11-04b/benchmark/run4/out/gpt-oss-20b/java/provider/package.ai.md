### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T12:50:13Z
- T: 2026-11-04T12:52:08Z
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
> A lightweight, configurable AI‑generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with robust request parsing, timing, and error handling.

#### Purpose
- Provide a unified interface for generating text from various AI backends.
- Offer a pluggable factory to select providers by key or configuration.
- Enable local inference via llama.cpp JNI with fine‑grained tuning.
- Support OpenAI‑compatible chat completions with secure API key handling.
- Strip internal reasoning blocks from Gemma‑4 responses.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` defines generation and timing contracts.
- **Factory & selection** – `AiGenerationProviderFactory` resolves provider names and creates instances.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model, runs chat completions, parses responses, and reports metrics.
- **Configuration** – `LlamaCppJniConfig` holds all runtime knobs for llama.cpp and exposes a fluent builder.
- **OpenAI compatibility** – `OpenAiCompatibleProviderSupport` builds request bodies, validates endpoints, retrieves API keys, and formats errors/timings.
- **Response parsing** – `AiCompletionParser` removes Gemma‑4 thinking blocks from raw completions.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `effectiveProvider`, `create`.
- `LlamaCppJniAiGenerationProvider` – lazy model loading, `generate`, `generateWithTimings`, `close`.
- `LlamaCppJniConfig` – immutable config with 37 parameters, `Builder`.
- `OpenAiCompatibleProviderSupport` – `requestBody`, `endpoint`, `apiKey`, `apiKeyFor`, `timings`.
- `AiCompletionParser` – `parseCompletion`.

#### Data flow
1. **Request creation** – `AiPromptSupport` supplies system/user messages; `OpenAiCompatibleProviderSupport.requestBody` builds JSON.
2. **Endpoint resolution** – `OpenAiCompatibleProviderSupport.endpoint` validates and appends the chat‑completion path.
3. **API key handling** – `apiKey`/`apiKeyFor` fetch credentials from env or config.
4. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a key; `create` instantiates the appropriate provider.
5. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider.generate` calls `model.chatComplete`, parses the `ChatResponse`, strips thinking blocks, logs cache reuse, and returns text.  
   - *OpenAI*: provider (not shown) would use the request body, endpoint, and key to call the API, then parse the response similarly.
6. **Timing** – `generateWithTimings` wraps generation, extracting token counts and rates from `ChatResponse` or defaults.
7. **Cleanup** – `close` releases native `LlamaModel` resources.

#### Dependencies
- **Internal** – `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson.databind` (JSON), `lombok` (annotations), `org.jspecify` (nullable), `java.io`, `java.util`, `java.net`.
- **Configuration** – `AiGenerationConfig` supplies defaults for `LlamaCppJniConfig`.

#### Cross-cutting
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable; builders are single‑threaded.
- **Error handling** – `AiCompletionParser` throws `IOException` on truncated blocks; `OpenAiCompatibleProviderSupport` throws `IllegalArgumentException` for bad URLs and `IOException` for key/host issues.
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache tokens, TPS, and decode rates across providers.
- **Logging** – Providers log cache reuse and truncated answers (via `OpenAiCompatibleProviderSupport` helpers).
- **Thread safety** – Providers are stateless except for the lazy `LlamaModel`; callers must guard first‑use initialization.  
- **Configuration knobs** – `LlamaCppJniConfig` exposes 37 tunable parameters (threads, GPU layers, cache types, DRY settings, etc.) for fine‑grained inference control.
