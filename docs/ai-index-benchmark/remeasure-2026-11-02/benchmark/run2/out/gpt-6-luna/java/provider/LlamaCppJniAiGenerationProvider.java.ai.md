### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:47Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Generates AI completions locally from GGUF models with llama.cpp, exposing response text, token timings, and prompt-cache reuse metrics.

#### Purpose
- Provides `AiGenerationProvider` completions using locally loaded llama.cpp models.
- Applies configured inference and hardware options; reports generation timings and cache reuse.

#### Type
- `public final class LlamaCppJniAiGenerationProvider`; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; native model handle and provider support are excluded from its rendered output.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- Generation methods consume `AiGenerationRequest`.
- Configuration supplies model path, context, threading, GPU/CPU placement, cache, attention, batching, device, and lazy-loading options.
- `LlamaCppJniProviderSupport` builds chat-template kwargs and per-request inference parameters.

#### Output
- `generate` returns parsed completion text; warns on truncated answers and logs prompt-cache reuse.
- `generateWithTimings` returns `AiGenerationTimings` with response text, prompt/cache/predicted token counts, and throughput.
- Lazily creates and retains a `LlamaModel`; `close` closes it if initialized.

#### Core logic
- Constructor rejects null config or prompt support and creates `LlamaCppJniProviderSupport`.
- First model access builds `ModelParameters` from config, always enabling automatic reasoning-format parsing and chat-template kwargs.
- Forwards optional model settings only under their configured guards: nonnegative GPU/MoE/FFN layer counts and main GPU; positive KV budget and batch/thread sizes; nonblank lazy mode and cache types; enabled Flash Attention; nonempty device list.
- Instantiates and caches `LlamaModel`; subsequent calls reuse it.
- Runs `chatComplete` with support-generated inference parameters, then parses the full response with `ChatResponseParser`.
- `generate` checks truncation and logs cache reuse before parsing first content through `AiCompletionParser`.
- `generateWithTimings` returns zero rates and counts when response timings are absent; otherwise surfaces prompt count, cache count, prompt throughput, predicted count, and predicted throughput.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> provider; validates dependencies.
- `generate(AiGenerationRequest) -> String` — generates completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns text and inference metrics.
- `close() -> void` — closes initialized model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`.
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`.
- `AiPromptSupport`, `AiCompletionParser`, `Java8CompatibilityHelper`.
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`.

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for config and prompt support.
- `generate` declares `IOException`; `generateWithTimings` does not.
- Missing response timings produce zero-valued metrics.

#### Concurrency
- Lazy model initialization and cached model access are unsynchronized; concurrent first use is not explicitly guarded.
