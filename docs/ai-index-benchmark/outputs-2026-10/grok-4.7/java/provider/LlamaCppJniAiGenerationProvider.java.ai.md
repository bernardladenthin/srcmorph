### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:41:30Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense, structured markdown summary of the Java source file.

The class is `LlamaCppJniAiGenerationProvider`, a final class implementing `AiGenerationProvider` and `AutoCloseable`. It exposes four public members: a constructor, `generate(AiGenerationRequest)`, `generateWithTimings(AiGenerationRequest)`, and `close()`.

The class declares seven private fields: `config`, `model`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, and `compatibilityHelper`. It also contains two private methods: `model()` and `chatResponse()`. No protected members exist.

The class performs local GGUF inference via llama.cpp JNI to generate chat completions for AI source-morph requests. It reports token timings through the `generateWithTimings` method.

The provider lazily loads the GGUF model on first use. `generate` throws `IOException`, while `generateWithTimings` does not declare it because the chat path only throws unchecked exceptions. Null checks guard `config` and `promptSupport`. A null timings object yields zero rates. The model field is initialized without synchronization, so concurrent first calls are not thread-safe.

The `model()` method mutates the `model` field without volatile or synchronized access. The public API is:

- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> LlamaCppJniAiGenerationProvider`
- `generate(AiGenerationRequest) -> String`
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`

Referenced types include `FlashAttn`, `ReasoningFormat`, `ModelParameters`, `ChatResponse`, and `Timings`.> Runs local GGUF chat completions for source-morph AI generation requests through the llama.cpp JNI binding.

#### Purpose
- `LlamaCppJniAiGenerationProvider` is the local `AiGenerationProvider` that loads a GGUF model via `LlamaModel` and turns `AiGenerationRequest` into completion text and timings.
- Native load is deferred until the first generation so construction stays cheap when every file is already cached.

#### Type
- Final class; implements `AiGenerationProvider`, `AutoCloseable`.
- `@ToString` over config and prompt support; `@ToString.Exclude` on `LlamaModel` and `LlamaCppJniProviderSupport`.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport` (both required).
- `generate` / `generateWithTimings`: `AiGenerationRequest`.
- Consumed config: model path, context size, threads, SWA-full, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified per slot, lazy mode, flash attention, cache type K/V, batch/ubatch size, batch threads, main GPU, devices.
- `LlamaCppJniProviderSupport` supplies chat-template kwargs and inference parameters.

#### Output
- `generate` returns parsed completion text from the first chat content.
- `generateWithTimings` returns `AiGenerationTimings` with text, prompt tokens, cache tokens, prompt rate, predicted tokens, and predicted rate.
- Lazily assigns the cached `LlamaModel`; `close` closes that native handle.
- Side effects: truncated-answer warning and prompt-cache-reuse log on `generate` only.

#### Core logic
- `model()` builds `ModelParameters` once: model path, context, threads, `ReasoningFormat.AUTO`, and chat-template kwargs.
- Optional flags are applied only when meaningful: SWA-full; cache reuse `> 0`; GPU/CPU-MoE/CPU-FFN/main GPU `>= 0`; KV slot, batch, ubatch, and batch threads `> 0`; non-blank lazy mode and cache types; flash attention `ON`; non-empty devices.
- `chatResponse` runs `LlamaModel.chatComplete` with `support.buildInferenceParameters` and parses JSON via `ChatResponseParser`.
- `generate` warns on truncation, logs KV prefix reuse, then `AiCompletionParser.parseCompletion` on first content.
- `generateWithTimings` uses the same completion but maps `Timings`; a null timings object becomes zero token counts and `0.0d` rates so the caller can fall back.
- `close` closes the cached `LlamaModel` when present and does not clear the field.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> LlamaCppJniAiGenerationProvider` — stores config and support
- `generate(AiGenerationRequest) -> String` — completion text; throws `IOException`
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — text plus model timings
- `close() -> void` — releases native `LlamaModel`

#### Dependencies
- `AiGenerationProvider`, `AiGenerationRequest`, `AiGenerationTimings`, `AiCompletionParser`, `AiPromptSupport`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `Java8CompatibilityHelper`
- `LlamaModel`, `ModelParameters`, `ChatResponse`, `ChatResponseParser`, `Timings`, `FlashAttn`, `ReasoningFormat`
- `IOException`, `Map`, `Objects`, `@Nullable`, Lombok `@ToString`

#### Exceptions / Errors
- Constructor rejects null `config` or `promptSupport` via `Objects.requireNonNull`.
- `generate` declares checked `IOException`; `generateWithTimings` does not, because chat complete and parse are treated as unchecked.
- Missing `Timings` is not an error: rates and counts are zeroed.

#### Concurrency
- Lazy `model` assignment is unsynchronized and not volatile; first-use initialization is not thread-safe.
- Cached `LlamaModel` is shared across later calls on the same provider.
