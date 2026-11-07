### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:39Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides local GGUF model inference via llama.cpp, exposing AI generation and timing APIs.

#### Purpose
- Loads a GGUF model lazily and runs chat completions.
- Parses responses into plain text or detailed timings.
- Manages model lifecycle and configuration.

#### Type
- `final class LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider`, `AutoCloseable`.  
  - Extends `Object`.  
  - Annotations: `@ToString`.  
  - Fields:  
    - `private final LlamaCppJniConfig config` – configuration.  
    - `@ToString.Exclude private @Nullable LlamaModel model` – native handle, lazy.  
    - `private final AiPromptSupport promptSupport` – prompt rendering.  
    - `@ToString.Exclude private final LlamaCppJniProviderSupport support` – helper.  
    - `private final AiCompletionParser completionParser` – parses text.  
    - `private final ChatResponseParser chatResponseParser` – parses JSON.  
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper.  

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest)` / `generateWithTimings(AiGenerationRequest)` – request contains prompt, parameters.  
- `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond).  
- `close` releases native `LlamaModel`.

#### Core logic
- **Lazy model load** (`model()`):  
  1. If `model` null, build `ModelParameters` with:  
     - `modelPath`, `ctxSize`, `threads`, `reasoningFormat`, `chatTemplateKwargs`.  
     - Optional flags: `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `flashAttn`, `cacheTypeK/V`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`.  
  2. Instantiate `LlamaModel` and cache it.  
- **Chat completion** (`chatResponse`):  
  1. Call `support.buildInferenceParameters(request)` to get inference params.  
  2. Invoke `model().chatComplete(params)` → JSON.  
  3. Parse with `chatResponseParser.parseResponse`.  
- **Generate** (`generate`):  
  1. Call `chatResponse(request)`.  
  2. Warn on truncated answer via `support.warnOnTruncatedAnswer`.  
  3. Log prompt cache reuse via `support.logPromptCacheReuse`.  
  4. Return parsed completion text.  
- **Generate with timings** (`generateWithTimings`):  
  1. Call `chatResponse(request)`.  
  2. Extract `text` and `Timings`.  
  3. If `Timings` null, return zeroed metrics.  
  4. Return `AiGenerationTimings` with prompt/cache/predicted metrics.  
- **Close** (`close`):  
  1. If `model` not null, call `model.close()`.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce completion.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- Constructor throws `NullPointerException` if `config` or `promptSupport` null.  
- `generate` declares `IOException` (though underlying chat path throws unchecked).  
- `generateWithTimings` no checked exceptions.  
- `close` safe even if model null.

#### Concurrency
- No explicit synchronization; assumes single-threaded or external coordination.  
- Lazy initialization of `model` is not thread-safe; concurrent calls may load multiple instances.
