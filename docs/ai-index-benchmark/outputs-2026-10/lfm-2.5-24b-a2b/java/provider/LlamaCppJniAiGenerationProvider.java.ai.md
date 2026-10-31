### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:56:13Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text completions by running GGUF models via llama.cpp JNI bindings with lazy model loading.

#### Purpose
- Implements AI generation provider backed by llama.cpp JNI, loading GGUF models lazily and executing prompts.

#### Type
- Class, final, implements AiGenerationProvider and AutoCloseable. Annotated with @ToString.

#### Input
- Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport. Injected dependencies: config and promptSupport.

#### Output
- generate returns parsed completion text (String).
- generateWithTimings returns AiGenerationTimings with timing metrics.
- close releases native model handle.

#### Core logic
- model() lazily builds LlamaModel via ModelParameters from config.
- Sets SWA, cache reuse, GPU, CPU layers, KV cache, and quantization options.
- chatResponse() calls model().chatComplete and parses full JSON response.
- generate() chains chatResponse, warns on truncation, logs cache reuse, and parses completion.
- generateWithTimings() extracts timings and returns AiGenerationTimings.
- close() closes the native model handle.

#### Public API
- generate(AiGenerationRequest) -> String: Runs generation and returns completion text.
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: Returns timing metrics for prompt cache.
- close() -> void: Releases native model handle.
- Public method count: generate, generateWithTimings, close (3 total)

#### Dependencies
- LlamaCppJniConfig, AiPromptSupport, LlamaCppJniProviderSupport, AiCompletionParser, ChatResponseParser, Java8CompatibilityHelper, AiGenerationRequest, AiGenerationTimings, LlamaModel, ModelParameters, ChatResponse, Timings, IOException.

#### Exceptions / Errors
- Declares throws IOException but only unchecked exceptions are thrown.
- NullPointerException possible from null config or promptSupport via Objects.requireNonNull.
- No other explicit exceptions.

#### Concurrency
- Not explicitly thread-safe; model initialization is not synchronized.
