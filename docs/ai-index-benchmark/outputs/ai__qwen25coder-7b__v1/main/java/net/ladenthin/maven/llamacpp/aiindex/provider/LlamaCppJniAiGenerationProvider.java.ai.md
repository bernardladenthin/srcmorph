### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:41:24Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Implements an AI generation provider using the llama.cpp JNI binding to generate text based on prompts.

#### Purpose
- Provides a service for generating AI text based on prompts using the llama.cpp JNI binding.
- Lazy loads the GGUF model on first use and caches it for subsequent calls.
- Handles the generation process, including building prompts, setting inference parameters, and parsing completions.

#### Type
- Class
- Final
- Implements `AiGenerationProvider` and `AutoCloseable`
- Imports Lombok's `@ToString` annotation

#### Input
- Constructor parameters:
  - `config` (`LlamaCppJniConfig`)
  - `promptSupport` (`AiPromptSupport`)
- Method parameters:
  - `request` (`AiGenerationRequest`)
  - `temperatureOverride` (`float`)

#### Output
- Return type: `String`
- Produced state: None
- Mutated fields: `model`
- Written resources: None
- Side effects: Loads the GGUF model, generates text using llama.cpp, and closes the model handle

#### Core logic
- Lazy loads the GGUF model on first use.
- Builds a prompt from the `AiGenerationRequest`.
- Sets inference parameters based on the configuration and request.
- Calls `chatCompleteText` on the model to generate text.
- Parses the generated completion using `AiCompletionParser`.

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig config, AiPromptSupport promptSupport)` -> Initializes the provider with a configuration and prompt support object.
- `String generate(AiGenerationRequest request) throws IOException` -> Generates text using the default temperature.
- `String generate(AiGenerationRequest request, float temperatureOverride) throws IOException` -> Generates text with a specified temperature override.
- `void close()` -> Closes the model handle.

#### Dependencies
- `net.ladenthin.llama.LlamaModel`
- `net.ladenthin.llama.parameters.InferenceParameters`
- `net.ladenthin.llama.parameters.ModelParameters`
- `net.ladenthin.llama.value.Pair`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `IOException` from the `generate` methods.

#### Concurrency
- Not explicitly handled in the provided source.
