### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:52:16Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation by interfacing with local GGUF models via the llama.cpp JNI binding.

#### Purpose
* Implements `AiGenerationProvider` to execute LLM inference locally.
* Manages the lifecycle and configuration of a native `LlamaModel` instance.

#### Type
Final class; implements `AiGenerationProvider`, `AutoCloseable`. Annotated with `@ToString`.

#### Input
* `LlamaCppJniConfig`: Configuration for model path, context size, threads, GPU layers, and cache settings.
* `AiPromptSupport`: Support for rendering request prompts.
* `AiGenerationRequest`: Parameters for a specific generation task.

#### Output
* `String`: The parsed completion text.
* `AiGenerationTimings`: Detailed inference metrics including token counts and throughput.
* Side effects: Loads native GGUF model into memory; manages native resources via `close()`.

#### Core logic
* **Lazy Initialization**: The `LlamaModel` is only instantiated upon the first call to `generate()` or `generateWithTimings()` to avoid unnecessary native memory allocation.
* **Model Configuration**: Maps `LlamaCppJniConfig` values to `ModelParameters`, including:
    * Hardware offloading (GPU layers, CPU MoE/FFN layers).
    * Performance tuning (Flash Attention, KV-cache quantization, batch sizes).
    * Memory management (SWA full cache, KV prefix reuse, lazy-loadable tensors).
    * Device selection (Main GPU index or specific device strings).
* **Inference Flow**: 
    * Uses `LlamaCppJniProviderSupport` to build inference parameters.
    * Executes `chatComplete` via the native model.
    * Parses the resulting JSON response into a `ChatResponse` using `ChatResponseParser`.
    * Extracts content via `AiCompletionParser`.
* **Metrics Extraction**: Captures `Timings` from the native response, specifically surfacing `cache_n` to verify KV-cache prefix reuse.

#### Public API
* `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`: Constructor.
* `generate(AiGenerationRequest) -> String`: Generates and parses completion text.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`: Generates text with performance metrics.
* `close() -> void`: Releases native `LlamaModel` resources.

#### Dependencies
* `net.ladenthin.llama.LlamaModel`: Native model handle.
* `net.ladenthin.llama.parameters.ModelParameters`: Configuration for the native model.
* `net.ladenthin.llama.value.ChatResponse`: Parsed model output.
* `net.ladenthin.srcmorph.document.AiGenerationRequest`: Input request object.
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`: Prompt rendering logic.
* `LlamaCppJniConfig`: Configuration POJO.
* `LlamaCppJniProviderSupport`: Helper for parameter building and logging.
* `AiCompletionParser`, `ChatResponseParser`: Response parsing utilities.

#### Exceptions / Errors
* `IOException`: Declared in `generate` per interface requirements.
* `NullPointerException`: Thrown by `Objects.requireNonNull` during construction if config or promptSupport are null.
* Handles null `Timings` by returning zeroed `AiGenerationTimings`.
