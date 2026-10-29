### java
- H: 1.0
- C: 5BF387FF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:09:49Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Provides the configuration schema and pluggable inference backends for AI-driven source code summarization and indexing within the srcmorph build pipeline.

#### Purpose
- Defines the root configuration surface (`SrcMorphConfiguration`) and granular settings for AI model routing, sampling, and hardware acceleration.
- Implements a pluggable AI text-generation layer supporting local llama.cpp JNI inference, OpenAI-compatible remote APIs, and mock backends.

#### Responsibilities
- **Configuration & Validation**: `SrcMorphConfiguration` aggregates source filters, output paths, and provider selection; `AiGenerationConfig` carries 40+ sampling and hardware parameters.
- **Filtering & Fact Extraction**: `AiConditionEvaluator` and `AiConditionGroup` implement recursive tree structures to filter files based on size, lines, and path globs; `AiFactExtractor` computes deterministic regex-based metrics for prompt enrichment.
- **Provider Abstraction**: `AiGenerationProvider` SPI and `AiGenerationProviderFactory` resolve and instantiate specific backends (`mock`, `llamacpp-jni`, `openai-compatible`).
- **Local Inference**: `LlamaCppJniAiGenerationProvider` manages native `LlamaModel` lifecycle and executes local GGUF model inference.
- **Remote API Support**: `OpenAiCompatibleProviderSupport` constructs JSON payloads, resolves secure API keys, and validates transport security for remote endpoints.
- **Response Processing**: `AiCompletionParser` strips chain-of-thought blocks from raw LLM outputs to extract clean answers.

#### Key units
- `SrcMorphConfiguration`: Root JavaBean holding base directory, filters, provider name, and lists of `AiModelDefinition`/`AiPromptDefinition`.
- `AiGenerationConfig`: Mutable data carrier for model path, context size, temperature, top-p/k, GPU layers, and provider routing details.
- `AiConditionEvaluator`: Stateless utility to `matches` file metadata against `AiCondition` trees and `validate` configuration integrity.
- `AiFactExtractor`: Static utility to generate formatted "facts blocks" from `AiFactCounter` regex definitions.
- `AiGenerationProvider`: SPI interface for text generation with `generate` and `generateWithTimings` methods.
- `AiGenerationProviderFactory`: Resolves provider keys and instantiates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, or `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider`: Final class implementing local inference via `net.ladenthin.llama.LlamaModel` with lazy initialization and native resource cleanup.
- `LlamaCppJniConfig`: Immutable configuration holder for all llama.cpp JNI parameters, including `modelPath`, `contextSize`, `gpuLayers`, and `stopStrings`.
- `OpenAiCompatibleProviderSupport`: Package-private helper for building OpenAI-compatible request bodies, validating API key security, and computing `AiGenerationTimings`.
- `AiCompletionParser`: Utility class that removes `<|channel>thought` ... `<channel|>` blocks from the start of LLM responses.

#### Data flow
1. **Configuration Binding**: Maven plugin or CLI populates `SrcMorphConfiguration` with global settings and nested `AiGenerationConfig` objects.
2. **Filtering**: `AiConditionEvaluator` uses `AiConditionGroup` trees to filter source files, determining if line counts are needed before processing.
3. **Fact Enrichment**: `AiFactExtractor` processes source text using `AiFactCounter` definitions to produce a deterministic header string.
4. **Provider Resolution**: `AiGenerationProviderFactory` resolves the effective provider key and instantiates the appropriate `AiGenerationProvider`.
5. **Inference Execution**:
   - **Local Path**: `LlamaCppJniAiGenerationProvider` lazily loads the `LlamaModel`, maps config to `ModelParameters`, calls `chatComplete`, parses the JSON `ChatResponse`, and uses `AiCompletionParser` to clean the output.
   - **Remote Path**: `OpenAiCompatibleProviderSupport` builds the JSON request body, resolves the API key from environment variables (validating HTTPS/loopback), and the provider sends the HTTP request.
6. **Output**: Both paths return `String` (clean text) or `AiGenerationTimings` (text + metrics like tokens/sec, latency, cache hits).

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `net.ladenthin.srcmorph.config.AiModelDefinition`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (JNI bindings for `LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`), `com.fasterxml.jackson.databind` (JSON serialization), `lombok` (code generation), `org.jspecify.annotations` (nullability), `java.time` (ISO-8601 parsing), `java.util.regex` (fact counting).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive null-checks in setters.
- **Immutability**: `LlamaCppJniConfig`, `AiOversizeStrategy`, and `AiConditionEvaluator` are stateless/immutable; `AiGenerationConfig` returns unmodifiable list views for collection fields.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable`; `LlamaCppJniAiGenerationProvider` explicitly releases native `LlamaModel` resources to prevent memory leaks.
- **Security**: `OpenAiCompatibleProviderSupport` enforces that API keys are never sent over plain HTTP to non-loopback hosts, throwing `IOException` if violated.
- **Error Handling**: `IOException` is the standard checked exception for provider failures; `IllegalArgumentException` for invalid configuration (e.g., unrecognized provider, invalid base URL, misconfigured trees).
- **Timing Metrics**: `AiGenerationTimings` is used across providers to standardize performance data (prompt/predicted tokens, decode rate, cache hits), with default zero-values for providers that don't report metrics.
