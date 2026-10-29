### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:03:56Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI text generation capabilities by interfacing with GGUF models via llama.cpp JNI bindings.

#### Purpose
* Implements AiGenerationProvider for local LLM inference.
* Manages the lifecycle and configuration of the LlamaModel instance.

#### Type
final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable;

#### Input
* Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport.
* Method `generate(AiGenerationRequest)`: AiGenerationRequest request.
* Method `generateWithTimings(AiGenerationRequest)`: AiGenerationRequest request.

#### Output
* Method `generate(AiGenerationRequest)`: String (generated text content).
* Method `generateWithTimings(AiGenerationRequest)`: AiGenerationTimings (text and performance metrics).
* Method `close()`: None (side effect: releases native resources).

#### Core logic
* Model loading is lazy: `model()` method initializes `LlamaModel` only on first call.
* `model()` constructs `ModelParameters` based on `LlamaCppJniConfig` settings (e.g., `contextSize`, `gpuLayers`, `flashAttn`).
* `model()` handles complex configuration mapping, including SWA full, cache reuse, and layer offloading.
* `generate()` calls `chatResponse()` then parses the result using `AiCompletionParser`.
* `chatResponse()` calls `model().chatComplete()` and parses the full response using `ChatResponseParser`.
* `generateWithTimings()` calls `chatResponse()` and extracts timing metrics from the response.

#### Public API
generate(AiGenerationRequest) -> String: Generates text content from request.
generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: Generates text with performance metrics.
close() -> void: Releases native LlamaModel resources.

#### Dependencies
LlamaCppJniConfig, AiPromptSupport, LlamaModel, AiCompletionParser, ChatResponseParser, Java8CompatibilityHelper, ModelParameters, ReasoningFormat, FlashAttn, AiGenerationRequest, ChatResponse, Timings, AiGenerationTimings.

#### Exceptions / Errors
* Throws IOException in `generate(AiGenerationRequest)`.
* Handles null `Timings` in `generateWithTimings` by returning zero-rate timings.

#### Concurrency
* The `LlamaModel` instance is lazily initialized and cached (`model` field), implying thread-safe access is required for concurrent calls to `generate()` or `generateWithTimings()`, though explicit synchronization is not shown.
* The class itself is final, suggesting intended immutability of its configuration state after construction.
