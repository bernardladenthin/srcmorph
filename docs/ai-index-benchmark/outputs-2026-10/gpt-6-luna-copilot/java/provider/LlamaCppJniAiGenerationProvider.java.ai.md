### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:35:12Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions locally with llama.cpp, applying configured model, prompt, and inference settings.

#### Purpose
- `LlamaCppJniAiGenerationProvider` connects source-generation requests to a local GGUF model.
- Returns completion text or text with model-measured generation timings.

#### Type
- `final` class; implements `AiGenerationProvider`, `AutoCloseable`.
- Annotated `@ToString`; excludes the native model handle and provider support.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`; rejects null values.
- Generation methods accept `AiGenerationRequest`.
- Reads model path, context, threading, GPU/CPU placement, cache, attention, and batching settings from config.

#### Output
- Produces completion text or `AiGenerationTimings`.
- Writes diagnostics through support methods; `close()` closes an initialized model.

#### Core logic
- Lazily builds `ModelParameters` and loads `LlamaModel` on first generation; reuses the cached handle.
- Applies configured reasoning format, chat-template arguments, GPU/CPU placement, KV settings, lazy loading, flash attention, cache quantization, batching, and device selection.
- Builds inference parameters from each request, runs chat completion, and parses the full response.
- `generate` warns on truncation, logs prompt-cache reuse, and parses first-content text.
- `generateWithTimings` returns prompt/prediction counts, cache count, and throughput; uses zero metrics when timings are absent.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> LlamaCppJniAiGenerationProvider` — validates and stores dependencies.
- `generate(AiGenerationRequest) -> String` — generates completion; declares `IOException`.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns completion and model timings.
- `close() -> void` — closes the loaded model.

#### Dependencies
- `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`.
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiCompletionParser`.
- `AiPromptSupport`, `Java8CompatibilityHelper`, `IOException`, `Map`, `Objects`, Lombok `ToString`, JSpecify `Nullable`.

#### Exceptions / Errors
- Constructor throws `NullPointerException` for null config or prompt support.
- `generate` declares `IOException`; `generateWithTimings` does not.
- Missing response timings produce zero rates and counts.

#### Concurrency
- Lazy model initialization and cached handle are unsynchronized; concurrent first calls are not protected.
