### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:45Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Provides local GGUF model inference via llama.cpp JNI, exposing AI generation and timing APIs.

#### Purpose
- Supplies AI generation for source‑morph requests.
- Exposes timing metrics for model inference.

#### Type
`final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`; Lombok `@ToString` with exclusions; fields: `config`, `model`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, `compatibilityHelper`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)`, `generateWithTimings(AiGenerationRequest request)` consume `AiGenerationRequest`.
- `model()` lazily loads model using `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns `String` completion.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt tokens, cache tokens, prompt/sec, predicted tokens, predicted/sec.
- `close` releases native `LlamaModel`.

#### Core logic
- Lazy‑load `LlamaModel` on first `generate`/`chatResponse` call.
- Build `ModelParameters` from `config` with extensive GPU, KV, cache, and flash‑attention options.
- `chatResponse` runs `model().chatComplete` with inference parameters from `support.buildInferenceParameters(request)` and parses full JSON via `ChatResponseParser`.
- `generate` extracts first content, warns on truncation, logs cache reuse, parses completion text.
- `generateWithTimings` extracts timings from parsed `ChatResponse`, constructs `AiGenerationTimings`.
- `close` invokes native `LlamaModel.close()` if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.
- `close() -> void` – release native resources.

#### Dependencies
- `java.io.IOException`, `java.util.Map`, `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`, `ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`, `Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `generate` declares `throws IOException` (checked from request handling).
- `model()` may throw unchecked runtime exceptions from native calls.
- `close` silently ignores if model not loaded.

#### Concurrency
- Single‑threaded usage implied; `model` field is lazily initialized without synchronization, assuming single‑threaded provider creation. No explicit thread‑safety guarantees.
