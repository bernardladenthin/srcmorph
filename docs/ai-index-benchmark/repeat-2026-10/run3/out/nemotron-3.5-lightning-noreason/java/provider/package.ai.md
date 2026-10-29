### java/provider
- H: 1.0
- C: 7F4A1655
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:41:27Z
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
> This package provides pluggable AI text generation capabilities for code transformation tasks, supporting multiple backends including local GGUF models via llama.cpp JNI, OpenAI-compatible APIs, and mock implementations, with integrated completion parsing and configuration management.

#### Purpose
The package orchestrates AI-assisted code transformation by selecting and configuring generation providers, parsing LLM outputs, and handling provider-specific logic such as prompt rendering, response parsing, and API key management. It abstracts over real and mock backends to enable flexible AI integration in code transformation workflows.

#### Responsibilities
- **Provider selection and instantiation**: Factory logic that resolves provider names and creates instances of `AiGenerationProvider` implementations (mock, llama.cpp JNI, OpenAI-compatible).
- **Text generation**: Core generation methods that accept `AiGenerationRequest` objects and return generated text or timed results.
- **Completion parsing**: Strips model-internal thinking blocks from raw LLM completion text before storage.
- **Configuration management**: Immutable configuration for GGUF model parameters via a fluent builder with thirty-seven named fields.
- **OpenAI-compatible interactions**: Pure logic for constructing request bodies, validating endpoints, handling API keys, and computing timings without network I/O.
- **Prompt support**: Integration with `AiPromptSupport` for prompt rendering and template construction.

#### Key units
- **AiCompletionParser**: Final class that parses raw LLM completion text, stripping `<|channel>thought...<channel|>` thinking blocks; throws `IOException` if a thinking block starts but never closes.
- **AiGenerationProvider**: Interface defining `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)`, with a default no-op `close()`; implemented by `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- **AiGenerationProviderFactory**: Final class that selects and instantiates providers by name, defaulting to mock when name is blank/null and throwing `IllegalArgumentException` for unsupported keys.
- **LlamaCppJniAiGenerationProvider**: Final class implementing `AiGenerationProvider` and `AutoCloseable`; lazily loads a native `LlamaModel` via JNI, builds chat templates and model parameters, invokes `chatComplete`, and parses responses using `ChatResponseParser` and `AiCompletionParser`.
- **LlamaCppJniConfig**: Final immutable class with thirty-seven fields (model path, context size, threads, GPU layers, cache settings, reasoning budget, dry mode parameters, etc.) constructed via a fluent `Builder`.
- **OpenAiCompatibleProviderSupport**: Final class providing pure logic for OpenAI-compatible model interactions: constructing JSON request bodies, validating/normalizing endpoints, looking up API keys from environment, checking loopback host policy, and computing `AiGenerationTimings` from `ChatResponse` usage.
- **AiPromptSupport**: Interface/collaborator used by providers for prompt rendering and building chat template kwargs.

#### Data flow
1. **Provider creation**: `AiGenerationProviderFactory.create()` receives a provider name, `LlamaCppJniConfig`, and `AiPromptSupport`; it resolves the effective key and returns the appropriate `AiGenerationProvider` instance (mock, LlamaCppJni, or OpenAI-compatible).
2. **Text generation**: Caller invokes `provider.generate(request)` or `provider.generateWithTimings(request)`.
3. **Llama.cpp JNI path**: `LlamaCppJniAiGenerationProvider.generate()` lazily initializes the native `LlamaModel` using `LlamaCppJniConfig` fields, builds chat template kwargs via `AiPromptSupport.buildChatTemplateKwargs()`, constructs `ModelParameters` with reasoning format and layer offloading settings, and calls `model.chatComplete()` with inference parameters built from the request. The native JSON response is parsed by `ChatResponseParser`; completion text is extracted via `AiCompletionParser.parseCompletion()`, which strips thinking blocks.
4. **OpenAI-compatible path**: `OpenAiCompatibleProviderSupport.requestBody()` serializes a JSON chat-completions body using `ObjectMapper`, incorporating config values (temperature, top_p, max_tokens, repetition_penalty, stop strings, seed, top_k/min_p, reasoning effort) and prompt messages from `AiPromptSupport`. The endpoint is validated and normalized via `OpenAiCompatibleProviderSupport.endpoint()`. API key lookup and loopback validation occur via `apiKeyFor()`.
5. **Completion parsing**: Regardless of backend, parsed completion text passes through `AiCompletionParser.parseCompletion()` to strip any model-internal thinking blocks before being stored or returned.
6. **Resource cleanup**: Caller invokes `provider.close()` to release native handles (e.g., `LlamaModel` in the JNI provider).

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `AiGenerationTimings`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, provider implementations (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`), `AiCompletionParser`.
- **External/JNI**: `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ModelParameters`, `ChatResponse`, `Timings`, `FlashAttn`; `com.fasterxml.jackson.databind.ObjectMapper` for JSON serialization; `org.jspecify.annotations.Nullable`; `lombok.ToString`, `lombok.EqualsAndHashCode`.
- **IO**: `java.io.IOException`, `java.util.regex.Pattern`, `java.util.function.Function`, `java.net.URL`.

#### Cross-cutting
- **Builder pattern**: `LlamaCppJniConfig` uses a fluent `Builder` with thirty-seven optional setters, all starting from `AiGenerationConfig.DEFAULT_*` defaults; the builder is not thread-safe, but the produced config is immutable.
- **Immutability**: `LlamaCppJniConfig` is final with private final fields and record-style accessors; `AiCompletionParser` has no threading concerns.
- **Exception handling**: `AiCompletionParser` throws `IOException` if a thinking block starts but closes; `AiGenerationProviderFactory` throws `IllegalArgumentException` for unrecognized provider names; `OpenAiCompatibleProviderSupport.endpoint()` throws `IllegalArgumentException` for non-http(s) base URLs; `OpenAiCompatibleProviderSupport.apiKeyFor()` throws `IOException` if an API key is sent over plain http to a non-loopback host.
- **Null safety**: `Objects.requireNonNull` checks on `config` and `promptSupport` in `LlamaCppJniAiGenerationProvider` constructor; `AiCompletionParser.parseCompletion()` returns empty string on null input; `OpenAiCompatibleProviderSupport.apiKey()` returns null when env var is unset/blank.
- **Timing computation**: `OpenAiCompatibleProviderSupport.timings()` computes decode rate as completion tokens / seconds, defaulting to 0 when seconds <= 0 or usage is unavailable.
- **Thinking block markers**: `AiCompletionParser` uses `<|channel>thought` as start and `<channel|>` as end markers; these constants are searchable across the package.
