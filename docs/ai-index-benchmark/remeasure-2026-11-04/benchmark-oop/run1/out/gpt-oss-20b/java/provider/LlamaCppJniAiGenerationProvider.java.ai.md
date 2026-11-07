### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:25:34Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and exposing a prompt‑based completion API.

#### Purpose
- Loads and caches a llama.cpp GGUF model lazily.
- Generates chat completions and parses full OpenAI‑compatible JSON responses.
- Supplies timing metrics and handles prompt‑cache reuse diagnostics.

#### Type
- `final class LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider`, `AutoCloseable`; extends `Object`.
  - Modifiers: `final`.
  - Annotations: `@ToString`.
  - Supertypes: `AiGenerationProvider`, `AutoCloseable`.
- Fields:
  - `private final LlamaCppJniConfig config` – configuration holder.
  - `@ToString.Exclude private @Nullable LlamaModel model` – native handle, lazily initialized.
  - `private final AiPromptSupport promptSupport` – prompt rendering helper.
  - `@ToString.Exclude private final LlamaCppJniProviderSupport support` – internal support logic.
  - `private final AiCompletionParser completionParser` – parses text completions.
  - `private final ChatResponseParser chatResponseParser` – parses full JSON responses.
  - `private final Java8CompatibilityHelper compatibilityHelper` – helper for string checks.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest` containing prompt data.
- `chatResponse(AiGenerationRequest request)` builds inference parameters via `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns `String` completion text.
- `generateWithTimings` returns `AiGenerationTimings` containing text, prompt token counts, cache usage, and throughput.
- `close` releases native `LlamaModel` resources.

#### Core logic
- **Lazy model loading (`model()`):**
  1. If `model` is null, build `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.
  2. Apply optional settings: SWA, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified slot, lazy mode, flash attention, cache quantization, batch sizes, GPU selection, device list.
  3. Instantiate `LlamaModel` with parameters and cache it.
- **Generation (`generate`):**
  1. Call `chatResponse(request)` to obtain full `ChatResponse`.
  2. Warn on truncated answers and log cache reuse via `support`.
  3. Parse first content with `completionParser`.
- **Chat response (`chatResponse`):**
  1. Invoke `model().chatComplete(...)` with inference parameters.
  2. Parse JSON into `ChatResponse` via `chatResponseParser`.
- **Timings (`generateWithTimings`):**
  1. Retrieve `ChatResponse` as above.
  2. Extract text, prompt/cache/token counts, and throughput from `Timings`.
  3. Return `AiGenerationTimings` with defaults if timings absent.
- **Resource cleanup (`close`):** Calls `current.close()` on the native model if present.

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
- `generate` declares `throws IOException` (though underlying calls are unchecked).
- `generateWithTimings` handles missing `Timings` by returning zeroed metrics.
- `close` silently ignores null model.

#### Concurrency
- No explicit synchronization; `model()` is not thread‑safe – concurrent first calls may load multiple models. Subsequent calls reuse the cached instance. No other shared mutable state.
