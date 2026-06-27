### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:15:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides AI text generation for documents based on prompts and parameters.

#### Purpose
- Defines an interface for pluggable AI backends
- Handles text generation requests for AI models

#### Type
- Interface
- Implements AutoCloseable
- Extends net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest

#### Input
- AiGenerationRequest object containing prompt details

#### Output
- Generated text as String
- May throw IOException

#### Core logic
- Generates text based on AI model and request parameters
- Supports temperature override for retry attempts
- Provides default close method

#### Public API
- generate(AiGenerationRequest) -> String
  Generates text using provider's default settings
- generate(AiGenerationRequest, float) -> String
  Generates text with specified temperature override
- close() -> void
  Closes the AI generation provider

#### Dependencies
- java.io.IOException
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest

#### Exceptions / Errors
- Throws IOException for underlying provider failures
- Returns null or blank string for empty model responses

#### Concurrency
- Thread-safe due to interface nature and AutoCloseable implementation
