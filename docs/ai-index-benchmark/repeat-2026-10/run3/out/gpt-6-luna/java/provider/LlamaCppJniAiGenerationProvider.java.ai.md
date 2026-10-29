### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:20:41Z
- G: 
- A: 0.0.0
- X: file
---
> Generates Java source transformations locally with a llama.cpp GGUF model and reports inference and prompt-cache metrics.

#### Purpose
- `LlamaCppJniAiGenerationProvider` implements AI generation using the `net.ladenthin:llama` JNI binding.
- Loads the model only when generation first runs; reuses it for later requests.

#### Type
- `final class`; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from the rendered output.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`; rejects null values.
- `generate` and `generateWithTimings` accept `AiGenerationRequest`.
- Reads model, context, threading, GPU, KV-cache, attention, batching, device, and lazy-loading settings from `LlamaCppJniConfig`.
- `LlamaCppJniProviderSupport` builds chat-template arguments and request inference parameters.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` containing text, prompt/cache/predicted token counts, and throughput rates.
- Caches a `LlamaModel` instance; `close` closes it if loaded.
- Logs prompt-cache reuse and warns about truncated answers through provider support.

#### Core logic
- On first use, builds `ModelParameters` from configuration and creates the `LlamaModel`; subsequent calls reuse the cached handle.
- Applies optional settings only when configured: SWA full, cache reuse, GPU and CPU layer placement, KV context budget, lazy mode, Flash Attention, KV-cache types, prefill sizes, GPU selection, and device list.
- Builds inference parameters, invokes `chatComplete`, and parses the full response so generation can use completion text, finish metadata, timings, and cache counters.
- `generate` checks for truncation, logs cache reuse, and parses the first response content.
- `generateWithTimings` returns zero rates and counts when timings are absent; otherwise forwards prompt count, cache count, predicted count, and measured rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> constructor` — validates inputs and initializes support.
- `generate(AiGenerationRequest) -> String` — generates and parses completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generates text and reports metrics.
- `close() -> void` — closes the loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponse`, `ChatResponseParser`, `Timings`
- `Java8CompatibilityHelper`, `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null configuration or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not declare a checked exception.
- Model initialization, inference, parsing, or model closing may propagate unchecked failures.

#### Concurrency
- Lazy model initialization and closing are unsynchronized; concurrent access is not explicitly protected.
