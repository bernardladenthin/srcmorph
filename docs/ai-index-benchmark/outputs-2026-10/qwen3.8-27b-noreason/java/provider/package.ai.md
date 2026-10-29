### java/provider
- H: 1.0
- C: 9FE1B40A
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T10:00:02Z
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
> Provides a pluggable abstraction for AI text generation, supporting local llama.cpp JNI inference and remote OpenAI-compatible APIs, with configuration management, response parsing, and timing metrics.

#### Purpose
- Defines the contract and factory logic for selecting and instantiating AI generation backends (mock, local JNI, remote API).
- Manages the lifecycle, configuration, and execution of LLM inference, including native resource handling and secure API key resolution.

#### Responsibilities
- **Provider Abstraction**: `AiGenerationProvider` interface defines the standard for text generation and timing retrieval.
- **Factory & Routing**: `AiGenerationProviderFactory` resolves the active provider based on configuration precedence (model-level vs. run-level) and instantiates the correct implementation.
- **Local Inference**: `LlamaCppJniAiGenerationProvider` executes local GGUF model inference via JNI, handling lazy model initialization, parameter mapping, and native resource cleanup.
- **Remote Inference Support**: `OpenAiCompatibleProviderSupport` constructs JSON payloads for OpenAI-compatible endpoints, validates URL schemes, and enforces security constraints on API key transmission.
- **Configuration**: `LlamaCppJniConfig` serves as an immutable value object holding 41+ parameters for sampling, GPU offloading, KV-cache management, and provider routing.
- **Post-Processing**: `AiCompletionParser` sanitizes raw LLM outputs by stripping leading chain-of-thought blocks before persistence.

#### Key units
- `AiGenerationProvider`: Interface for `generate` and `generateWithTimings`, extending `AutoCloseable`.
- `AiGenerationProviderFactory`: Resolves provider keys (`mock`, `llamacpp-jni`, `openai-compatible`) and constructs instances.
- `LlamaCppJniAiGenerationProvider`: Final class implementing local inference using `net.ladenthin.llama` JNI bindings.
- `LlamaCppJniConfig`: Immutable builder-based configuration holder for all inference parameters.
- `OpenAiCompatibleProviderSupport`: Package-private helper for remote request serialization, endpoint validation, and timing calculation.
- `AiCompletionParser`: Utility to remove `THINKING_BLOCK_START_MARKER`/`END_MARKER` segments from responses.

#### Data flow
1. **Initialization**: `AiGenerationProviderFactory` resolves the provider key from `LlamaCppJniConfig` and run-level settings, instantiating either `LlamaCppJniAiGenerationProvider` or an OpenAI-compatible provider.
2. **Request Construction**: For remote providers, `OpenAiCompatibleProviderSupport` builds the JSON body using `AiPromptSupport` and `LlamaCppJniConfig` parameters. For local providers, `LlamaCppJniProviderSupport` (referenced) maps config to `ModelParameters`.
3. **Inference Execution**:
   - **Local**: `LlamaCppJniAiGenerationProvider` lazily initializes `LlamaModel`, executes `chatComplete`, and parses the `ChatResponse`.
   - **Remote**: The provider (not fully detailed in files but implied) uses `OpenAiCompatibleProviderSupport` to send HTTP requests and parse responses.
4. **Post-Processing**: `AiCompletionParser.parseCompletion` strips thinking blocks from the raw string output.
5. **Output**: Returns clean text and `AiGenerationTimings` (token counts, rates) to the caller.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig` (for defaults), `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- **External**:
  - `net.ladenthin.llama` (JNI library for local inference: `LlamaModel`, `ModelParameters`, `ChatResponseParser`).
  - `com.fasterxml.jackson.databind` (JSON serialization for remote requests).
  - `lombok` (code generation for `@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`).
  - `org.jspecify.annotations` (nullability annotations).

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` is immutable; `OpenAiCompatibleProviderSupport` uses final fields and a static `ObjectMapper`.
- **Resource Management**: `AiGenerationProvider` extends `AutoCloseable`; `LlamaCppJniAiGenerationProvider` explicitly manages native `LlamaModel` lifecycle.
- **Security**: `OpenAiCompatibleProviderSupport` enforces that API keys are not sent over plain HTTP to non-loopback hosts.
- **Error Handling**: `IOException` is used for provider failures and security violations; `IllegalArgumentException` for invalid configuration (e.g., unknown provider, bad URL scheme).
- **Concurrency**: `LlamaCppJniAiGenerationProvider` is not thread-safe (lazy init without sync); `OpenAiCompatibleProviderSupport` is thread-safe for concurrent use.
