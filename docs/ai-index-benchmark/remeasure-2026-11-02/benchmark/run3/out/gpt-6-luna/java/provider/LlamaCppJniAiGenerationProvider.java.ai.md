### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:56Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates Java code completions locally with llama.cpp, using configured GGUF models and reporting inference and prompt-cache timings.

#### Purpose
- Provides AI generation for `AiGenerationRequest` through the `net.ladenthin:llama` JNI binding.
- Loads the GGUF model only when generation is first requested, then reuses it.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; Lombok excludes the native model handle and provider support from the rendered string.

#### Input
- Constructor receives `LlamaCppJniConfig` and `AiPromptSupport`; both are required.
- `generate` and `generateWithTimings` receive an `AiGenerationRequest`.
- Model setup reads paths, context and thread counts, GPU/CPU layer settings, KV-cache options, batching, device selection, and other configuration from `LlamaCppJniConfig`.
- `LlamaCppJniProviderSupport` builds chat-template arguments and inference parameters from configuration and requests.

#### Output
- `generate` returns parsed completion text.
- `generateWithTimings` returns completion text, token counts, prompt-cache count, and measured throughput in `AiGenerationTimings`.
- Lazily creates and retains a `LlamaModel`; `close()` closes it if initialized.
- Logs prompt-cache reuse and warns about truncated answers through provider support.

#### Core logic
- Constructor validates config and prompt support with `Objects.requireNonNull`, then creates `LlamaCppJniProviderSupport`.
- On first use, builds `ModelParameters` with model path, context size, threads, automatic reasoning parsing, and chat-template kwargs.
- Applies optional model settings only when their configured values meet the relevant guards: SWA, cache reuse, GPU/CPU layer placement, unified KV budget, lazy mode, Flash Attention, KV-cache types, batch sizing, main GPU, and devices.
- Creates and caches the native `LlamaModel`; subsequent calls reuse it.
- Builds inference parameters, runs `chatComplete`, and parses the full response as `ChatResponse`.
- `generate` warns on truncation, logs prompt-cache reuse, and parses the first response content as a completion.
- `generateWithTimings` returns response content and model-reported prompt/prediction counts, cache count, and throughput; missing timings yield zero counts and rates.
- `close` closes the model only if it has been initialized.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — initialize provider; model remains unloaded.
- `generate(AiGenerationRequest) -> String` — generate parsed completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate completion with measured timings.
- `close() -> void` — close initialized native model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiCompletionParser`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null config or prompt support with `NullPointerException`.
- `generate` declares `IOException`; `generateWithTimings` narrows away the interface’s checked exception.
- Missing response timings degrade to zero-valued timing metrics.

#### Concurrency
- No synchronization protects lazy model initialization or model use; concurrent first calls may race to initialize the model.
