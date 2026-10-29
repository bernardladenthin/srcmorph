### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:42:37Z
- G: 
- A: 0.0.0
- X: file
---
> Runs local GGUF model inference via the llama.cpp JNI binding to produce AI-generated text completions for source-morphing requests.

#### Purpose
- Concrete `AiGenerationProvider` that loads a GGUF model through the `net.ladenthin:llama` JNI binding and executes chat completions locally.
- Supports KV-cache prefix reuse, GPU offload, MoE layer partitioning, and flash-attention configuration for multi-file batch runs.

#### Type
- `final class` implementing `AiGenerationProvider`, `AutoCloseable`
- Annotated `@ToString` (Lombok); `@ToString.Exclude` on `model` and `support` fields

#### Input
- `LlamaCppJniConfig config` — model path, context size, threads, GPU layers, cache reuse, flash-attn, KV quantization, batch sizes, device selection, lazy mode
- `AiPromptSupport promptSupport` — prompt lookup/rendering
- `AiGenerationRequest request` — per-call generation parameters (prompt, inference params)

#### Output
- `String` — parsed completion text from the model
- `AiGenerationTimings` — text plus prompt/predicted token counts, cache-hit count, and per-second throughput rates
- Side effect: native `LlamaModel` handle loaded (lazy) and closed on `close()`

#### Core logic
- **Lazy model init** (`model()`): on first call, builds `ModelParameters` from config (model path, ctx size, threads, reasoning format AUTO, chat-template kwargs, SWA full, cache reuse, GPU/CPU MoE/FFN layers, KV unified per-slot, lazy mode, flash-attn, KV cache quantization, batch/ubatch/threads-batch, main GPU, devices), then instantiates `LlamaModel`.
- **`generate`**: calls `chatResponse(request)` → `model().chatComplete(support.buildInferenceParameters(request))` → parses via `ChatResponseParser`; warns on truncated answer; logs prompt-cache reuse; returns `completionParser.parseCompletion(firstContent)`.
- **`generateWithTimings`**: same native call but extracts `Timings` from the `ChatResponse` (promptN, cacheN, promptPerSecond, predictedN, predictedPerSecond); degrades to zero rates if timings absent.
- **`close`**: closes the native `LlamaModel` handle if non-null.

#### Public API
- `generate(AiGenerationRequest) -> String` — returns parsed completion text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — returns text plus measured throughput
- `close() -> void` — releases native model handle

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.value.ChatResponse`, `Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`, `AiGenerationTimings`, `AiCompletionParser`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- `generate` declares `IOException` (interface contract); actual native path throws only unchecked exceptions
- `Objects.requireNonNull` on constructor params (`config`, `promptSupport`)
- Null `Timings` in `generateWithTimings` degrades to zero-rate `AiGenerationTimings`

#### Concurrency
- `model` field is a plain (non-volatile) `@Nullable` reference with a check-then-act pattern in `model()`; not thread-safe — assumes single-threaded use per provider instance
- `LlamaModel` native handle is not safe for concurrent `chatComplete` calls
