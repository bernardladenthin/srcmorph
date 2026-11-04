### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:49Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and exposing a prompt‑based completion API.

#### Purpose
- Implements `AiGenerationProvider` using the `net.ladenthin:llama` JNI binding.
- Lazily loads a GGUF model and runs chat completions, returning plain text or detailed timings.

#### Type
- `final class LlamaCppJniAiGenerationProvider`  
  *implements* `AiGenerationProvider`, `AutoCloseable`  
  *extends* none  
  *annotations*: `@ToString` (Lombok)  
  *fields* (private):  
  - `LlamaCppJniConfig config` – provider configuration  
  - `@Nullable LlamaModel model` – native model handle, lazily initialised  
  - `AiPromptSupport promptSupport` – prompt rendering helper  
  - `LlamaCppJniProviderSupport support` – shared logic for prompt and inference parameters  
  - `AiCompletionParser completionParser` – parses completion text  
  - `ChatResponseParser chatResponseParser` – parses full chat JSON  
  - `Java8CompatibilityHelper compatibilityHelper` – string utilities  

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume `AiGenerationRequest`.  
- `chatResponse(AiGenerationRequest request)` builds inference parameters via `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns `String` completion text.  
- `generateWithTimings` returns `AiGenerationTimings` containing text, prompt token counts, cache usage, and throughput.  
- `close` releases the native `LlamaModel` if loaded.

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` built from `config` and `support.buildChatTemplateKwargs()`.  
  - Sets model path, context size, threads, reasoning format, chat template kwargs.  
  - Conditionally enables SWA full cache, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified per slot, lazy mode, flash attention, KV cache quantization, batch sizes, GPU selection, and device list.  
  - Instantiates `new LlamaModel(modelParameters)` and caches it.  
- `generate(request)`  
  1. Calls `chatResponse(request)` to obtain `ChatResponse`.  
  2. `support.warnOnTruncatedAnswer(request, response)` and `support.logPromptCacheReuse(request, response)` for diagnostics.  
  3. Parses first content via `completionParser.parseCompletion`.  
- `chatResponse(request)`  
  - Calls `model().chatComplete(support.buildInferenceParameters(request))` and parses JSON with `chatResponseParser`.  
- `generateWithTimings(request)`  
  1. Obtains `ChatResponse` via `chatResponse`.  
  2. Extracts text, timings, and constructs `AiGenerationTimings`.  
  3. Handles missing timings by returning zeroed rates.  
- `close()` invokes `current.close()` on the native model if present.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion text with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, `lombok.ToString`.

#### Exceptions / Errors
- Constructor throws `NullPointerException` if `config` or `promptSupport` is null.  
- `generate` declares `IOException` (though underlying chat path throws unchecked).  
- `generateWithTimings` does not throw checked exceptions.  
- `close` handles null model gracefully.

#### Concurrency
- No explicit synchronization; `model()` is not thread‑safe, but the provider is typically used single‑threaded or externally synchronized.  
- Native `LlamaModel` handles its own thread safety.
