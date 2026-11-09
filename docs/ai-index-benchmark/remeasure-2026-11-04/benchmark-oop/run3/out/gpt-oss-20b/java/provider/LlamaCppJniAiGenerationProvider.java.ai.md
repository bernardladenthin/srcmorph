### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:55:13Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Provides local AI text generation by loading a GGUF model via llama.cpp and exposing a prompt‑based completion API.

#### Purpose
- Loads a GGUF model lazily and caches the native handle.
- Generates text completions from `AiGenerationRequest` using llama.cpp.
- Parses full chat responses, including timings and cache metrics.

#### Type
- `final class LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider`, `AutoCloseable`.  
  - Extends `Object`.  
  - Annotations: `@ToString`.  
  - Fields:  
    - `private final LlamaCppJniConfig config` – configuration.  
    - `@ToString.Exclude private @Nullable LlamaModel model` – native handle.  
    - `private final AiPromptSupport promptSupport` – prompt lookup.  
    - `@ToString.Exclude private final LlamaCppJniProviderSupport support` – helper.  
    - `private final AiCompletionParser completionParser` – parses completion text.  
    - `private final ChatResponseParser chatResponseParser` – parses chat JSON.  
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper for string checks.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` accept `AiGenerationRequest`.  
- `chatResponse(AiGenerationRequest request)` builds inference parameters via `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` containing text, prompt tokens, cache tokens, prompt throughput, predicted tokens, predicted throughput.  
- `close` releases native `LlamaModel`.

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` built from `config` and `support.buildChatTemplateKwargs()`.  
- Configures GPU layers, CPU MoE/FFN layers, KV cache settings, flash attention, quantization, batch sizes, device selection.  
- `generate` calls `chatResponse`, warns on truncation, logs cache reuse, parses completion.  
- `chatResponse` invokes `model().chatComplete` with inference parameters, parses JSON via `chatResponseParser`.  
- `generateWithTimings` extracts timings from `ChatResponse` and constructs `AiGenerationTimings`.  
- `close` calls `model.close()` if model exists.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, `lombok.ToString`.

#### Exceptions / Errors
- Constructor throws `NullPointerException` if `config` or `promptSupport` is null.  
- `generate` declares `throws IOException` (though underlying chat path throws unchecked).  
- `model()` may throw runtime exceptions from native binding if parameters invalid.

#### Concurrency
- No explicit synchronization; native `LlamaModel` is assumed thread‑safe or used single‑threaded.  
- Lazy initialization of `model` is not synchronized; potential race if multiple threads call `generate` concurrently.
