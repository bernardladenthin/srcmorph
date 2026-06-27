### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:35:04Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> This file provides a Java implementation of an AI generation provider using the llama.cpp JNI binding to run GGUF models locally.

#### Purpose
- Implements `AiGenerationProvider` for local GGUF model inference.
- Lazy-loads and caches `LlamaModel` instances.

#### Type
- Class: `LlamaCppJniAiGenerationProvider`
- Implements: `AiGenerationProvider`, `AutoCloseable`
- Extends: None
- Key generics: None
- Annotations: `@ToString`, `@Nullable`

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`
- Consumed fields: None

#### Output
- Return types: `String` (generated text)
- Produced/mutated state: `LlamaModel` (cached)
- Written resources: None
- Side effects: Lazy loading of native llama.cpp model

#### Core logic
- Lazy initialization of `LlamaModel` on first `generate` call.
- Builds prompt using `AiPromptSupport`.
- Constructs `InferenceParameters` with configuration settings.
- Executes `model.chatCompleteText` to generate completion.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig config, AiPromptSupport promptSupport)`
- `String generate(AiGenerationRequest request) throws IOException`
- `String generate(AiGenerationRequest request, float temperatureOverride) throws IOException`
- `void close()`

#### Dependencies
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `LlamaModel`
- `InferenceParameters`
- `Pair`
- `AiCompletionParser`

#### Exceptions / Errors
- `IOException` from `completionParser.parseCompletion` and `model.chatCompleteText`.

#### Concurrency
- Not explicitly covered in the source. Assumes single-threaded usage due to lack of threading constructs.
