### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:36:02Z
- G: 
- A: 0.0.0
- X: file
---
> Generates local AI completions from GGUF models through llama.cpp, with configurable inference, caching, and performance reporting.

#### Purpose
- `LlamaCppJniAiGenerationProvider` implements local chat-completion generation for `AiGenerationRequest` inputs.
- Loads and retains the native model only when generation first requires it.

#### Type
- `LlamaCppJniAiGenerationProvider` — `public final class`; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; Lombok excludes the native model handle and provider support from the rendered output.

#### Input
- Constructor receives `LlamaCppJniConfig` and `AiPromptSupport`; rejects either if null.
- Generation methods receive an `AiGenerationRequest`.
- Reads model and inference settings from `LlamaCppJniConfig`; `LlamaCppJniProviderSupport` builds chat-template arguments and request inference parameters.

#### Output
- `generate` returns parsed completion text and invokes support hooks for truncation warnings and prompt-cache logging.
- `generateWithTimings` returns completion text, prompt/cache/predicted token counts, and prompt/decode rates in `AiGenerationTimings`.
- Creates and caches a `LlamaModel`; `close` closes it if loaded.

#### Core logic
- Builds `ModelParameters` on first model access using the configured model path, context size, threads, automatic reasoning format, and chat-template arguments.
- Applies optional SWA, cache reuse, GPU and CPU layer placement, unified KV budget, lazy mode, flash attention, KV-cache types, batch sizing, GPU selection, and device selection according to configuration guards.
- Calls `LlamaModel.chatComplete` with support-built inference parameters, then parses the full response through `ChatResponseParser`.
- `generate` warns on truncated answers and logs prompt-cache reuse before parsing the first response content with `AiCompletionParser`.
- `generateWithTimings` exposes response timings and cache-token count; when timings are absent, returns zero counts and rates.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)` -> constructor; validates and wires provider.
- `generate(AiGenerationRequest) -> String` — generate parsed completion.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generate completion with metrics.
- `close() -> void` — close loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiPromptSupport`
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `ChatResponseParser`, `ChatResponse`, `Timings`, `AiCompletionParser`
- `Java8CompatibilityHelper`, `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for configuration and prompt support.
- `generate` declares `IOException`; the chat-completion and parsing path is described as throwing unchecked exceptions.
- Invalid model or inference settings may fail during native model construction or inference.

#### Concurrency
- Model initialization and cached-handle access are unsynchronized; concurrent first calls may race.
- No explicit thread-safety guarantees are provided for generation or closing.
