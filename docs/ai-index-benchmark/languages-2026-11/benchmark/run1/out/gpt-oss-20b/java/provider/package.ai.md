### java/provider
- H: 1.0
- C: 4CF08994
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:21:01Z
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
> A modular AI generation framework that supplies pluggable back‑ends (local llama.cpp, OpenAI‑compatible, or mock) for source‑code transformation tasks, handling prompt construction, response parsing, and timing metrics.

#### Purpose
- Provide a unified interface for generating AI text in source‑morph workflows.
- Support local inference via llama.cpp JNI and remote OpenAI‑compatible APIs.
- Offer timing and token‑usage metrics for calibration.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface defines generation contracts.
- **Factory logic** – `AiGenerationProviderFactory` resolves provider keys and instantiates concrete providers.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model, runs chat completions, parses responses, and reports metrics.
- **Remote compatibility** – `OpenAiCompatibleProviderSupport` builds request bodies, resolves endpoints, validates API keys, and interprets responses.
- **Configuration** – `LlamaCppJniConfig` holds immutable llama.cpp settings with a fluent builder.
- **Response cleanup** – `AiCompletionParser` strips internal thinking blocks from Gemma‑4 completions.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `create`, `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` – local inference, lazy `LlamaModel`, `generateWithTimings`.
- `OpenAiCompatibleProviderSupport` – request body, endpoint, API key, error handling, `timings`.
- `LlamaCppJniConfig` – immutable config record with builder.
- `AiCompletionParser` – `parseCompletion`.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (source file, prompt key) is passed to a provider.
2. **Prompt resolution** – `AiPromptSupport` supplies the prompt template.
3. **Provider selection** – `AiGenerationProviderFactory` chooses the concrete provider.
4. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls `model.chatComplete`, parses JSON, extracts text and timing.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds a JSON body, resolves endpoint, sends HTTP request, parses `ChatResponse`, extracts text and usage.
5. **Post‑processing** – `AiCompletionParser` removes any internal thinking block.
6. **Metrics** – `AiGenerationTimings` returned with token counts and rates.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (model, args, JSON parser), `com.fasterxml.jackson.databind` (ObjectMapper), `org.jspecify.annotations.Nullable`, `lombok` annotations.
- **Runtime** – llama.cpp native library for local provider; HTTP client for remote provider.

#### Cross‑cutting
- Immutable configuration (`LlamaCppJniConfig`) ensures thread‑safe provider construction.
- `AiCompletionParser` is stateless and reusable across providers.
- Error handling follows a consistent pattern: throw `IOException` for missing markers or HTTP failures, `IllegalArgumentException` for unknown provider keys.
- Timing metrics are captured uniformly via `AiGenerationTimings`, regardless of provider type.
- API key validation enforces loopback restrictions for plain HTTP endpoints.
