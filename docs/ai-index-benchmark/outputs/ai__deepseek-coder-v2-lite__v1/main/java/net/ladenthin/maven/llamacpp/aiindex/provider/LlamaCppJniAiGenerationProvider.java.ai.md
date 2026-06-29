### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:17:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices backed by the JNI binding of the llama.cpp library, providing AI-generated responses based on user prompts.

#### Purpose
- Provide AI-generated text responses based on user prompts using a local GGUF model via the llama.cpp library.

#### Type
- Class: `LlamaCppJniAiGenerationProvider`
- Modifiers: `final`
- Implements: `AiGenerationProvider`, `AutoCloseable`

#### Input
- Constructor parameters: `config`, `promptSupport`
- Method parameters: `request`, `temperatureOverride`

#### Output
- Return type: `String`
- Produced state: AI-generated text response
- Mutated fields: `model`

#### Core logic
- Lazily initializes the `LlamaModel` on the first call to `generate()`.
- Constructs a prompt using `AiPromptSupport.buildPrompt()`.
- Sets up `InferenceParameters` for model inference.
- Parses the completion result using `AiCompletionParser.parseCompletion()`.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates AI-generated text based on the request.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates AI-generated text with an optional temperature override.
- `close()`: Closes the `LlamaModel` when no longer needed.

#### Dependencies
- `java.io.IOException`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`, `java.util.Objects`
- `lombok.ToString`
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.InferenceParameters`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.Pair`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IOException` in `generate()` methods.
- Handles null inputs and parameters gracefully, using `Objects.requireNonNull()`.

#### Concurrency
- The provider is designed to be thread-safe as long as the underlying `LlamaModel` is thread-safe.
- The `model` field is marked with `@ToString.Exclude` to exclude it from string representations for security and performance reasons.
