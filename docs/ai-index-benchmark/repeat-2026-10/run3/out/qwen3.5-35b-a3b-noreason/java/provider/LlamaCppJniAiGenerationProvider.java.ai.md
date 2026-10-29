### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:43:01Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text completions and chat responses using locally loaded GGUF models via llama.cpp JNI.

#### Purpose
- Implements AI text generation by loading and running GGUF models locally via the llama.cpp native library.
- Provides both standard text generation and detailed performance timing metrics for inference requests.

#### Type
- `public final class LlamaCppJniAiGenerationProvider`
- Implements `AiGenerationProvider`, `AutoCloseable`
- Annotated with `@ToString` (Lombok) and `@ToString.Exclude` for native model handle
- Generics: None

#### Input
- `LlamaCppJniConfig config`: Configuration for model path, context size, threads, GPU layers, and caching strategies.
- `AiPromptSupport promptSupport`: Service for resolving and rendering prompt templates.
- `AiGenerationRequest request`: Contains the user prompt and generation parameters.
- Internal: `LlamaModel` native handle (lazy-loaded), `AiCompletionParser`, `ChatResponseParser`.

#### Output
- `String`: Generated text content from the model response.
- `AiGenerationTimings`: Object containing token counts (prompt, cache, predicted) and throughput rates (tokens/sec).
- `void`: Closes the native `LlamaModel` handle on resource cleanup.
- Side effects: Lazy initialization of the native model context and KV cache population.

#### Core logic
- **Lazy Model Loading**: `model()` method checks if the native `LlamaModel` is initialized; if null, constructs `ModelParameters` from `config` and instantiates `LlamaModel`.
- **Parameter Configuration**: Configures `ModelParameters` with specific settings including:
  - Model path, context size, thread count.
  - Reasoning format (AUTO) and chat template kwargs.
  - Sliding Window Attention (SWA) full KV cache enablement.
  - KV cache reuse thresholds and Flash Attention settings.
  - GPU offloading layers, CPU MoE/FFN layer retention, and unified KV slot limits.
  - Lazy tensor loading, batch sizes, and specific device/GPU selection.
- **Inference Execution**: `chatResponse()` invokes `model().chatComplete()` with built inference parameters and parses the JSON response into a `ChatResponse`.
- **Response Processing**:
  - `generate()`: Parses the first content of the `ChatResponse` into a completion string.
  - `generateWithTimings()`: Extracts `Timings` from the response to calculate prompt/cache/predicted token counts and per-second rates.
- **Resource Management**: `close()` safely closes the native `LlamaModel` handle if it exists.

#### Public API
- `generate(AiGenerationRequest) -> String` Generates text completion for the request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` Generates text with detailed performance metrics.
- `close() -> void` Releases the native model handle and resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`: Native JNI model handle.
- `net.ladenthin.llama.args.FlashAttn`: Flash attention configuration enum.
- `net.ladenthin.llama.args.ReasoningFormat`: Reasoning channel format enum.
- `net.ladenthin.llama.json.ChatResponseParser`: JSON response parser.
- `net.ladenthin.llama.parameters.ModelParameters`: Native model configuration builder.
- `net.ladenthin.llama.value.ChatResponse`: Parsed inference response object.
- `net.ladenthin.llama.value.Timings`: Inference performance metrics.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`: Input request DTO.
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`: Prompt template resolver.
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`: Configuration holder.
- `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`: Helper for parameter building.
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`: Interface contract.
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`: Timing result DTO.
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`: Utility for string checks.
- `org.jspecify.annotations.Nullable`: Nullability annotation.

#### Exceptions / Errors
- `IOException`: Declared on `generate()` and `generateWithTimings()` (though implementation notes suggest only unchecked exceptions are thrown internally).
- `NullPointerException`: Guarded against `config` and `promptSupport` in constructor via `Objects.requireNonNull`.
- Null handling: `model` field is nullable; `timings` in `generateWithTimings` defaults to zero rates if null.

#### Concurrency
- Thread safety not explicitly guaranteed; `model` field is lazily initialized without synchronization, implying single-threaded usage or external synchronization is required for concurrent access to the native handle.
- `LlamaModel` native handle is not thread-safe by default in llama.cpp.
