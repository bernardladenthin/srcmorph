### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:24:19Z
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
> This package provides AI text generation capabilities using various backends, including local GGUF models via llama.cpp JNI binding and mock implementations for testing.

#### Purpose
- Offers pluggable AI text generation services
- Supports multiple AI provider implementations

#### Responsibilities
- AI generation request handling
- Provider selection and instantiation
- Text parsing and cleaning
- Mock provider for testing

#### Key units
- `AiCompletionParser`: Parses LLM completion text
- `AiGenerationProvider`: Defines AI generation interface
- `AiGenerationProviderFactory`: Creates AI generation providers
- `LlamaCppJniAiGenerationProvider`: Local GGUF model provider
- `LlamaCppJniConfig`: Configuration for llama.cpp JNI
- `MockAiGenerationProvider`: Mock implementation for testing

#### Data flow
AI generation requests flow through the factory to select an appropriate provider, which then uses the completion parser to process model outputs.

#### Dependencies
- Internal collaboration between providers and parsers
- External dependencies include llama.cpp JNI bindings and AI model files

#### Cross-cutting
- Shared configuration handling across providers
- Common exception handling for I/O operations
- Thread-safe implementations for concurrent use
