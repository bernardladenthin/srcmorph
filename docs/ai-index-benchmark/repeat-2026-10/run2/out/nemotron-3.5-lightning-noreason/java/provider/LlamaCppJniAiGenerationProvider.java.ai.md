### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:45Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates text generation for AI-augmented source files using GGUF models via llama.cpp JNI bindings.

#### Purpose
Provides an {@code AiGenerationProvider} implementation that runs local GGUF models through the llama.cpp JNI bridge for AI-assisted source code transformations.

#### Type
Class; final; implements {@code AiGenerationProvider}, {@code AutoCloseable}. Extends no class; implements {@code AiGenerationProvider}, {@code AutoCloseable}. Key generics: none. Notable annotations: {@code @ToString} (Lombok, excludes native model handle); {@code @Nullable} on model field.

#### Input
Constructor requires: {@code LlamaCppJniConfig config} (llama.cpp configuration), {@code AiPromptSupport promptSupport} (prompt lookup). Consumed fields: model path, context size, thread count, GPU layers, cache reuse settings, flash attention flag, KV quantization type, batch sizes, device names, lazy mode, Moe/FFN layer settings. Injected dependency: {@code Java8CompatibilityHelper compatibilityHelper}.

#### Output
Method {@code generate(AiGenerationRequest)} returns {@code String} (completed text). Method {@code generateWithTimings(AiGenerationRequest)} returns {@code AiGenerationTimings} (text plus token counts and per-second rates). Method {@code close()} closes the native model handle. Returned types: {@code ChatResponse} (from parser), {@code Timings} (prompt/cache/per-second metrics).

#### Core logic
- Lazily initializes native {@code LlamaModel} on first generate() call using configured parameters (model path, context size, threads, GPU layers, cache reuse, Flash Attention, KV quantization, MoE/FFN layer offloading, device selection).
- Builds chat template kwargs and model parameters from config, enabling SWA full cache, cross-request KV prefix reuse, and lazy tensor loading when configured.
- Executes model inference via {@code model().chatComplete(...)} with parameters built from request and prompt support.
- Parses full JSON response via {@code ChatResponseParser} to extract content, finish reason, token usage, and KV-cache timings.
- Returns parsed text via {@code AiCompletionParser} or wraps it with timing data.

#### Public API
- `generate(AiGenerationRequest request) -> String`
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`
- `close() -> void`
- `model() -> LlamaModel` (private, returns cached native handle)

Total: 3 public members.

#### Dependencies
Imports: java.io.IOException, java.util.Map, java.util.Objects, lombok.ToString, net.ladenthin.llama.*, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable.
Referenced types: LlamaModel, FlashAttn, ReasoningFormat, ChatResponseParser, ModelParameters, ChatResponse, Timings, AiGenerationProvider, AiCppJniConfig, AiPromptSupport, LlamaCppJniProviderSupport, AiCompletionParser, Java8CompatibilityHelper.

#### Exceptions / Errors
Throws IOException on generate(). chatComplete() and parse operations throw only unchecked exceptions; IOException declaration on generate() is a compatibility shim. Null-handling: config and promptSupport validated for null in constructor; model may be null before lazy initialization.

#### Concurrency
No explicit synchronization; model is lazily initialized via single-threaded check-then-act pattern. Native handle is not thread-safe for concurrent generation; typical usage assumes single-threaded or externally synchronized access.
