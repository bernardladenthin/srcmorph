### LlamaCppJniAiGenerationProvider.java
- H: 1.0
- C: 2E0029DC
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:46:18Z
- G: 
- A: 0.0.0
- X: file
---
> Provides local AI code generation by running GGUF models through the net.ladenthin:llama JNI binding for llama.cpp.

#### Purpose
- Implements AiGenerationProvider for srcmorph using a locally loaded GGUF model via llama.cpp JNI.
- Lazily initializes the native LlamaModel on first generate() to keep construction cheap and native-free.

#### Type
- final class LlamaCppJniAiGenerationProvider
- implements AiGenerationProvider, AutoCloseable
- annotated @ToString (Lombok); @ToString.Exclude on model and support fields

#### Input
- Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport (both null-checked via Objects.requireNonNull)
- generate(AiGenerationRequest request): generation request
- generateWithTimings(AiGenerationRequest request): generation request
- model(): reads config fields (modelPath, contextSize, threads, swaFull, cacheReuse, gpuLayers, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, flashAttn, cacheTypeK, cacheTypeV, batchSize, ubatchSize, threadsBatch, mainGpu, devices)
- chatResponse(AiGenerationRequest request): delegates to model().chatComplete(support.buildInferenceParameters(request))

#### Output
- generate: String completion text parsed from response first content
- generateWithTimings: AiGenerationTimings with text, prompt_n, cache_n, prompt_per_second, predicted_n, predicted_per_second (zeros if timings absent)
- close: void, closes native LlamaModel handle if loaded
- model(): cached LlamaModel native handle
- chatResponse: parsed ChatResponse from native chat completion
- Side effect: loads ~90 MB GGUF + native context on first use; mutates model field

#### Core logic
- model() lazily constructs LlamaModel via ModelParameters builder, applying config-driven settings: reasoning format AUTO, chat template kwargs, optional SWA full KV cache, cache reuse, GPU layers, CPU MoE/FFN layers, KV unified per slot, lazy mode, flash attention ON, cache type K/V, batch/ubatch/threads batch sizes, main GPU, devices; caches result in model field.
- generate() calls chatResponse(), warns on truncated answer, logs prompt cache reuse, returns completionParser.parseCompletion(response.getFirstContent()).
- chatResponse() invokes model().chatComplete(...) and parses via chatResponseParser.parseResponse(...).
- generateWithTimings() calls chatResponse(), extracts Timings, returns AiGenerationTimings; degrades to zero rates when Timings is null.
- close() calls current.close() on the native model handle if non-null.

#### Public API
- LlamaCppJniAiGenerationProvider(LlamaCppJniConfig, AiPromptSupport) -> LlamaCppJniAiGenerationProvider: constructs provider, validates non-null inputs
- generate(AiGenerationRequest) -> String: produces completion text
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: produces completion with measured throughput
- close() -> void: releases native model handle

#### Dependencies
java.io.IOException, java.util.Map, java.util.Objects, lombok.ToString, net.ladenthin.llama.LlamaModel, net.ladenthin.llama.args.FlashAttn, net.ladenthin.llama.args.ReasoningFormat, net.ladenthin.llama.json.ChatResponseParser, net.ladenthin.llama.parameters.ModelParameters, net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Timings, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.provider.AiGenerationProvider, net.ladenthin.srcmorph.provider.LlamaCppJniConfig, net.ladenthin.srcmorph.provider.LlamaCppJniProviderSupport, net.ladenthin.srcmorph.provider.AiCompletionParser, net.ladenthin.srcmorph.provider.AiGenerationTimings, java.lang.AutoCloseable

#### Exceptions / Errors
- generate declares throws IOException; chat path throws only unchecked exceptions per javadoc
- Constructor throws NullPointerException if config or promptSupport is null
- model field is @Nullable; close() null-checks before calling current.close()
- chatResponseParser.parseResponse and model().chatComplete may surface unchecked native/parsing failures

#### Concurrency
- No synchronization; lazy model() initialization is not thread-safe for concurrent first calls
- Not designed for concurrent generation on a single instance
- Immutable config, promptSupport, support, and parser fields after construction; only model field is mutated once
