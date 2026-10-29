### java/provider
- H: 1.0
- C: D2D2268E
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:54:38Z
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
> This package provides pluggable AI generation backends for source code transformation, supporting local GGUF models via llama.cpp JNI bindings and OpenAI-compatible API integration, with parsing and configuration infrastructure.

#### Purpose
The package orchestrates AI-augmented source code transformations by selecting and configuring AI generation providers (mock, local GGUF via llama.cpp JNI, or OpenAI-compatible APIs), parsing LLM completion text, and managing request/response lifecycles for code transformation tasks.

#### Responsibilities
- **Provider selection and instantiation**: Factory logic to create provider instances based on configured names (mock, LlamaCppJni, OpenAI-compatible).
- **AI text generation**: Core generation interface and implementation using local GGUF models through llama.cpp JNI bindings.
- **Completion parsing**: Stripping thinking blocks from LLM output to extract clean model answers.
- **Request/response handling**: Constructing JSON request bodies, resolving endpoints, handling API keys, and computing generation timings for OpenAI-compatible services.
- **Configuration management**: Immutable configuration carrier with thirty-seven parameters controlling model loading, generation settings, and provider interaction.

#### Key units
- **AiCompletionParser**: Strips leading Gemma-4 thinking blocks (`<|channel>thought` ... `<channel|>`) from raw LLM completion text to extract the model answer; throws `IOException` if a thinking block starts without a closing marker.
- **AiGenerationProvider**: Interface for pluggable AI backends, extending `AutoCloseable`; defines `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)` methods.
- **AiGenerationProviderFactory**: Selects and instantiates an `AiGenerationProvider` implementation by name; supports mock, LlamaCppJni, and OpenAI-compatible providers; returns `MockAiGenerationProvider` for blank/null names.
- **LlamaCppJniAiGenerationProvider**: Implementation of `AiGenerationProvider` that runs local GGUF models through llama.cpp JNI bindings; lazily loads and caches a native `LlamaModel` on first generation.
- **LlamaCppJniConfig**: Immutable value class with thirty-seven fields controlling model loading, generation settings, and provider interaction; built via a mutable builder pattern.
- **OpenAiCompatibleProviderSupport**: Provides pure logic for OpenAI-compatible model requests: constructing JSON request bodies, resolving endpoint URLs, looking up API keys from environment variables, checking HTTP success status, formatting error messages, and computing generation timings.
- **MockAiGenerationProvider**: (Implied) Mock implementation of `AiGenerationProvider` for testing/defaults.

#### Data flow
1. **Provider creation**: `AiGenerationProviderFactory.create()` receives a provider name, `LlamaCppJniConfig`, and `AiPromptSupport`; returns the appropriate `AiGenerationProvider` instance (mock, LlamaCppJni, or OpenAI-compatible).
2. **Text generation**: Client calls `provider.generate(request)` or `provider.generateWithTimings(request)`.
   - For LlamaCppJni: The provider lazily initializes a native `LlamaModel` using config parameters, builds chat template kwargs and model parameters, executes native chat completion, and parses the JSON response via `ChatResponseParser`. The first content fragment is parsed via `AiCompletionParser` for `generate`, or timing data is extracted from the response for `generateWithTimings`.
   - For OpenAI-compatible: `OpenAiCompatibleProviderSupport` constructs the JSON request body, resolves the endpoint, and handles API key logic; actual network I/O is external.
3. **Completion parsing**: `AiCompletionParser.parseCompletion()` strips leading thinking blocks from the raw text output, returning cleaned answer text or throwing `IOException` if a thinking block is incomplete.
4. **Configuration**: `LlamaCppJniConfig.Builder` sets parameters from `AiGenerationConfig.DEFAULT_*` defaults; `modelPath` is required. The built config is immutable and passed to the provider constructor.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.document.AiGenerationTimings`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `net.ladenthin.srcmorph.provider.AiGenerationProvider`, `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`, `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`, `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`.
- **External/Llama.cpp**: `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`, `net.ladenthin.llama.value.Usage`.
- **JSON/Jackson**: `com.fasterxml.jackson.databind.ObjectMapper`, `com.fasterxml.jackson.databind.node.ArrayNode`, `com.fasterxml.jackson.databind.node.ObjectNode`.
- **Java stdlib**: `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`.
- **Specify**: `org.jspecify.annotations.Nullable`.
- **Lombok**: `@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`, `@Nullable`.

#### Cross-cutting
- **Immutability/configuration**: `LlamaCppJniConfig` is an immutable value type with value semantics; builder is mutable and not thread-safe, intended for single-threaded fill-then-build pattern.
- **Exception handling**: `AiCompletionParser` throws `IOException` when a leading thinking block starts but the closing marker is absent (exhausted model token budget). `OpenAiCompatibleProviderSupport.apiKeyFor` throws `IOException` if a key is sent in cleartext to a non-loopback host. `AiGenerationProviderFactory` throws `IllegalArgumentException` for unrecognized provider names.
- **Null safety**: `OpenAiCompatibleProviderSupport.apiKey()` returns `@Nullable String`; returns null when `apiKeyEnv` is unset, blank, or the looked-up key is null/blank after trimming. `LlamaCppJniConfig` builder setter `apiKeyEnv(String)` accepts `null` to restore default.
- **Threading**: `LlamaCppJniAiGenerationProvider` has no synchronization or immutability annotations; native model handle is lazily initialized on first use, which is not thread-safe without external synchronization. Other classes are stateless or final with no mutable shared state across threads.
- **API key handling**: `OpenAiCompatibleProviderSupport` resolves API keys from environment variables and enforces that keys for plain HTTP endpoints must be to loopback hosts (`localhost`, `127.x.x.x`, `[::1]`), throwing `IOException` otherwise.
