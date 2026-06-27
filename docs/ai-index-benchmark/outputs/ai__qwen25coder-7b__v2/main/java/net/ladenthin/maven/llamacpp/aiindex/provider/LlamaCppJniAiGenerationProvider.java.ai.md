### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:35:11Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides AI generation functionality using the Llama.cpp JNI binding.

#### Purpose
- Implements `AiGenerationProvider` for generating AI text based on requests.
- Uses a native Llama model for inference.
- Supports lazy loading of the model to save memory and improve initialization performance.

#### Type
```java
public final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable
```

#### Input
- `config` (`LlamaCppJniConfig`): Configuration for Llama.cpp.
- `promptSupport` (`AiPromptSupport`): Support for building AI prompts.
- `request` (`AiGenerationRequest`): Request containing the text prompt and other parameters.

#### Output
- Returns generated AI text as a `String`.

#### Core logic
- **Initialization**: Stores configuration and prompt support in fields.
- **Model Loading**: Lazily loads the native Llama model on first use.
- **Text Generation**: Constructs a prompt, sets inference parameters, and generates completion using the model.
- **Resource Management**: Closes the model when the provider is closed.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates AI text using the default temperature.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates AI text with an overridden temperature.
- `close() -> void`: Closes the native Llama model.

#### Dependencies
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `AiCompletionParser`
- `LlamaModel`
- `InferenceParameters`
- `ModelParameters`
- `Pair`
- `AiGenerationRequest`

#### Exceptions / Errors
- Throws `IOException` if an I/O error occurs during model loading or text generation.

#### Concurrency
- The native model handle is lazily initialized, so the provider can be created without loading the model.
