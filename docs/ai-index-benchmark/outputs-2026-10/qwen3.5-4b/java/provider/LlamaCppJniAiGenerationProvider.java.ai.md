### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:03:10Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a JNI-backed local Llama.cpp inference engine that loads GGUF models lazily and executes chat completions with configurable GPU offloading, KV caching, and reasoning format support.

#### Purpose
- Implements `AiGenerationProvider` using native Llama.cpp bindings for local AI generation.
- Manages lazy GGUF model loading and GPU/CPU layer configuration for inference performance.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` implements `AiGenerationProvider` and `AutoCloseable`.
- Fields: `LlamaCppJniConfig`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `ChatResponseParser`, `Java8CompatibilityHelper`.
- Annotations: `@ToString`, `@ToString.Exclude`, `@Nullable`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- Methods: `AiGenerationRequest request` (for `generate`, `generateWithTimings`); `LlamaCppJniConfig` properties (model path, context size, threads, GPU layers, cache reuse, etc.).
- Resources: None (no file/stream reading).

#### Output
- `generate`: Returns `String` (parsed completion text).
- `generateWithTimings`: Returns `AiGenerationTimings` (text + token counts + throughput rates).
- `close`: Closes native `LlamaModel` handle.
- Side effects: Lazy model initialization, KV cache reuse, GPU offloading configuration.

#### Core logic
- Lazily initializes native `LlamaModel` on first `generate()` call using `LlamaCppJniConfig` parameters.
- Configures model parameters: context size, threads, GPU layers, CPU MoE/FFN layers, KV cache quantization, flash attention, batch sizes, and device selection.
- Executes chat completion via `model().chatComplete()` with `LlamaCppJniProviderSupport` inference parameters.
- Parses response via `ChatResponseParser` to extract completion text and timing metrics.
- Handles GPU offloading, KV cache reuse, and MoE expert weight placement based on config flags.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> `void` (constructor)
- `generate(AiGenerationRequest)` -> `String`
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings`
- `close()` -> `void`

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `ChatResponseParser`, `Java8CompatibilityHelper`.
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponse`, `Timings`.
- `AiGenerationRequest`, `AiGenerationTimings`.

#### Exceptions / Errors
- Throws `IOException` from `generate()` (checked exception for native I/O).
- Null checks on `config` and `promptSupport` in constructor.
- Handles null `timings` in `generateWithTimings` by returning zero rates.

#### Concurrency
- Not thread-safe; model handle is single-instance per provider.
- Lazy initialization ensures native model is created once per provider instance.
- No explicit synchronization; assumes single-threaded use per provider.
