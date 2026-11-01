### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:59:54Z
- G: 
- A: 0.0.0
- X: file
---
> Generates Java code completions locally from GGUF models through llama.cpp, with configurable inference, prompt-cache reuse, and throughput reporting.

#### Purpose
- Implements local AI completion generation and timing collection using the llama.cpp JNI binding.
- Loads the configured model only when generation first requires it.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated with Lombok `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both required.
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume generation requests.
- Reads model and inference settings from `LlamaCppJniConfig`; `LlamaCppJniProviderSupport` builds chat-template kwargs and inference parameters.
- Uses `AiPromptSupport` to render request prompts.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns `AiGenerationTimings` with response text, prompt/cache/predicted token counts, and measured rates; absent timings produce zero counts and rates.
- Lazily creates and retains a `LlamaModel`; `close()` closes it when initialized.
- Logs prompt-cache reuse and warns on truncated answers through support methods.

#### Core logic
- Constructor null-checks configuration and prompt support, then creates `LlamaCppJniProviderSupport`.
- `model()` builds `ModelParameters` on first use, including model path, context size, threads, automatic reasoning parsing, and chat-template kwargs.
- Applies optional SWA, cache reuse, GPU/CPU layer placement, unified KV budget, lazy tensor mode, flash attention, KV-cache types, prefill sizing, GPU selection, and device selection according to configuration guards.
- Creates and caches one `LlamaModel`; later calls reuse it.
- `chatResponse()` builds inference parameters, calls `chatComplete`, and parses the full response.
- `generate()` warns about truncation, logs cache reuse, then parses the first response content.
- `generateWithTimings()` returns model-reported token counts and throughput, including KV-cache prompt tokens; returns zero metrics if timing data is absent.
- `close()` closes the cached model if present.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> initializes provider dependencies.
- `generate(AiGenerationRequest) -> String` — generates completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns completion and model timings.
- `close() -> void` — closes initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `Java8CompatibilityHelper`, `Objects`, `Map`, `IOException`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null config or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` narrows away the interface’s checked exception.
- Configuration values are conditionally forwarded to model parameters; invalid binding/native options may fail during model initialization or inference.

#### Concurrency
- Lazily initialized mutable `model` is neither synchronized nor volatile; concurrent first use is not protected.
- `close()` and generation have no explicit coordination.
