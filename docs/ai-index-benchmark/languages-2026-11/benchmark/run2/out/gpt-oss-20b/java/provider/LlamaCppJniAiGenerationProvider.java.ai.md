### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:58Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel` on first generation.
- Parses chat responses into plain text and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Extends `Object`; no inheritance.  
- Implements `AiGenerationProvider`, `AutoCloseable`.  
- Lombok `@ToString` with exclusions for native handle.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)`, `generateWithTimings(AiGenerationRequest request)`.  
- Internal: `support.buildInferenceParameters(request)`; `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt TPS, predicted tokens, predicted TPS).  
- `close` releases native `LlamaModel`.

#### Core logic
- **Lazy model loading**: `model()` builds `ModelParameters` from `config`, sets GPU/CPU/MoE/FFN layers, KV cache options, flash‑attn, quantization, batch sizes, device selection, then creates `LlamaModel`.  
- **Chat completion**: `chatResponse()` calls `model().chatComplete(...)` with inference parameters, parses full JSON via `ChatResponseParser`.  
- **Completion extraction**: `generate()` obtains first content, warns on truncation, logs cache reuse, parses completion text.  
- **Timing extraction**: `generateWithTimings()` pulls `Timings` from `ChatResponse`, constructs `AiGenerationTimings` with prompt, cache, and predicted metrics.  
- **Resource cleanup**: `close()` calls `LlamaModel.close()` if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text completion.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.  
- Lombok `@ToString`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization.  
- Native `LlamaModel` is reused across calls; thread safety depends on underlying JNI implementation.
