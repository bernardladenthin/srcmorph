### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:43:42Z
- G: 
- A: 0.0.0
- X: file
---
> This class provides a local AI generation provider using the llama.cpp JNI bindings to run GGUF models.

#### Purpose
* Implements `AiGenerationProvider` to facilitate local LLM inference.
* Manages the lifecycle of a `LlamaModel` instance, including lazy loading and resource cleanup.

#### Type
final class implements AiGenerationProvider, AutoCloseable

#### Input
* `LlamaCppJniConfig`: Configuration for model paths, context size, GPU layers, and KV cache settings.
* `AiPromptSupport`: Support for prompt lookup and rendering.
* `AiGenerationRequest`: The specific request containing the prompt and inference parameters.

#### Output
* `String`: The parsed completion text from the model.
* `AiGenerationTimings`: A record containing the generated text and native performance metrics (tokens per second, cache hits).
* `void`: Closes the native model handle.

#### Core logic
* **Lazy Model Initialization**: The `LlamaModel` is instantiated only upon the first generation request to minimize startup overhead.
* **Parameter Mapping**: Maps `LlamaCppJniConfig` fields to `ModelParameters`, handling specific logic for:
    * GPU/CPU layer offloading (MoE and FFN weights).
    * KV cache quantization and unified context budgets.
    * Flash Attention and lazy-loadable tensor settings.
    * Batch size and thread configurations.
* **Inference Execution**: Executes `chatComplete` via the native binding, which returns a JSON response.
* **Response Parsing**: Uses `ChatResponseParser` to extract content, finish reasons, and timing data.
* **Timing Extraction**: Surfaces native `Timings` (prompt/predicted tokens and rates) and `cache_n` (KV cache hits) into the `AiGenerationTimings` object.
* **Resource Management**: Implements `AutoCloseable` to ensure the native `LlamaModel` handle is released.

#### Public API
* `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void` + Constructor for provider initialization.
* `generate(AiGenerationRequest) -> String` + Returns the primary completion text.
* `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` + Returns text with native performance metrics.
* `close() -> void` + Releases native model resources.

#### Dependencies
* `net.ladenthin.llama.LlamaModel`
* `net.ladenthin.llama.args.FlashAttn`
* `net.ladenthin.llama.args.ReasoningFormat`
* `net.ladenthin.llama.json.ChatResponseParser`
* `net.ladenthin.llama.parameters.ModelParameters`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Timings`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
* `net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport`
* `net.ladenthin.srcmorph.provider.AiCompletionParser`

#### Exceptions / Errors
* `IOException`: Thrown during the generation process if native communication fails.
* `Objects.requireNonNull`: Validates mandatory configuration and prompt support during construction.

#### Concurrency
* The `model` field is lazily initialized; however, the class does not explicitly use `synchronized` blocks, implying thread-safety depends on the underlying `LlamaModel` implementation or external synchronization.
