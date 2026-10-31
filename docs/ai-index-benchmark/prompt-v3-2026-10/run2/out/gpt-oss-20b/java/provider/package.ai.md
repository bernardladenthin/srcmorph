### java/provider
- H: 1.0
- C: CDF7F2E3
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:21:12Z
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
> A lightweight, pluggable AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompt handling, token‑budget control, and timing metrics.

#### Purpose
- Provide a unified interface for generating text from various AI backends.
- Offer runtime configuration for local and remote models, including sampling, caching, and resource limits.
- Parse and clean LLM responses, expose generation timings, and support prompt templates.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`).
- **Factory & resolution** – `AiGenerationProviderFactory` selects the appropriate provider based on run‑ or model‑level keys.
- **Configuration** – `LlamaCppJniConfig` holds all knobs for llama.cpp and remote‑model settings.
- **Prompt & request building** – `AiPromptSupport` (not shown) supplies prompt templates; `OpenAiCompatibleProviderSupport` constructs JSON bodies and validates endpoints.
- **Response handling** – `AiCompletionParser` strips internal reasoning blocks; `ChatResponseParser` (via llama.cpp) parses raw chat JSON.
- **Timing & metrics** – `AiGenerationTimings` captures token counts and decode rates; providers expose timing data.

#### Key units
- `AiGenerationProvider` – interface for generating text and optional timings.
- `AiGenerationProviderFactory` – resolves provider key and instantiates the correct provider.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp and remote‑model options.
- `LlamaCppJniAiGenerationProvider` – local inference provider using llama.cpp JNI.
- `OpenAiCompatibleProviderSupport` – builds and validates OpenAI‑compatible chat requests.
- `AiCompletionParser` – removes internal “thinking” blocks from raw completions.
- `AiGenerationTimings` – DTO for text, prompt tokens, cache tokens, and decode rate.

#### Data flow
1. **Request creation** – caller builds an `AiGenerationRequest` (source file + prompt key).
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses the key; `create` returns a provider instance.
3. **Prompt assembly** – provider uses `AiPromptSupport` to fetch the prompt template and injects request data.
4. **Chat request** – `OpenAiCompatibleProviderSupport` (remote) or `LlamaCppJniAiGenerationProvider` (local) builds the JSON body and endpoint.
5. **Inference** – local provider calls `LlamaModel.chatComplete`; remote provider sends HTTP POST.
6. **Response parsing** – `ChatResponseParser` extracts the raw chat JSON; `AiCompletionParser` trims reasoning blocks.
7. **Timing extraction** – provider pulls token counts and decode rate into `AiGenerationTimings`.
8. **Output** – caller receives the cleaned text (and optionally timings).

#### Dependencies
- **Internal** – `AiPromptSupport`, `Java8CompatibilityHelper`, `AiCompletionParser`, `ChatResponseParser`, `LlamaCppJniConfig`.
- **External** – `net.ladenthin.llama` (JNI bindings, JSON parsing), `com.fasterxml.jackson.databind` (JSON serialization), `org.jspecify.annotations.Nullable`, `lombok` annotations.
- **Remote** – OpenAI‑compatible HTTP endpoints, environment variables for API keys.

#### Cross-cutting
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable, ensuring thread safety.
- **Error handling** – `IOException` is the primary checked exception for malformed responses, missing keys, or unsupported providers.
- **Timing metrics** – All providers expose `generateWithTimings` to capture prompt/cache token counts and decode rates.
- **Configuration precedence** – `AiGenerationProviderFactory.effectiveProvider` prefers model‑level keys over run‑level keys.
- **Resource cleanup** – `LlamaCppJniAiGenerationProvider` implements `AutoCloseable` to release native `LlamaModel` handles.
