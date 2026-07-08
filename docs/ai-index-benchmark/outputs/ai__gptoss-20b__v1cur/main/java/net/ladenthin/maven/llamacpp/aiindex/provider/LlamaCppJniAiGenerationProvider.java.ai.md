### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 3EAED1F5
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:13:50Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 5; TODO/FIXME: 0; @Override: 3; methods (approx): 6; constructors: 1; field declarations (w/ modifier): 9

> Provides local, llama.cpp‑backed text generation for Maven AI indexing, exposing a high‑level request API and timing metrics.  

#### Purpose
- Implements `AiGenerationProvider` using the `net.ladenthin:llama` JNI binding.  
- Delivers text completions and optional timing data for AI index generation.

#### Type
- `final class` with `@ToString` Lombok annotation.  
- Implements `AiGenerationProvider, AutoCloseable`.  
- Fields: `config`, `model`, `promptSupport`, `completionParser`, `chatResponseParser`, `compatibilityHelper`.  
- Static constants: `REUSE_SLOT_ID`, `ENABLE_THINKING_KWARG`, `REASONING_EFFORT_KWARG`, `CHAT_TEMPLATE_KWARG_COUNT`.

#### Input
- Constructor receives `LlamaCppJniConfig config` and `AiPromptSupport promptSupport`.  
- `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` consume `AiGenerationRequest` objects.  
- `buildInferenceParameters` uses `AiGenerationRequest` plus `config` to create `InferenceParameters`.

#### Output
- `generate` returns a `String` completion.  
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt token count, prompt rate, predicted token count, predicted rate).  
- `close()` frees the native `LlamaModel`.

#### Core logic
- **Lazy model loading**: `model()` constructs a `LlamaModel` on first use, configuring it with model path, context size, threads, GPU settings, SWA, cache reuse, and chat‑template kwargs.  
- **Prompt construction**: `buildInferenceParameters` builds immutable `InferenceParameters` by combining system and user prompts, chat‑template usage, temperature, token limits, top‑p/k, penalties, DRY settings, stop strings, cache prompt, and slot ID.  
- **Text generation**: `generate` calls `chatCompleteText` via `completionParser`.  
- **Timing extraction**: `generateWithTimings` parses full JSON response with `chatResponseParser`, extracts `Timings`, and packages them with the first choice’s content.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce completion text with model timings.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.*`, `net.ladenthin.llama.value.*`.  
- `net.ladenthin.maven.llamacpp.aiindex.*` (request, prompt, support).  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- Internal unchecked exceptions may propagate from native calls or parsing.  
- Null checks via `Objects.requireNonNull` for constructor parameters.

#### Concurrency
- Thread‑unsafe; `LlamaModel` is not synchronized.  
- Instance state (model, config) is immutable after construction except for lazy initialization.
