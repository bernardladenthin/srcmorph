### ai
- H: 1.0
- C: F01CDC07
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:50:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [main/](main/package.ai.md)
---
> Generates AI‑driven Maven documentation index fields by parsing XML config, selecting model definitions, and invoking llama.cpp for clean completions.

#### Purpose
- Automates creation of AI‑driven index fields for Maven projects.  
- Enables deterministic testing and live llama.cpp execution.

#### Responsibilities
- **Configuration** – parse `<aiFieldGeneration>` & `<aiModelDefinition>` XML into immutable beans (`AiFieldGenerationConfig`, `AiGenerationConfig`, `AiModelDefinition`).  
- **Selection** – `AiFieldGenerationSelector` matches file‑extension filters to the first applicable config.  
- **Provider Creation** – `AiGenerationProviderFactory` supplies `LlamaCppJniAiGenerationProvider` or `MockAiGenerationProvider`.  
- **Prompt Handling** – `AiPromptSupport` builds prompts; `AiCompletionParser` extracts clean answers from raw completions.

#### Key units
- `AiFieldGenerationConfig` – prompt key, model key, optional file‑extension filters.  
- `AiGenerationConfig` – model path, context size, temperature.  
- `AiModelDefinition` – reusable model definition referenced by key.  
- `AiGenerationProvider` – interface; implementations `LlamaCppJniAiGenerationProvider` and `MockAiGenerationProvider`.  
- `AiCompletionParser` – removes Gemma‑4 thinking blocks, returns clean string.  
- `AiFieldGenerationSelector` – matches source file extensions to the first matching config.  
- `AiPromptSupport` – constructs prompt text from configuration.

#### Data flow
1. Maven parses XML → constructs config beans.  
2. `AiModelDefinitionSupport` maps keys to `AiGenerationConfig`.  
3. `AiFieldGenerationSelector` picks applicable config for a source file.  
4. Selected config’s model key yields `AiGenerationConfig`.  
5. `AiGenerationProviderFactory.create` supplies the appropriate provider.  
6. Caller submits `AiGenerationRequest` (prompt key, source, header).  
7. JNI provider builds prompt, configures `InferenceParameters`, calls `model.chatCompleteText`.  
8. `AiCompletionParser` cleans raw completion → final answer returned.

#### Dependencies
- Java collections (`List`, `Map`).  
- Lombok `@ToString`, `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.llama` model and parameter classes.  
- Native JNI via `LlamaCppJniConfig`.  
- Internal helpers: `Java8CompatibilityHelper`, `AiPromptSupport`, `AiGenerationRequest`.

#### Cross-cutting
- Immutability of configuration beans.  
- Thread‑safety: JNI provider lazily initializes model.  
- Exception handling: `IOException` for JNI errors, `IllegalArgumentException` for unknown provider keys.  
- Factory pattern: `AiGenerationProviderFactory` centralizes provider creation.  
- Consistent logging via Lombok `@ToString`.
