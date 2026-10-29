### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:22Z
- G: 
- A: 0.0.0
- X: file
---
> Executes local AI text generation using GGUF models via llama.cpp JNI, supporting lazy model loading, KV-cache reuse, and GPU offloading.

#### Purpose
- Provides a Java implementation for generating AI responses (text completion/chat) using the `llama.cpp` native library through JNI bindings.
- Manages the lifecycle of the native `LlamaModel`, including lazy initialization to minimize startup overhead and resource usage.

#### Type
- `public final class LlamaCppJniAiGenerationProvider`
- Implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated with `@ToString` (excluding `model` field) and `@Nullable` on fields.
- Key dependencies: `LlamaModel`, `AiGenerationRequest`, `LlamaCppJniConfig`.

#### Input
- **Constructor**: `LlamaCppJniConfig config` (model path, context size, threads, GPU layers, etc.), `AiPromptSupport promptSupport`.
- **Methods**: `AiGenerationRequest request` (containing prompts and parameters).
- **Configuration**: Reads settings for model path, context size, thread count, reasoning format, SWA cache, KV cache reuse, GPU offloading (`gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`), Flash Attention, and device selection.

#### Output
- **Return Types**: `String` (generated text), `AiGenerationTimings` (performance metrics like tokens/sec, cache hits).
- **State**: Lazily initializes and caches the private `LlamaModel` instance.
- **Side Effects**: Logs prompt cache reuse warnings; closes native resources on `close()`.

#### Core logic
- **Lazy Model Loading**: The `model()` method checks if the native `LlamaModel` is null; if so, it constructs a `ModelParameters` object based on `config` and instantiates `new LlamaModel(modelParameters)`.
- **Parameter Mapping**: Translates configuration flags into specific `ModelParameters` settings:
  - Sets `ReasoningFormat.AUTO` to clean up reasoning channels.
  - Enables `SwaFull` or sets `CacheReuse` based on config for KV prefix reuse.
  - Configures GPU/CPU layer distribution (`setGpuLayers`, `setCpuMoeLayers`, `setCpuFfnLayers`).
  - Handles Flash Attention, KV quantization types, batch sizes, and device selection.
- **Generation Flow**:
  - `generate()`: Calls `chatResponse()` to get a `ChatResponse`, parses the first content via `AiCompletionParser`, and warns on truncation.
  - `generateWithTimings()`: Retrieves `ChatResponse` and extracts `Timings` (prompt/predicted tokens, throughput) to construct an `AiGenerationTimings` object.
- **Resource Management**: `close()` safely shuts down the native `LlamaModel` handle if it has been initialized.

#### Public API
- `generate(AiGenerationRequest) -> String` Generates text response from a request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` Generates text with performance metrics.
- `close() -> void` Closes the native model handle.

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
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IOException` from `generate()` (declared but handled internally as unchecked in chat path).
- Null checks on `config` and `promptSupport` in constructor.
- Handles missing `Timings` by returning zero values instead of throwing.

#### Concurrency
- Single-threaded design for model access; `model` field is lazily initialized within a synchronized-like check pattern (though not explicitly synchronized, relies on single-threaded usage or safe publication guarantees of the JVM).
- No explicit synchronization blocks visible; assumes caller manages concurrency or uses single-threaded execution.
