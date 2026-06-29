### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:48:00Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides AI text generation using locally-run GGUF models via llama.cpp JNI binding.

#### Purpose
- Implements `AiGenerationProvider` interface
- Manages lifecycle of native LLama model
- Handles AI prompt generation and completion parsing

#### Type
class final implements AiGenerationProvider, AutoCloseable

#### Input
- `LlamaCppJniConfig`, `AiPromptSupport` in constructor
- `AiGenerationRequest` in generate methods
- Model parameters from config

#### Output
- Generated text as `String`
- May mutate internal `LlamaModel` state
- Closes native resources on `close()`

#### Core logic
1. Lazy-loads GGUF model on first generation
2. Builds AI prompts using `AiPromptSupport`
3. Configures inference parameters from config
4. Calls native model for text completion
5. Parses and returns generated text

#### Public API
- `LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> void`
  Initializes provider with configuration and prompt support
- `generate(AiGenerationRequest) -> String`
  Generates text using default temperature
- `generate(AiGenerationRequest, float) -> String`
  Generates text with specified temperature override
- `close() -> void`
  Releases native resources

#### Dependencies
- `LlamaCppJniConfig`
- `AiPromptSupport`
- `AiGenerationRequest`
- `LlamaModel`
- `InferenceParameters`
- `Pair`
- `AiCompletionParser`

#### Exceptions / Errors
- Throws `IOException` for native model loading or generation errors
- Handles null values in input parameters
