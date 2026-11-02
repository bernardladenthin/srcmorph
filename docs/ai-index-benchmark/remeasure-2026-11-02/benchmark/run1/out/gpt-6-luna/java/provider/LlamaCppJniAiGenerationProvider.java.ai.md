### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:27:20Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates AI completions locally from GGUF models using llama.cpp, with configurable inference, GPU, cache, and timing behavior.

#### Purpose
- Provides local llama.cpp-backed AI generation for `AiGenerationRequest` inputs.
- Reuses a lazily loaded model and exposes completion text, generation timings, and cache statistics.

#### Type
- `public final class LlamaCppJniAiGenerationProvider`; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`; rejects null values.
- Generation methods accept an `AiGenerationRequest`.
- Configuration supplies model path, context/thread settings, GPU and CPU layer settings, KV/cache options, batching, and device selection.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters from configuration and requests.

#### Output
- `generate` returns parsed completion text; warns about truncated answers and logs prompt-cache reuse.
- `generateWithTimings` returns completion text, token counts, cache count, and measured prompt/decode rates; missing timings produce zero values.
- `close` closes the model if it has been initialized.

#### Core logic
- Lazily constructs and caches `LlamaModel` on first inference.
- Builds `ModelParameters` from configuration, including reasoning format `AUTO` and chat-template kwargs.
- Applies optional settings only when their configuration guards permit: SWA full, cache reuse, GPU/CPU layer counts, KV budget, lazy mode, Flash Attention, cache quantization, batching, GPU index, and devices.
- Runs `chatComplete` with support-built inference parameters, then parses the full response with `ChatResponseParser`.
- `generate` checks truncation and logs cache reuse before parsing first-content text with `AiCompletionParser`.
- `generateWithTimings` surfaces prompt/predicted token counts, KV-cache token count, and model-reported throughput.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor; validates and stores dependencies.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — return text and model timings.
- `close() -> void` — close initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiPromptSupport`, `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for configuration and prompt support.
- `generate` declares `IOException`.
- `generateWithTimings` does not declare checked exceptions.
- A response without timing data returns zero token counts and rates.

#### Concurrency
- Lazy model initialization and model access are unsynchronized; concurrent first use or close is not guarded.
