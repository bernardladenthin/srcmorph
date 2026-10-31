### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:45:17Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions for source-processing requests using a locally loaded llama.cpp GGUF model, with optional timing and cache-reuse metrics.

#### Purpose
- Provides request prompt generation through a local llama.cpp model.
- Loads the model on first use, reuses it, and exposes model timings through the timing API.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both required non-null.
- `generate` and `generateWithTimings`: `AiGenerationRequest`, converted by support into inference parameters.
- Configuration supplies model path, context/thread settings, and optional GPU, KV cache, attention, batching, device, and lazy-loading settings.

#### Output
- `generate(...)` returns parsed completion text.
- `generateWithTimings(...)` returns `AiGenerationTimings` with text, prompt/cache/prediction token counts, and throughput; missing timings produce zero metrics.
- `close()` closes the loaded model if present.

#### Core logic
- `model()` lazily constructs and caches `LlamaModel` using `ModelParameters`; applies configured options only when their values meet the relevant guards.
- Enables automatic reasoning-format parsing and obtains chat-template kwargs from `LlamaCppJniProviderSupport`.
- Runs chat completion with support-built inference parameters, then parses the full response.
- `generate` warns on truncated answers, logs prompt-cache reuse, and parses the first response content.
- `generateWithTimings` returns response text and native prompt, cache, prediction, and rate metrics.
- `close` releases the native model only if it was initialized.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor; initializes configuration and support.
- `generate(AiGenerationRequest)` -> `String`; generates a completion.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings`; generates completion with metrics.
- `close()` -> `void`; closes loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`
- `AiPromptSupport`, `Java8CompatibilityHelper`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `IOException`, `Map`, `Objects`, Lombok `@ToString`, `@Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; model loading, inference, and parsing may also fail at runtime.
- `generateWithTimings` has no checked exception declaration.
- Missing response timings are handled by returning zero metrics.
