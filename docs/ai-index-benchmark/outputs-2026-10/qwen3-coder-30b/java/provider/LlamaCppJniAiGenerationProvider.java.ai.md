### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:15:06Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation using GGUF models via JNI bindings, supporting chat completions and timing metrics.

#### Purpose
- Wraps JNI-based llama.cpp model for local inference.
- Handles GGUF model loading and caching.
- Parses AI responses and provides timing information.

#### Type
Final class implementing `AiGenerationProvider` and `AutoCloseable`. Uses Lombok `@ToString` with `@ToString.Exclude` for native model handle.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`.
- `generate()` and `generateWithTimings()` take `AiGenerationRequest`.
- Configuration values include model path, context size, threads, GPU layers, cache reuse, flash attention, and more.

#### Output
- `generate()` returns generated text string.
- `generateWithTimings()` returns `AiGenerationTimings` with prompt/predicted token counts and throughput.
- Side effect: loads GGUF model on first use.

#### Core logic
- Lazily initializes native `LlamaModel` on first `generate()` call.
- Builds `ModelParameters` from config and loads model with specified options (GPU layers, KV cache, flash attention).
- Constructs inference parameters from request and prompt support.
- Executes chat completion and parses full response including timings and cache stats.
- Closes native model handle during `close()`.

#### Public API
- `generate(AiGenerationRequest) -> String`: Generates text from request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Generates text with performance metrics.
- `close() -> void`: Releases native model resources.

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

#### Exceptions / Errors
- Throws `IOException` from `generate()` method.
- Null checks on constructor parameters.
- Uses `Objects.requireNonNull()` for validation.

#### Concurrency
- Not thread-safe; model handle is not synchronized.
- Lazy initialization ensures single model instance per provider.
