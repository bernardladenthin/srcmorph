### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:56:58Z
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
> This package enables AI-driven field generation in Maven plugin workflows by defining model configurations, prompt mappings, and execution scopes tailored to source file types.

#### Purpose  
- Configures AI model parameters and inference behavior for Maven-based source processing  
- Maps AI prompt templates to specific file extensions to enable per-file or per-package field generation  

#### Responsibilities  
- AI model configuration management (parameters, context, retries)  
- Prompt/template routing based on file extension  
- Scope definition: individual files vs. entire packages  
- Model lookup and validation via key-based configuration  

#### Key units  
- `AiFieldGenerationConfig`: maps prompt and model keys to file extensions for selective AI field generation  
- `AiFieldGenerationSelector`: selects the appropriate generation config by file extension with fallback logic  
- `AiGenerationConfig`: holds mutable inference parameters (temperature, context size, retries, stop strings)  
- `AiModelDefinition`: stores reusable AI model settings keyed by identifier  
- `AiModelDefinitionSupport`: resolves model keys to full generation configs with validation and error handling  
- `AiGenerationKind`: defines scope of AI output as file-level or package-level  

#### Data flow  
Input file name → `AiFieldGenerationSelector` matches extension → selects `AiFieldGenerationConfig` → retrieves associated prompt and model key → `AiModelDefinitionSupport` resolves model key to full `AiGenerationConfig` → applies model parameters to generate field values via AI inference pipeline  

#### Dependencies  
- Internal: `AiModelDefinition`, `AiGenerationConfig`, `AiFieldGenerationConfig`, `AiGenerationKind`, `Collection`, `List`, `Iterable`, `Map`, `Objects`  
- External: Maven plugin reflection, llama.cpp inference engine (via model paths), Java 8+ features  

#### Cross-cutting  
- Immutable output views for lists and configs (e.g., `getStopStrings()` returns unmodifiable list)  
- Null handling via `@Nullable` annotations and fallback behavior for missing values  
- Thread-safe by design due to immutability and no shared mutable state  
- Lombok `@ToString` used across all classes for debugging and logging  
- Static defaults in `AiGenerationConfig` provide baseline inference behavior  
- Error handling: explicit `NullPointerException` on null keys, `IllegalArgumentException` on missing definitions or invalid inputs
