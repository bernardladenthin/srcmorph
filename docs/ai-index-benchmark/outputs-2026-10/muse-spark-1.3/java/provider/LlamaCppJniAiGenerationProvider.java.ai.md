### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:15:14Z
- G: 
- A: 0.0.0
- X: file
---
> Runs local GGUF models via llama.cpp to generate AI code transformations.
#### Purpose
- `AiGenerationProvider` backed by `net.ladenthin:llama` JNI binding for local inference.
- Lazily loads native `LlamaModel` only on first generation to keep construction cheap.
#### Type
- `final class` implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString` with `@ToString.Exclude` on `model`, `support` fields.
#### Input
- Constructor `LlamaCppJniConfig config`, `AiPromptSupport promptSupport` via `Objects.requireNonNull`.
- `AiGenerationRequest request` for `generate`, `generateWithTimings`.
- `LlamaCppJniConfig` options: `modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK/V`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`.
- `LlamaCppJniProviderSupport.buildInferenceParameters`, `buildChatTemplateKwargs` for prompt/inference params.
#### Output
- `String` cleaned completion from `AiCompletionParser.parseCompletion`.
- `AiGenerationTimings` with text, prompt/cache/predicted counts and per-second rates.
- Cached `LlamaModel` in `model` field; native handle closed on `close`.
- Side effects: `warnOnTruncatedAnswer`, `logPromptCacheReuse` diagnostics.
#### Core logic
- Lazy `model()` builds `ModelParameters` from `LlamaCppJniConfig` and caches `LlamaModel`.
- Sets `ReasoningFormat.AUTO` to split reasoning channels into `reasoning_content`.
- Conditionally forwards `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `FlashAttn.ON`, `cacheTypeK/V`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices` only when non-default.
- `chatResponse` calls `model().chatComplete` then `ChatResponseParser.parseResponse`.
- `generate` extracts `getFirstContent`, checks truncation/cache reuse, parses completion.
- `generateWithTimings` surfaces native `Timings.promptN/cacheN/promptPerSecond/predictedN/predictedPerSecond`, degrades to zeros when `timings==null`.
#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport)` -> provider with lazy GGUF load.
- `generate(request) -> String` -> runs chat completion and returns text.
- `generateWithTimings(request) -> AiGenerationTimings` -> returns text plus native throughput.
- `close() -> void` -> closes native `LlamaModel` if loaded.
#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `args.FlashAttn`, `args.ReasoningFormat`, `json.ChatResponseParser`, `parameters.ModelParameters`, `value.ChatResponse`, `value.Timings`.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `prompt.AiPromptSupport`, `support.Java8CompatibilityHelper`.
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationTimings`, `AiGenerationProvider`.
- `java.io.IOException`, `java.util.Map`, `java.util.Objects`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
#### Exceptions / Errors
- `generate(request) throws IOException` per interface; chat path itself throws only unchecked.
- `generateWithTimings` narrows away `IOException` as bogus.
- `NullPointerException` on null `config`/`promptSupport`.
- Zero-rate `AiGenerationTimings` fallback when native `Timings` missing.
#### Concurrency
- Lazy `model` initialization is unsynchronized non-volatile; not thread-safe for concurrent first `generate`.
