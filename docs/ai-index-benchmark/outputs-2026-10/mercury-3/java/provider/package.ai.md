### java/provider
- H: 1.0
- C: 5AC65435
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:33:17Z
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
> Pluggable AI text-generation backend for srcmorph's code-morphing and calibration workflow, supporting local GGUF inference via llama.cpp JNI, OpenAI-compatible endpoints, and a mock provider, with thinking-block stripping and timing-aware generation.

#### Purpose
- Abstracts AI generation backends behind `AiGenerationProvider` so srcmorph can generate replacement source text (and time it) across local native, remote, and mock providers.
- Centralizes provider selection (`AiGenerationProviderFactory`), provider-specific config (`LlamaCppJniConfig`), and completion post-processing (`AiCompletionParser`) in one package.

#### Responsibilities
- Provider abstraction & lifecycle: `AiGenerationProvider` interface (extends `AutoCloseable`) with `generate`, `generateWithTimings`, and `close`; default `generateWithTimings` degrades to zero-rate timings.
- Provider selection: `AiGenerationProviderFactory` maps keys (`mock`, `llamacpp-jni`, `openai-compatible`) to implementations; resolves model-level provider over run-level provider via `effectiveProvider`.
- Local GGUF inference: `LlamaCppJniAiGenerationProvider` runs llama.cpp JNI (`net.ladenthin:llama`) with lazy `LlamaModel` init, full response/timings parsing, and config-to-`ModelParameters` translation.
- Configuration: `LlamaCppJniConfig` immutable value object with fluent `Builder`; 40 optional fields defaulted from `AiGenerationConfig.DEFAULT_*` (sampling, KV-cache, GPU, batching, DRY, remote/provider).
- Completion parsing: `AiCompletionParser` strips Gemma-4 `thinking` blocks (`<|channel>thought` … `<channel|>`) from raw completions; throws `IOException` on unclosed leading thinking block.
- OpenAI-compatible offline support: `OpenAiCompatibleProviderSupport` builds chat-completions JSON body, endpoint URL, leak-safe API-key lookup (rejects cleartext to non-loopback hosts), success/error reporting, and throughput timings.

#### Key units
- `AiGenerationProvider` — interface contract for text generation with optional timing and resource closing.
- `AiGenerationProviderFactory` — selects and instantiates provider implementation from provider key; defaults to `MockAiGenerationProvider` when no key given.
- `LlamaCppJniAiGenerationProvider` — final class; native GCUF generation via `LlamaModel`, lazy model caching, `chatResponse`/`timings` parsing, `close()` on native handle.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable config with 41 accessors and 40 fluent setters; sole required field `modelPath`.
- `AiCompletionParser` — static-marker parser returning only the model's final answer text.
- `OpenAiCompatibleProviderSupport` — package-private utility for request serialization, endpoint validation, API-key gating, and timing extraction.

#### Data flow
- `AiGenerationRequest` (prompt key, source file, source text, current header) → provider (`generate`/`generateWithTimings`).
- `LlamaCppJniAiGenerationProvider.generate`: `model().chatComplete(support.buildInferenceParameters(request))` → `ChatResponseParser.parseResponse` → `support.warnOnTruncatedAnswer` / `logPromptCacheReuse` → `AiCompletionParser.parseCompletion(response.getFirstContent())` → clean answer `String`.
- `generateWithTimings`: parses `Timings` from response; null timings degrade to zero-rate `AiGenerationTimings` for caller fallback.
- `OpenAiCompatibleProviderSupport` builds request JSON and endpoint; success/error messages and timings derived from response status, body excerpt, and `Usage` counts.

#### Dependencies
- External: `net.ladenthin:llama` JNI (`LlamaModel`, `ChatResponseParser`, `ModelParameters`, `ChatResponse`, `Timings`, `FlashAttn`, `ReasoningFormat`); Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`); Lombok `@ToString`/`@EqualsAndHashCode`; `org.jspecify.annotations.Nullable`; `java.io.IOException`, `java.net.URL`, `java.util.function.Function`.
- Internal: `AiGenerationConfig.DEFAULT_*` from `net.ladenthin.srcmorph.config`; `AiPromptSupport` (prompt building); `Java8CompatibilityHelper` (blank checking); `AiGenerationRequest` from `net.ladenthin.srcmorph.document`.

#### Cross-cutting
- Effective immutability and thread-safety: `LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, and `AiCompletionParser` are immutable (final fields, unmodifiable list views, static markers); safe for concurrent sharing.
- Lombok value semantics: `@ToString` (with `@ToString.Exclude` on native handles/helpers) and `@EqualsAndHashCode` across config/provider/support classes for build-log diagnostics.
- Null tolerance: `Objects.requireNonNull` on constructor inputs; String setters restore defaults on null; List setters coalesce null to empty; `parseCompletion(null)` returns empty string.
- Error contract: `IOException` for unclosed completion thinking block, Jackson serialization failure, and cleartext API key to non-loopback host; `IllegalArgumentException` for unrecognized provider keys or non-http(s) base URLs.
- Single-threaded assumption on lazily initialized `LlamaModel` field; no synchronization or volatile.
