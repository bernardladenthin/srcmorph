### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:57:56Z
- G: 
- A: 0.0.0
- X: file
---
> Executes local AI code generation by invoking a GGUF model through the llama.cpp JNI binding and parsing the resulting chat completion.

#### Purpose
- Implements `AiGenerationProvider` to run local LLM inference using the `net.ladenthin:llama` JNI library.
- Manages the lifecycle of the native `LlamaModel` handle, including lazy initialization and resource cleanup.

#### Type
- `final class` `LlamaCppJniAiGenerationProvider`
- `implements AiGenerationProvider, AutoCloseable`
- Annotated with `@ToString` (Lombok)

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`
- Methods: `AiGenerationRequest request`
- Consumes configuration values from `LlamaCppJniConfig` (model path, context size, GPU layers, cache settings, etc.)

#### Output
- `generate`: `String` (parsed completion text)
- `generateWithTimings`: `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt speed, predicted tokens, predicted speed)
- `close`: `void` (releases native resources)
- Mutates internal `model` field (lazy initialization)

#### Core logic
- **Lazy Model Initialization**: `model()` method initializes `LlamaModel` on first use to avoid loading ~90MB GGUF + native context if all files are cached.
- **Parameter Configuration**: Builds `ModelParameters` from `LlamaCppJniConfig`, handling:
    - Reasoning format (`ReasoningFormat.AUTO`)
    - Sliding Window Attention (SWA) full KV cache
    - Cross-request KV prefix reuse
    - GPU layer offloading (`gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`)
    - KV context budget (`kvUnifiedPerSlot`)
    - Lazy tensor loading (`lazyMode`)
    - Flash Attention (`FlashAttn.ON`)
    - KV cache quantization (`cacheTypeK`, `cacheTypeV`)
    - Prefill sizing (`batchSize`, `ubatchSize`, `threadsBatch`)
    - GPU device selection (`mainGpu`, `devices`)
- **Inference Execution**: `chatResponse()` calls `model().chatComplete()` with inference parameters built by `LlamaCppJniProviderSupport`.
- **Response Parsing**: Uses `ChatResponseParser` to parse OpenAI-compatible JSON response, extracting content, finish reason, token usage, and timings.
- **Timings Extraction**: `generateWithTimings()` extracts `Timings` from `ChatResponse`, handling null timings by returning zero rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> `void` (constructor)
- `generate(AiGenerationRequest)` -> `String` (generates completion text)
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` (generates with performance metrics)
- `close()` -> `void` (releases native model handle)

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
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
- `net.ladenthin.srcmorph.provider.AiCompletionParser`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` (declared in interface, but implementation notes it's unchecked in practice)
- Null handling: `Objects.requireNonNull` in constructor for `config` and `promptSupport`
- Null check in `close()` before closing model
- Null check in `generateWithTimings()` for `Timings` object

#### Concurrency
- Not thread-safe: `model` field is lazily initialized without synchronization
- `LlamaModel` handle is shared across calls; native library likely handles internal synchronization
