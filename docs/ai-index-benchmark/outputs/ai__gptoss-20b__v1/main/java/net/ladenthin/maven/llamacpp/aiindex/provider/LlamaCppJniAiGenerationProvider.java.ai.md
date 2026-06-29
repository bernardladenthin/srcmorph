### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:22:42Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates text with a local llama.cpp model via JNI.

#### Purpose
- Implements local text generation for AiGenerationProvider
- Lazy‑initializes llama.cpp model to reduce startup cost

#### Type
- final class `LlamaCppJniAiGenerationProvider`
- implements `AiGenerationProvider`, `AutoCloseable`
- fields: `LlamaCppJniConfig config`; `@Nullable LlamaModel model`; `AiPromptSupport promptSupport`; `AiCompletionParser completionParser`

#### Input
- Constructor receives `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`
- `generate(request)` consumes `AiGenerationRequest`
- `generate(request, temperatureOverride)` consumes `AiGenerationRequest`, `float temperatureOverride`

#### Output
- `generate(...)` returns generated text `String`
- `close()` frees native llama model resources

#### Core logic
- Lazily create `LlamaModel` with `ModelParameters` derived from `config`
- Build request prompt via `promptSupport.buildPrompt(request)`
- Wrap prompt in a user `Pair<String,String>` message list
- Configure `InferenceParameters` with chat template, temperature, max output tokens, top‑p, top‑k, repeat penalty, stop strings
- Call `model.chatCompleteText(inferenceParameters)` to get raw completion
- Parse raw completion through `AiCompletionParser.parseCompletion`

#### Public API
- `LlamaCppJniAiGenerationProvider(config, promptSupport)` → initializes provider
- `generate(request)` → returns text, uses default temperature
- `generate(request, temperatureOverride)` → returns text with overridden temperature
- `close()` → releases native model

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.InferenceParameters`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.Pair`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `org.jspecify.annotations.Nullable`
- `java.io.IOException`
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.ToString`

#### Exceptions / Errors
- `generate` declares `throws IOException`
- Constructor enforces non‑null `config` and `promptSupport` via `Objects.requireNonNull`

#### Concurrency
- `model()` performs unsynchronized lazy initialization; not thread‑safe
- Provider assumes single‑threaded usage or external synchronization
