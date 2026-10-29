### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:57:02Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation capabilities by interfacing with GGUF models via llama.cpp JNI bindings.

#### Purpose
* Implements AiGenerationProvider for local LLM inference.
* Manages the lifecycle and configuration of the LlamaModel instance.

#### Type
final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable

#### Input
* Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport.
* Method `generate`: AiGenerationRequest request.
* Method `generateWithTimings`: AiGenerationRequest request.

#### Output
* Method `generate`: String (generated text content).
* Method `generateWithTimings`: AiGenerationTimings (text and performance metrics).
* Method `close`: None (releases native resources).

#### Core logic
* Lazily initializes LlamaModel upon first call to `model()` method.
* `model()` constructs ModelParameters based on LlamaCppJniConfig settings (e.g., context size, GPU layers, cache reuse).
* `model()` initializes LlamaModel using the configured parameters.
* `generate()` calls `chatResponse()` to get the full response, then parses the content using AiCompletionParser.
* `chatResponse()` calls `model().chatComplete()` and parses the result using ChatResponseParser.
* `generateWithTimings()` retrieves the full response, extracts content and Timings, and returns AiGenerationTimings.
* `close()` calls `current.close()` on the LlamaModel if it exists.

#### Public API
generate(AiGenerationRequest) -> String: Generates text content from the request.
generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: Generates text and performance metrics.
close() -> void: Releases native LlamaModel resources.

#### Dependencies
AiGenerationProvider, AutoCloseable, LlamaCppJniConfig, AiPromptSupport, LlamaModel, FlashAttn, ReasoningFormat, ChatResponseParser, ModelParameters, ChatResponse, Timings, AiGenerationRequest, AiCompletionParser, Java8CompatibilityHelper, Nullable.

#### Exceptions / Errors
* Throws IOException in `generate` if native calls fail.
* Handles null Timings in `generateWithTimings` by returning zero-rate timings.

#### Concurrency
* The LlamaModel instance is lazily initialized and cached (`model` field), implying thread-safe access is required for concurrent calls to `generate` or `generateWithTimings` if not externally synchronized.
* The class itself is final, suggesting intended immutability of configuration after construction.
