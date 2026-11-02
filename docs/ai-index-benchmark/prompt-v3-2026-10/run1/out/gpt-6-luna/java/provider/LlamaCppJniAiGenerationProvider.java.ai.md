### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:49:04Z
- G: 
- A: 0.0.0
- X: file
---
> Generates invoice or document completions locally with llama.cpp, reusing a lazily loaded GGUF model and reporting inference timings.

#### Purpose
- Provides `AiGenerationProvider` completions using the `net.ladenthin:llama` JNI binding.
- Exposes generated text and model-reported throughput and cache statistics.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor: `LlamaCppJniConfig` for model and inference settings; `AiPromptSupport` for prompt rendering.
- `generate` and `generateWithTimings`: `AiGenerationRequest`.
- Model loading reads the configured model path and settings; inference parameters and chat-template kwargs come from `LlamaCppJniProviderSupport`.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/cache/predicted token counts, and rates; missing timings produce zero values.
- Lazily creates and retains a `LlamaModel`; `close()` closes it if loaded.
- Emits truncation warnings and prompt-cache-reuse logs through support.

#### Core logic
- Constructor null-checks config and prompt support, then creates `LlamaCppJniProviderSupport`.
- `model()` lazily builds `ModelParameters` from config, applies enabled cache, GPU/CPU offload, KV, attention, batching, and device options, then creates and caches `LlamaModel`.
- `chatResponse()` builds request inference parameters, invokes `chatComplete`, and parses the full response.
- `generate()` warns on truncation, logs cache reuse, and parses the first response content.
- `generateWithTimings()` returns first content with model-reported token counts, cache count, and throughput; returns zero metrics if timings are absent.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — initialize provider.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text and metrics.
- `close() -> void` — close loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiCompletionParser`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null config or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not.
- No exceptions are caught; model-loading and inference failures propagate.

#### Concurrency
- Lazy model initialization and cached model access are unsynchronized; concurrent first calls may race.
- No explicit thread-safety guarantees.
