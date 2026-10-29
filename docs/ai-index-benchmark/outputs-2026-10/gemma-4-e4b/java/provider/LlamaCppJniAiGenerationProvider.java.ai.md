### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:57:02Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation capabilities by interfacing with GGUF models via llama.cpp JNI bindings.

#### Purpose
* Implements AiGenerationProvider interface for AI text generation.
* Implements AutoCloseable for resource cleanup of the native model handle.

#### Type
final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable;

#### Input
* Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport.
* Method `generate(AiGenerationRequest)`: AiGenerationRequest request.
* Method `generateWithTimings(AiGenerationRequest)`: AiGenerationRequest request.

#### Output
* Method `generate(AiGenerationRequest)`: String (generated text content).
* Method `generateWithTimings(AiGenerationRequest)`: AiGenerationTimings (text and performance metrics).
* Method `close()`: void (releases native model resources).

#### Core logic
* Lazy initialization of LlamaModel: The private `model()` method loads the GGUF model only on the first call to `generate()` or `generateWithTimings()`.
* Model configuration: `model()` constructs `ModelParameters` using `LlamaCppJniConfig` settings (e.g., context size, threads, GPU layers, cache reuse) and applies specific logic for SWA, Flash Attention, and KV cache types.
* Inference execution: `chatResponse()` calls `model().chatComplete(...)` to run the inference using parameters built from `AiGenerationRequest`.
* Response parsing: `chatResponse()` uses `ChatResponseParser` to extract structured results from the native response.
* Timing calculation: `generateWithTimings()` extracts prompt tokens (`PromptN`) and cache tokens (`CacheN`) from the response timings for performance reporting.

#### Public API
generate(AiGenerationRequest) -> String: Generates text content from the request.
generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: Generates text and performance metrics.
close() -> void: Releases the native LlamaModel resources.

#### Dependencies
LlamaCppJniConfig, AiPromptSupport, LlamaModel, AiCompletionParser, ChatResponseParser, Java8CompatibilityHelper, ModelParameters, ReasoningFormat, FlashAttn, AiGenerationRequest, ChatResponse, Timings, LlamaCppJniProviderSupport.

#### Exceptions / Errors
* Throws IOException in `generate()` if native I/O fails.
* Handles null `Timings` in `generateWithTimings()` by returning zero-rate timings.

#### Concurrency
* Model loading is lazy and thread-safe due to the check `if (current == null)` within the private `model()` method, ensuring the native handle is initialized once.
* The class itself is final, suggesting intended immutability of configuration post-construction.
