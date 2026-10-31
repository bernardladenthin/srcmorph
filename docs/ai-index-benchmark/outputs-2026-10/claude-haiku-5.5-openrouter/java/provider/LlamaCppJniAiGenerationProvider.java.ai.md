### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:35:03Z
- G: 
- A: 0.0.0
- X: file
---
> Java provider that runs GGUF chat-completion prompts locally through the llama.cpp JNI binding, with lazy model loading and per-call timing metrics.

#### Purpose
- Implements `AiGenerationProvider` for local GGUF inference via `net.ladenthin:llama` `LlamaModel`.
- Lazily loads the model on first generation, so construction stays native-free and cheap.

#### Type
- Class, `public final`; `@Lombok ToString` on the class, with `model` and `support` marked `@ToString.Exclude`.
- Implements `AiGenerationProvider`, `AutoCloseable`.
- Annotation `@Nullable` (org.jspecify) on field `model`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport` (both null-checked).
- Per call: `AiGenerationRequest request`.
- Config getters read at model load: `modelPath()`, `contextSize()`, `threads()`, `swaFull()`, `cacheReuse()`, `gpuLayers()`, `cpuMoeLayers()`, `cpuFfnLayers()`, `kvUnifiedPerSlot()`, `lazyMode()`, `flashAttn()`, `cacheTypeK()`, `cacheTypeV()`, `batchSize()`, `ubatchSize()`, `threadsBatch()`, `mainGpu()`, `devices()`.
- Support-derived: `buildChatTemplateKwargs()`, `buildInferenceParameters(request)`.

#### Output
- `generate` returns the parsed completion `String`.
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt tokens/s, predicted tokens, predicted tokens/s).
- Side effects: lazily creates `LlamaModel` field; `support.warnOnTruncatedAnswer` and `support.logPromptCacheReuse` log; `close()` closes native model.

#### Core logic
- `model()`: if `model` is null, builds `ModelParameters` (model path, ctx size, threads, `ReasoningFormat.AUTO`, chat template kwargs), then conditionally applies:
  - `enableSwaFull()` when `swaFull`
  - `setCacheReuse` when `cacheReuse > 0`
  - `setGpuLayers` when `gpuLayers >= 0`
  - `setCpuMoeLayers` when `cpuMoeLayers >= 0`
  - `setCpuFfnLayers` when `cpuFfnLayers >= 0`
  - `setKvUnifiedPerSlot` when `> 0`
  - `setLazyMode` when `lazyMode` not blank
  - `setFlashAttn(FlashAttn.ON)` when `flashAttn`
  - `setCacheTypeK` / `setCacheTypeV` when not blank
  - `setBatchSize`, `setUbatchSize`, `setThreadsBatch` when `> 0`
  - `setMainGpu` when `>= 0`
  - `setDevices` when not empty
  - Then creates `new LlamaModel(modelParameters)` and caches it.
- `chatResponse(request)`: calls `model().chatComplete(...)` with inference parameters, parses via `ChatResponseParser.parseResponse`.
- `generate(request)`: gets `ChatResponse`, runs truncation warning and cache-reuse logging, returns `completionParser.parseCompletion(response.getFirstContent())`.
- `generateWithTimings(request)`: reads `Timings`; if null, returns zeroed rates with text. Otherwise maps `promptN`, `cacheN`, `promptPerSecond`, `predictedN`, `predictedPerSecond` into `AiGenerationTimings`.
- `close()`: closes `model` if non-null; does not reset the field.

#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport) -> ctor` — validates args, builds support.
- `generate(request) -> String` — parsed completion text; throws IOException declared.
- `generateWithTimings(request) -> AiGenerationTimings` — text plus native throughput metrics.
- `close() -> void` — releases native model handle.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationProvider`, `AiGenerationTimings`
- `lombok.ToString`, `org.jspecify.annotations.Nullable`, `java.io.IOException`, `java.util.Map`, `java.util.Objects`

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null `config` or `promptSupport`.
- `generate` declares `IOException`, but the chat path throws only unchecked exceptions; `generateWithTimings` narrows it away.
- `close()` does not null the `model` field, so a repeated call invokes `LlamaModel.close()` again.
- Null `Timings` yields zero rates instead of an error.

#### Concurrency
- No synchronization. Lazy init of `model` in `model()` is unguarded; concurrent first calls may create multiple `LlamaModel` instances.
- Field `model` is not volatile.
