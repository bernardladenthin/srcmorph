### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:37:49Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
---
> A package for parsing LLM completion text, generating AI text, and providing configurable AI generation providers.

#### Purpose
- Parses raw LLM completion text.
- Generates AI text with configurable providers.

#### Responsibilities
- `AiCompletionParser`: Strips thinking block from completion text.
- `AiGenerationProvider`: Interface for AI text generation.
- `AiGenerationProviderFactory`: Factory for instantiating `AiGenerationProvider` implementations.
- `LlamaCppJniAiGenerationProvider`: JNI-based local GGUF model inference.
- `LlamaCppJniConfig`: Configuration for llama.cpp JNI provider.
- `MockAiGenerationProvider`: Mock provider for deterministic testing.

#### Key units
- `AiCompletionParser`: Parses and cleans completion text.
- `AiGenerationProvider`: Generates AI text.
- `AiGenerationProviderFactory`: Factory for provider instantiation.
- `LlamaCppJniAiGenerationProvider`: Local GGUF model inference.
- `LlamaCppJniConfig`: Configuration for llama.cpp.
- `MockAiGenerationProvider`: Mock text generation.

#### Data flow
- `AiGenerationRequest` -> `AiGenerationProviderFactory` -> `AiGenerationProvider` -> `String` (generated text).

#### Dependencies
- `java.io.IOException`.
- `lombok.ToString`.
- `lombok.EqualsAndHashCode`.
- `lombok.ToString`.
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`.
- `lombok`.

#### Cross-cutting
- Thread-safe design for `AiGenerationProvider`.
- Lazy loading of `LlamaModel` in `LlamaCppJniAiGenerationProvider`.

#### Notable internal collaborations
- `AiCompletionParser` for text parsing.
- `LlamaCppJniConfig` for llama.cpp configuration.
- `AiGenerationRequest` for request handling.
