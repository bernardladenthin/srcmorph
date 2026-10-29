### java/provider
- H: 1.0
- C: 3F966E26
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:05:40Z
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
> Provides a pluggable local and remote AI generation engine supporting Llama.cpp JNI inference, OpenAI-compatible APIs, and mock backends with reasoning block parsing and timing metrics.

#### Purpose
*   Orchestrates AI text generation via local Llama.cpp or remote OpenAI-compatible providers.
*   Handles reasoning block extraction, configuration management, and timing metrics for generation requests.

#### Responsibilities
*   **Provider Abstraction**: Defines `AiGenerationProvider` interface for pluggable backends (Mock, Llama.cpp, OpenAI).
*   **Local Inference**: Manages native Llama.cpp JNI bindings, lazy GGUF model loading, GPU offloading, and KV caching via `LlamaCppJniAiGenerationProvider`.
*   **Remote API Support**: Serializes requests to JSON, validates API keys, constructs endpoints, and parses HTTP responses for `OpenAiCompatibleProviderSupport`.
*   **Configuration**: Provides immutable `LlamaCppJniConfig` for sampling parameters, caching, and dry-run modes.
*   **Text Processing**: Strips internal reasoning blocks from raw completion text using `AiCompletionParser`.
*   **Factory Pattern**: Instantiates providers based on configuration keys via `AiGenerationProviderFactory`.

#### Key units
*   `AiGenerationProvider`: Interface defining `generate()`, `generateWithTimings()`, and `close()` contracts.
*   `LlamaCppJniAiGenerationProvider`: Implements native inference with lazy model loading and GPU configuration.
*   `LlamaCppJniConfig`: Immutable configuration holding 37 fields for model path, sampling, caching, and dry-run settings.
*   `AiCompletionParser`: Extracts clean answer text by removing `THINKING_BLOCK_START_MARKER`/`END_MARKER` sequences.
*   `OpenAiCompatibleProviderSupport`: Handles JSON serialization, endpoint construction, API key retrieval, and HTTP status parsing.
*   `AiGenerationProviderFactory`: Resolves provider keys and instantiates `Mock`, `LlamaCppJni`, or `OpenAiCompatible` providers.
*   `AiGenerationTimings`: Tracks token counts, throughput rates, and wall-clock time for generation requests.

#### Data flow
*   **Request Ingestion**: `AiGenerationRequest` (prompt key, source text) enters the pipeline.
*   **Provider Resolution**: `AiGenerationProviderFactory` resolves the effective provider key (model config > run config) and instantiates the backend.
*   **Execution**:
    *   *Local*: `LlamaCppJniAiGenerationProvider` loads GGUF model, executes `chatComplete()`, and parses response.
    *   *Remote*: `OpenAiCompatibleProviderSupport` serializes request to JSON, sends to endpoint, parses HTTP response.
*   **Post-Processing**: `AiCompletionParser` strips reasoning blocks from the raw completion string.
*   **Metrics**: `AiGenerationTimings` aggregates token counts and decode rates.

#### Dependencies
*   **Internal**: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `ChatResponse`, `Usage`.
*   **External Modules**: `LlamaCppJniProviderSupport` (native bindings), `ObjectMapper` (JSON), `Java8CompatibilityHelper`.
*   **Native**: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`.
*   **Configuration**: `AiGenerationConfig` (defaults), `LlamaCppJniConfig`.

#### Cross-cutting
*   **Concurrency**: Providers are generally single-threaded per instance; `LlamaCppJniAiGenerationProvider` is not thread-safe due to native handle sharing.
*   **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS or loopback-only connections for API keys.
*   **Reasoning Support**: `AiCompletionParser` and `LlamaCppJniConfig` support `chat_template_enable_thinking` and reasoning effort/budget parameters.
*   **Dry Run Mode**: `LlamaCppJniConfig` supports `dryMultiplier`, `dryBase`, and `dryAllowedLength` for simulation without inference.
*   **Caching**: `LlamaCppJniConfig` manages `cachePrompt`, `cacheReuse`, and `cacheTypeK/V` for KV cache optimization.
