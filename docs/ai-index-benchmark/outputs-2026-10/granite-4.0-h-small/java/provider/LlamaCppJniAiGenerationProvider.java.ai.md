### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T05:00:34Z
- G: 
- A: 0.0.0
- X: file
---
> Implements AI generation using the llama.cpp JNI binding for GGUF models.

#### Purpose
- Provides AI generation functionality using local GGUF models via llama.cpp JNI binding.
- Handles model loading, inference parameters, and response parsing.

#### Type
- Class: `LlamaCppJniAiGenerationProvider`
- Implements: `AiGenerationProvider`, `AutoCloseable`
- Annotations: `@ToString`

#### Input
- `LlamaCppJniConfig config`: Configuration for llama.cpp.
- `AiPromptSupport promptSupport`: Prompt lookup used to render request prompts.

#### Output
- `String generate(AiGenerationRequest request)`: Returns the generated text.
- `AiGenerationTimings generateWithTimings(AiGenerationRequest request)`: Returns the generated text along with timings.

#### Core logic
- Lazily loads the GGUF model on the first generate call.
- Builds inference parameters based on the request and configuration.
- Calls the native `chatComplete` method to generate the response.
- Parses the response to extract the generated text and timings.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig config, AiPromptSupport promptSupport)`: Constructor.
- `String generate(AiGenerationRequest request)`: Generates text based on the request.
- `AiGenerationTimings generateWithTimings(AiGenerationRequest request)`: Generates text with timings.
- `void close()`: Closes the model.

#### Dependencies
- `java.io.IOException`
- `java.util.Map`
- `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.*`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IOException`: Thrown by `generate` and `generateWithTimings` methods.

#### Concurrency
- No specific concurrency notes or thread-safety considerations mentioned.
