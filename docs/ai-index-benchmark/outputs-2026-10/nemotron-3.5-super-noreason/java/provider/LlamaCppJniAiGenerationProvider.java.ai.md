### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:02:04Z
- G: 
- A: 0.0.0
- X: file
---
> Runs local GGUF language models through the net.ladenthin:llama JNI binding to generate AI completions for source-morph requests, loading the model lazily and exposing both plain-text and timing-aware generation.

#### Purpose
- Implements AiGenerationProvider using llama.cpp via JNI for local AI code generation.
- Provides AutoCloseable lifecycle management for the native LlamaModel handle.

#### Type
- final class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable
- Lombok @ToString with @ToString.Exclude on model and support fields

#### Input
- Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport
- generate(AiGenerationRequest request): generation request
- generateWithTimings(AiGenerationRequest request): generation request
- Internal: config fields (modelPath, contextSize, threads, swaFull, cacheReuse, gpuLayers, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, flashAttn, cacheTypeK, cacheTypeV, batchSize, ubatchSize, threadsBatch, mainGpu, devices)

#### Output
- generate: String completion text
- generateWithTimings: AiGenerationTimings with text, prompt tokens, cache hits, rates, predicted tokens, and rates
- close: releases native LlamaModel resources
- Mutated field: model (lazily cached LlamaModel)

#### Core logic
- Constructor validates config and promptSupport non-null, instantiates LlamaCppJniProviderSupport
- model() lazily constructs LlamaModel from ModelParameters built from config, caching the native handle
- ModelParameters configured with reasoning format AUTO, optional SWA full KV cache, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified per slot, lazy mode, flash attention, cache types K/V, batch sizes, thread batch, main GPU, and devices
- generate delegates to chatResponse, warns on truncation, logs prompt cache reuse, parses first content via AiCompletionParser
- chatResponse builds inference parameters via support, calls model().chatComplete, parses via ChatResponseParser
- generateWithTimings extracts text and Timings from ChatResponse, degrades to zero rates if timings null, surfaces cache_n and throughput metrics
- close closes the native LlamaModel if loaded

#### Public API
- LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> constructs provider with lazy model loading
- generate(AiGenerationRequest) -> String returns generated completion text
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings returns completion with measured timings
- close() -> void releases native model resources

#### Dependencies
- java.io.IOException
- java.util.Map
- java.util.Objects
- lombok.ToString
- net.ladenthin.llama.LlamaModel
- net.ladenthin.llama.args.FlashAttn
- net.ladenthin.llama.args.ReasoningFormat
- net.ladenthin.llama.json.ChatResponseParser
- net.ladenthin.llama.parameters.ModelParameters
- net.ladenthin.llama.value.ChatResponse
- net.ladenthin.llama.value.Timings
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.provider.LlamaCppJniConfig
- net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport
- net.ladenthin.srcmorph.provider.AiCompletionParser
- net.ladenthin.srcmorph.provider.AiGenerationTimings

#### Exceptions / Errors
- generate throws IOException (declared per interface, though native path throws unchecked)
- Constructor throws NullPointerException if config or promptSupport is null
- model() may throw runtime exceptions from LlamaModel construction if native loading fails
- close handles null model gracefully

#### Concurrency
- Not thread-safe: model field is lazily initialized without synchronization, native LlamaModel handle is not guarded
- Intended for single-threaded use per provider instance
