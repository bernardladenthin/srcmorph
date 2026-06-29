### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:25:13Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiFieldGenerationConfig.java](AiFieldGenerationConfig.java.ai.md)
- F: [AiFieldGenerationSelector.java](AiFieldGenerationSelector.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiGenerationKind.java](AiGenerationKind.java.ai.md)
- F: [AiModelDefinition.java](AiModelDefinition.java.ai.md)
- F: [AiModelDefinitionSupport.java](AiModelDefinitionSupport.java.ai.md)
---
> This package configures and orchestrates AI-driven field generation and model settings for Maven plugins using llama.cpp, enabling language-specific prompts and per-file or per-package summarization.

#### Purpose
- Defines AI configuration for field generation based on file extensions  
- Manages model parameters and generation behavior (temperature, context size, retries)  
- Supports both per-file and per-package AI summary scopes  

#### Responsibilities
- Field generation configuration via file extension matching  
- Model-specific AI parameter definitions and lookup  
- Generation parameter tuning and validation  
- Scope selection for AI summaries (file-level vs package-level)  

#### Key units
- **AiFieldGenerationConfig**: maps prompt and model keys to file extensions for selective field generation  
- **AiFieldGenerationSelector**: selects the appropriate config by file extension, with fallback support  
- **AiGenerationConfig**: holds core AI inference settings like temperature, max tokens, stop strings  
- **AiModelDefinition**: stores reusable model configurations with defaults from AiGenerationConfig  
- **AiModelDefinitionSupport**: resolves model definitions by key to generate runtime AI configs  

#### Data flow
Input file name → matched against file extensions in AiFieldGenerationConfig → selected via AiFieldGenerationSelector → resolved into AiGenerationConfig via AiModelDefinitionSupport → used for AI prompt generation and inference  

#### Dependencies
- AiFieldGenerationSelector depends on AiFieldGenerationConfig  
- AiGenerationConfig depends on AiModelDefinition and Llama model parameters  
- AiModelDefinitionSupport uses AiModelDefinition and AiGenerationConfig for conversion  
- Cross-package: net.ladenthin.llama.parameters.ModelParameters, org.jspecify.annotations.Nullable  

#### Cross-cutting
- Immutable stop strings via unmodifiableList wrappers  
- Null safety enforced with @Nullable and defensive checks in setters  
- Immutability of output views (fileExtensions, stopStrings)  
- Final classes (AiFieldGenerationSelector, AiModelDefinitionSupport) ensure thread safety  
- Uniform use of @ToString for readable instance representation across classes
