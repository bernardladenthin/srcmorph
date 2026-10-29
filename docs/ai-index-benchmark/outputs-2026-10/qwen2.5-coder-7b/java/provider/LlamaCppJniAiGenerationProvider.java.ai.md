### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:17:14Z
- G: 
- A: 0.0.0
- X: file
---
> Implements an AI generation provider using the Llama.cpp JNI binding for running GGUF models locally.

#### Purpose
- Provides an AI generation service using the Llama.cpp JNI binding.
- Caches the GGUF model for subsequent generation calls.
- Parses chat responses and extracts completion text.
- Supports logging and warning on truncated answers.
- Measures and returns generation timings.

#### Type
- Class: `LlamaCppJniAiGenerationProvider`
- Modifiers: `final`
- Implements: `AiGenerationProvider`, `AutoCloseable`
- Extends: None
- Key generics: None
- Notable annotations: `@ToString`, `@Nullable`

#### Input
- Constructor parameters:
  - `config` (`LlamaCppJniConfig`): llama.cpp configuration.
  - `promptSupport` (`AiPromptSupport`): prompt lookup for rendering request prompts.
- Method parameters:
  - `request` (`AiGenerationRequest`): the generation request.
- Consumed fields:
  - `config.modelPath()`, `config.contextSize()`, `config.threads()`, etc.
- Read resources:
  - `config.modelPath()`: path to the GGUF model file.

#### Output
- Return types:
  - `generate`: `String` (completion text)
  - `generateWithTimings`: `AiGenerationTimings` (completion text and timings)
- Produced state:
  - Caches the GGUF model.
- Mutated fields:
  - `model`: stores the native LlamaModel handle.
- Written resources:
  - None
- Side effects:
  - Loads the GGUF model on first use.
  - Logs prompt cache reuse.
  - Warns on truncated answers.

#### Core logic
- **Constructor**: Initializes the provider with configuration and prompt support.
- **model()**: Lazily loads and caches the GGUF model based on configuration settings.
- **generate(AiGenerationRequest request)**: Generates AI completion text and logs warnings/truncations.
- **chatResponse(AiGenerationRequest request)**: Runs a chat completion and parses the full response.
- **generateWithTimings(AiGenerationRequest request)**: Generates AI completion text and returns timings.
- **close()**: Closes the native LlamaModel handle if it exists.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates AI completion text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`: Generates AI completion text and returns timings.
- `close() -> void`: Closes the native LlamaModel handle.

#### Dependencies
- `java.io.IOException`
- `java.util.Map`
- `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.args.FlashAttn`
- `net.ladenthin.llama.args.ReasoningFormat`
- `net.ladenthin.llama.json.ChatResponseParser`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Timings`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IOException` from `generate` method.
- Handles `null` values for `config` and `promptSupport` using `Objects.requireNonNull`.
- Caches the GGUF model and handles potential `IOException` during model loading.

#### Concurrency
- The provider is designed to be thread-safe, with lazy initialization of the native model handle.
- Uses `@ToString.Exclude` to avoid noisy `toString` output from the native model handle.
