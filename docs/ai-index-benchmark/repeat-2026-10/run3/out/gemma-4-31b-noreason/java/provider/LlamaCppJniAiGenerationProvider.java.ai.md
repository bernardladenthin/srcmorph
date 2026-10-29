### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:28:08Z
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
* `AiPromptSupport`: Logic for rendering request prompts.
* `AiGenerationRequest`: Parameters for a specific generation task.

#### Output
* `String`: The parsed completion text from the model.
* `AiGenerationTimings`: Detailed performance metrics including token counts and throughput.
* Side effect: Loads native GGUF model into memory/VRAM on first use.

#### Core logic
* **Lazy Initialization**: The `LlamaModel` is only instantiated during the first call to `model()`, preventing native resource allocation during provider construction.
* **Model Configuration**: Maps `LlamaCppJniConfig` values to `ModelParameters`, including:
    * Hardware offloading (GPU layers, CPU MoE/FFN layers).
    * Memory optimizations (SWA full KV cache, cache reuse, lazy-load tensors).
    * Performance tuning (Flash Attention, KV-cache quantization, batch/ubatch sizes).
    * Device selection (Main GPU index or specific backend device strings).
* **Inference Flow**:
    1. Builds inference parameters via `LlamaCppJniProviderSupport`.
    2. Executes `chatComplete` on the native model.
    3. Parses the JSON response into a `ChatResponse` using `ChatResponseParser`.
    4. Extracts content via `AiCompletionParser`.
* **Timing Extraction**: Captures native `Timings` (prompt/predicted tokens and speeds) and surfaces `cache_n` to verify KV-cache prefix reuse.

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
* `IOException`: Declared on `generate` per interface requirements, though internal chat path uses unchecked exceptions.
* `Objects.requireNonNull`: Validates `config` and `promptSupport` during construction.
* Null-handling: Returns zeroed `AiGenerationTimings` if the native response lacks timing data.

#### Concurrency
* Not explicitly thread-safe; relies on the underlying `LlamaModel` JNI implementation for concurrency handling.
