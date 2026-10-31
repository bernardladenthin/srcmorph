### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:50:55Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local text generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Implements `AiGenerationProvider` for local inference.
- Lazily loads and caches a `LlamaModel` on first generation.
- Parses chat responses into plain text and timing metrics.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Modifiers: final  
- Annotations: `@ToString` (Lombok)  
- Extends: none  
- Implements: `AiGenerationProvider`, `AutoCloseable`

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`  
- `generate(AiGenerationRequest request)`  
- `generateWithTimings(AiGenerationRequest request)`  
- `close()`  
- Internal: `support.buildInferenceParameters(request)`, `support.buildChatTemplateKwargs()`

#### Output
- `generate` returns a `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt TPS, predicted tokens, predicted TPS).  
- `close` releases native resources.

#### Core logic
- **Lazy model loading** (`model()`): builds `ModelParameters` from `config`, sets GPU/CPU layers, KV cache options, flash attention, quantization, batch sizes, device selection, then creates `LlamaModel`.  
- **Chat completion** (`chatResponse`): calls `model().chatComplete(...)`, parses full JSON via `ChatResponseParser`.  
- **Generate**: obtains chat response, warns on truncation, logs cache reuse, extracts first content via `AiCompletionParser`.  
- **Generate with timings**: similar to `generate`, but extracts `Timings` from response and constructs `AiGenerationTimings`.  
- **Close**: calls `close()` on cached `LlamaModel` if present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` → construct provider  
- `String generate(AiGenerationRequest)` → produce completion text  
- `AiGenerationTimings generateWithTimings(AiGenerationRequest)` → produce completion with metrics  
- `void close()` → release native model

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
- `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`  
- `net.ladenthin.srcmorph.provider.AiCompletionParser`  
- `java.io.IOException`  
- `java.util.Map`  
- `java.util.Objects`  
- `org.jspecify.annotations.Nullable`  
- Lombok `@ToString`

#### Exceptions / Errors
- `generate` declares `throws IOException` (though underlying calls are unchecked).  
- `model()` may throw runtime exceptions if native binding fails.  
- `close()` silently ignores null model.

#### Concurrency
- Instance fields are not thread‑safe; `model()` is lazily initialized without synchronization, so concurrent first calls may create multiple models.  
- No explicit synchronization or immutability guarantees.
