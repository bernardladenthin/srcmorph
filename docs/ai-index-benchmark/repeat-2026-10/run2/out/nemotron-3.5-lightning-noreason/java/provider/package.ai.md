### java/provider
- H: 1.0
- C: 78791043
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:02:37Z
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
> This package provides pluggable AI generation backends for source code transformation, supporting local GGUF models via llama.cpp JNI, OpenAI-compatible APIs, and mock implementations, with completion parsing and configuration management.

#### Purpose
The package orchestrates AI-augmented source code processing by selecting and configuring generation providers (mock, llama.cpp JNI, OpenAI-compatible), parsing model outputs, and handling request/response construction without network I/O for compatible endpoints.

#### Responsibilities
- **Provider selection and instantiation**: Factory logic to create provider instances by name (mock, llama.cpp JNI, OpenAI-compatible).
- **Native model execution**: LLama.cpp JNI bridge for GGUF model inference with chat templating, KV-cache reuse, and timing.
- **Completion parsing**: Stripping thinking blocks from LLM outputs and extracting model answers.
- **Request/response construction**: Building JSON bodies and endpoints for OpenAI-compatible calls; API key validation and loopback checks.
- **Configuration management**: Fluent builder-driven immutable configuration for llama.cpp parameters.

#### Key units
- **AiCompletionParser**: Strips leading Gemma-4 thinking blocks from raw completion text to extract the model answer; returns cleaned text or throws IOException if thinking block starts but end marker is absent.
- **AiGenerationProvider** (interface): Pluggable backend producing text for generation requests; extended by MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, and OpenAiCompatibleAiGenerationProvider.
- **AiGenerationProviderFactory**: Selects and instantiates an AiGenerationProvider implementation by name; defaults to mock when provider name is blank or null; instantiates MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, or OpenAiCompatibleAiGenerationProvider.
- **LlamaCppJniAiGenerationProvider**: Runs local GGUF models through the llama.cpp JNI bridge; lazily initializes native LlamaModel; builds chat template kwargs and model parameters; executes inference via model().chatComplete(...); parses JSON response via ChatResponseParser.
- **LlamaCppJniConfig**: Immutable configuration for the llama.cpp JNI provider built through a fluent builder with thirty-seven named fields; all defaults sourced from AiGenerationConfig.DEFAULT_*.
- **OpenAiCompatibleProviderSupport**: Constructs JSON request bodies and endpoints for OpenAI-compatible model calls; validates API key transmission (allows cleartext only over HTTPS or to loopback hosts); formats error messages and extracts token counts.
- **MockAiGenerationProvider**: (Implied) Mock implementation of AiGenerationProvider for testing.

#### Data flow
User code invokes `AiGenerationProviderFactory.create(providerName, llamaConfig, promptSupport)` to obtain a provider instance. The provider's `generate(AiGenerationRequest)` or `generateWithTimings(AiGenerationRequest)` method is called, which for llama.cpp lazily initializes a native model, builds chat/template parameters, executes inference, and parses the JSON response via `ChatResponseParser`, ultimately returning text via `AiCompletionParser`. For OpenAI-compatible, `OpenAiCompatibleProviderSupport` constructs the JSON body and endpoint, validates the API key, and the provider executes the request. Parsed results are returned as `String` text or `AiGenerationTimings`.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`; `net.ladenthin.srcmorph.provider.*` interfaces and implementations; `net.ladenthin.llama.*` native bindings; `net.ladenthin.llama.value.ChatResponse`, `Usage`.
- **External**: `java.io.IOException`, `java.util.Collections`, `java.util.List`, `java.util.Objects`; `org.jspecify.annotations.Nullable`; Lombok annotations (`@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`, `@Nullable`); Jackson `ObjectMapper` for JSON serialization; `org.jspecify` for null annotations.

#### Cross-cutting
- **Builder pattern**: `LlamaCppJniConfig` uses a fluent builder with thirty-seven setters; builder is not thread-safe, config is immutable after `build()`.
- **Null handling**: `@Nullable` annotations on model fields, chatTemplateEnableThinking, and method returns; constructor null-guards on config, promptSupport, environment.
- **Error handling**: `IOException` thrown by `AiCompletionParser` if thinking block incomplete; by `generate`/`generateWithTimings` if provider fails; by `OpenAiCompatibleProviderSupport` endpoint/key validation; `IllegalArgumentException` from factory for unrecognized provider names.
- **Concurrency**: Stateless interfaces and pure functions; llama.cpp model lazily initialized via single-threaded check-then-act; native handle not thread-safe for concurrent generation—typical usage assumes single-threaded or externally synchronized access.
- **Configuration defaults**: All `LlamaCppJniConfig` fields default to `AiGenerationConfig.DEFAULT_*` values; `drySequenceBreakers`/`stopStrings` default to empty list; `chatTemplateEnableThinking` null means model default.
