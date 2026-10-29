### java/provider
- H: 1.0
- C: 7F4A1655
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:50:02Z
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
> Provides a pluggable abstraction for AI text generation backends, supporting local llama.cpp JNI inference and remote OpenAI-compatible APIs, with configuration management, response parsing, and timing metrics for source-morphing tasks.

#### Purpose
- Defines the SPI contract (`AiGenerationProvider`) and factory logic for instantiating AI generation backends based on configuration.
- Manages the lifecycle, parameter mapping, and response parsing for local (llama.cpp) and remote (OpenAI-compatible) LLM inference.

#### Responsibilities
- **Provider Abstraction**: `AiGenerationProvider` interface defines `generate` and `generateWithTimings` contracts, extending `AutoCloseable` for resource management.
- **Factory & Selection**: `AiGenerationProviderFactory` resolves the effective provider key (model-level overrides run-level) and instantiates the correct implementation (`Mock`, `LlamaCppJni`, or `OpenAiCompatible`).
- **Local Inference**: `LlamaCppJniAiGenerationProvider` handles lazy loading of native GGUF models, maps configuration to `ModelParameters`, and executes inference via JNI bindings.
- **Remote Inference Support**: `OpenAiCompatibleProviderSupport` constructs JSON payloads, resolves secure API keys (with loopback validation for cleartext HTTP), and calculates generation timings.
- **Configuration**: `LlamaCppJniConfig` serves as an immutable value object (Builder pattern) encapsulating 41 parameters for sampling, GPU offloading, KV-cache, and remote API settings.
- **Response Parsing**: `AiCompletionParser` strips Gemma-4 chain-of-thought reasoning blocks from raw LLM output to extract clean answers.

#### Key units
- `AiGenerationProvider`: SPI interface for AI backends; methods `generate(AiGenerationRequest)`, `generateWithTimings(AiGenerationRequest)`, `close()`.
- `AiGenerationProviderFactory`: Factory class; methods `create(...)`, `effectiveProvider(...)`; constants `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider`: Final class implementing local inference; manages `LlamaModel` lifecycle and native resource cleanup.
- `LlamaCppJniConfig`: Immutable configuration holder; nested `Builder` class; 41 accessor methods for model, sampling, and hardware parameters.
- `OpenAiCompatibleProviderSupport`: Package-private helper; methods `requestBody(...)`, `endpoint()`, `apiKeyFor(...)`, `timings(...)`; handles JSON serialization and security checks.
- `AiCompletionParser`: Utility class; method `parseCompletion(String)`; constants `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.

#### Data flow
1. **Initialization**: `AiGenerationProviderFactory` resolves the provider key from `LlamaCppJniConfig` and run configuration, instantiating the appropriate `AiGenerationProvider`.
2. **Request Processing**: The provider receives an `AiGenerationRequest`. For local inference, `LlamaCppJniAiGenerationProvider` lazily loads the `LlamaModel` and maps `LlamaCppJniConfig` to `ModelParameters`. For remote inference, `OpenAiCompatibleProviderSupport` builds the JSON payload and validates the endpoint/API key.
3. **Inference**: The backend executes the generation (JNI call or HTTP request).
4. **Post-processing**: Raw response text is passed to `AiCompletionParser` to strip thinking blocks. Timing data is extracted (via `ChatResponseParser` for local, or calculated from token counts/duration for remote) and wrapped in `AiGenerationTimings`.
5. **Output**: Cleaned text and timing metrics are returned to the caller.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (JNI bindings: `LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`), `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (annotations), `org.jspecify.annotations` (nullability).

#### Cross-cutting
- **Immutability & Thread Safety**: `LlamaCppJniConfig` is immutable and thread-safe; `OpenAiCompatibleProviderSupport` is stateless/thread-safe. `LlamaCppJniAiGenerationProvider` is **not** thread-safe due to unsynchronized lazy initialization of the native model handle.
- **Resource Management**: All providers extend `AutoCloseable` to ensure native handles or network resources are released.
- **Error Handling**: `IOException` is used for inference failures, token budget exhaustion (unterminated thinking blocks), and security violations (cleartext API key transmission). `IllegalArgumentException` is used for invalid provider keys or endpoint schemes.
- **Configuration Defaults**: `LlamaCppJniConfig` centralizes defaults from `AiGenerationConfig` to prevent drift, with null-safe builder setters that restore defaults.
