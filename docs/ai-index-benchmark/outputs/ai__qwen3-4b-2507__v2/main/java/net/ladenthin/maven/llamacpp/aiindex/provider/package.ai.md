### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:27:08Z
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
> Enables local AI text generation and response parsing in a Maven-based indexing pipeline using llama.cpp JNI bindings or mock providers  

#### Purpose  
- Facilitates AI-driven source code summarization via pluggable generation backends  
- Supports both real-time LLM inference (via llama.cpp) and deterministic testing (via mock provider)  

#### Responsibilities  
- AI generation orchestration through provider factory and interface abstraction  
- Local model execution via llama.cpp JNI bindings with configurable inference parameters  
- Parsing of raw model outputs to extract final answers from reasoning chains  
- Test simulation through a mock provider with predictable, deterministic responses  

#### Key units  
- `AiGenerationProvider`: abstracts AI text generation across backends  
- `LlamaCppJniAiGenerationProvider`: implements local GGUF model inference via llama.cpp JNI  
- `AiCompletionParser`: extracts final answer from raw completion text containing reasoning blocks  
- `LlamaCppJniConfig`: immutable configuration for model and inference settings  
- `AiGenerationProviderFactory`: selects and instantiates provider based on name  

#### Data flow  
AiGenerationRequest → AiGenerationProviderFactory (selects provider) → Provider.generate() → LlamaCppJniAiGenerationProvider (loads model, runs inference) → Native chat completion → AiCompletionParser (strips thinking blocks) → Final answer output  

#### Dependencies  
- `LlamaCppJniConfig` for model and generation parameters  
- `AiPromptSupport` to build prompts from source context  
- `MockAiGenerationProvider` as fallback/test provider  
- `AiCompletionParser` to validate and clean response outputs  

#### Cross-cutting  
- Immutable configuration via record-like design (LlamaCppJniConfig)  
- Shared error handling: IOException for provider failures or parsing issues  
- Deterministic mock behavior for testing  
- Temperature override during retries to avoid empty responses  
- Thread safety in immutable units; model loading is lazy but not synchronized, risking concurrent access issues
