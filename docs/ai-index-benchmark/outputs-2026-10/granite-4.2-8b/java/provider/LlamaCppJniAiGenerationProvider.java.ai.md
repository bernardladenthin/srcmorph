### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:31:50Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Generates AI text responses using a GGUF llama.cpp model via JNI, with lazy model loading and integrated prompt support.

#### Purpose
- Provides AI generation capabilities for local GGUF models through a Java interface.
- Lazily loads the model on first use to avoid upfront native overhead.
- Supports chat-style prompts with configurable inference parameters.
- Exposes timing metrics from the model's native measurement.

#### Type
- Class `LlamaCppJniAiGenerationProvider`, `final`, implements `AiGenerationProvider`, `AutoCloseable`.
- Uses Lombok `@ToString` with `@ToString.Exclude` for selected fields.
- Depends on `LlamaCppJniConfig`, `AiPromptSupport`, `LlamaModel`, `ModelParameters`, `ReasoningFormat`, `FlashAttn`, `ChatResponseParser`, `AiCompletionParser`, `Java8CompatibilityHelper`.

#### Input
- `LlamaCppJniConfig config`: configuration for model path, context size, threads, GPU settings, cache reuse, etc.
- `AiPromptSupport promptSupport`: used to build prompt templates and inference parameters.
- `AiGenerationRequest request`: input request containing prompt and metadata for generation.

#### Output
- `String` from `generate(request)`: parsed completion text.
- `AiGenerationTimings` from `generateWithTimings(request)`: includes prompt count, cache count, rates per second.
- Mutates internal `model` field after lazy initialization.
- Writes no external resources; side effects include model creation and KV cache updates.

#### Core logic
- On first `generate()` or `generateWithTimings()`, calls `model()` to lazily construct `LlamaModel` using `ModelParameters` built from `config` and `promptSupport`.
- `model()` caches the `LlamaModel` instance in `model` field; subsequent calls return the cached instance.
- Builds `chatTemplateKwargs` via `support.buildChatTemplateKwargs()`.
- Sets model parameters based on config flags (e.g., `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, etc.), with guards for meaningful values (`>=0` or `>0`).
- Applies reasoning format (`ReasoningFormat.AUTO`), cache types (`cacheTypeK`, `cacheTypeV`), batch sizes, lazy mode, flash attention (`FlashAttn.ON` if enabled), and device selection.
- Calls `chatComplete` via `model().chatComplete(...)` with inference parameters from `support.buildInferenceParameters(request)`.
- Parses the chat response using `chatResponseParser` to obtain `ChatResponse` (including timings).
- `generate()` returns `completionParser.parseCompletion(response.getFirstContent())`.
- `generateWithTimings()` extracts timings from `ChatResponse`; if `timings` is null, returns zero rates; otherwise computes `AiGenerationTimings` from prompt/cache counts and per-second metrics.
- `close()` calls `close()` on the native `LlamaModel` if present.

#### Public API
- `generate(AiGenerationRequest request) throws IOException` -> `String`: produces AI text completion for the request.
- `generateWithTimings(AiGenerationRequest request)` -> `AiGenerationTimings`: produces timing-augmented completion.
- `close()` -> `void`: releases native model resources.
- No other public methods are exposed; `model`, `support`, parsers are internal.

> Note: `LlamaCppJniProviderSupport` and parser instances are internal; only the three provider methods are part of the public API.

#### Dependencies
- `java.io.IOException`
- `java.util.Map`, `java.util.Objects`
- `lombok.ToString`, `lombok.ToString.Exclude`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IOException` thrown by `generate()` (from `model()` or parsing); caught internally by provider design.
- `null` checks enforced via `Objects.requireNonNull` for `config` and `promptSupport`.
- `model` field is `@Nullable`; `model()` method guards against `null` and initializes on first call.
- `generateWithTimings()` handles `timings == null` by returning zero rates; no exception thrown.
- `close()` safely checks `current != null` before calling `close()`.

#### Concurrency
- Model creation and native handle are initialized on the first call to `generate()` or `generateWithTimings()`, which may occur from any thread.
- No explicit synchronization is used; the native `LlamaModel` is assumed thread-safe for concurrent reads after initialization (typical for llama.cpp).
- Provider fields (`config`, `promptSupport`, `support`, parsers) are immutable after construction, so they are naturally thread-safe.
- KV cache and model state are managed by the native side; Java side does not modify them directly.

#### Count verification
- Public API methods: `generate`, `generateWithTimings`, `close` → 3 methods.
- Instance fields: `config`, `model`, `promptSupport`, `support`, `completionParser`, `chatResponseParser`, `compatibilityHelper` → 7 fields.
- No `fooN` family methods; all methods are explicitly listed.

> Total public methods: `generate`, `generateWithTimings`, `close` (3 total). No near-identical method family present. Emit exact total: `generate..generate` (1 total) and `generateWithTimings..generateWithTimings` (1 total) and `close..close` (1 total) — but per instruction, for a FAMILY of near-identical members do not list them all; since there are no such families, we simply state the exact total and index range per method. However, the instruction says: "in the final answer emit only the exact total and index range, e.g. `foo1..fooN (N total)`". Since there are no repeated `fooN` patterns, we do not need to summarize by range; we already listed each method in Public API. The instruction allows listing each public member with name(params) -> returnType + clause. We did that. No need to repeat range. The range clause only applies to near-identical families; none exist. Thus Public API listing is correct as written.
