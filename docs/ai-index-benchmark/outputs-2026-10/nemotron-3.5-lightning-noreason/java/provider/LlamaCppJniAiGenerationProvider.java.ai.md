### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:52:19Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates text generation for AI-augmented source code transformations using GGUF models via llama.cpp JNI bindings.

#### Purpose
An {@code AiGenerationProvider} implementation that runs local GGUF models through the llama.cpp JNI binding to produce text completions and structured chat responses for source code transformation tasks.

#### Type
Class; final; implements {@code AiGenerationProvider}, {@code AutoCloseable}. Extends no class; implements {@code AiGenerationProvider}, {@code AutoCloseable}. Key generics: none. Notable annotations: {@lombok @ToString} (excludes native model handle and support field).

#### Input
- Constructor: {@code LlamaCppJniConfig config}, {@code AiPromptSupport promptSupport} — configuration and prompt lookup.
- Method {@code generate(AiGenerationRequest)}: request payload containing transformation instructions.
- Method {@code generateWithTimings(AiGenerationRequest)}: same request payload with timing requirements.
- Lazy-initialization trigger: first call to {@code model()}, {@code chatComplete}, or {@code chatResponse}.

#### Output
- Method {@code generate}: returns {@code String} — the parsed completion text.
- Method {@code generateWithTimings}: returns {@code AiGenerationTimings} — text plus prompt/cache/predicted token counts and per-second rates.
- Method {@code close}: releases the native {@code LlamaModel} handle; no return value.

#### Core logic
- Lazily loads and caches a native {@code LlamaModel} on first generation using configured {@code LlamaCppJniConfig} parameters (model path, context size, threads, GPU layers, MoE/FFN layer offloading, KV-cache settings, Flash Attention, device selection).
- Builds chat template kwargs and model parameters from config, then instantiates {@code new LlamaModel(modelParameters)}.
- Executes native chat completion via {@code model().chatComplete(inferenceParameters)} and parses the JSON response through {@code ChatResponseParser}.
- Parses the first content fragment via {@code AiCompletionParser} for {@code generate} or extracts timing data via {@code response.getTimings()} for {@code generateWithTimings}.
- Warns on truncated answers and logs prompt cache reuse events through {@code LlamaCppJniProviderSupport}.

#### Public API
- `generate(AiGenerationRequest request) -> String`
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`
- `close() -> void`

#### Dependencies
- `java.io.IOException`
- `java.util.Map`, `java.util.Objects`
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
- Declares {@code throws IOException} on {@code generate} (though chat path throws only unchecked exceptions).
- Handles null timings by returning zero rates in {@code AiGenerationTimings}.
- No explicit null-checking on request payload beyond what support methods perform.

#### Concurrency
- No synchronization or immutability annotations; native model handle is lazily initialized on first use, which is not thread-safe without external synchronization.
