### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:51:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI responses using a local GGUF model via JNI bindings, supporting customizable inference parameters and prompt templating.

#### Purpose
- Provides AI text generation using a native llama.cpp backend.
- Supports dynamic configuration of inference parameters and prompt rendering.

#### Type
Final class implementing `AiGenerationProvider` and `AutoCloseable`. Uses Lombok's `@ToString` with `@ToString.Exclude` for native model handle.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`.
- `generate` methods take `AiGenerationRequest` and optional `temperatureOverride`.
- Reads model path, context size, thread count, and other config from `LlamaCppJniConfig`.

#### Output
- Returns generated AI text as a `String`.
- Mutates internal `model` field on first use.
- Closes native `LlamaModel` handle during `close()`.

#### Core logic
- Lazily initializes native `LlamaModel` on first `generate()` call.
- Builds prompt using `AiPromptSupport`.
- Configures inference parameters via `InferenceParameters` chain.
- Invokes `chatCompleteText` on the model with configured parameters.
- Parses output using `AiCompletionParser`.

#### Public API
- `generate(AiGenerationRequest) -> String` Generates AI text with default temperature.
- `generate(AiGenerationRequest, float) -> String` Generates AI text with custom temperature.
- `close() -> void` Releases native model resources.

#### Dependencies
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `AiCompletionParser`
- `LlamaModel`
- `InferenceParameters`
- `ModelParameters`
- `Pair<String, String>`
- `AiGenerationRequest`

#### Exceptions / Errors
- Throws `IOException` during model inference or prompt building.
- Null checks on constructor parameters and prompt support.

#### Concurrency
- Not thread-safe; native `LlamaModel` is not marked as thread-safe.
- Lazy initialization uses double-checked locking pattern for model.
