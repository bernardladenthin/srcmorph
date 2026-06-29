### main/java/net
- H: 1.0
- C: 9BBA9341
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:34:37Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [ladenthin/](ladenthin/package.ai.md)
---
> Enables AI-driven field generation and source code summarization in Maven plugins using local Llama.cpp inference with configurable model parameters and pluggable generation backends.

#### Purpose
- Defines AI configuration for field generation based on file extensions  
- Manages model parameters and generation behavior (temperature, context size, retries)  

#### Responsibilities
- Field generation configuration via file extension matching  
- Model-specific AI parameter definitions and lookup  
- Generation parameter tuning and validation  
- Scope selection for AI summaries (file-level vs package-level)  
- AI text generation orchestration through pluggable backends  
- Local LLM inference via llama.cpp JNI bindings  
- Parsing of model outputs to extract final answers from reasoning chains  
- Test simulation using deterministic mock providers  

#### Key units
- **AiFieldGenerationConfig**: maps prompt and model keys to file extensions for selective field generation  
- **AiFieldGenerationSelector**: selects the appropriate config by file extension, with fallback support  
- **AiGenerationConfig**: holds core AI inference settings like temperature, max tokens, stop strings  
- **AiModelDefinition**: stores reusable model configurations with defaults from AiGenerationConfig  
- **AiModelDefinitionSupport**: resolves model definitions by key to generate runtime AI configs  
- **AiGenerationProvider**: abstracts AI text generation across backends  
- **LlamaCppJniAiGenerationProvider**: implements local GGUF model inference via llama.cpp JNI  
- **AiCompletionParser**: extracts final answer from raw completion text containing reasoning blocks  
- **LlamaCppJniConfig**: immutable configuration for model and inference settings  
- **AiGenerationProviderFactory**: selects and instantiates provider based on name  

#### Data flow
Input file name → matched against file extensions in AiFieldGenerationConfig → selected via AiFieldGenerationSelector → resolved into AiGenerationConfig via AiModelDefinitionSupport → used to build prompt (via AiPromptSupport) → passed to AiGenerationProviderFactory → provider selected and executed → LlamaCppJniAiGenerationProvider runs inference → raw output processed by AiCompletionParser → final answer extracted and returned  

#### Dependencies
- AiFieldGenerationSelector depends on AiFieldGenerationConfig  
- AiGenerationConfig depends on AiModelDefinition and Llama model parameters  
- AiModelDefinitionSupport uses AiModelDefinition and AiGenerationConfig for conversion  
- LlamaCppJniAiGenerationProvider depends on LlamaCppJniConfig and AiPromptSupport  
- AiCompletionParser depends on raw output and prompt context  
- AiGenerationProviderFactory selects from AiGenerationProvider and MockAiGenerationProvider  
- Cross-package: net.ladenthin.llama.parameters.ModelParameters, org.jspecify.annotations.Nullable  

#### Cross-cutting
- Immutable stop strings via unmodifiableList wrappers  
- Null safety enforced with @Nullable and defensive checks in setters  
- Immutability of output views (fileExtensions, stopStrings)  
- Final classes (AiFieldGenerationSelector, AiModelDefinitionSupport) ensure thread safety  
- Uniform use of @ToString for readable instance representation across classes  
- Immutable configuration via record-like design (LlamaCppJniConfig)  
- Shared error handling: IOException for provider failures or parsing issues  
- Deterministic mock behavior for testing  
- Temperature override during retries to avoid empty responses  
- Thread safety in immutable units; model loading is lazy but not synchronized, risking concurrent access issues
