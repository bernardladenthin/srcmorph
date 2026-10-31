### java/provider
- H: 1.0
- C: 962F0B37
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:04:25Z
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
> <one-sentence domain essence>
> A pluggable AI text generation backend that parses LLM responses, extracts answers from thinking blocks, and optionally measures timing, supporting local llama.cpp models and OpenAI-compatible providers.

#### Purpose
- Provide a unified interface (`AiGenerationProvider`) for generating textual answers from source code analysis.
- Parse LLM completions to strip leading "thinking" blocks and return clean answers.
- Support local inference via JNI-linked llama.cpp models and remote OpenAI-compatible endpoints without direct network calls.
- Measure generation timings for calibration and monitoring.

#### Responsibilities
- **Parsing**: `AiCompletionParser` removes leading thinking blocks from raw LLM responses.
- **Provider abstraction**: `AiGenerationProvider` defines the contract for all AI generation backends.
- **Factory & selection**: `AiGenerationProviderFactory` selects and instantiates the correct provider (`Mock`, `LlamaCppJni`, or `OpenAiCompatible`) based on configuration.
- **Local inference**: `LlamaCppJniAiGenerationProvider` loads and runs a GGUF model via JNI, lazily on first use, and parses chat responses.
- **Configuration**: `LlamaCppJniConfig` holds immutable settings for the local model (path, params, threads, cache, etc.).
- **OpenAI support**: `OpenAiCompatibleProviderSupport` builds request bodies, validates endpoints, safely handles API keys (rejecting plain HTTP to non-loopback), and computes timings from server responses.

#### Key units
- `AiCompletionParser` – parses completion text, strips `THINKING_BLOCK_START_MARKER` to `THINKING_BLOCK_END_MARKER`, throws `IOException` if block unclosed.
- `AiGenerationProvider` – interface requiring `generate(request)`, `generateWithTimings(request)`, and `close()`.
- `AiGenerationProviderFactory` – stateless factory selecting provider via `effectiveProvider` and `create`.
- `LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider` for local llama.cpp via JNI; lazy model loading, uses `AiCompletionParser` to parse response.
- `LlamaCppJniConfig` – immutable fluent-builder config for local model settings.
- `OpenAiCompatibleProviderSupport` – final helper building JSON request bodies, computing endpoints, validating API keys (no network), and computing timings from `ChatResponse`.

#### Data flow
- `AiGenerationRequest` (containing prompt/metadata) flows into a provider (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, or `MockAiGenerationProvider`).
- Provider calls `AiCompletionParser.parseCompletion()` on the raw chat response to extract the answer text (removing thinking block).
- `LlamaCppJniAiGenerationProvider` lazily initializes the `LlamaModel` on first `generate`/`generateWithTimings`, then calls `chatComplete` and parses via `chatResponseParser`.
- `OpenAiCompatibleProviderSupport` receives `LlamaCppJniConfig` and `AiPromptSupport`; it builds `requestBody`, `endpoint`, and `apiKeyFor`; it computes `AiGenerationTimings` from `ChatResponse` usage fields.
- `AiGenerationProviderFactory` receives `providerName`, `LlamaCppJniConfig`, `AiPromptSupport`; it computes `effectiveProvider` and returns the concrete provider instance.
- `generateWithTimings` returns `AiGenerationTimings` (prompt count, cache count, rates); `generate` returns only the cleaned answer string.

#### Dependencies
- Internal: `AiCompletionParser`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ChatResponseParser`, `ModelParameters`, `ReasoningFormat`, `FlashAttn`.
- External Java: `java.io.IOException`, `java.util.*`, `org.jspecify.annotations.Nullable`.
- Jackson: `ObjectMapper`, `ObjectNode`, `ArrayNode`.
- Lombok: `@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`.
- `net.ladenthin.llama.*` types (`LlamaModel`, `ChatResponse`, `Timings`).
- No direct network libraries; `OpenAiCompatibleProviderSupport` validates URLs but does not make HTTP calls.

#### Cross-cutting
- All provider classes are stateless or immutable after construction; `LlamaCppJniAiGenerationProvider` caches the model internally but fields (`config`, `promptSupport`, `support`) are immutable.
- `IOException` is the only checked exception propagated from providers (`generate` may throw it; `generateWithTimings` catches and handles gracefully).
- `null` inputs are safely handled: `parseCompletion` treats `null` as empty; factory methods trim/check for null/empty; builder validates `modelPath` not null.
- Thread safety: no shared mutable state; `LlamaCppJniAiGenerationProvider`'s native model is assumed thread-safe after initialization; provider methods are pure or use only immutable fields.
- Fluent builder pattern used by `LlamaCppJniConfig`; getters only, no setters after `build`.
- Static utility methods (`isLoopbackHost`, `isSuccess`, `errorMessage`) in `OpenAiCompatibleProviderSupport` for safe host/status checks.
- Provider selection relies on `effectiveProvider` string constant (`PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`); factory throws `IllegalArgumentException` for unknown providers.

> Note: `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider` are referenced in summaries but not detailed here; only their existence and role (fallback/remote provider) are implied by factory logic. No implementation details for these are provided in the given summaries.
