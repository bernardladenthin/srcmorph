### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:26:18Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local, llama.cpp‑backed AI text generation with configurable GPU, KV‑cache, and prompt‑reuse settings.

#### Purpose
- Implements `AiGenerationProvider` for local GGUF model inference.
- Lazily loads and caches a `LlamaModel` on first generation.
- Parses chat responses into completions and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`
- Modifiers: final
- Annotations: `@ToString`

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`
- `generate(AiGenerationRequest request)`
- `generateWithTimings(AiGenerationRequest request)`
- `close()`

#### Output
- `generate` returns `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt/cache stats, and throughput.
- `close` releases native `LlamaModel` resources.

#### Core logic
- `model()` lazily builds `LlamaModel` with `ModelParameters` derived from `config` and `support`.
- `generate` obtains a `ChatResponse`, warns on truncation, logs cache reuse, and parses the first content into a completion string.
- `chatResponse` runs `model().chatComplete` with inference parameters from `support` and parses the JSON response.
- `generateWithTimings` extracts text and `Timings` from the parsed `ChatResponse`; if missing, returns zeroed metrics.
- `close` calls `LlamaModel.close()` if the model was loaded.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructs provider
- `generate(AiGenerationRequest) -> String` -> produce completion
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` -> produce completion with metrics
- `close()` -> release native resources

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
- `generate` declares `throws IOException` (though underlying chat path throws unchecked).
- `model()` may throw runtime exceptions if native binding fails.
- `close` silently ignores if model is null.

#### Concurrency
- Instance fields are not thread‑safe; `model()` is lazily initialized without synchronization, so concurrent calls may load the model multiple times. No explicit synchronization or immutability guarantees.
