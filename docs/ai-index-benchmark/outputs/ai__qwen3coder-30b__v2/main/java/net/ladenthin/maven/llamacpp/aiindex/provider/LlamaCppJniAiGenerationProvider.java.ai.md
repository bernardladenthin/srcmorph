### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:18:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides local AI text generation using GGUF models via JNI bindings.

#### Purpose
- Wraps JNI llama.cpp integration for local inference.
- Supports prompt building, completion parsing, and resource management.

#### Type
Final class implementing `AiGenerationProvider` and `AutoCloseable`; extends no types; key generics: `LlamaCppJniConfig`, `AiPromptSupport`.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`
- `generate(AiGenerationRequest, float)`: `AiGenerationRequest`, `temperatureOverride`
- Internal use: `config` fields (`modelPath`, `contextSize`, etc.), `promptSupport.buildPrompt()`

#### Output
- `generate(...)`: `String` completion
- Side effects: lazy model loading, native resource cleanup via `close()`

#### Core logic
- Lazily initializes `LlamaModel` on first use to defer native load.
- Builds prompt using `AiPromptSupport`.
- Constructs `InferenceParameters` with config and request data.
- Calls `model.chatCompleteText()` and parses result with `AiCompletionParser`.

#### Public API
- `generate(AiGenerationRequest) -> String`: Generates text from request.
- `generate(AiGenerationRequest, float) -> String`: Generates with override temperature.
- `close() -> void`: Releases native model resources.

#### Dependencies
`LlamaModel`, `InferenceParameters`, `ModelParameters`, `Pair`, `AiGenerationRequest`, `AiPromptSupport`, `AiCompletionParser`

#### Exceptions / Errors
Throws `IOException` during generation; null-checked inputs via `Objects.requireNonNull`.

#### Concurrency
Not explicitly thread-safe; model access synchronized via lazy initialization.
