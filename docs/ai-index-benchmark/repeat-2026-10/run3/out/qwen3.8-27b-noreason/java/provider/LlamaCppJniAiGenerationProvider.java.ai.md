### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:47:34Z
- G: 
- A: 0.0.0
- X: file
---
> Executes local GGUF model inference via llama.cpp JNI bindings to generate AI text completions for source-morphing requests.

#### Purpose
- Implements `AiGenerationProvider` using the `net.ladenthin:llama` JNI library to run local LLM inference.
- Manages the lifecycle of the native `LlamaModel` handle, including lazy loading and resource cleanup.

#### Type
- `final class` `LlamaCppJniAiGenerationProvider`
- `implements` `AiGenerationProvider`, `AutoCloseable`
- Annotated with `@ToString` (Lombok)

#### Input
- `LlamaCppJniConfig`: Configuration for model path, context size, threads, GPU layers, cache settings, and flash attention.
- `AiPromptSupport`: Service for rendering prompt templates.
- `AiGenerationRequest`: The specific generation request containing prompt data.

#### Output
- `String`: The parsed text completion from the model.
- `AiGenerationTimings`: Detailed inference metrics including prompt/predicted token counts, cache hits, and throughput rates.
- Side effects: Loads native GGUF model into memory; closes native resources on `close()`.

#### Core logic
- **Lazy Initialization**: The `LlamaModel` is instantiated only on the first `generate` call to avoid loading the ~90MB model if all files are cached.
- **Parameter Mapping**: `model()` maps `LlamaCppJniConfig` fields to `ModelParameters`, handling conditional logic for GPU offloading, MoE/FFN CPU layers, KV cache quantization, and flash attention.
- **Inference Execution**: `chatResponse()` builds inference parameters via `LlamaCppJniProviderSupport` and calls `model().chatComplete()`.
- **Response Parsing**: Uses `ChatResponseParser` to extract content, finish reasons, and timing data from the OpenAI-compatible JSON response.
- **Timing Extraction**: `generateWithTimings` extracts `Timings` from the response, defaulting to zero rates if timing data is absent.
- **Resource Management**: `close()` safely closes the native `LlamaModel` handle if it has been initialized.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void` : Initializes provider with config and prompt support.
- `generate(AiGenerationRequest) -> String` : Returns parsed text completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` : Returns text and inference performance metrics.
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
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `lombok.ToString`

#### Exceptions / Errors
- `IOException`: Declared in `generate` but noted as effectively unchecked in the chat path.
- `NullPointerException`: Prevented via `Objects.requireNonNull` in constructor for `config` and `promptSupport`.
- Null handling: `model` field is `@Nullable`; `Timings` null check in `generateWithTimings` returns zeroed metrics.

#### Concurrency
- Not thread-safe: `model` field is mutated during lazy initialization without synchronization.
- Native handle `LlamaModel` is shared across calls; concurrent `generate` calls may conflict during initialization or inference.
