### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:59Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions locally from GGUF models using llama.cpp, with configurable inference, GPU, and KV-cache settings.

#### Purpose
- Provides `AiGenerationProvider` completions through the `net.ladenthin:llama` JNI binding.
- Loads and retains the native model on first generation; exposes completion text and model-reported timing and cache metrics.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- `@ToString` generates a configuration-focused diagnostic string; excludes the native model handle and provider support.

#### Input
- Constructor requires non-null `LlamaCppJniConfig` and `AiPromptSupport`.
- `generate` and `generateWithTimings` consume `AiGenerationRequest`; provider support builds chat-template arguments and inference parameters.
- Model loading reads configuration for model path, context size, threads, reasoning format, SWA, cache reuse, GPU/CPU offload, KV cache types and budget, lazy loading, flash attention, batch sizing, and device selection.

#### Output
- `generate` returns parsed completion text; warns on truncated answers and logs prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` containing text, prompt/cache/prediction token counts, and throughput.
- `close` closes the loaded native model, if present.

#### Core logic
- Constructor validates and stores configuration and prompt support, then creates `LlamaCppJniProviderSupport`; it does not load native resources.
- On first use, `model()` builds `ModelParameters`, always setting model path, context size, threads, automatic reasoning format, and chat-template kwargs.
- Applies optional model settings only when their configured values meet the relevant guards: SWA and flash attention when enabled; cache reuse, KV budget, batch sizes, and batch threads when positive; GPU, CPU MoE, CPU FFN, and main GPU indices when non-negative; lazy mode and cache types when nonblank; explicit devices when nonempty.
- Constructs and caches `LlamaModel`; subsequent calls reuse it.
- `chatResponse` runs `chatComplete` with support-generated inference parameters and parses the full response.
- `generate` checks truncation and logs cache reuse before parsing first-content text.
- `generateWithTimings` returns zero counts and rates when timings are absent; otherwise surfaces prompt, cache, and prediction counts and measured rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructs provider
- `generate(AiGenerationRequest) -> String` — generates completion text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns text and model timings
- `close() -> void` — closes loaded model

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiCompletionParser`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`
- `Java8CompatibilityHelper`, `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; model loading, inference, and parsing may also fail through unchecked exceptions.
- `generateWithTimings` does not declare checked `IOException`.
- Missing response timings degrade to zero-valued metrics.

#### Concurrency
- Lazy model initialization and cached model access are unsynchronized; concurrent first calls may race.
- No explicit thread-safety guarantees; `close` may overlap with inference.
