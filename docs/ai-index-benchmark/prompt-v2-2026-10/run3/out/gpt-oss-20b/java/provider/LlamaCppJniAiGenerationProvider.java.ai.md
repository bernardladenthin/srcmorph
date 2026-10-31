### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:04:44Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions locally with llama.cpp via JNI, handling chat prompts and timing metrics.

#### Purpose
- Provides AI generation using llama.cpp JNI binding.
- Handles chat prompts, parses responses, and reports timings.

#### Type
`final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable`  
- Modifiers: final, implements.  
- Annotations: `@ToString`, `@ToString.Exclude` on fields.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- Methods:  
  - `generate(AiGenerationRequest request)` – request data.  
  - `generateWithTimings(AiGenerationRequest request)` – request data.  
  - `chatResponse(AiGenerationRequest request)` – request data.  
- Fields used: `config`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, `compatibilityHelper`.

#### Output
- `generate` returns `String` (completion text).  
- `generateWithTimings` returns `AiGenerationTimings`.  
- `chatResponse` returns `ChatResponse`.  
- `model` returns `LlamaModel`.  
- `close` returns `void`.

#### Core logic
- Lazy‑load GGUF model on first `generate` call via `model()`.  
- Build `ModelParameters` from `config` and `support`.  
- Set GPU, CPU, KV cache, flash‑attention, quantization, batch sizes, and device options.  
- Instantiate `LlamaModel`.  
- Execute `chatComplete` with inference parameters from `support`.  
- Parse full JSON response with `ChatResponseParser`.  
- Warn on truncated answers and log cache reuse.  
- Extract completion text via `AiCompletionParser`.  
- Compute and expose timings from `Timings`.  
- Close native model on `close()`.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` – constructor.  
- `generate(AiGenerationRequest) -> String` – produce completion.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `java.io.IOException`, `java.util.Map`, `java.util.Objects`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`.  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.  
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `AiGenerationTimings`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` and `chatResponse()` throw unchecked exceptions only.  
- `close` performs no checked exceptions.

#### Concurrency
- No explicit synchronization; provider is not thread‑safe.  
- `model()` lazily initializes the native handle; concurrent calls may race.  
- `close` safely checks for null before closing.
