### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:23:48Z
- G: 
- A: 0.0.0
- X: file
---
> Runs GGUF models locally via llama.cpp JNI to generate text for prompts.

#### Purpose
- Implements `AiGenerationProvider` for local LLM inference via llama.cpp JNI.
- Parses chat and completion responses into domain types.
- Manages lazy model loading and native resource cleanup.

#### Type
- `final class LlamaCppJniAiGenerationProvider`
- `implements AiGenerationProvider, AutoCloseable`
- `@ToString` (Lombok), `@Nullable` (jspecify)
- Fields: `config`, `model`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, `compatibilityHelper`

#### Input
- `LlamaCppJniConfig config` (constructor, required)
- `AiPromptSupport promptSupport` (constructor, required)
- `AiGenerationRequest request` (generate, generateWithTimings)

#### Output
- `String` from `generate(request)` (parsed completion text)
- `AiGenerationTimings` from `generateWithTimings(request)` (text + token/rate metrics)
- Mutated: none (model loaded lazily, cached)
- Written: none

#### Core logic
- Lazily initializes `LlamaModel` on first `generate()` call via `model()`
- Builds inference parameters using `support.buildInferenceParameters(request)` and `support.buildChatTemplateKwargs()`
- Configures `ModelParameters` with model path, context size, threads, reasoning format, SWA, cache reuse, GPU/CPU offload, KV cache, lazy mode, Flash Attention, cache quantization, batch sizes, GPU selection, and device selection
- Calls `model().chatComplete(...)` for full chat response
- Parses response via `chatResponseParser.parseResponse(...)`
- `generate()` extracts first content via `completionParser.parseCompletion(...)`
- `generateWithTimings()` extracts timings from response, falls back to zeros if missing
- `close()` releases native model handle

#### Public API
- `generate(AiGenerationRequest) -> String` (generates text)
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` (generates with metrics)
- `close() -> void` (releases native resources)

#### Dependencies
- `LlamaModel`, `LlamaCppJniConfig`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, `AiCompletionParser`, `ChatResponseParser`, `Java8CompatibilityHelper`, `ModelParameters`, `ReasoningFormat`, `FlashAttn`, `ChatResponse`, `Timings`, `AiGenerationRequest`, `AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate(request)` (narrowed in `chatResponse`)
- Null checks on `config`, `promptSupport`, `model`
- Fallback to zero rates when timings are missing

#### Concurrency
- None noted
