### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:45:01Z
- G: 
- A: 0.0.0
- X: file
---
> Configures and runs local GGUF chat generation through llama.cpp JNI, returning parsed code completions and timing metadata.

#### Purpose
- Implements local AI generation for Java source requests using native llama.cpp-backed models.
- Produces completion text and optional timing statistics while managing the lazy-loaded native model lifecycle.

#### Type
- `final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
- `@ToString` on the type; `@ToString.Exclude` on `model`, `support`
- No generics; no `extends` clause

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both are required via `Objects.requireNonNull(...)`
- Method input: `AiGenerationRequest request` passed to `generate(...)` and `generateWithTimings(...)`
- Reads config values for model path, context size, threads, SWA, cache reuse, GPU layers, CPU MoE/FFN layers, KV quantization, lazy mode, flash attention, device selection, batch sizes, and `chatTemplateKwargs`
- Consumes `support.buildChatTemplateKwargs()`, `support.buildInferenceParameters(request)`, and the cached native `LlamaModel` handle

#### Output
- `generate(AiGenerationRequest) -> String`: returns parsed completion text from `response.getFirstContent()`
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: returns text plus prompt/prediction timing metrics
- Mutates cached `model` field on first load
- `close() -> void`: closes the underlying native `LlamaModel` instance
- Side effects: `support.warnOnTruncatedAnswer(...)` and `support.logPromptCacheReuse(...)`

#### Core logic
- `model()` lazily creates a single `LlamaModel` using `ModelParameters` built from `LlamaCppJniConfig`
- Applies config-driven llama.cpp settings: reasoning format, chat template kwargs, SWA full cache, cache reuse, GPU layers, CPU expert/FFN layers, per-slot KV budget, lazy mode, flash attention, KV cache type K/V, batch sizes, main GPU, and devices
- `chatResponse(AiGenerationRequest)` calls `chatResponseParser.parseResponse(model().chatComplete(support.buildInferenceParameters(request)))`
- `generate(AiGenerationRequest)` invokes `chatResponse(...)`, emits warning/cache logs, and extracts the first content via `completionParser.parseCompletion(...)`
- `generateWithTimings(AiGenerationRequest)` extracts `ChatResponse` content and `Timings`, returning zeroed values when timings are absent
- `close()` releases the cached native model only if it was already created

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> constructor` creates provider
- `generate(AiGenerationRequest) -> String` returns completion text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` returns text and timing data
- `close() -> void` closes native model

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
- `AiGenerationProvider`
- `AiGenerationTimings`
- `LlamaCppJniConfig`
- `LlamaCppJniProviderSupport`
- `AiCompletionParser`

#### Exceptions / Errors
- Constructor guards null inputs with `Objects.requireNonNull(config, "config")` and `Objects.requireNonNull(promptSupport, "promptSupport")`
- `generate(AiGenerationRequest)` declares `throws IOException`; native chat path is handled via parsed response and unchecked native exceptions are not caught here
- `generateWithTimings(AiGenerationRequest)` guards a missing `Timings` object by returning zeroed `AiGenerationTimings`
- `close()` checks `model != null` before closing the handle

#### Concurrency
- No explicit synchronization, lock, or `volatile` on `model`; lazy initialization is not guarded for thread safety
- The provider caches a single native `LlamaModel` handle after first use; parser/helper fields are final but the model lifecycle is not explicitly synchronized
