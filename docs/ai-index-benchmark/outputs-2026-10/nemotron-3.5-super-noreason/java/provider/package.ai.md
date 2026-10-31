### java/provider
- H: 1.0
- C: 416BB43E
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:04:47Z
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
> Resolves and instantiates pluggable AI text-generation providers for source morphing, supporting local llama.cpp JNI models and OpenAI-compatible backends, while parsing model completions to strip internal thinking blocks.

#### Purpose
- Provides the AI generation backend abstraction and factory for the srcmorph tool.
- Parses raw LLM completion text to extract clean answers by removing model-internal thinking blocks.

#### Responsibilities
- Provider contract and default timing behavior (`AiGenerationProvider`)
- Provider selection and instantiation by provider key (`AiGenerationProviderFactory`)
- Local GGUF model execution via llama.cpp JNI (`LlamaCppJniAiGenerationProvider`)
- Immutable llama.cpp configuration with thirty-seven parameters (`LlamaCppJniConfig`)
- OpenAI-compatible request building, endpoint validation, API key handling, and timing computation without network I/O (`OpenAiCompatibleProviderSupport`)
- Completion text parsing that strips leading thinking blocks (`AiCompletionParser`)

#### Key units
- `AiGenerationProvider` — interface defining `generate`, `generateWithTimings`, and `close`; extends `AutoCloseable`.
- `AiGenerationProviderFactory` — stateless factory resolving mock, llama.cpp JNI, or OpenAI-compatible providers.
- `LlamaCppJniAiGenerationProvider` — final provider implementing local GGUF generation with lazy native model loading.
- `LlamaCppJniConfig` — immutable value type holding thirty-seven generation parameters via a fluent Builder.
- `OpenAiCompatibleProviderSupport` — package-private helper for OpenAI-compatible request bodies, endpoints, API keys, and timings.
- `AiCompletionParser` — stateless parser stripping Gemma-4 thinking blocks from raw completion text.

#### Data flow
- `AiGenerationProviderFactory.effectiveProvider` selects the provider key from model or run configuration, defaulting to mock when blank.
- `AiGenerationProviderFactory.create` instantiates the chosen provider, injecting `LlamaCppJniConfig` and `AiPromptSupport`.
- `LlamaCppJniAiGenerationProvider.generate` builds model parameters from config, lazily loads the native `LlamaModel`, calls `chatComplete`, and pipes the response through `AiCompletionParser.parseCompletion` to strip thinking blocks.
- `OpenAiCompatibleProviderSupport.requestBody` serializes a Jackson JSON chat-completions body; `apiKeyFor` guards against cleartext keys over non-loopback HTTP; `timings` derives `AiGenerationTimings` from `ChatResponse` usage and wall-clock seconds.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.ConvertToRecord`.
- External: `net.ladenthin.llama.*` (LlamaModel, ModelParameters, ChatResponse, Timings, Usage, ChatResponseParser, FlashAttn, ReasoningFormat), Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok (`@ToString`, `@EqualsAndHashCode`), `org.jspecify.annotations.Nullable`.
- JDK: `java.io.IOException`, `java.net.URL`, `java.util.Collections`, `java.util.List`, `java.util.Map`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`, `java.lang.AutoCloseable`.

#### Cross-cutting
- Lombok `@ToString` used across `AiCompletionParser`, `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, and `OpenAiCompatibleProviderSupport`; `LlamaCppJniConfig` also uses `@EqualsAndHashCode` and `@ConvertToRecord`.
- Nullability expressed via `org.jspecify.annotations.Nullable` and `Java8CompatibilityHelper.isBlank` for blank-string handling.
- `AiGenerationProvider` and `LlamaCppJniAiGenerationProvider` implement `AutoCloseable` for native resource cleanup.
- `LlamaCppJniAiGenerationProvider` is not thread-safe due to unsynchronized lazy model initialization; other units are stateless or effectively immutable and safe for concurrent use.
- `IllegalArgumentException` is thrown by the factory and `OpenAiCompatibleProviderSupport.endpoint` for unrecognized providers or invalid URL schemes; `IOException` surfaces JSON serialization, missing thinking-block end markers, and cleartext API key violations.
