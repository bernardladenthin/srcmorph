### java/provider
- H: 1.0
- C: 962F0B37
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T12:36:06Z
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
> Provides pluggable AI text generation backends and utilities for parsing LLM completions and supporting offline OpenAI-compatible generation within the srcmorph package.

#### Purpose
- Supply text generation services for srcmorph AI requests via multiple backends (llama.cpp JNI, mock, OpenAI-compatible).
- Extract model answers from raw LLM completion text by stripping internal thinking blocks before storage.
- Configure generation knobs and model paths for the llama.cpp JNI provider.

#### Responsibilities
- Parsing and cleaning raw LLM completion text (AiCompletionParser).
- Pluggable AI generation backend interface and implementations (AiGenerationProvider, LlamaCppJniAiGenerationProvider, Mock, OpenAiCompatible).
- Factory for selecting and instantiating providers by name (AiGenerationProviderFactory).
- Lazy model loading and execution via llama.cpp JNI bindings (LlamaCppJniAiGenerationProvider).
- Immutable configuration holder for generation parameters and model path (LlamaCppJniConfig).
- Offline OpenAI-compatible request body, endpoint, API key, and timing utilities (OpenAiCompatibleProviderSupport).

#### Key units
- AiCompletionParser: extracts model answer from raw completion text, strips thinking blocks.
- AiGenerationProvider: interface for text generation, extends AutoCloseable.
- AiGenerationProviderFactory: factory selecting provider implementations by name.
- LlamaCppJniAiGenerationProvider: final class implementing AiGenerationProvider and AutoCloseable, uses JNI.
- LlamaCppJniConfig: immutable configuration with 41 generation knobs and model path.
- OpenAiCompatibleProviderSupport: offline request/response utilities for local OpenAI-compatible providers.

#### Data flow
- Raw completion text -> AiCompletionParser.parseCompletion(String) -> cleaned answer String.
- AiGenerationRequest -> AiGenerationProvider.generate(request) -> completion text.
- Provider selection via AiGenerationProviderFactory.create(providerName, llamaConfig, promptSupport) -> provider instance.
- LlamaCppJniConfig provides generation parameters; LlamaCppJniAiGenerationProvider uses config and prompt support to execute chat.
- OpenAiCompatibleProviderSupport builds JSON requestBody, endpoint, and timing info for offline providers.

#### Dependencies
- Internal: AiCompletionParser, AiGenerationProvider, AiGenerationProviderFactory, LlamaCppJniAiGenerationProvider, LlamaCppJniConfig, OpenAiCompatibleProviderSupport.
- External packages: net.ladenthin.srcmorph.document (AiGenerationRequest), net.ladenthin.srcmorph.prompt (AiPromptSupport), net.ladenthin.srcmorph.support (Java8CompatibilityHelper), net.ladenthin.llama.value (ChatResponse, Usage), com.fasterxml.jackson.databind (ObjectMapper, ArrayNode, ObjectNode), java.io, java.util, java.net.
- Libraries: Lombok (@ToString, @EqualsAndHashCode), org.jspecify.annotations.Nullable.

#### Cross-cutting
- Stateless design; no shared mutable state; no synchronization required for parsers and providers.
- Immutable configuration after construction; builder single-threaded.
- Lazy model loading in LlamaCppJniAiGenerationProvider; model initialization not synchronized.
- Null handling: null input treated as empty string; null config throws NullPointerException.
- Thread-safety: factory and provider are stateless; final class immutable; no explicit thread-safety guarantees.
- Error handling: IOException on generation/close failures; IOException or null for unclosed thinking blocks; IllegalArgumentException for unknown providers.
