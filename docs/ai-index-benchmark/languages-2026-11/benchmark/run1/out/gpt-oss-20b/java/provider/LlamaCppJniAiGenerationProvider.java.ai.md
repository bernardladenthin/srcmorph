### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:47Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel` on first generation.
- Parses chat responses into plain completions and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- @ToString (excludes native handle)  
- Fields: `LlamaCppJniConfig config`, `@Nullable LlamaModel model`, `AiPromptSupport promptSupport`, `LlamaCppJniProviderSupport support`, `AiCompletionParser completionParser`, `ChatResponseParser chatResponseParser`, `Java8CompatibilityHelper compatibilityHelper`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` receive `AiGenerationRequest`.  
- `chatResponse(AiGenerationRequest request)` uses `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt TPS, predicted tokens, predicted TPS).  
- `close` releases native `LlamaModel`.

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` built from `config` and `support`.  
- `generate` obtains `ChatResponse`, warns on truncation, logs cache reuse, parses first content.  
- `chatResponse` runs `model().chatComplete(...)` and parses full JSON.  
- `generateWithTimings` extracts timings from `ChatResponse` and builds `AiGenerationTimings`.  
- `close` disposes native model if present.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization.  
- `model` is lazily initialized without double‑checked locking; safe for single‑threaded context.
