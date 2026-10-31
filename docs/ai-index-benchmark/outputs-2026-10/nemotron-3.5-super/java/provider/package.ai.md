### java/provider
- H: 1.0
- C: 5AC65435
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:54:26Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Provides pluggable AI text-generation backends for source-code morphing, including local GGUF models via llama.cpp JNI, OpenAI-compatible endpoints, and mock implementations, while stripping leading model reasoning blocks from completions.

#### Purpose
- Defines and instantiates AI generation providers that transform source code via LLM completion requests.
- Parses raw LLM output to remove leading thinking/reasoning blocks before answers are persisted.

#### Responsibilities
- Provider contract and lifecycle: AiGenerationProvider interface with generate, generateWithTimings, and close.
- Provider selection and instantiation: AiGenerationProviderFactory resolves provider keys from run/model configuration and returns mock, llama.cpp JNI, or OpenAI-compatible providers.
- Local GGUF execution: LlamaCppJniAiGenerationProvider lazily loads a native LlamaModel and generates chat completions.
- OpenAI-compatible request support: OpenAiCompatibleProviderSupport builds JSON bodies, endpoints, API key rules, and timings without performing network I/O.
- Completion sanitization: AiCompletionParser strips leading Gemma-4 thinking blocks from raw completion text.

#### Key units
- AiGenerationProvider: interface defining generate, generateWithTimings, and close for AI backends.
- AiGenerationProviderFactory: selects and creates provider instances by provider key, defaulting to MockAiGenerationProvider when no key is named.
- LlamaCppJniAiGenerationProvider: final provider wrapping net.ladenthin:llama JNI LlamaModel for local GGUF inference.
- LlamaCppJniConfig: configuration for the llama.cpp JNI provider; no behavioral summary provided.
- OpenAiCompatibleProviderSupport: package-private helper for OpenAI-compatible request payloads, endpoints, API key gating, and timings.
- AiCompletionParser: stateless parser removing leading thinking blocks from completion text.

#### Data flow
- LlamaCppJniConfig and AiPromptSupport are passed to AiGenerationProviderFactory.create to resolve the effective provider key and instantiate an AiGenerationProvider.
- Callers invoke generate or generateWithTimings with an AiGenerationRequest containing prompt key, source file, source text, and current header.
- LlamaCppJniAiGenerationProvider lazily constructs a native LlamaModel, calls chatComplete, then passes the first content through AiCompletionParser to strip thinking blocks.
- OpenAiCompatibleProviderSupport prepares request bodies, endpoints, API keys, and timings for the OpenAI-compatible provider without performing network I/O.
- Results are wrapped in AiGenerationTimings with prompt/cached/completion tokens and decode rates, defaulting to zero rates when timings are absent.

#### Dependencies
- Internal: AiGenerationRequest, AiGenerationTimings, AiPromptSupport, Java8CompatibilityHelper, MockAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, LlamaCppJniProviderSupport.
- External: net.ladenthin.llama (LlamaModel, ModelParameters, ChatResponseParser, ChatResponse, Timings, FlashAttn, ReasoningFormat), org.jspecify.annotations.Nullable, lombok.ToString, java.io.IOException, java.util.Objects, java.util.Map, java.util.function.Function, java.lang.AutoCloseable.

#### Cross-cutting
- Lombok @ToString applied to AiCompletionParser, AiGenerationProviderFactory, LlamaCppJniAiGenerationProvider, and OpenAiCompatibleProviderSupport.
- Null safety via Objects.requireNonNull in constructors and org.jspecify @Nullable on optional returns and parameters.
- IOException declared on generate, generateWithTimings, close, and provider-support methods for I/O or serialization failures.
- IllegalArgumentException thrown by the factory for unrecognized provider keys and by endpoint validation for non-http(s) base URLs.
- Stateless or effectively immutable design: AiCompletionParser is stateless and thread-safe; the factory is stateless except an immutable compatibility helper; provider instances hold immutable config and support with only a lazily mutated native model handle.
- LlamaCppJniAiGenerationProvider lazy model initialization is not thread-safe for concurrent first calls and is not designed for concurrent generation on a single instance.
