### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:48Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides local, llama.cpp‑backed AI text generation with lazy GGUF loading, prompt rendering, and detailed timing extraction.

#### Purpose
- Supplies AI generation via llama.cpp JNI binding.
- Lazily loads GGUF model on first request.
- Parses chat responses and extracts timings.

#### Type
- `final class LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider`, `AutoCloseable`; extends `Object`.
  - Modifiers: `final`.
  - Annotations: `@ToString`.
  - Supertypes: `AiGenerationProvider`, `AutoCloseable`.
- Fields:
  - `private final LlamaCppJniConfig config` – configuration holder.
  - `@ToString.Exclude private @Nullable LlamaModel model` – native handle, lazily initialized.
  - `private final AiPromptSupport promptSupport` – prompt lookup.
  - `@ToString.Exclude private final LlamaCppJniProviderSupport support` – helper for prompt and inference parameters.
  - `private final AiCompletionParser completionParser` – parses completion text.
  - `private final ChatResponseParser chatResponseParser` – parses full chat JSON.
  - `private final Java8CompatibilityHelper compatibilityHelper` – string utilities.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest`.
- `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt tokens, cache tokens, prompt/sec, predicted tokens, predicted/sec.
- `close()` releases native `LlamaModel` if loaded.

#### Core logic
- `model()`:
  1. If `model` null, create `ModelParameters`:
     - Set model path, context size, threads, reasoning format, chat template kwargs.
     - Conditionally enable SWA full, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified per slot, lazy mode, flash attention, cache type K/V, batch sizes, main GPU, devices.
  2. Instantiate `LlamaModel` with parameters; cache in `model`.
- `generate(request)`:
  1. Call `chatResponse(request)` → `ChatResponse`.
  2. `support.warnOnTruncatedAnswer(request, response)`.
  3. `support.logPromptCacheReuse(request, response)`.
  4. Return `completionParser.parseCompletion(response.getFirstContent())`.
- `chatResponse(request)`:
  - `chatResponseParser.parseResponse(model().chatComplete(support.buildInferenceParameters(request)))`.
- `generateWithTimings(request)`:
  1. Obtain `ChatResponse` via `chatResponse(request)`.
  2. Extract `text`, `Timings`.
  3. If `timings` null, return zeroed `AiGenerationTimings`.
  4. Else construct `AiGenerationTimings` with promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond.
- `close()`:
  - If `model` not null, call `model.close()`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.
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

#### Exceptions / Errors
- Constructor throws `NullPointerException` if `config` or `promptSupport` null.
- `generate` declares `IOException` (though underlying chat path throws unchecked).
- `generateWithTimings` no checked exceptions.
- `close` no exceptions.

#### Concurrency
- No explicit synchronization; `model` is lazily initialized without double‑checked locking; assumes single‑threaded or external synchronization.  
- `close()` may be called concurrently; native `close()` is idempotent.
