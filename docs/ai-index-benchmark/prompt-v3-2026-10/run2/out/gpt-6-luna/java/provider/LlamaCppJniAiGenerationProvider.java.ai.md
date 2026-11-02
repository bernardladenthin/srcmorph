### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:32Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions locally from GGUF models using llama.cpp, with configurable inference, prompt-cache reporting, and throughput metrics.

#### Purpose
- Provides `AiGenerationProvider` completions backed by the llama.cpp JNI binding.
- Loads the configured model on first generation and reuses it for later requests.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated with Lombok `@ToString`; excludes the native model handle and provider support from its rendered output.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`; both are required.
- `generate` and `generateWithTimings` consume `AiGenerationRequest` values.
- Model initialization reads configuration for model path, context, threads, GPU/CPU placement, cache, attention, batch sizing, devices, and lazy loading.
- Inference parameters and chat-template kwargs come from `LlamaCppJniProviderSupport`.

#### Output
- `generate` returns parsed completion text; warns on truncation and logs prompt-cache reuse.
- `generateWithTimings` returns completion text with prompt/cache/prediction token counts and measured throughput; missing timings produce zero metrics.
- Lazily creates and caches a `LlamaModel`; `close` closes it if initialized.

#### Core logic
- Constructor validates configuration and prompt support with `Objects.requireNonNull`, then creates `LlamaCppJniProviderSupport`.
- `model()` builds `ModelParameters` on first use:
  - Sets model path, context size, threads, automatic reasoning format, and chat-template kwargs.
  - Applies configured SWA, cache reuse, GPU layers, CPU MoE/FFN layers, unified KV budget, lazy mode, flash attention, KV-cache types, batch sizes, batch threads, main GPU, and devices when their values pass the relevant guards.
  - Creates and stores the native `LlamaModel`; subsequent calls reuse it.
- `chatResponse()` builds inference parameters, calls `chatComplete`, and parses the full response.
- `generate()` checks truncation and logs cache reuse before parsing the first response content.
- `generateWithTimings()` extracts first content and native timing counters, including KV-cache prompt tokens.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — validates dependencies and prepares support.
- `generate(AiGenerationRequest) -> String` — generates completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns text and inference metrics.
- `close() -> void` — closes the initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiPromptSupport`, `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` does not.
- `generateWithTimings` returns zero rates and counts when response timings are absent.
- Invalid model parameters or native inference failures may propagate from the llama.cpp binding; no local catch is present.

#### Concurrency
- Model initialization and cached `model` access are unsynchronized; concurrent first use is not guarded.
- `close()` closes the current model but does not clear the cached field.
