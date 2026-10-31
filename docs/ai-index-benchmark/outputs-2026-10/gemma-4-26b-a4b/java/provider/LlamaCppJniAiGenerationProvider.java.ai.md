### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:40:12Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation by interfacing with local GGUF models via the llama.cpp JNI binding.

#### Purpose
* Implements `AiGenerationProvider` to run local LLM inference.
* Manages the lifecycle of a native `LlamaModel` handle, including lazy initialization and resource cleanup.

#### Type
final class; implements `AiGenerationProvider`, `AutoCloseable`; annotated with `@ToString`.

#### Input
* `LlamaCppJniConfig`: Configuration for model path, context size, threads, GPU offloading, and KV cache settings.
* `AiPromptSupport`: Service for rendering request prompts.
* `AiGenerationRequest`: The specific prompt and parameters for a generation task.

#### Output
* `String`: The parsed text completion.
* `AiGenerationTimings`: Metadata including token counts (prompt, cache, predicted) and throughput rates.
* Mutates internal `model` state via lazy loading.

#### Core logic
* **Lazy Model Loading**: The `LlamaModel` is only instantiated upon the first call to `generate` or `generateWithTimings`, converting `LlamaCppJniConfig` into `ModelParameters`.
* **Parameter Mapping**: Maps complex configuration (Flash Attention, KV quantization, MoE/FFN layer offloading, SWA, and GPU device selection) to native `ModelParameters`.
* **Inference Execution**: Uses `chatComplete` to retrieve full JSON responses, allowing access to finish reasons and timing metadata that text-only methods discard.
* **Response Parsing**: Utilizes `ChatResponseParser` and `AiCompletionParser` to extract content and performance metrics.
* **Resource Management**: Implements `AutoCloseable` to ensure the native model handle is released.

#### Public API
* `LlamaCppJniAiGenerationProvider(config, promptSupport) -> void` + Constructor initializing support services.
* `generate(request) -> String` + Returns parsed text completion.
* `generateWithTimings(request) -> AiGenerationTimings` + Returns text and performance metrics.
* `close() -> void` + Closes the native model handle.

#### Dependencies
* `net.ladenthin.llama.LlamaModel`
* `net.ladenthin.llama.args.*` (FlashAttn, ReasoningFormat)
* `net.ladenthin.llama.json.ChatResponseParser`
* `net.ladenthin.llama.parameters.ModelParameters`
* `net.ladenthin.llama.value.*` (ChatResponse, Timings)
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `LlamaCppJniConfig`
* `LlamaCppJniProviderSupport`
* `AiCompletionParser`

#### Exceptions / Errors
* Throws `IOException` during generation if native calls fail.
* Handles `null` timings by returning zeroed-out `AiGenerationTimings`.

#### Concurrency
* Thread-safety is not explicitly guaranteed; the `model` field is lazily initialized but not synchronized (potential race condition on first access).
* The class is designed to manage a single native model instance.
