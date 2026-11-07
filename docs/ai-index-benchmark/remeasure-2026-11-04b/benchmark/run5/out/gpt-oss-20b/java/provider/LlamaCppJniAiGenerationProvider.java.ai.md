### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:54:05Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Lazily loads a GGUF model for inference.
- Executes chat completions and parses responses.
- Exposes timings and handles resource cleanup.

#### Type
- `final class LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider`, `AutoCloseable`.  
  - Extends `Object`.  
  - Annotations: `@ToString`.  
- Fields:  
  - `private final LlamaCppJniConfig config` – configuration.  
  - `@ToString.Exclude private @Nullable LlamaModel model` – native handle.  
  - `private final AiPromptSupport promptSupport` – prompt rendering.  
  - `@ToString.Exclude private final LlamaCppJniProviderSupport support` – helper.  
  - `private final AiCompletionParser completionParser` – parses text completions.  
  - `private final ChatResponseParser chatResponseParser` – parses full chat JSON.  
  - `private final Java8CompatibilityHelper compatibilityHelper` – string utilities.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest`.  
- `chatResponse(AiGenerationRequest request)` builds inference parameters via `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns `String` completion text.  
- `generateWithTimings` returns `AiGenerationTimings` containing text, prompt tokens, cache tokens, prompt throughput, predicted tokens, predicted throughput.  
- `close` releases native `LlamaModel` resources.

#### Core logic
- `model()` lazily initializes `LlamaModel`:
  - Builds `ModelParameters` with config values (model path, context size, threads, reasoning format, chat template kwargs).
  - Applies optional settings: SWA full, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified per slot, lazy mode, flash attention, cache type K/V, batch sizes, main GPU, devices.
  - Instantiates `LlamaModel` and caches it.
- `generate`:
  - Calls `chatResponse(request)` → parses full chat JSON.
  - Warns on truncated answer via `support.warnOnTruncatedAnswer`.
  - Logs prompt cache reuse via `support.logPromptCacheReuse`.
  - Parses completion text from first content using `completionParser`.
- `chatResponse`:
  - Calls `model().chatComplete(support.buildInferenceParameters(request))`.
  - Parses response JSON with `chatResponseParser`.
- `generateWithTimings`:
  - Calls `chatResponse(request)`.
  - Extracts text and `Timings` from response.
  - If `Timings` null, returns zeroed metrics.
  - Otherwise constructs `AiGenerationTimings` with promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond.
- `close`:
  - If `model` non‑null, calls `model.close()`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion text with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, `lombok.ToString`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked exceptions from native calls.  
- `generateWithTimings` handles missing `Timings` by returning zeroed metrics.

#### Concurrency
- No explicit synchronization; `model` is lazily initialized without double‑checked locking, relying on single‑threaded usage or external synchronization.  
- `close` safely checks for null before closing.
