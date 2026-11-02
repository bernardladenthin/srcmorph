### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:52Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Provides local GGUF model inference via llama.cpp JNI for AI text generation.

#### Purpose
- Supplies AI generation service using llama.cpp.
- Implements `AiGenerationProvider` and `AutoCloseable`.

#### Type
`final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider`, `AutoCloseable`; Lombok `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest)` / `generateWithTimings(AiGenerationRequest)` receive `AiGenerationRequest`.
- `close()` has no parameters.

#### Output
- `generate` returns `String` completion.
- `generateWithTimings` returns `AiGenerationTimings`.
- `close` releases native `LlamaModel`.

#### Core logic
- Lazy load `LlamaModel` on first `generate`/`chatResponse` call via `model()`.
- Build `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.
- Configure GPU, KV cache, flash attention, quantization, batch sizes, device selection.
- `generate` runs chat completion, warns on truncation, logs cache reuse, parses completion text.
- `chatResponse` executes native `chatComplete` and parses full JSON response.
- `generateWithTimings` extracts timings from `ChatResponse` and constructs `AiGenerationTimings`.
- `close` disposes native model if loaded.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> construct provider  
- `generate(AiGenerationRequest) -> String` -> produce text  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` -> produce text + metrics  
- `close() -> void` -> release resources  

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
- `lombok.ToString`

#### Exceptions / Errors
- `generate` throws `IOException`.
- `model()` may throw unchecked exceptions from native calls.
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; native `LlamaModel` not thread‑safe. No explicit synchronization.
