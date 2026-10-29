### java/provider
- H: 1.0
- C: 61221DB3
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:20:30Z
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
> Provides pluggable local and remote AI text generation for source code transformation, with support for GGUF models via JNI and OpenAI-compatible APIs.

#### Purpose
- Enables AI-powered source code morphing through configurable text generation backends
- Supports both local inference with llama.cpp and remote API services

#### Responsibilities
- AI model configuration and instantiation
- Text generation with timing metrics
- Response parsing and error handling
- Provider selection and factory management

#### Key units
- **AiCompletionParser** extracts clean model answers from LLM responses by stripping thinking blocks
- **AiGenerationProvider** defines the contract for AI text generation in source code workflows
- **AiGenerationProviderFactory** selects and creates AI generation providers based on configuration
- **LlamaCppJniAiGenerationProvider** wraps JNI-based llama.cpp for local GGUF model inference
- **LlamaCppJniConfig** encapsulates 37 parameters for llama.cpp model behavior and inference settings
- **OpenAiCompatibleProviderSupport** handles OpenAI-compatible API request construction and response processing

#### Data flow
Configuration flows from `LlamaCppJniConfig` through `AiGenerationProviderFactory` to instantiate `AiGenerationProvider` implementations. Input `AiGenerationRequest` objects are processed by providers to generate text responses, with timing data returned from `generateWithTimings` methods. Responses are parsed by `AiCompletionParser` before being stored in AI index files.

#### Dependencies
- `net.ladenthin.llama` for JNI-based model interactions
- `net.ladenthin.srcmorph.document.AiGenerationRequest` for input prompts
- `net.ladenthin.srcmorph.prompt.AiPromptSupport` for prompt handling
- `net.ladenthin.srcmorph.config.AiGenerationConfig` for default parameter values
- `com.fasterxml.jackson.databind.ObjectMapper` for JSON processing
- `java.io.IOException` for I/O operations
- `lombok.ToString` and `EqualsAndHashCode` for object utilities

#### Cross-cutting
- All providers implement `AiGenerationProvider` interface for consistent API
- Configuration objects use builder pattern with immutable value semantics
- Error handling includes actionable messages for incomplete thinking blocks and API validation
- Thread safety considerations: `AiCompletionParser` and `LlamaCppJniConfig` are immutable; `LlamaCppJniAiGenerationProvider` is not thread-safe due to native model handle
- Environment variable lookup for API keys via `Function<String, @Nullable String>` parameter
- Null handling with `@Nullable` annotations and `Objects.requireNonNull` checks
