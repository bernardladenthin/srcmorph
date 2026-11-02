### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:41:14Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI generation via llama.cpp JNI, handling chat completions, timing metrics, and native resource cleanup.

#### Purpose
- Implements AI generation using llama.cpp.
- Handles chat completions, timing extraction, and cache reuse diagnostics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Modifiers: final.  
- Annotation: `@ToString`.  
- Extends: none.  
- Implements: `AiGenerationProvider`, `AutoCloseable`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest`.  
- Internal: uses `LlamaCppJniProviderSupport` to build inference parameters.

#### Output
- `generate` returns `String` (completion text).  
- `generateWithTimings` returns `AiGenerationTimings` (text + metrics).  
- `close` releases native `LlamaModel` handle.

#### Core logic
- **Lazy model load**: `model()` builds `ModelParameters` from config, sets GPU, CPU, KV, flash‑attn, quantization, batch sizes, etc., then creates `LlamaModel`.  
- **Chat completion**: `chatResponse()` calls `model().chatComplete(...)`, parses JSON via `ChatResponseParser`.  
- **Generate**: obtains chat response, warns on truncation, logs cache reuse, parses completion text.  
- **Generate with timings**: extracts `Timings` from response, constructs `AiGenerationTimings`.  
- **Close**: calls `current.close()` if model exists.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor.  
- `generate(AiGenerationRequest) -> String` – produce completion.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, `java.io.IOException`, `java.util.Map`, `java.util.Objects`, `lombok.ToString`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `chatResponse` and `generateWithTimings` rely on unchecked exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- Lazy initialization of `model` is not synchronized; intended for single‑threaded use.  
- No explicit thread‑safety guarantees.
