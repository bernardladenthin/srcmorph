### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:53Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and exposing a simple generate API.

#### Purpose
- Loads a GGUF model lazily and caches it for repeated calls.
- Generates chat completions, parses responses, and exposes timings.

#### Type
- `final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
  - Extends `Object`; implements `AiGenerationProvider`, `AutoCloseable`.
  - Modifiers: `final`.
  - Fields:
    - `private final LlamaCppJniConfig config`
    - `@Nullable private LlamaModel model`
    - `private final AiPromptSupport promptSupport`
    - `private final LlamaCppJniProviderSupport support`
    - `private final AiCompletionParser completionParser`
    - `private final ChatResponseParser chatResponseParser`
    - `private final Java8CompatibilityHelper compatibilityHelper`
  - Constructor: `public LlamaCppJniAiGenerationProvider(final LlamaCppJniConfig config, final AiPromptSupport promptSupport)`
  - Methods:
    - `private LlamaModel model()`
    - `public String generate(final AiGenerationRequest request) throws IOException`
    - `private ChatResponse chatResponse(final AiGenerationRequest request)`
    - `public AiGenerationTimings generateWithTimings(final AiGenerationRequest request)`
    - `public void close()`

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate`/`generateWithTimings` parameter: `AiGenerationRequest request`.
- `model()` reads `config` fields (`modelPath`, `contextSize`, `threads`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK`, `cacheTypeV`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`) and `support.buildChatTemplateKwargs()`.
- `chatResponse` uses `support.buildInferenceParameters(request)`.

#### Output
- `generate`: returns `String` completion text.
- `generateWithTimings`: returns `AiGenerationTimings` containing text, prompt token counts, cache usage, throughput rates.
- `close`: releases native `LlamaModel` resources.

#### Core logic
- `model()`:
  - If `model` is null, construct `ModelParameters` with config values.
  - Apply optional settings (swaFull, cacheReuse, gpuLayers, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, flashAttn, cacheTypeK/V, batch sizes, mainGpu, devices).
  - Instantiate `LlamaModel` with `modelParameters` and cache it.
- `generate(request)`:
  - Call `chatResponse(request)` to get `ChatResponse`.
  - Warn on truncated answer via `support.warnOnTruncatedAnswer(request, response)`.
  - Log prompt cache reuse via `support.logPromptCacheReuse(request, response)`.
  - Parse completion text with `completionParser.parseCompletion(response.getFirstContent())`.
- `chatResponse(request)`:
  - Invoke `model().chatComplete(support.buildInferenceParameters(request))`.
  - Parse JSON into `ChatResponse` with `chatResponseParser.parseResponse(...)`.
- `generateWithTimings(request)`:
  - Obtain `ChatResponse` via `chatResponse(request)`.
  - Extract text and `Timings` from response.
  - If `Timings` null, return zeroed `AiGenerationTimings`.
  - Else construct `AiGenerationTimings` with promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond.
- `close()`:
  - If `model` not null, call `model.close()`.

#### Public API
- `public LlamaCppJniAiGenerationProvider(final LlamaCppJniConfig, final AiPromptSupport)` – create provider.
- `public String generate(final AiGenerationRequest) throws IOException` – generate text.
- `public AiGenerationTimings generateWithTimings(final AiGenerationRequest)` – generate with metrics.
- `public void close()` – release resources.

#### Dependencies
- `java.io.IOException`
- `java.util.Map`
- `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`
- `net.ladenthin.llama.args.ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `generate` declares `throws IOException` (propagated from `chatComplete`/`chatCompleteText`).
- No explicit null checks beyond `Objects.requireNonNull` in constructor.

#### Concurrency
- No synchronization; single‑threaded usage assumed. The native `LlamaModel` is used only after lazy initialization.
