### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:39:23Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text completions and inference timings by running local GGUF models through the llama.cpp JNI binding for source code transformation requests.

#### Purpose
- Implements `AiGenerationProvider` backed by the `net.ladenthin:llama` JNI binding to execute GGUF models locally via llama.cpp
- Lazily loads the native model on first use and exposes both plain-text completions and detailed token-throughput metrics

#### Type
- `final class` implementing `AiGenerationProvider`, `AutoCloseable`
- Annotated `@ToString` (Lombok); fields `model` and `support` excluded via `@ToString.Exclude`

#### Input
- Constructor: `LlamaCppJniConfig config` (model path, context size, threads, GPU/CPU layer splits, KV cache settings, flash attention, batch sizes, device selection), `AiPromptSupport promptSupport`
- `generate` / `generateWithTimings`: `AiGenerationRequest request`
- Config-driven `ModelParameters` built in `model()`: modelPath, ctxSize, threads, reasoningFormat (AUTO), chatTemplateKwargs, swaFull, cacheReuse, gpuLayers, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, flashAttn, cacheTypeK/V, batchSize, ubatchSize, threadsBatch, mainGpu, devices

#### Output
- `generate` → `String`: parsed completion extracted from first chat response content
- `generateWithTimings` → `AiGenerationTimings`: text plus prompt/predicted token counts, per-second throughput rates, and KV-cache hit count (`cache_n`)
- `close` → releases native `LlamaModel` handle
- Side effects: loads GGUF model + native context into memory on first call; logs truncation warnings and prompt-cache reuse via `LlamaCppJniProviderSupport`

#### Core logic
- Lazy initialization: `model()` constructs `ModelParameters` from `LlamaCppJniConfig` using 20 conditional setters (GPU offload, MoE/FFN CPU pinning, KV quantization, flash attention, batch sizing, device selection, etc.), then creates and caches `LlamaModel`
- Inference: `chatResponse()` invokes `model().chatComplete(support.buildInferenceParameters(request))` and parses the full OpenAI-compatible JSON via `ChatResponseParser` to retain finish reason, token usage, and timings
- `generate()` warns on truncated answers, logs cache reuse, and returns `AiCompletionParser.parseCompletion(response.getFirstContent())`
- `generateWithTimings()` maps `Timings` fields (prompt_n, cache_n, prompt_per_second, predicted_n, predicted_per_second) into `AiGenerationTimings`; returns zeroed rates when `Timings` is null

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor, null-checks args and creates support delegate
- `generate(AiGenerationRequest) -> String` (throws IOException) -> returns parsed completion text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` -> returns text with token counts and measured throughput
- `close() -> void` -> closes native model if loaded

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`
- `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- Same-package: `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationTimings`
- `lombok.ToString`, `org.jspecify.annotations.Nullable`, `java.io.IOException`, `java.util.Map`, `java.util.Objects`

#### Exceptions / Errors
- `generate` declares `throws IOException` per interface contract (chat path itself throws only unchecked exceptions)
- Constructor throws `NullPointerException` via `Objects.requireNonNull` on null `config` or `promptSupport`
- `generateWithTimings` handles null `Timings` by returning `AiGenerationTimings` with zero rates

#### Concurrency
- `model` field lazily initialized without synchronization; not thread-safe (concurrent first calls may load the model twice); designed for single-threaded use per provider instance
