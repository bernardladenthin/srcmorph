### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:26:48Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text completions using local GGUF models via llama.cpp JNI bindings.

#### Purpose
*   Implements `AiGenerationProvider` interface for source morphing AI tasks.
*   Wraps native llama.cpp model execution with configuration and prompt support.

#### Type
*   `public final class`
*   Implements `AiGenerationProvider`, `AutoCloseable`
*   Annotated `@ToString` (Lombok)
*   Fields annotated `@ToString.Exclude` (native handle)

#### Input
*   `LlamaCppJniConfig` (model path, GPU layers, context size, cache settings)
*   `AiGenerationRequest` (prompt data, request parameters)
*   `AiPromptSupport` (prompt lookup service)

#### Output
*   `String` (generated text completion)
*   `AiGenerationTimings` (performance metrics, token counts)
*   `LlamaModel` (native handle, lazy loaded)
*   `ChatResponse` (parsed JSON response structure)

#### Core logic
*   Lazy initializes `LlamaModel` on first `generate()` call to defer native memory load.
*   Constructs `ModelParameters` from config (GPU layers, KV cache, threads, quantization, Flash Attention).
*   Invokes `LlamaModel.chatComplete()` with built inference parameters.
*   Parses `ChatResponse` to extract content and timings via `ChatResponseParser`.
*   Warns on truncated answers and logs prompt cache reuse metrics.
*   Closes native model handle on `close()` to release resources.

#### Public API
*   `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`
*   `generate(AiGenerationRequest) -> String`
*   `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`
*   `close() -> void`

#### Dependencies
*   `net.ladenthin.llama.LlamaModel`
*   `net.ladenthin.llama.parameters.ModelParameters`
*   `net.ladenthin.llama.value.ChatResponse`
*   `net.ladenthin.llama.value.Timings`
*   `net.ladenthin.llama.json.ChatResponseParser`
*   `net.ladenthin.llama.args.FlashAttn`
*   `net.ladenthin.llama.args.ReasoningFormat`
*   `net.ladenthin.srcmorph.document.AiGenerationRequest`
*   `net.ladenthin.srcmorph.prompt.AiPromptSupport`
*   `net.ladenthin.srcmorph.provider.AiGenerationProvider`
*   `net.ladenthin.srcmorph.provider.AiGenerationTimings`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
*   `net.ladenthin.srcmorph.provider.AiCompletionParser`
*   `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`
*   `java.io.IOException`
*   `java.util.Map`
*   `java.util.Objects`

#### Exceptions / Errors
*   `IOException` thrown by `generate()` (native IO operations).
*   Null checks on `config` and `promptSupport` in constructor.
*   Null `Timings` handled gracefully in `generateWithTimings()` (returns zero rates).

#### Concurrency
*   Lazy initialization of `model` field without explicit synchronization.
*   `LlamaModel` native handle management requires external thread safety.
*   Immutable configuration fields (`config`, `promptSupport`, `support`).
