### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:04:00Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text completions and chat responses using a locally loaded GGUF model via llama.cpp JNI, supporting lazy loading, GPU offloading, and KV-cache reuse.

#### Purpose
- Provides an implementation of `AiGenerationProvider` backed by the native `llama.cpp` library for local AI generation tasks.
- Manages the lifecycle of a `LlamaModel` instance, handling lazy initialization and resource cleanup.

#### Type
- `public final class LlamaCppJniAiGenerationProvider`
- Implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated with `@ToString` (excluding `model` field) and `@Nullable` on `model`.
- Uses Lombok for `toString` generation.

#### Input
- **Constructor**: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- **Methods**: `AiGenerationRequest request` (for `generate`, `generateWithTimings`).
- **Configuration**: Model path, context size, threads, reasoning format, SWA settings, cache reuse, GPU layers, MoE/FFN CPU layers, KV unified slot size, lazy mode, flash attention, cache types, batch sizes, main GPU index, and device selection strings.

#### Output
- **Returns**: `String` (generated text), `AiGenerationTimings` (text + token counts/rates).
- **State**: Lazily initializes and caches `LlamaModel` instance in private field `model`.
- **Side Effects**: Loads native GGUF model into memory, manages native resources via `close()`.

#### Core logic
- **Lazy Model Loading**: The `model()` method checks if `LlamaModel` is null; if so, it constructs `ModelParameters` from `config` and `support`, then instantiates `new LlamaModel(modelParameters)`.
- **Parameter Configuration**: Configures `ModelParameters` based on `LlamaCppJniConfig` settings including:
  - Reasoning format (`ReasoningFormat.AUTO`) to clean content channels.
  - Sliding Window Attention (SWA) full KV cache enablement.
  - Cache reuse thresholds and Flash Attention toggles.
  - Quantization settings for K/V cache types.
  - Batch sizing (batch, ubatch, threadsBatch).
  - Hardware offloading (GPU layers, CPU MoE/FFN layers, main GPU index, specific devices).
  - Lazy tensor loading modes.
- **Response Generation**:
  - Calls `chatComplete` via `ChatResponseParser` to get a `ChatResponse`.
  - Parses completion text using `AiCompletionParser`.
  - Warns on truncated answers and logs prompt cache reuse metrics.
- **Timing Extraction**: Extracts `Timings` object from `ChatResponse` to calculate prompt/predicted tokens per second and cache hit counts (`cache_n`).
- **Resource Cleanup**: `close()` method safely closes the underlying `LlamaModel` if initialized.

#### Public API
- `generate(AiGenerationRequest) -> String` Returns generated text completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` Returns text with performance metrics.
- `close() -> void` Releases native model resources.

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
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` on `generate` (declared but handled internally as unchecked by JNI binding).
- Null checks on `config` and `promptSupport` in constructor.
- Handles missing `Timings` in response by returning zero rates.

#### Concurrency
- Single-threaded access pattern implied by `private @Nullable LlamaModel model` without explicit synchronization.
- Native `LlamaModel` handle management relies on external thread-safety guarantees or single-threaded usage within this provider instance.
