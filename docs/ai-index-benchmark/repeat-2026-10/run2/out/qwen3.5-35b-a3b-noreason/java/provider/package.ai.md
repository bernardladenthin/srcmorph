### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:05:31Z
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
> Provides a pluggable architecture for local and remote AI text generation, managing model lifecycle, configuration, and response parsing for llama.cpp and OpenAI-compatible backends.

#### Purpose
- Orchestrates AI generation requests across mock, local (llama.cpp), and remote OpenAI-compatible providers via a unified factory and interface.
- Cleans raw LLM outputs by stripping internal reasoning blocks (e.g., Gemma-4 thought markers) to extract final answers.

#### Responsibilities
- **Provider Abstraction**: Defines the `AiGenerationProvider` contract for text generation with optional timing metrics.
- **Factory Resolution**: Instantiates specific providers (`Mock`, `LlamaCppJni`, `OpenAiCompatible`) based on effective configuration keys.
- **Local Inference**: Manages native `llama.cpp` model loading, GPU offloading, KV-cache reuse, and parameter configuration.
- **Remote Compatibility**: Constructs JSON payloads, validates API key security, and parses HTTP responses for OpenAI-compatible endpoints.
- **Response Sanitization**: Parses raw completion text to remove chain-of-thought markers and validate block closure.

#### Key units
- `AiGenerationProvider`: Interface defining `generate`, `generateWithTimings`, and `close` for pluggable backends.
- `AiGenerationProviderFactory`: Resolves effective provider names and instantiates concrete implementations.
- `LlamaCppJniAiGenerationProvider`: Native JNI wrapper for `LlamaModel` with lazy loading and hardware offloading.
- `LlamaCppJniConfig`: Immutable builder-based configuration for model paths, sampling, and GPU/CPU layer settings.
- `OpenAiCompatibleProviderSupport`: Stateless utility for JSON request construction, security validation, and timing calculation.
- `AiCompletionParser`: Utility to strip `<|channel>thought` blocks and extract clean answers from raw LLM responses.
- `AiGenerationRequest`: Input object containing prompt keys, source files, and context headers.
- `AiGenerationTimings`: Wrapper for generated text and performance metrics (tokens/sec, cache hits).

#### Data flow
1. **Configuration**: `LlamaCppJniConfig` or `AiGenerationConfig` defines model parameters and provider selection.
2. **Resolution**: `AiGenerationProviderFactory` resolves the effective provider key (model vs. run config) and instantiates the provider.
3. **Request**: `AiGenerationRequest` is passed to the provider's `generate` or `generateWithTimings` method.
4. **Execution**:
   - **Local**: `LlamaCppJniAiGenerationProvider` initializes `LlamaModel` (lazy), applies `ModelParameters`, and calls `chatComplete`.
   - **Remote**: `OpenAiCompatibleProviderSupport` builds a JSON payload, validates API key security, and prepares the request.
5. **Parsing**: Raw response text is passed to `AiCompletionParser` to remove thinking blocks.
6. **Metrics**: `AiGenerationTimings` is populated with token counts and rates from `ChatResponse` or `Usage` objects.

#### Dependencies
- **Internal**: `net.ladenthin.llama` (native bindings, `LlamaModel`, `ChatResponse`), `net.ladenthin.srcmorph.document`, `net.ladenthin.srcmorph.prompt`, `net.ladenthin.srcmorph.config`.
- **External**: `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (annotations), `org.jspecify` (null safety), `java.io` (exceptions).
- **Native**: `llama.cpp` JNI bindings for model inference.

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and `AiGenerationTimings` are immutable value objects; `OpenAiCompatibleProviderSupport` is stateless.
- **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS for non-loopback hosts when transmitting API keys.
- **Error Handling**: `IOException` used for generation failures, truncated outputs, and serialization errors; `IllegalArgumentException` for invalid provider names or URLs.
- **Concurrency**: No explicit synchronization; `LlamaModel` lifecycle is single-threaded per provider instance; `ObjectMapper` is shared static.
- **Lazy Loading**: `LlamaCppJniAiGenerationProvider` defers `LlamaModel` instantiation until the first generation request.
