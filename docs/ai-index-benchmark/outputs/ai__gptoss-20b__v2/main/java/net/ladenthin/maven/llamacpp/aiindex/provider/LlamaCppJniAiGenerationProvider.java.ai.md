### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 7DBF06B9
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:14:42Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides local AI text generation using llama.cpp via JNI, lazily loading a GGUF model and parsing chat completions.

#### Purpose
- Supplies text completions for AI generation requests.
- Lazily loads and caches a native LlamaModel.

#### Type
final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable @ToString

#### Input
- LlamaCppJniConfig config (modelPath, contextSize, threads, etc.)
- AiPromptSupport promptSupport
- AiGenerationRequest request
- Optional float temperatureOverride

#### Output
- String completion
- closes native LlamaModel on close()

#### Core logic
- `model()` lazily constructs `LlamaModel` with `ModelParameters` from config.
- `generate(request)` calls `generate(request, config.temperature())`.
- `generate(request, temp)` builds prompt via `promptSupport.buildPrompt`.
- Builds `InferenceParameters` with messages, temperature, NPredict, topP, topK, repeatPenalty, stopStrings.
- Calls `model().chatCompleteText(inferenceParameters)` and parses result with `AiCompletionParser.parseCompletion`.
- `close()` releases native resources if model is loaded.

#### Public API
- generate(AiGenerationRequest) -> String   produces completion with configured temperature
- generate(AiGenerationRequest, float) -> String   produces completion with overridden temperature
- close() -> void   releases native model

#### Dependencies
- LlamaCppJniConfig, AiPromptSupport, AiGenerationRequest, AiCompletionParser, LlamaModel, InferenceParameters, ModelParameters, Pair

#### Exceptions / Errors
- generate methods throw IOException if native call fails
- close() handles absent model gracefully

#### Concurrency
- No thread‑safety guarantees; single‑thread use implied.
