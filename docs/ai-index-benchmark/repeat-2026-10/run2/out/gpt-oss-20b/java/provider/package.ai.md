### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:16:08Z
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
> Provides a pluggable AI generation layer that can invoke local llama.cpp models or OpenAI‑compatible APIs, parse their responses, and expose timing metrics for source‑morph transformations.

#### Purpose
- Supply AI‑generated text for source‑morph requests.
- Abstract over local and remote providers, exposing a uniform `AiGenerationProvider` API.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and concrete implementations (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Factory logic** – `AiGenerationProviderFactory` resolves provider keys from run or model config.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via llama.cpp JNI, builds inference parameters, executes chat completions, and extracts timings.
- **Remote API support** – `OpenAiCompatibleProviderSupport` constructs JSON bodies, resolves endpoints, secures API keys, and parses OpenAI‑compatible responses.
- **Configuration** – `LlamaCppJniConfig` holds immutable runtime options for the llama.cpp provider.
- **Response parsing** – `AiCompletionParser` strips internal “thinking” blocks from raw completions.

#### Key units
- `AiGenerationProvider` – interface for generating text and optional timing data.
- `AiGenerationProviderFactory` – creates provider instances based on keys.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference provider.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible chat‑completion APIs.
- `LlamaCppJniConfig` – immutable config record for llama.cpp settings.
- `AiCompletionParser` – strips reasoning blocks from LLM output.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file, text, header) is built by the caller.
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a key; `create` returns the matching provider.
3. **Generation** – `generate` or `generateWithTimings` is invoked on the provider.
   - *Local*: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, builds `ModelParameters`, runs `chatComplete`, parses `ChatResponse`, and returns text or `AiGenerationTimings`.
   - *Remote*: `OpenAiCompatibleProviderSupport` builds a JSON body, resolves the endpoint, secures the API key, sends the request, parses the response, and computes timings.
4. **Post‑processing** – `AiCompletionParser.parseCompletion` removes any internal thinking blocks before the result is returned to the caller.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External**: `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON construction), `java.net.URL`, `java.io.IOException`.
- **Utilities**: `Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, Lombok annotations.

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` is a record‑style immutable holder; provider factories are stateless.
- **Error handling** – `IOException` propagates from provider failures; `IllegalArgumentException` signals unknown provider keys.
- **Thread safety** – Providers are generally stateless after construction; local provider reuses a single `LlamaModel` instance, implying single‑threaded usage unless externally synchronized.
- **Timing metrics** – `AiGenerationTimings` captures generated text, token counts, and throughput; local provider parses `Timings` from the llama.cpp JSON, remote provider derives usage from OpenAI’s `usage` field.
