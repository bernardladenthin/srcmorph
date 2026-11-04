### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:51:04Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides local AI generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Lazily loads a GGUF model for inference.
- Executes chat completions and parses responses.
- Supplies timing metrics and handles resource cleanup.

#### Type
- `final class LlamaCppJniAiGenerationProvider`  
  *implements* `AiGenerationProvider`, `AutoCloseable`  
  *extends* none  
  *annotations*: `@ToString`  
  *fields*:  
  - `private final LlamaCppJniConfig config` (final)  
  - `@ToString.Exclude private @Nullable LlamaModel model` (nullable)  
  - `private final AiPromptSupport promptSupport` (final)  
  - `@ToString.Exclude private final LlamaCppJniProviderSupport support` (final)  
  - `private final AiCompletionParser completionParser` (final)  
  - `private final ChatResponseParser chatResponseParser` (final)  
  - `private final Java8CompatibilityHelper compatibilityHelper` (final)

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest`.
- `chatResponse(AiGenerationRequest request)` uses `support.buildInferenceParameters(request)` and `model().chatComplete(...)`.

#### Output
- `generate` returns `String` (parsed completion).
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt TPS, predicted tokens, predicted TPS).
- `close` releases native `LlamaModel` resources.

#### Core logic
- `model()` lazily constructs `LlamaModel`:
  - Builds `chatTemplateKwargs` via `support.buildChatTemplateKwargs()`.
  - Configures `ModelParameters` with model path, context size, threads, reasoning format, chat template kwargs.
  - Applies optional settings: SWA full, cache reuse, GPU layers, CPU MoE layers, CPU FFN layers, KV unified per slot, lazy mode, flash attention, cache type K/V, batch sizes, main GPU, devices.
  - Instantiates `new LlamaModel(modelParameters)` and caches it.
- `generate(request)`:
  - Calls `chatResponse(request)` → parses full chat response.
  - Warns on truncated answer via `support.warnOnTruncatedAnswer`.
  - Logs prompt cache reuse via `support.logPromptCacheReuse`.
  - Parses completion text with `completionParser.parseCompletion`.
- `chatResponse(request)`:
  - Calls `chatResponseParser.parseResponse(model().chatComplete(support.buildInferenceParameters(request)))`.
- `generateWithTimings(request)`:
  - Obtains `ChatResponse` via `chatResponse`.
  - Extracts text, timings; if null, returns zeroed metrics.
  - Returns `AiGenerationTimings` with promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond.
- `close()`:
  - If `model` is non‑null, calls `current.close()`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion text with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
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
- `java.io.IOException`  
- `java.util.Map`  
- `java.util.Objects`  
- Lombok `@ToString`

#### Exceptions / Errors
- Constructor throws `NullPointerException` if `config` or `promptSupport` is null.
- `generate` declares `throws IOException` (though underlying chat path throws unchecked).
- `generateWithTimings` does not throw checked exceptions.
- `close` handles null model gracefully.

#### Concurrency
- No explicit synchronization; `model()` is not thread‑safe for concurrent first‑use; callers should ensure single‑threaded initialization or external locking.  
- Native `LlamaModel` operations are assumed thread‑safe per library contract.
