### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:02:18Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI responses using a local GGUF model via JNI bindings, supporting configurable inference parameters and prompt templating.

#### Purpose
- Provides AI text generation using a native llama.cpp model.
- Supports dynamic prompt building and inference configuration.

#### Type
Final class implementing `AiGenerationProvider` and `AutoCloseable`. Extends no types. Implements interfaces: `AiGenerationProvider`, `AutoCloseable`.

#### Input
- Constructor accepts `LlamaCppJniConfig` and `AiPromptSupport`.
- `generate()` methods take `AiGenerationRequest` and optional `temperatureOverride`.
- Reads model path, context size, thread count, and inference settings from `config`.
- Consumes `promptSupport` to render prompts from requests.

#### Output
- Returns generated text as `String`.
- Mutates internal `model` field on first use.
- Produces side effect of loading native GGUF model on first call.

#### Core logic
- Lazily initializes native `LlamaModel` on first `generate()` call.
- Builds user prompt using `AiPromptSupport`.
- Constructs `InferenceParameters` with request and config values.
- Invokes `chatCompleteText` on the model with parameters.
- Parses output using `AiCompletionParser`.

#### Public API
- `generate(AiGenerationRequest) -> String` generates response with default temperature.
- `generate(AiGenerationRequest, float) -> String` generates response with custom temperature.
- `close() -> void` releases native model resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.InferenceParameters`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.Pair`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniConfig`
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiCompletionParser`

#### Exceptions / Errors
- Throws `IOException` during model inference or prompt building.
- Null checks on constructor parameters.

#### Concurrency
- Not thread-safe; assumes single-threaded use per instance.
- Model loading is synchronized via null check and assignment.
