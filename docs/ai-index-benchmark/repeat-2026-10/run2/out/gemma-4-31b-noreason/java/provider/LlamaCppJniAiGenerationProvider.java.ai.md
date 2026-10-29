### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:55:08Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation by interfacing with local GGUF models via llama.cpp JNI bindings.

#### Purpose
* Implements `AiGenerationProvider` to execute LLM inference locally.
* Manages the lifecycle and configuration of a native `LlamaModel` instance.

#### Type
* Class; final.
* Implements `AiGenerationProvider`, `AutoCloseable`.
* Annotated with `@ToString` (Lombok).

#### Input
* `LlamaCppJniConfig`: Configuration for model path, context size, threads, GPU layers, and cache settings.
* `AiPromptSupport`: Support for rendering request prompts.
* `AiGenerationRequest`: Parameters for a specific generation task.

#### Output
* `String`: The parsed completion text from the model.
* `AiGenerationTimings`: Detailed performance metrics including token counts and throughput.
* Side effect: Loads a native GGUF model into memory/VRAM on first use.

#### Core logic
* **Lazy Initialization**: The `LlamaModel` is only instantiated upon the first call to `generate` or `generateWithTimings` to avoid unnecessary native overhead.
* **Model Configuration**: Maps `LlamaCppJniConfig` values to `ModelParameters`, including:
    * Hardware offloading (GPU layers, CPU MoE/FFN layers).
    * Memory optimization (SWA full cache, KV cache reuse, lazy-load tensors).
    * Performance tuning (Batch/Ubatch sizes, Flash Attention, KV quantization).
    * Device selection (Main GPU index or specific backend device strings).
* **Inference Flow**:
    * Uses `LlamaCppJniProviderSupport` to build inference parameters.
    * Executes `model().chatComplete(...)` to get a JSON response.
    * Parses the response via `ChatResponseParser` to extract content and metadata.
* **Timing Extraction**: Extracts `Timings` from the `ChatResponse` to provide precise prompt/prediction token counts and speeds, including `cache_n` for KV-cache reuse verification.

#### Public API
* `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`: Constructor.
* `generate(AiGenerationRequest) -> String`: Generates and returns completion text.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Generates text with performance metrics.
* `close() -> void`: Closes the native model handle.

#### Dependencies
* `net.ladenthin.llama.LlamaModel`
* `net.ladenthin.llama.parameters.ModelParameters`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.json.ChatResponseParser`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `LlamaCppJniConfig`
* `LlamaCppJniProviderSupport`
* `AiCompletionParser`
* `Java8CompatibilityHelper`

#### Exceptions / Errors
* `IOException`: Declared in `generate` per interface requirements, though internal chat path uses unchecked exceptions.
* `Objects.requireNonNull`: Validates `config` and `promptSupport` during construction.
* Null-handling: Returns zeroed `AiGenerationTimings` if the model response lacks timing data.

#### Concurrency
* Not explicitly thread-safe; relies on the underlying `LlamaModel` JNI implementation for concurrency handling.
