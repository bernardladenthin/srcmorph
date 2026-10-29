### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:14:37Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation using locally-run GGUF models via llama.cpp JNI binding

#### Purpose
- Implements `AiGenerationProvider` interface
- Manages native `LlamaModel` handle for AI text generation

#### Type
- `final class`
- Implements `AutoCloseable`
- Uses Lombok `@ToString`

#### Input
- `LlamaCppJniConfig` and `AiPromptSupport` in constructor
- `AiGenerationRequest` in `generate` and `generateWithTimings` methods

#### Output
- Generated text as `String`
- `AiGenerationTimings` object in `generateWithTimings`

#### Core logic
- Lazy-loads GGUF model on first use
- Configures model parameters based on input config
- Parses chat responses and completions
- Handles timing information for generation

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`
- `generate(AiGenerationRequest) -> String`
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`
- `close() -> void`

#### Dependencies
- `java.io.IOException`
- `java.util.Map`
- `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.*` packages
- `net.ladenthin.srcmorph.*` packages
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IOException` in `generate` method
- Handles null timings in `generateWithTimings`

#### Concurrency
- Not explicitly thread-safe
- Uses `@Nullable` for model handle
