### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:12:40Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices using the llama.cpp JNI binding and GGUF models locally.

#### Purpose
- Provides a service for generating AI-based completion based on llama.cpp configurations.

#### Type
- Class, final, implements `AiGenerationProvider` and `AutoCloseable`.

#### Input
- `AiGenerationRequest request`: Request parameters for generation.
- `float temperatureOverride`: Override temperature for generation.

#### Output
- `String`: Generated completion text.

#### Core Logic
- Loads the GGUF model lazily on first use.
- Constructs `InferenceParameters` with provided configurations.
- Builds prompt using `AiPromptSupport`.
- Parses completion using `completionParser`.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig config, AiPromptSupport promptSupport)`: Initializes the provider.
- `String generate(AiGenerationRequest request)`: Generates completion text.
- `String generate(AiGenerationRequest request, float temperatureOverride)`: Generates completion text with temperature override.
- `void close()`: Closes the native llama.cpp model.

#### Dependencies
- `java.io.IOException`: For handling I/O exceptions.
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`: For data structures.
- `java.util.Objects`: For object utility methods.
- `lombok.ToString`: For generating toString method.
- `net.ladenthin.llama.LlamaModel`: For llama.cpp model handling.
- `net.ladenthin.llama.parameters.InferenceParameters`: For inference parameters.
- `net.ladenthin.llama.parameters.ModelParameters`: For model parameters.
- `net.ladenthin.llama.value.Pair`: For pairing strings.
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`: For generation request.
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`: For prompt support.

#### Exceptions / Errors
- `IOException`: Thrown when handling I/O operations.

#### Concurrency
- The class is thread-safe due to the lazy initialization of the model and use of immutable parameters. No explicit synchronization is required.
