### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:43Z
- G: 
- A: 0.0.0
- X: file
---
> Generates Java source transformations locally with llama.cpp and reports completion text, truncation, prompt-cache reuse, and inference timings.

#### Purpose
- Provides `AiGenerationProvider` backed by the `net.ladenthin:llama` JNI binding and local GGUF models.
- Delays native model loading until generation is requested; reuses the loaded model afterward.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from generated output.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`; rejects null values.
- Generation methods accept `AiGenerationRequest`.
- Configuration supplies model path, context and thread settings, GPU/CPU placement, KV-cache, attention, batching, device, and lazy-loading options.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters from configuration and requests.

#### Output
- `generate` returns parsed completion text; warns about truncation and logs prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` with completion text, prompt/cache/prediction token counts, and throughput rates.
- `close` closes the native model if it was loaded.

#### Core logic
- On first model access, builds `ModelParameters` from configuration and creates a `LlamaModel`; stores it for subsequent calls.
- Applies optional settings only under their configured guards: SWA, cache reuse, GPU/CPU layer placement, unified KV budget, lazy mode, Flash Attention, cache types, batch sizes, thread batch, main GPU, and devices.
- Builds inference parameters through `support`, calls `chatComplete`, and parses the full response so text, finish information, usage, and cache counters remain available.
- `generate` checks truncation and logs cache reuse before parsing the first content as a completion.
- `generateWithTimings` uses model-reported timings and cache count; when timings are absent, returns zero counts and rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor; validates dependencies and initializes support.
- `generate(AiGenerationRequest) -> String` — generate parsed completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — return completion and model timings.
- `close() -> void` — close loaded native model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationTimings`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`.
- `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`.
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`.
- `IOException`, `Map`, `Objects`, Lombok `@ToString`, JSpecify `@Nullable`.

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for configuration and prompt support.
- `generate` declares `IOException`; `generateWithTimings` narrows away the interface’s checked exception.
- Invalid native/model configuration or response parsing may fail through unchecked exceptions; no local recovery is shown.
- Missing timing data degrades to zero timing values.

#### Concurrency
- Lazy model initialization and close are unsynchronized; concurrent calls may race to initialize or close the model.
- Configuration and helper fields are final; the cached model field is nullable and mutable.
