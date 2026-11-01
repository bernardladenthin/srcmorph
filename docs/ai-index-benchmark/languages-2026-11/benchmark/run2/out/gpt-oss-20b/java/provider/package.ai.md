### java/provider
- H: 1.0
- C: 43C2C5D3
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:42:49Z
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
> A modular AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust request/response handling.

#### Purpose
- Enable pluggable AI text generation back‑ends.
- Provide consistent request/response abstraction and timing instrumentation.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Factory & configuration** – `AiGenerationProviderFactory` resolves provider keys; `LlamaCppJniConfig` holds immutable llama.cpp settings.
- **Request parsing** – `AiCompletionParser` strips internal reasoning blocks from raw completions.
- **OpenAI‑compatible support** – `OpenAiCompatibleProviderSupport` builds JSON bodies, resolves endpoints, secures API keys, and extracts timing data.
- **Prompt handling** – `AiPromptSupport` (not shown) supplies prompt templates used by providers.

#### Key units
- `AiGenerationProvider` – interface for generating text and optional timings.
- `AiGenerationProviderFactory` – creates provider instances based on run‑ or model‑level keys.
- `LlamaCppJniConfig` – immutable configuration for llama.cpp JNI provider.
- `LlamaCppJniAiGenerationProvider` – local inference provider using `LlamaModel`.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible chat‑completion calls.
- `AiCompletionParser` – cleans raw LLM completions by removing leading thinking blocks.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file/text, header) is built by the caller.
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the key; `create` returns the appropriate provider.
3. **Generation** – Provider’s `generate` or `generateWithTimings` is invoked:
   - *Local*: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, builds inference parameters, calls `chatComplete`, parses JSON, extracts text and timing.
   - *Remote*: `OpenAiCompatibleProviderSupport` builds request body, resolves endpoint, secures API key, sends HTTP request, parses response, extracts timings.
4. **Post‑processing** – `AiCompletionParser.parseCompletion` removes any leading thinking block from the raw completion before persisting or returning the answer.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External** – `net.ladenthin.llama` (JNI model, parameters, JSON parser), `com.fasterxml.jackson` (JSON serialization), `java.net`, `java.io`, `java.util`, `lombok`, `org.jspecify`.
- **Configuration** – `AiGenerationConfig` defaults used by `LlamaCppJniConfig.Builder`.

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable, thread‑safe.
- **Error handling** – `IOException` for missing end markers, invalid URLs, or API key issues; `IllegalArgumentException` for unknown provider keys.
- **Timing instrumentation** – `AiGenerationTimings` captures prompt/cache tokens, TPS, predicted tokens/TPS for both local and remote providers.
- **Logging & warnings** – providers log truncation or cache reuse; `AiCompletionParser` warns on malformed thinking blocks.
