### java/provider
- H: 1.0
- C: FA7FDCD7
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:25:07Z
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
> This package provides AI text generation capabilities, supporting various AI providers and models through a unified interface and configuration system.

#### Purpose
- Facilitates AI-driven text generation
- Offers pluggable backends for different AI implementations
- Manages AI model execution and request handling

#### Responsibilities
- AI Provider Selection and Instantiation
- Text Generation and Timing Management
- Configuration Management
- Error Handling and API Interaction

#### Key units
- `AiCompletionParser`: Parses and cleans AI model answers
- `AiGenerationProvider`: Interface for AI text generation
- `AiGenerationProviderFactory`: Selects and instantiates AI providers
- `LlamaCppJniAiGenerationProvider`: Local GGUF model-based AI generation
- `LlamaCppJniConfig`: Configuration for llama.cpp JNI provider
- `OpenAiCompatibleProviderSupport`: Manages OpenAI-compatible AI requests

#### Data flow
AI generation requests flow through the provider factory, configuration, and execution pipeline, with results returned as generated text or timing information.

#### Dependencies
- Core Java utilities and I/O
- Jackson library for JSON processing
- Custom classes for request/response handling and configuration

#### Cross-cutting
- Immutable configuration objects
- Thread-safe implementations where applicable
- Error handling and timing management across providers
- Unified API for different AI provider implementations
