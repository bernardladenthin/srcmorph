### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:25Z
- G: 
- A: 0.0.0
- X: file
---
> Executes local GGUF model inference via llama.cpp JNI bindings to generate AI completions for source code transformation requests.

#### Purpose
- Implements `AiGenerationProvider` using the `net.ladenthin:llama` JNI library to run local LLM inference.
- Manages the lifecycle of the native `LlamaModel` handle, including lazy loading and resource cleanup.

#### Type
- `final class` `LlamaCppJniAiGenerationProvider`
- Implements `AiGenerationProvider`, `AutoCloseable`
- Annotated with `@ToString` (Lombok)

#### Input
- `LlamaCppJniConfig config`: Configuration for model path, context size, threads, GPU layers, cache settings, and flash attention.
- `AiPromptSupport promptSupport`: Service for rendering prompt templates.
- `AiGenerationRequest request`: The specific generation request containing prompt data.

#### Output
- `String`: The parsed completion text from the model.
- `AiGenerationTimings`: Object containing generated text and performance metrics (prompt/predicted tokens, cache hits, throughput).
- Side effects: Loads native GGUF model into memory on first use; closes native resources on `close()`.

#### Core logic
- **Lazy Initialization**: The `LlamaModel` is instantiated only on the first `generate` or `generateWithTimings` call via the private `model()` method.
- **Parameter Mapping**: `model()` maps `LlamaCppJniConfig` fields to `ModelParameters`, handling conditional logic for GPU offloading (`gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`), cache reuse (`cacheReuse`, `swaFull`), and attention settings (`flashAttn`, `cacheTypeK/V`).
- **Inference Execution**: `chatResponse()` builds inference parameters via `LlamaCppJniProviderSupport` and calls `LlamaModel.chatComplete()`.
- **Response Parsing**: Uses `ChatResponseParser` to parse the raw JSON response into a `ChatResponse` object, extracting content, finish reasons, and timing data.
- **Timing Extraction**: `generateWithTimings` extracts `Timings` from the response, defaulting to zero rates if timing data is absent, and surfaces KV cache hit counts (`cacheN`).
- **Completion Parsing**: `generate` parses the first content block using `AiCompletionParser` to return clean text.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void` : Initializes provider with config.
- `generate(AiGenerationRequest) -> String` : Returns generated text completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` : Returns text and performance metrics.
- `close() -> void` : Releases native model resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Timings`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
- `net.ladenthin.srcmorph.provider.AiCompletionParser`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `generate` throws `IOException` (declared in interface, though native path throws unchecked).
- `Objects.requireNonNull` used in constructor to fail fast on null `config` or `promptSupport`.
- Null check on `Timings` in `generateWithTimings` to handle responses lacking performance data.

#### Concurrency
- Not thread-safe; `LlamaModel` is a mutable field updated during lazy initialization.
- `AutoCloseable` ensures native resources are released, preventing memory leaks in long-running processes.
