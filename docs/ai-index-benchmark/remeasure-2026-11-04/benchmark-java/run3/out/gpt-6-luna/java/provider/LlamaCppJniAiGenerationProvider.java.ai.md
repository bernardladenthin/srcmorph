### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:44:15Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Runs local GGUF language models for AI-assisted code generation and reports inference throughput and prompt-cache reuse.

#### Purpose
- Generates completion text for `AiGenerationRequest` using llama.cpp through the JNI binding.
- Loads the native model on first generation and reuses it for subsequent requests.

#### Type
- `public final class LlamaCppJniAiGenerationProvider` — class; implements `AiGenerationProvider`, `AutoCloseable`.
- `@ToString` Lombok annotation; native model handle and provider support are excluded from the generated string.

#### Input
- Constructor: `LlamaCppJniConfig` for model and inference options; `AiPromptSupport` for request prompt rendering.
- Generation methods consume `AiGenerationRequest`.
- Configuration supplies GGUF path, context/thread settings, GPU and CPU offload controls, KV cache options, batching, lazy loading, devices, and chat-template options.

#### Output
- `generate(...)` returns parsed completion text.
- `generateWithTimings(...)` returns `AiGenerationTimings` with text, prompt/cache/prediction token counts, and throughput; absent timings produce zero metrics.
- `close()` closes the cached native model when loaded.

#### Core logic
- Builds `ModelParameters` on first model access, including automatic reasoning-channel parsing and chat-template kwargs from `LlamaCppJniProviderSupport`.
- Applies optional settings only when their configured values meet the relevant guards; creates and caches `LlamaModel`.
- Builds inference parameters via support, calls `chatComplete`, and parses the full response with `ChatResponseParser`.
- `generate(...)` warns on truncated answers, logs prompt-cache reuse, and parses first response content with `AiCompletionParser`.
- `generateWithTimings(...)` exposes native prompt, cache, and predicted-token metrics and their rates.
- Exposes `close()` for native model cleanup.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` — validates configuration and initializes support.
- `generate(AiGenerationRequest) -> String` — generate completion text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate text and metrics.
- `close() -> void` — close loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`
- `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null config or prompt support with `NullPointerException`.
- `generate(...)` declares `IOException`; chat and response parsing paths are documented as throwing unchecked exceptions.
- Native model construction, inference, parsing, or close failures are not caught here.

#### Concurrency
- Lazy model initialization and the nullable model field are unsynchronized; concurrent first calls or close/generation are not coordinated.
