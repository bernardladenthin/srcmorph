### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:15:40Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Provides local AI generation using llama.cpp via JNI, loading GGUF models lazily and parsing chat responses.

#### Purpose
- Implements `AiGenerationProvider` for local inference.  
- Extracts timing metrics from chat completions.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Modifiers: `final`.  
- Annotation: `@ToString`.  
- No inheritance.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` → request data.  
- `generateWithTimings(AiGenerationRequest request)` → request data.  
- `close()` → no input.

#### Output
- `generate` returns `String` (generated text).  
- `generateWithTimings` returns `AiGenerationTimings` (text + metrics).  
- `close` returns `void`.  
- `model()` caches and returns `LlamaModel`.  
- `chatResponse` returns `ChatResponse`.  
- Side effects: logs prompt cache reuse, warns on truncated answers, caches model.

#### Core logic
- `model()` lazily loads GGUF model: builds `ModelParameters` from `config`, sets GPU/CPU/MOE/FFN layers, KV cache, flash attention, quantization, batch sizes, device selection, then creates `LlamaModel`.  
- `generate` obtains `ChatResponse` via `chatResponse`, warns on truncation, logs cache reuse, parses first content into completion text.  
- `chatResponse` calls `model().chatComplete` with inference parameters from `support`, parses full JSON response.  
- `generateWithTimings` obtains `ChatResponse`, extracts text and `Timings`; if missing, returns zeroed metrics; otherwise returns metrics including promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond.  
- `close` disposes cached `LlamaModel` if present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` → construct provider.  
- `generate(AiGenerationRequest) -> String` → generate text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` → generate with metrics.  
- `close() -> void` → release native resources.

#### Dependencies
- `java.io.IOException`  
- `java.util.Map`  
- `java.util.Objects`  
- `lombok.ToString`  
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

#### Exceptions / Errors
- `generate` throws `IOException`.  
- `model()` and `chatResponse` may throw unchecked runtime exceptions.  
- `close` throws none.

#### Concurrency
- Lazy initialization of `model` is not synchronized; not thread‑safe.  
- No explicit synchronization or immutability guarantees.
