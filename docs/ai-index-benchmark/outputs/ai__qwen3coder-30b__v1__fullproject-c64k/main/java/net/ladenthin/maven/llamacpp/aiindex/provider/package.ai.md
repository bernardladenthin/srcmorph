### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: CC70AD52
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:23:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
- F: [package-info.java](package-info.java.ai.md)
---
> Provides pluggable AI text generation for code indexing, supporting local GGUF models and mock backends for testing.

#### Purpose
- Generates AI-derived text from structured input requests for code indexing tasks.
- Offers flexible backend selection (local or mock) with configurable inference parameters.

#### Responsibilities
- AI model inference using native llama.cpp via JNI bindings.
- Mock AI generation for unit testing and development.
- Parsing and cleaning of raw LLM responses to remove internal reasoning blocks.
- Dynamic instantiation and configuration of AI generation providers based on runtime settings.

#### Key units
AiCompletionParser extracts clean text from LLM outputs by removing thinking blocks  
AiGenerationProvider defines contract for AI text generation with retry support  
AiGenerationProviderFactory selects and instantiates AI backend implementations  
LlamaCppJniAiGenerationProvider uses native GGUF models via JNI for inference  
LlamaCppJniConfig encapsulates llama.cpp model configuration parameters  
MockAiGenerationProvider delivers deterministic mock responses for testing  

#### Data flow
Input requests are processed through AiGenerationRequest objects, which are transformed into prompts by AiPromptSupport. These are fed into either LlamaCppJniAiGenerationProvider (for native inference) or MockAiGenerationProvider (for testing), with outputs parsed by AiCompletionParser to remove internal reasoning before being returned.

#### Dependencies
- net.ladenthin.llama.LlamaModel for native model invocation  
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport for prompt construction  
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest for input data structure  
- java.io.IOException for error handling in text generation  

#### Cross-cutting
Shared interface AiGenerationProvider defines common API across implementations  
Stateless, immutable configuration via LlamaCppJniConfig ensures safe concurrent access  
Exception handling standardizes on IOException for backend failures  
MockAiGenerationProvider enables deterministic test behavior without external dependencies
