### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:48:15Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a JNI-backed implementation for generating AI text using local llama.cpp models, supporting configurable prompts and inference settings.

#### Purpose
- **To provide a JNI-backed implementation** of an AI text generation provider that uses local llama.cpp models.
- **To support configurable prompts and inference settings** for generating AI text based on user requests.

#### Type
```java
public final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable
```

#### Input
- `config`: `LlamaCppJniConfig` - configuration for the llama.cpp model.
- `promptSupport`: `AiPromptSupport` - support for building prompts based on user requests.

#### Output
- `String` - generated AI text based on the provided request and configuration settings.
- Throws `IOException` - if there's an issue with input/output operations.

#### Core logic
1. **Initialization**: Constructs a new `LlamaCppJniAiGenerationProvider` object, initializing it with the given configuration and prompt support.
2. **Model Loading**: Loads the GGUF model lazily on the first call to `generate(...)`, using the provided configuration settings.
3. **Prompt Building**: Constructs a prompt based on the user request using the `promptSupport`.
4. **Inference Parameters Configuration**: Sets up inference parameters including messages, temperature, and other settings based on the configuration.
5. **Completion Parsing**: Parses the completion text from the model's output using `AiCompletionParser`.
6. **Resource Management**: Closes the model when the provider is closed, releasing resources.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates AI text based on the given request.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates AI text with an optional temperature override.
- `close()`: Closes the provider, releasing native resources.

#### Dependencies
- `net.ladenthin.llama.LlamaModel` - JNI binding for llama.cpp models.
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest` - Represents an AI generation request.
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport` - Supports building prompts for AI generation.
- `org.jspecify.annotations.Nullable` - For nullable type annotations.

#### Exceptions / Errors
- Throws `IOException` if there's an issue with input/output operations during generation.

#### Concurrency
- The provider is not thread-safe; it should be managed in a single-threaded context or with appropriate synchronization.
