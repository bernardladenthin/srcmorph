### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:36Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Provides local AI generation by loading a GGUF model via llama.cpp and executing chat completions.

#### Purpose
- Loads and caches a native `LlamaModel` lazily.
- Executes chat completions for `AiGenerationRequest`.
- Parses responses into plain text or detailed timings.

#### Type
- `final class LlamaCppJniAiGenerationProvider`  
  *implements* `AiGenerationProvider`, `AutoCloseable`  
  *extends* none  
  *annotations* `@ToString` (excludes `model`, `support`)  
  *fields*  
  - `private final LlamaCppJniConfig config` (final)  
  - `@Nullable private LlamaModel model` (lazy, excluded)  
  - `private final AiPromptSupport promptSupport` (final)  
  - `private final LlamaCppJniProviderSupport support` (final, excluded)  
  - `private final AiCompletionParser completionParser` (final)  
  - `private final ChatResponseParser chatResponseParser` (final)  
  - `private final Java8CompatibilityHelper compatibilityHelper` (final)

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`.
- `generate(AiGenerationRequest request)` / `generateWithTimings(AiGenerationRequest request)` receive `AiGenerationRequest`.
- `chatResponse(AiGenerationRequest request)` builds inference parameters via `support.buildInferenceParameters(request)`.

#### Output
- `generate` returns `String` (first content of chat response).
- `generateWithTimings` returns `AiGenerationTimings` (text, prompt tokens, cache tokens, prompt rate, predicted tokens, predicted rate).
- `close` releases native `LlamaModel` resources.

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` built from `config` and `support.buildChatTemplateKwargs()`.  
- `generate`:
  1. Calls `chatResponse(request)` to get `ChatResponse`.
  2. Warns on truncated answer via `support.warnOnTruncatedAnswer`.
  3. Logs prompt cache reuse via `support.logPromptCacheReuse`.
  4. Parses completion text with `completionParser`.
- `chatResponse`:
  1. Calls `model().chatComplete(support.buildInferenceParameters(request))`.
  2. Parses full JSON with `chatResponseParser`.
- `generateWithTimings`:
  1. Calls `chatResponse(request)`.
  2. Extracts text and `Timings`.
  3. Builds `AiGenerationTimings` with metrics or defaults if missing.
- `close` disposes native model if loaded.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce completion text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce completion text with metrics.  
- `close() -> void` – release native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.*`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.parameters.ModelParameters`, `net.ladenthin.llama.value.*`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `generate` declares `throws IOException`.  
- `model()` may throw unchecked exceptions from native calls.  
- `close` handles null model gracefully.

#### Concurrency
- No explicit synchronization; `model` is lazily initialized without double‑checked locking, relying on single‑threaded usage or external coordination.  
- `AutoCloseable` ensures deterministic cleanup.
