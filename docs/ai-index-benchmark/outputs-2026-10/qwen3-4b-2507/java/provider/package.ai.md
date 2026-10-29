### java/provider
- H: 1.0
- C: 90FC146A
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:15:15Z
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
> This package provides pluggable, configurable AI text generation for source code transformation, supporting local models (via llama.cpp JNI), OpenAI-compatible APIs, and mock providers with full timing and reasoning extraction.

#### Purpose
- Enables flexible AI text generation using local or remote models with configurable parameters and fallbacks  
- Extracts clean final responses from raw model outputs by stripping internal reasoning blocks  

#### Responsibilities
- AI generation provider selection and instantiation  
- Local model execution via llama.cpp JNI with hardware-aware configuration  
- OpenAI-compatible API request construction and secure key handling  
- Response parsing and timing metrics extraction  
- Null-safe, thread-aware generation with consistent output formatting  

#### Key units
- `AiGenerationProvider`: interface defining generation and timing capabilities  
- `AiGenerationProviderFactory`: selects and creates provider instances based on configuration  
- `LlamaCppJniAiGenerationProvider`: executes local GGUF models via llama.cpp JNI with full timing  
- `LlamaCppJniConfig`: immutable configuration for model parameters, GPU, sampling, and cache policies  
- `AiCompletionParser`: strips internal reasoning from raw model responses to extract final answer  
- `OpenAiCompatibleProviderSupport`: builds secure, validated API requests and computes performance metrics  

#### Data flow
Request → `AiGenerationProviderFactory` selects provider → Provider (llama.cpp or OpenAI) generates text → Response parsed by `AiCompletionParser` → Final answer + timing metrics returned to caller  

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport` (prompt rendering)  
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (default config values)  
- `com.fasterxml.jackson.databind.ObjectMapper` (JSON request body building)  
- `java.net.URL`, `java.util.regex.Pattern` (URL validation and parsing)  
- `net.ladenthin.llama.value.ChatResponse`, `Usage` (response parsing)  
- `java.util.Collections`, `java.util.Objects` (immutable collections and null safety)  

#### Cross-cutting
- Immutable design: all configuration and response objects are immutable  
- Null safety: all inputs are validated or treated as empty; outputs are never null  
- Error handling: consistent use of `IOException` for I/O failures, `IllegalArgumentException` for invalid inputs  
- Concurrency: no shared state; thread-safe via immutability and lazy initialization  
- Reasoning extraction: all model responses are processed to remove internal thinking blocks before storage or use
