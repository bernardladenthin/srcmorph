### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:20:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides local LLM text generation via JNI-backed llama.cpp for AI index document processing.

#### Purpose
- Implements `AiGenerationProvider` interface for generating AI completions using local GGUF models.
- Manages lifecycle of native llama.cpp instance including lazy initialization and cleanup.

#### Type
Class: `final`, extends `Object`, implements `AiGenerationProvider` and `AutoCloseable`. Fields annotated with `@ToString.Exclude` and `@Nullable`. Lombok `@ToString` applied.

#### Input
- Constructor parameters: `LlamaCppJniConfig`, `AiPromptSupport`.
- Method parameters: `AiGenerationRequest`, `float temperatureOverride`.
- Consumed fields: `config`, `promptSupport`, internal `model` handle, `completionParser`.
- Resources read: configuration values (model path, context size, threads), prompt templates.

#### Output
- Return type: `String` containing generated AI completion text.
- Produced state: Cached `LlamaModel` instance after first generation.
- Side effects: Native memory allocation for model loading, potential native pointer access.

#### Core logic
- Lazily initializes native `LlamaModel` from GGUF file on first `generate()` call using configuration parameters.
- Constructs chat messages list from request and prompt support template.
- Configures inference parameters (temperature, tokens, top-p/k, repeat penalty) before calling native `chatCompleteText`.
- Parses raw completion output into structured text via `AiCompletionParser`.

#### Public API
- `generate(AiGenerationRequest) -> String`: Generates text using default temperature.
- `generate(AiGenerationRequest, float) -> String`: Generates text with overridden temperature.
- `close() -> void`: Releases native model resources and closes handle.

#### Dependencies
`net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `LlamaModel`, `InferenceParameters`, `AiCompletionParser`, `java.util.List`, `java.io.IOException`.

#### Exceptions / Errors
- Throws `IOException` from `generate()` and `close()` methods.
- Validates input arguments via `Objects.requireNonNull` in constructor.
- Handles null model gracefully during close operation.

#### Concurrency
- Single-threaded design assuming sequential access to shared `model` state.
- Lazy initialization ensures native resources are not shared across threads without synchronization.
- Immutable inference parameters prevent race conditions during configuration assembly.
