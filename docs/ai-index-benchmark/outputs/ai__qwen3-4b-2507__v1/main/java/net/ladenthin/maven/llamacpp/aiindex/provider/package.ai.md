### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:59:19Z
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
> This package enables pluggable, configurable AI text generation from document prompts using local LLMs like llama.cpp, with support for mocking and deterministic testing of AI response pipelines.

#### Purpose
- Provides a modular, extensible pipeline for generating AI responses from document inputs using configurable backends  
- Supports both real (llama.cpp JNI) and mock-based AI generation for testing and fallback scenarios  

#### Responsibilities
- AI generation execution via provider interfaces (e.g., llama.cpp or mock)  
- Configuration management for LLM inference parameters  
- Parsing raw LLM output to extract final answers by removing internal reasoning blocks  
- Provider instantiation and selection based on configuration name  
- Deterministic mock generation for test environments  

#### Key units
- `AiGenerationProvider`: abstracts AI text generation with pluggable backends; supports temperature overrides and I/O error handling  
- `LlamaCppJniAiGenerationProvider`: executes GGUF model inference via llama.cpp JNI with lazy loading and prompt templating  
- `LlamaCppJniConfig`: immutable configuration object defining model path, context size, temperature, top-p/k, stop strings  
- `AiCompletionParser`: extracts final model output from raw completion text by removing internal thinking blocks  
- `MockAiGenerationProvider`: generates deterministic, testable summaries from file names to simulate AI responses in isolation  
- `AiGenerationProviderFactory`: creates and returns an instance of an AI provider based on configuration name with fallback to mock  

#### Data flow
- A document request is processed into an `AiGenerationRequest` object containing prompt and metadata  
- The `AiGenerationProviderFactory` selects a provider (real or mock) by name and instantiates it  
- The selected provider generates text via `generate()` using configured or overridden temperature parameters  
- For real providers, the model is lazily loaded and inference parameters are set; output is parsed and returned  
- For mock providers, a deterministic summary derived from the input file name is returned  
- Raw completion text (if generated) is passed to `AiCompletionParser` to strip internal reasoning blocks before storage  

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`  
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`  
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiCompletionParser`  
- `net.ladenthin.llama.LlamaModel`, `InferenceParameters`, `ModelParameters`  
- `java.io.IOException`  
- `java.util.List`, `java.util.Collections`  
- `lombok.ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`  

#### Cross-cutting
- All providers are stateless and thread-safe, with no shared mutable state  
- Immutable configuration objects (`LlamaCppJniConfig`) used throughout to ensure safe concurrent access  
- Common exception handling: `IOException` thrown on I/O failures or null model paths  
- Deterministic output patterns in mock provider and parser for consistent test behavior  
- Use of Lombok annotations for immutability, toString, equals, and hash code generation across units
