### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:09:38Z
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
> Provides a pluggable AI text-generation layer for source-morphing pipelines, supporting local llama.cpp JNI inference, OpenAI-compatible remote APIs, and mock backends with configurable sampling, timing metrics, and response parsing.

#### Purpose
- Defines the SPI and factory for selecting and instantiating AI generation backends (`AiGenerationProvider`) based on configuration.
- Encapsulates the logic for local model inference (llama.cpp JNI), remote API interaction (OpenAI-compatible), and post-processing of raw LLM outputs.

#### Responsibilities
- **Provider Abstraction & Factory**: `AiGenerationProvider` interface and `AiGenerationProviderFactory` for resolving provider implementations (`mock`, `llamacpp-jni`, `openai-compatible`) from configuration keys.
- **Local Inference**: `LlamaCppJniAiGenerationProvider` manages the lifecycle of native `LlamaModel` handles, maps configuration to `ModelParameters`, and executes local GGUF model inference.
- **Remote API Support**: `OpenAiCompatibleProviderSupport` constructs JSON payloads for OpenAI-compatible endpoints, resolves secure API keys, validates transport security, and calculates performance timings.
- **Configuration**: `LlamaCppJniConfig` serves as an immutable value object holding 41+ inference parameters (sampling, GPU offload, cache settings, DRY/stop strings) via a fluent builder.
- **Response Parsing**: `AiCompletionParser` strips leading chain-of-thought blocks (Gemma-4 style) from raw completion text to extract clean answers.

#### Key units
- `AiGenerationProvider`: SPI interface for text generation with `generate` and `generateWithTimings` methods.
- `AiGenerationProviderFactory`: Resolves provider keys and instantiates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider`: Final class implementing local inference via `net.ladenthin.llama.LlamaModel` with lazy initialization and native resource cleanup.
- `LlamaCppJniConfig`: Immutable configuration holder for all llama.cpp JNI parameters, including `modelPath`, `contextSize`, `gpuLayers`, and `stopStrings`.
- `OpenAiCompatibleProviderSupport`: Package-private helper for building OpenAI-compatible request bodies, validating API key security (loopback checks), and computing `AiGenerationTimings`.
- `AiCompletionParser`: Utility class that removes `<|channel>thought` ... `<channel|>` blocks from the start of LLM responses.

#### Data flow
1. `AiGenerationProviderFactory` resolves the effective provider key from run/model configuration and instantiates the appropriate `AiGenerationProvider`.
2. The provider receives an `AiGenerationRequest` (containing prompt data) and `LlamaCppJniConfig`.
3. **Local Path**: `LlamaCppJniAiGenerationProvider` lazily loads the `LlamaModel`, maps config to `ModelParameters`, calls `chatComplete`, parses the JSON `ChatResponse`, and uses `AiCompletionParser` to clean the output.
4. **Remote Path**: `OpenAiCompatibleProviderSupport` builds the JSON request body, resolves the API key from environment variables (validating HTTPS/loopback), and the provider sends the HTTP request.
5. Both paths return `String` (clean text) or `AiGenerationTimings` (text + metrics like tokens/sec, latency, cache hits).

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig` (for defaults), `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`), `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (code generation), `org.jspecify.annotations` (nullability).

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` is immutable with unmodifiable list views; `OpenAiCompatibleProviderSupport` is thread-safe with final fields.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable`; `LlamaCppJniAiGenerationProvider` explicitly releases native `LlamaModel` resources to prevent memory leaks.
- **Security**: `OpenAiCompatibleProviderSupport` enforces that API keys are never sent over plain HTTP to non-loopback hosts, throwing `IOException` if violated.
- **Error Handling**: `IOException` is the standard checked exception for provider failures; `IllegalArgumentException` for invalid configuration (e.g., unrecognized provider, invalid base URL).
- **Timing Metrics**: `AiGenerationTimings` is used across providers to standardize performance data (prompt/predicted tokens, decode rate, cache hits), with default zero-values for providers that don't report metrics.
