### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:27:03Z
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
> Provides a pluggable AI generation layer that supports local llama.cpp models, OpenAI‑compatible APIs, and mock providers, exposing a unified request/response interface for source‑code transformation tasks.

#### Purpose
- Central AI generation engine for source‑code transformation.
- Abstracts provider selection, configuration, and timing.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Factory & configuration** – `AiGenerationProviderFactory` resolves provider keys and builds `LlamaCppJniConfig` via a fluent builder.
- **Parsing & post‑processing** – `AiCompletionParser` strips Gemma‑4 thinking blocks; `OpenAiCompatibleProviderSupport` builds request bodies, validates endpoints, and extracts timing metrics.
- **Model handling** – `LlamaCppJniAiGenerationProvider` lazily loads and caches a `LlamaModel`, performs chat completions, and parses JSON responses.

#### Key units
- **`AiGenerationProvider`** – interface for generating text and optional timings.
- **`AiGenerationProviderFactory`** – creates providers based on run/model keys.
- **`LlamaCppJniConfig`** – immutable config holder with 37 knobs for llama.cpp.
- **`LlamaCppJniAiGenerationProvider`** – local llama.cpp provider with lazy model loading, chat completion, and timing extraction.
- **`OpenAiCompatibleProviderSupport`** – helper for building OpenAI‑compatible requests, endpoint validation, API key handling, and error/timing utilities.
- **`AiCompletionParser`** – removes Gemma‑4 thinking blocks from raw completions.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file/text, header) is passed to a provider.
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the key; `create` returns the appropriate provider.
3. **Generation** – Provider’s `generate` (or `generateWithTimings`) is invoked:
   - For **llama.cpp**: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls native `chatComplete`, parses JSON via `ChatResponseParser`, and extracts text via `AiCompletionParser`.
   - For **OpenAI‑compatible**: `OpenAiCompatibleProviderSupport` builds the request body, validates the endpoint, retrieves the API key, sends the request, parses the response, and computes timings.
4. **Output** – A plain text answer (or `AiGenerationTimings` with metrics) is returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`, `org.jspecify.annotations.Nullable`.
- **Configuration** – `AiGenerationConfig.DEFAULT_*` constants used by `LlamaCppJniConfig.Builder`.

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` is immutable; builders are single‑threaded.
- **Error handling** – `IOException` for missing markers, endpoint validation, and API key issues; `IllegalArgumentException` for unknown provider keys.
- **Timing** – `AiGenerationTimings` captures prompt/cache token counts and throughput; defaulted to zero when unavailable.
- **Thread safety** – Providers are stateless except for the optional native model cache; `LlamaCppJniAiGenerationProvider.model()` is not thread‑safe on first call but subsequent calls reuse the cached instance. All helper classes are immutable and thread‑safe.
