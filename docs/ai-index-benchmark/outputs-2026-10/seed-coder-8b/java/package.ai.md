### java
- H: 1.0
- C: C0DA741C
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:25:31Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides a comprehensive set of AI-driven capabilities for condition evaluation, fact extraction, and source code transformation, along with flexible AI text generation support.

#### Purpose
- AI-powered condition evaluation and fact extraction
- Source code transformation using AI
- AI-driven text generation

#### Responsibilities
- Condition evaluation and validation
- Fact extraction from code
- Source code transformation
- AI provider management and text generation

#### Key units
- `AiConditionEvaluator`: Evaluates and validates AI condition trees
- `AiFactExtractor`: Computes deterministic facts from AI fact counters
- `AiGenerationConfig`: Configures AI generation parameters
- `SrcMorphConfiguration`: Configures AI-driven source code transformations
- `AiCompletionParser`: Parses and cleans AI model answers
- `AiGenerationProvider`: Interface for AI text generation
- `AiGenerationProviderFactory`: Selects and instantiates AI providers
- `LlamaCppJniAiGenerationProvider`: Local GGUF model-based AI generation
- `LlamaCppJniConfig`: Configuration for llama.cpp JNI provider
- `OpenAiCompatibleProviderSupport`: Manages OpenAI-compatible AI requests

#### Data flow
AI conditions are evaluated against file contexts, facts are extracted from source text, and source code is transformed based on AI configurations. AI generation requests flow through the provider factory, configuration, and execution pipeline, with results returned as generated text or timing information.

#### Dependencies
- Core Java utilities
- AI-specific libraries (e.g., jSpecify for nullability annotations)
- Custom AI condition and fact counter classes
- Jackson library for JSON processing
- Custom classes for request/response handling and configuration

#### Cross-cutting
- Thread-safe immutable classes for condition evaluation and fact extraction
- Configurable AI generation parameters
- Extensible source code transformation strategies
- Immutable configuration objects
- Thread-safe implementations where applicable
- Error handling and timing management across providers
- Unified API for different AI provider implementations
