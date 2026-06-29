### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:57:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a local Java implementation for generating text using GGUF models via the llama.cpp JNI backend.

#### Purpose
- Implements `AiGenerationProvider` to run LLM inference locally.
- Manages lazy loading and caching of native GGUF model handles.

#### Type
Final class; implements `AiGenerationProvider` and `AutoCloseable`; annotated with `@ToString`.

#### Input
- `LlamaCppJniConfig`: model path, context size, threads, chat template settings.
- `AiPromptSupport`: prompt builder for request rendering.
- `AiGenerationRequest`: input data for generation.
- `float temperatureOverride`: optional temperature override (defaulting to config).

#### Output
- `String`: generated text completion.
- Side effect: loads native `LlamaModel` handle on first use if not cached; releases handle in `close()`.

#### Core logic
- Validates and stores configuration and prompt support.
- Lazily instantiates `LlamaModel` using config parameters only when `generate()` is called.
- Constructs inference parameters (messages, chat template, temperature, topP, topK, stop strings) from request and config.
- Executes `model().chatCompleteText()` to obtain completion tokens.
- Parses the raw completion text via `AiCompletionParser` into a clean string result.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport)`: creates provider with config and prompt support.
- `String generate(AiGenerationRequest)`: generates text using default temperature.
- `String generate(AiGenerationRequest, float)`: generates text with optional temperature override.
- `void close()`: releases native model resources.

#### Dependencies
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `AiCompletionParser`
- `LlamaModel`
- `InferenceParameters`
- `AiGenerationRequest`
- `Pair`

#### Exceptions / Errors
- Throws `IOException` during generation if native call fails.
- Throws `NullPointerException` if config or prompt support is null (via `Objects.requireNonNull`).

#### Concurrency
- Not thread-safe; `LlamaModel` handle must be accessed sequentially.
- Uses immutable builder pattern for inference parameters to avoid concurrency issues during configuration setup.
