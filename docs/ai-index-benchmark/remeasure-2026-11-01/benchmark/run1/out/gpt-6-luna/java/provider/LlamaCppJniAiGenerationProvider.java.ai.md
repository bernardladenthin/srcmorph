### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:55Z
- G: 
- A: 0.0.0
- X: file
---
> Generates invoice or document completions locally with llama.cpp, using configured GGUF models and exposing inference timing and prompt-cache metrics.

#### Purpose
- Provides AI text generation through the `net.ladenthin:llama` JNI binding.
- Loads the GGUF model only when generation first requires it, then reuses the handle.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; native model handle and provider support are excluded from generated output.

#### Input
- Constructor: non-null `LlamaCppJniConfig` and `AiPromptSupport`.
- `generate` and `generateWithTimings`: `AiGenerationRequest`; support builds inference parameters and chat-template kwargs.
- Model initialization reads configuration for model path, context size, threads, GPU/CPU placement, KV cache, attention, batching, devices, and lazy loading.

#### Output
- `generate` returns parsed completion text; may warn about truncation and log prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/cache/predicted token counts, and throughput rates.
- Lazily creates and stores a `LlamaModel`; `close` closes it if initialized.

#### Core logic
- Builds `ModelParameters` from configuration, forwarding optional settings only when their configured values meet the relevant guards.
- Creates and caches `LlamaModel` on first use; subsequent calls reuse it.
- Runs `chatComplete` with support-built inference parameters and parses the full response.
- `generate` checks truncation, logs cache reuse, then parses the first response content as a completion.
- `generateWithTimings` returns response timing and cache metrics; missing timings produce zero counts and rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — constructs provider with required dependencies.
- `generate(AiGenerationRequest) -> String` — generates completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generates text and timing metrics.
- `close() -> void` — closes initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`.
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `LlamaModel`.
- `ModelParameters`, `FlashAttn`, `ReasoningFormat`.
- `ChatResponseParser`, `ChatResponse`, `Timings`, `AiCompletionParser`.
- `Java8CompatibilityHelper`; `IOException`, `Map`, `Objects`; Lombok `ToString`; JSpecify `Nullable`.

#### Exceptions / Errors
- Constructor rejects null config or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not declare a checked exception.
- Invalid model/configuration or native inference failures may propagate from model setup or chat completion.
- `generateWithTimings` handles absent response timings by returning zero metrics.

#### Concurrency
- Lazy model initialization and model access are unsynchronized; concurrent first calls may race.
- `close` does not reset the stored model reference.
