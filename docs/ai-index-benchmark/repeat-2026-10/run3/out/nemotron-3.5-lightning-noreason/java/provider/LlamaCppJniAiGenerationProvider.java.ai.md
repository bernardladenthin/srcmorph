### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:37:07Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates text generation for AI requests using GGUF models via llama.cpp JNI bindings.

#### Purpose
Provides an {@code AiGenerationProvider} implementation that runs GGUF models locally via the llama.cpp JNI library, handling model loading, prompt rendering, and response parsing for code transformation tasks.

#### Type
Class; final; implements {@code AiGenerationProvider}, {@code AutoCloseable}. Extends no class; implements {@code AiGenerationProvider}, {@code AutoCloseable}. Key generics: none. Notable annotations: {@code @ToString} (Lombok, excludes native model handle).

#### Input
- Constructor: {@code LlamaCppJniConfig config}, {@code AiPromptSupport promptSupport}.
- Method {@code generate(AiGenerationRequest)}: request object containing generation parameters.
- Method {@code generateWithTimings(AiGenerationRequest)}: same request type with timing output.
- Lazy-initialization field {@code model}: native handle loaded on first generate call.
- Consumed fields from {@code config}: model path, context size, threads, GPU layers, cache reuse, flash attention, cache type K/V, batch sizes, devices, lazy mode, CPU MoE/FFN layers.
- Consumed fields from {@code promptSupport}: prompt rendering and support logic.

#### Output
- Method {@code generate(AiGenerationRequest)}: returns {@code String} — the parsed completion text.
- Method {@code generateWithTimings(AiGenerationRequest)}: returns {@code AiGenerationTimings} — text plus prompt tokens, cache tokens, prompt/ predicted per-second rates, and predicted token count.
- Method {@code close()}: releases the native {@code LlamaModel} handle; no return value.

#### Core logic
- Lazily initializes and caches the native {@code LlamaModel} on first generate call, configuring model parameters from {@code config} fields (context size, threads, GPU layers, cache reuse, Flash Attention, KV-cache quantization, lazy loading, devices).
- Builds chat template kwargs via {@code support.buildChatTemplateKwargs()} and constructs {@code ModelParameters} with reasoning format, SWA full cache, and layer offloading settings.
- Invokes {@code model.chatComplete(...)} with inference parameters built from the request via {@code support.buildInferenceParameters(request)}.
- Parses the native JSON response using {@code chatResponseParser.parseResponse(...)}.
- Extracts completion text via {@code completionParser.parseCompletion(...)} or full response timing/data via {@code response.getTimings()}.
- On {@code close()}, closes the native model handle if initialized.

#### Public API
- `generate(AiGenerationRequest request) -> String`
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`
- `close() -> void`

#### Dependencies
- `java.io.IOException`
- `java.util.Map`
- `java.util.Objects`
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
- `lombok.ToString`

#### Exceptions / Errors
- Method {@code generate} declares {@code throws IOException} (though internal chat path throws only unchecked exceptions; the declaration is retained for interface compatibility).
- Null checks on {@code config} and {@code promptSupport} in constructor via {@code Objects.requireNonNull}.
- Timings may be null; falls back to zero rates in {@code generateWithTimings}.
- Native model close operation in {@code close()}.

#### Concurrency
- No explicit threading or synchronization; model handle is lazily initialized on first use and assumed single-threaded or externally synchronized.
