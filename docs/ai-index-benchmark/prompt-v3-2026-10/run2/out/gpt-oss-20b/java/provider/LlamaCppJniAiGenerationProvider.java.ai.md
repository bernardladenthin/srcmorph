### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:18:40Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a native `LlamaModel`.
- Parses chat responses into completions and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Lombok `@ToString` (excludes native handle).  
- Fields: `LlamaCppJniConfig config`, `@Nullable LlamaModel model`, `AiPromptSupport promptSupport`, `LlamaCppJniProviderSupport support`, `AiCompletionParser completionParser`, `ChatResponseParser chatResponseParser`, `Java8CompatibilityHelper compatibilityHelper`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)`, `generateWithTimings(AiGenerationRequest request)` use request data.  
- `model()` builds `ModelParameters` from `config` and `support.buildChatTemplateKwargs()`.

#### Output
- `generate` returns a `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, rates).  
- `close` releases native `LlamaModel`.

#### Core logic
- **Lazy model loading**: `model()` constructs `ModelParameters` from config, sets GPU/CPU layers, KV cache options, flash‑attn, quantization, batch sizes, device selection, then creates `LlamaModel`.  
- **Chat completion**: `chatResponse` calls `model().chatComplete(...)` with inference parameters from `support.buildInferenceParameters(request)`, parses full JSON via `ChatResponseParser`.  
- **Completion extraction**: `generate` parses first content via `AiCompletionParser`.  
- **Timing extraction**: `generateWithTimings` pulls `Timings` from `ChatResponse`, builds `AiGenerationTimings`.  
- **Resource cleanup**: `close` invokes `LlamaModel.close()` if loaded.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructs provider.  
- `String generate(AiGenerationRequest)` -> text completion.  
- `AiGenerationTimings generateWithTimings(AiGenerationRequest)` -> completion + metrics.  
- `void close()` -> release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying chat path throws unchecked).  
- `model()` may throw runtime exceptions from native binding.  
- `close` handles null safely.

#### Concurrency
- Single‑threaded usage implied; no explicit synchronization.  
- `model` field is lazily initialized without double‑checked locking; safe for single‑threaded context.
