### java/provider
- H: 1.0
- C: DA89EBE3
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:47:39Z
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
> Pluggable AI text-generation backends for the srcmorph source-transformation pipeline, supporting local llama.cpp GGUF models via JNI and remote OpenAI-compatible chat APIs with completion parsing, timing metrics, and secure configuration.

#### Purpose
- Define and instantiate pluggable AI generation backends (local llama.cpp JNI, OpenAI-compatible HTTP, mock) for source-morphing requests
- Parse raw LLM completions to strip chain-of-thought thinking blocks and expose token-throughput timings for calibration

#### Responsibilities
- Provider contract and lifecycle: `AiGenerationProvider` interface defining `generate`, `generateWithTimings`, and `close` for AutoCloseable backends
- Factory-based instantiation: `AiGenerationProviderFactory` resolving provider keys ("mock", "llamacpp-jni", "openai-compatible") from run/model config with model-level override
- Local inference: `LlamaCppJniAiGenerationProvider` lazily loading GGUF models via `net.ladenthin:llama` JNI, building `ModelParameters` from config, executing chat completions, and mapping token timings
- Remote API support: `OpenAiCompatibleProviderSupport` serializing chat-completion JSON, enforcing HTTPS/loopback API-key security, and computing decode rates from usage stats
- Completion post-processing: `AiCompletionParser` stripping Gemma-4 `<|channel>thought` thinking blocks to isolate final answers
- Configuration: `LlamaCppJniConfig` immutable 41-field value object with fluent builder defaulting to `AiGenerationConfig.DEFAULT_*` constants

#### Key units
- `AiGenerationProvider` — interface extending AutoCloseable defining the backend contract: `generate(AiGenerationRequest)` for text, `generateWithTimings(AiGenerationRequest)` returning `AiGenerationTimings`, and `close()` for resource cleanup
- `AiGenerationProviderFactory` — factory resolving provider keys (`PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`) via `effectiveProvider()` (model overrides run) and instantiating concrete providers in `create()`
- `LlamaCppJniAiGenerationProvider` — final implementation for local GGUF inference via `net.ladenthin:llama` JNI; lazily builds `ModelParameters` from `LlamaCppJniConfig`, executes `chatComplete`, parses responses, and maps `Timings` to `AiGenerationTimings`
- `LlamaCppJniConfig` — immutable 41-field configuration value object with nested `Builder`; covers model path, sampling knobs, GPU/CPU offload, KV cache, reasoning, and stop strings; constructed via `builder(String modelPath)` with defaults from `AiGenerationConfig`
- `OpenAiCompatibleProviderSupport` — package-private helper for remote OpenAI-compatible APIs; serializes chat-completion JSON via `requestBody()`, validates endpoints, enforces loopback-only cleartext API keys via `apiKeyFor()`, and computes `AiGenerationTimings` from `ChatResponse` usage
- `AiCompletionParser` — stateless parser stripping Gemma-4 thinking blocks delimited by `THINKING_BLOCK_START_MARKER`/`THINKING_BLOCK_END_MARKER` from raw completions; throws `IOException` on unterminated blocks

#### Data flow
- `AiGenerationProviderFactory.create()` resolves effective provider key (model config overrides run config) → instantiates provider with `LlamaCppJniConfig` and `AiPromptSupport`
- `AiGenerationRequest` (prompt key, source file/text, header) → provider: local path builds `ModelParameters`, lazily loads `LlamaModel`, calls `chatComplete` → `ChatResponse`; remote path serializes JSON via `OpenAiCompatibleProviderSupport.requestBody()` and POSTs to validated `/chat/completions` endpoint
- Raw completion → `AiCompletionParser.parseCompletion()` strips thinking blocks → final text returned by `generate()` or wrapped with token counts/rates in `AiGenerationTimings` by `generateWithTimings()`

#### Dependencies
- Internal: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `net.ladenthin.srcmorph.support.ConvertToRecord`, same-package `AiGenerationTimings`, `MockAiGenerationProvider`, `LlamaCppJniProviderSupport`, `OpenAiCompatibleAiGenerationProvider`
- External: `net.ladenthin:llama` JNI binding (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `ChatResponseParser`, `FlashAttn`, `ReasoningFormat`), Jackson Databind (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok, `org.jspecify.annotations.Nullable`
- JDK: `java.io.IOException`, `java.net.URL`, `java.util.*`, `java.util.function.Function`

#### Cross-cutting
- Immutability patterns: `LlamaCppJniConfig` (final fields, unmodifiable lists), `OpenAiCompatibleProviderSupport` (final fields), `AiCompletionParser` (stateless), `AiGenerationProviderFactory` (immutable); `LlamaCppJniAiGenerationProvider` explicitly single-threaded (unsynchronized lazy model init)
- Builder pattern: `LlamaCppJniConfig.Builder` with null-coalescing setters defaulting to `AiGenerationConfig.DEFAULT_*`; `builder(String)` enforces non-null model path via `Objects.requireNonNull`
- Security: `OpenAiCompatibleProviderSupport.apiKeyFor()` blocks API keys over plain HTTP to non-loopback hosts (`localhost`, `127.x.x.x`, `[::1]`)
- Error handling: `IOException` for I/O and security violations (unterminated thinking blocks, cleartext key leakage); `IllegalArgumentException` for unknown providers and invalid URL schemes; NPE on null constructor args
- Configuration precedence: model-level provider overrides run-level via `effectiveProvider()`; blank/null keys fall back to mock provider
- Lombok: `@ToString` across classes, `@EqualsAndHashCode`/`@ConvertToRecord` on config, `@ToString.Exclude` on sensitive provider fields
