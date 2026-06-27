### main/java
- H: 1.0
- C: E9AF7086
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:08:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [net/](net/package.ai.md)
---
> This package enables AI-driven metadata generation for source files in Maven plugin workflows by dynamically creating field definitions based on file type using local LLMs or mock providers.

#### Purpose  
- Configures AI model behavior and prompt routing for per-file or per-package field generation in Maven-based source processing  
- Provides a pluggable, testable pipeline for AI text generation using local LLMs or mock providers  

#### Responsibilities  
- AI model configuration and parameter management (temperature, context size, retries)  
- Prompt/template selection based on file extension or type  
- Scope definition: file-level vs. package-level field generation  
- Real-time AI inference via llama.cpp or deterministic mock execution  
- Input parsing and output cleaning to extract final responses from raw LLM outputs  

#### Key units  
- `AiFieldGenerationConfig`: maps prompt templates and model keys to file extensions for selective field generation  
- `AiFieldGenerationSelector`: selects config by file extension with fallback logic  
- `AiGenerationConfig`: holds mutable inference parameters (temperature, context size, stop strings)  
- `AiModelDefinition`: stores reusable AI model settings by identifier  
- `AiModelDefinitionSupport`: resolves model keys to full configs with validation and error handling  
- `AiGenerationKind`: defines scope of AI output as file-level or package-level  
- `AiGenerationProvider`: abstract interface for pluggable AI generation backends  
- `LlamaCppJniAiGenerationProvider`: executes GGUF models via llama.cpp JNI with lazy loading  
- `LlamaCppJniConfig`: immutable config object defining model path, context, temperature, stop strings  
- `AiCompletionParser`: strips internal reasoning blocks from raw LLM output to extract final answer  

#### Data flow  
Input file name → `AiFieldGenerationSelector` matches extension → selects `AiFieldGenerationConfig` → retrieves prompt and model key → `AiModelDefinitionSupport` resolves key to full `AiGenerationConfig` → applies parameters to AI provider → provider generates text (real or mock) → raw output passed to `AiCompletionParser` → final clean response stored  

#### Dependencies  
- Internal: `AiFieldGenerationConfig`, `AiGenerationConfig`, `AiModelDefinition`, `AiGenerationKind`, `Collection`, `List`, `Map`, `Objects`, `AiGenerationRequest`, `AiPromptSupport`  
- External: Maven plugin reflection, llama.cpp inference engine (via GGUF models), Java 8+ features  

#### Cross-cutting  
- Immutable configuration and state objects (e.g., `LlamaCppJniConfig`, `AiGenerationConfig`) for thread safety and concurrent access  
- Null handling via `@Nullable` annotations and fallback logic for missing values  
- Stateless, thread-safe providers with no shared mutable state  
- Lombok annotations (`@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`) used across all classes for immutability and debugging  
- Explicit error handling: `NullPointerException` on null keys, `IllegalArgumentException` on invalid inputs or missing definitions  
- Deterministic output patterns in mock provider and parser for consistent test behavior  
- Static defaults in `AiGenerationConfig` provide baseline inference parameters
