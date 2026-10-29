### java/provider
- H: 1.0
- C: 7F4A1655
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:55:16Z
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
> Provides pluggable AI back‑ends for generating source‑code transformation text, supporting both local llama.cpp inference and OpenAI‑compatible APIs, with configurable prompts, timing metrics, and completion parsing.

#### Purpose
- Deliver AI‑generated text for source‑code transformation requests.
- Offer configurable, pluggable providers (local or remote).
- Capture and expose generation timing metrics.

#### Responsibilities
- **Provider selection & instantiation** – `AiGenerationProviderFactory` chooses the appropriate provider based on run‑ or model‑level keys.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via llama.cpp JNI, runs chat completions, and parses responses.
- **Remote inference** – `OpenAiCompatibleProviderSupport` builds JSON requests, resolves endpoints, secures API keys, and computes timings from OpenAI‑compatible responses.
- **Completion parsing** – `AiCompletionParser` removes internal thinking blocks from raw completions.
- **Configuration** – `LlamaCppJniConfig` holds immutable llama.cpp runtime options via a fluent builder.

#### Key units
- `AiGenerationProvider` (interface) – abstract generation API with optional timing support.
- `AiGenerationProviderFactory` – static factory for provider instances.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp provider implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible request/response handling.
- `AiCompletionParser` – strips thinking blocks from completions.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp options.
- `MockAiGenerationProvider` – placeholder provider used when no key is supplied.

#### Data flow
1. **Request creation** – `AiGenerationRequest` (prompt key, source file, source text, header) is built.
2. **Provider selection** – `AiGenerationProviderFactory.create(...)` resolves the effective provider key and returns an `AiGenerationProvider`.
3. **Generation** – `provider.generate(...)` or `generateWithTimings(...)` is invoked:
   - *Local*: `LlamaCppJniAiGenerationProvider` builds chat template via `AiPromptSupport`, runs `chatComplete`, parses `ChatResponse`, returns text and timing metrics.
   - *Remote*: `OpenAiCompatibleProviderSupport` constructs JSON body, endpoint, API key, sends request, parses `ChatResponse`, computes timings.
4. **Completion cleaning** – `AiCompletionParser.parseCompletion(...)` removes any internal thinking block from the raw completion before returning the final answer.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `ChatResponseParser`, `ChatResponse`, `Usage`.
- **External**: `net.ladenthin.llama` (JNI bindings, model parameters, JSON parser), `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (annotations), `org.jspecify.annotations` (nullability), `java.net.URL`, `java.io.IOException`, `java.util` collections.
- **Provider‑specific**: `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
- **Immutability & thread safety** – `LlamaCppJniConfig` is immutable; provider factories and support classes are stateless.
- **Builder pattern** – `LlamaCppJniConfig.Builder` provides fluent configuration of llama.cpp knobs.
- **Error handling** – Providers throw `IOException` on failures; `OpenAiCompatibleProviderSupport` validates API key usage and endpoint correctness.
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache token counts and throughput; both local and remote providers expose these metrics.
- **Lombok usage** – `@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord` for concise data classes.
