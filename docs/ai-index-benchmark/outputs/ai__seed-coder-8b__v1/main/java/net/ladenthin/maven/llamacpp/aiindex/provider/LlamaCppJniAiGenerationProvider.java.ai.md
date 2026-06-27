### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:17:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides AI text generation using locally-run GGUF models via llama.cpp JNI binding

#### Purpose
- Implements `AiGenerationProvider` for AI text generation
- Uses llama.cpp JNI binding to run GGUF models locally

#### Type
- Class
- Final
- Implements `AiGenerationProvider`, `AutoCloseable`
- Uses Lombok's `@ToString` annotation

#### Input
- `LlamaCppJniConfig` and `AiPromptSupport` in constructor
- `AiGenerationRequest` and optional temperature in `generate` methods

#### Output
- Generated text as `String` from `generate` methods
- Closes native resources with `close` method

#### Core logic
- Lazy initialization of llama.cpp model
- Builds AI prompts using `AiPromptSupport`
- Configures inference parameters for text generation
- Parses completion results using `AiCompletionParser`

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`
- `generate(AiGenerationRequest) -> String`
- `generate(AiGenerationRequest, float) -> String`
- `close() -> void`

#### Dependencies
- `java.io.IOException`
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.InferenceParameters`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.Pair`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IOException` for I/O errors during generation
- Handles null model state in `generate` methods
- Propagates exceptions from native llama.cpp calls

#### Concurrency
- Thread-safe due to immutable configuration and local model execution
- Model initialization is synchronized via double-checked locking pattern
