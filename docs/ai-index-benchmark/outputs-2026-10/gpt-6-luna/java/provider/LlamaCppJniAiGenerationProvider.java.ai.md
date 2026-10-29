### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:32Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI completions from locally loaded GGUF models through llama.cpp, with configurable inference, cache reuse, and model-reported timings.

#### Purpose
- `LlamaCppJniAiGenerationProvider` runs chat-completion requests against a local llama.cpp model.
- Loads the model on first generation and reuses the handle for later requests.

#### Type
- `final` class; implements `AiGenerationProvider`, `AutoCloseable`.
- `@ToString`; excludes the native model handle and provider support from rendered output.

#### Input
- Constructor: `LlamaCppJniConfig` and `AiPromptSupport`, both required non-null.
- Generation methods: `AiGenerationRequest`.
- Model initialization reads `LlamaCppJniConfig` settings for context, threads, GPU/CPU placement, KV cache, attention, batching, devices, and lazy loading.

#### Output
- `generate` returns parsed completion text; warns about truncation and logs prompt-cache reuse.
- `generateWithTimings` returns completion text, token counts, cache reuse count, and model-reported throughput.
- `close` closes the loaded `LlamaModel`, if initialized.

#### Core logic
- Constructor creates `LlamaCppJniProviderSupport` from the validated config and prompt support.
- `model()` lazily builds `ModelParameters` and initializes one cached `LlamaModel`.
  - Always configures model path, context size, threads, automatic reasoning format, and chat-template kwargs.
  - Applies optional settings only when their configured values meet the relevant guards; supports SWA, cache reuse, GPU/CPU layer placement, KV limits, lazy mode, Flash Attention, KV-cache types, batching, GPU selection, and explicit devices.
- `chatResponse()` builds inference parameters through `support`, calls `chatComplete`, and parses the full response.
- `generate()` checks truncation and logs cache reuse before parsing the first response content.
- `generateWithTimings()` returns zero rates and counts when response timings are absent; otherwise surfaces prompt/predicted token counts, cache tokens, and throughput.

#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport) -> LlamaCppJniAiGenerationProvider` — validates inputs and initializes support.
- `generate(request) -> String` — generates parsed completion text.
- `generateWithTimings(request) -> AiGenerationTimings` — returns completion and model timings.
- `close() -> void` — closes the loaded model.

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `LlamaModel`
- `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponse`, `ChatResponseParser`, `Timings`
- `AiCompletionParser`, `Java8CompatibilityHelper`
- `IOException`, `Map`, `Objects`, Lombok `@ToString`, JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null config or prompt support via `Objects.requireNonNull`.
- `generate` declares `IOException`; `generateWithTimings` declares no checked exception.
- Missing response timings produce zero-valued timing fields.

#### Concurrency
- Lazy model initialization and model access are unsynchronized; concurrent first calls may initialize multiple models.
- `close()` closes the cached model when present.
