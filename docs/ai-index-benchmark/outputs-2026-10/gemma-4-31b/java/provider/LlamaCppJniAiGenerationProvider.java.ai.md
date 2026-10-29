### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:31:20Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation by interfacing with local GGUF models via llama.cpp JNI bindings.

#### Purpose
* Implements `AiGenerationProvider` to execute LLM inference locally.
* Manages the lifecycle and configuration of a native `LlamaModel` handle.

#### Type
Final class; implements `AiGenerationProvider`, `AutoCloseable`. Annotated with Lombok `@ToString`.

#### Input
* `LlamaCppJniConfig`: Configuration for model path, context size, threads, GPU/CPU layer offloading, and cache settings.
* `AiPromptSupport`: Support for rendering request prompts.
* `AiGenerationRequest`: Parameters for a specific generation call.

#### Output
* `String`: The parsed completion text from the model.
* `AiGenerationTimings`: Text combined with performance metrics (token counts, throughput, cache reuse).
* Side effect: Closes the native `LlamaModel` resource upon `close()` call.

#### Core logic
* **Lazy Model Initialization**: The `LlamaModel` is only instantiated on the first call to `generate` or `generateWithTimings` to avoid loading large GGUF files (~90MB+) during provider construction.
* **Parameter Mapping**: Maps `LlamaCppJniConfig` to `ModelParameters`, including:
    * Hardware acceleration: GPU layers, CPU MoE/FFN layers, and specific GPU device selection.
    * Performance: Flash Attention, KV-cache quantization (`cacheTypeK`/`cacheTypeV`), and batch/ubatch sizes.
    * Memory: SWA full KV cache and cache reuse settings.
    * Model behavior: Reasoning format (AUTO) and chat template kwargs.
* **Inference Pipeline**: 
    1. Converts `AiGenerationRequest` to inference parameters via `LlamaCppJniProviderSupport`.
    2. Executes `model().chatComplete()`.
    3. Parses the raw JSON response into a `ChatResponse` using `ChatResponseParser`.
    4. Extracts the final content using `AiCompletionParser`.
* **Timing Extraction**: Captures `Timings` from the `ChatResponse`, specifically surfacing `cache_n` to validate KV-cache prefix reuse.

#### Public API
* `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`: Initializes provider.
* `generate(AiGenerationRequest) -> String`: Returns generated text.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Returns text and performance metrics.
* `close() -> void`: Releases native model handle.

#### Dependencies
* `net.ladenthin.llama.LlamaModel`
* `net.ladenthin.llama.parameters.ModelParameters`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Timings`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.document.AiGenerationTimings`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
* `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
* `net.ladenthin.llama.json.ChatResponseParser`
* `net.ladenthin.srcmorph.provider.AiCompletionParser`

#### Exceptions / Errors
* `IOException`: Declared in `generate` to satisfy the `AiGenerationProvider` interface.
* Null handling: Returns zeroed `AiGenerationTimings` if the model response contains no timing data.
