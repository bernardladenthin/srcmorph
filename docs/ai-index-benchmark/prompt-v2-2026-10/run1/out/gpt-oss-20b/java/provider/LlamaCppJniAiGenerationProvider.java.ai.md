### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:37:37Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local GGUF model inference via llama.cpp for AI text generation.

#### Purpose
- Implements `AiGenerationProvider` using native llama.cpp.
- Lazily loads and caches a `LlamaModel` for repeated requests.
- Parses chat responses into completions and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`; Lombok `@ToString` with exclusions.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` consume `AiGenerationRequest`.
- Internal `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns a `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt/sec, predicted tokens, predicted/sec).
- `close` releases native `LlamaModel`.

#### Core logic
- Lazy initialization of `LlamaModel` in `model()`: constructs `ModelParameters` with config options (context, threads, GPU layers, KV cache, flash attention, quantization, batch sizes, device selection).
- `generate`: obtains `ChatResponse` via `chatResponse`, warns on truncation, logs cache reuse, parses first content into completion string.
- `chatResponse`: calls `model().chatComplete` with inference parameters from `support.buildInferenceParameters(request)` and parses JSON via `ChatResponseParser`.
- `generateWithTimings`: similar to `generate` but extracts `Timings` from `ChatResponse` and constructs `AiGenerationTimings`.
- `close`: closes the native model if loaded.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> construct provider
- `generate(AiGenerationRequest) -> String` -> produce completion
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` -> produce completion with metrics
- `close() -> void` -> release native resources

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
- Lombok `@ToString`

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying chat path throws unchecked exceptions).
- `model()` may throw runtime exceptions from native binding if parameters invalid.
- `close` handles null model gracefully.

#### Concurrency
- Instance fields are not thread‑safe; `model()` lazily initializes without synchronization—concurrent calls may load multiple models. No explicit synchronization or immutability guarantees.
