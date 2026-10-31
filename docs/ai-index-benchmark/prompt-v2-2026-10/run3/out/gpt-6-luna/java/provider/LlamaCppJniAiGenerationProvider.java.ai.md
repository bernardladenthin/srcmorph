### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:25Z
- G: 
- A: 0.0.0
- X: file
---
> Generates Java-code completions locally with llama.cpp, using configurable GGUF models and returning inference timing and KV-cache reuse metrics.

#### Purpose
- Provides AI generation through the `net.ladenthin:llama` JNI binding.
- Loads the GGUF model on first generation and reuses it for later calls.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support from the rendered output.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both are required.
- `generate` and `generateWithTimings`: `AiGenerationRequest`, used to build inference parameters and prompt data.
- Model configuration includes model path, context and thread settings, reasoning format, GPU/CPU placement, KV-cache options, batching, flash attention, and devices.

#### Output
- `generate` returns parsed completion text; warns on truncated answers and logs prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/prediction token counts, cache tokens, and throughput; missing timings produce zero metrics.
- `close` closes the model if it has been loaded.

#### Core logic
- `model()` lazily creates and caches `LlamaModel` with `ModelParameters`.
- Sets reasoning format to `AUTO` and chat-template kwargs from `LlamaCppJniProviderSupport`.
- Applies optional model settings only when their configured values meet the relevant guards; maps lazy mode and KV-cache types through support helpers.
- Builds inference parameters via `support.buildInferenceParameters`, runs `chatComplete`, and parses the full response.
- `generate` parses the first response content and invokes truncation and cache-reuse diagnostics.
- `generateWithTimings` extracts response text and native timing metrics, including KV-cache hits.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor; validates dependencies.
- `generate(AiGenerationRequest)` -> `String`; generates completion text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings`; returns text and inference metrics.
- `close()` -> `void`; closes loaded model.

#### Dependencies
- `AiGenerationProvider`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `AiCompletionParser`.
- llama binding: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`.
- `Java8CompatibilityHelper`, `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`.

#### Exceptions / Errors
- Constructor rejects null config or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`.
- `generateWithTimings` does not declare checked exceptions.
- Invalid native parameters or model/inference failures may propagate from the binding; no local handling is shown.

#### Concurrency
- No synchronization around lazy model initialization or closing; concurrent first calls may race.
- Model reference is mutable and not volatile; thread safety is not established.
