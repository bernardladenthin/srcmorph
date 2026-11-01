### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:49Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel` on first generation.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- @ToString (Lombok)  
- @ToString.Exclude on `model` and `support`

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`  
- `generate(AiGenerationRequest request)`  
- `generateWithTimings(AiGenerationRequest request)`  
- `close()`  
- Internal: `support.buildInferenceParameters(request)`, `support.buildChatTemplateKwargs()`

#### Output
- `generate` returns generated text `String`.  
- `generateWithTimings` returns `AiGenerationTimings` with text, token counts, and throughput.  
- `close` releases native `LlamaModel` resources.

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` built from `config` and `support`.  
- `generate` obtains a `ChatResponse`, warns on truncation, logs cache reuse, parses completion text.  
- `chatResponse` runs `model().chatComplete(...)` and parses full JSON via `ChatResponseParser`.  
- `generateWithTimings` extracts timings from `ChatResponse` and builds `AiGenerationTimings`.  
- `close` disposes the native model if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce text + metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `lombok.ToString`, `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization.  
- `model` is lazily initialized without double‑checked locking; safe for single‑threaded context.
