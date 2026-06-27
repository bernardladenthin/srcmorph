### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:22:42Z
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
> Enables pluggable local and mock AI text generation for Maven-based code indexing with structured prompt handling and response parsing.

#### Purpose
- Provides standardized interface for AI text generation in Maven plugins
- Supports local llama.cpp inference and deterministic mocking for testing

#### Responsibilities
- AI generation provider selection and instantiation
- Local llama.cpp JNI integration with configuration management
- Prompt building, completion parsing, and response cleaning
- Mock implementation for testing and development

#### Key units
- AiGenerationProvider interface defines text generation contract
- LlamaCppJniAiGenerationProvider wraps llama.cpp JNI for local inference
- AiCompletionParser strips internal reasoning from LLM responses
- LlamaCppJniConfig holds immutable inference parameters
- AiGenerationProviderFactory selects and creates provider instances

#### Data flow
Input request flows through AiGenerationProviderFactory to instantiate LlamaCppJniAiGenerationProvider or MockAiGenerationProvider, which builds prompt using AiPromptSupport, generates completion via llama.cpp or mock logic, and returns parsed response via AiCompletionParser

#### Dependencies
- AiPromptSupport for prompt building
- LlamaModel and InferenceParameters for JNI integration
- AiGenerationRequest for structured input data
- Java8CompatibilityHelper for runtime compatibility

#### Cross-cutting
- Shared AiGenerationRequest type across provider implementations
- Exception handling with IOException for generation failures
- Immutable configuration via Lombok value semantics
- Deterministic mock behavior for testing consistency
