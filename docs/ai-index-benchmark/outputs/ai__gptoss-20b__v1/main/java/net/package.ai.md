### main/java/net
- H: 1.0
- C: 9BBA9341
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:44:24Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [ladenthin/](ladenthin/package.ai.md)
---
> Generates AI‑driven index fields for Maven projects, converting configured prompts into clean, model‑produced text for documentation.  

#### Purpose
- Automates creation of documentation index fields via AI prompts.  
- Supports deterministic testing with a mock provider alongside live llama.cpp execution.  

#### Responsibilities  
- **Configuration** – parse `<aiFieldGeneration>` & `<aiModelDefinition>` XML into immutable beans (`AiFieldGenerationConfig`, `AiGenerationConfig`, `AiModelDefinition`).  
- **Selection** – `AiFieldGenerationSelector` matches file‑extension filters to the first applicable config.  
- **Provider Creation** – `AiGenerationProviderFactory` supplies `LlamaCppJniAiGenerationProvider` or `MockAiGenerationProvider`.  
- **Prompt Handling** – `AiPromptSupport` builds prompts; `AiCompletionParser` extracts clean answers from raw completions.  

#### Key units  
- `AiFieldGenerationConfig`: prompt key, model key, optional file‑extension filters.  
- `AiGenerationConfig`: model path, context size, temperature.  
- `AiModelDefinition`: reusable model definition referenced by key.  
- `AiGenerationProvider`: interface; implementations include `LlamaCppJniAiGenerationProvider` and `MockAiGenerationProvider`.  
- `LlamaCppJniAiGenerationProvider`: configures `InferenceParameters`, calls `model.chatCompleteText`.  
- `MockAiGenerationProvider`: deterministic summaries for unit tests.  
- `AiCompletionParser`: removes Gemma‑4 thinking blocks, returns clean string.  
- `AiFieldGenerationSelector`: matches source file extensions to the first matching `AiFieldGenerationConfig`.  
- `AiPromptSupport`: constructs prompt text from configuration.  

#### Data flow  
1. Maven reads XML → constructs config objects.  
2. `AiModelDefinitionSupport` maps definition keys to `AiGenerationConfig`.  
3. During execution, `AiFieldGenerationSelector` selects applicable config for a source file.  
4. Selected config’s model key retrieves an `AiGenerationConfig`.  
5. `AiGenerationProviderFactory.create` provides the appropriate provider.  
6. Caller submits `AiGenerationRequest` (prompt key, source, header).  
7. JNI provider builds prompt, configures `InferenceParameters`, invokes `model.chatCompleteText`.  
8. `AiCompletionParser` cleans raw completion → final answer returned.  

#### Dependencies  
- Java collections (`List`, `Map`), Lombok `@ToString`, `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.llama` model and parameter classes.  
- Native JNI library via `LlamaCppJniConfig`.  
- Internal helpers: `Java8CompatibilityHelper`, `AiPromptSupport`, `AiGenerationRequest`.  

#### Cross-cutting  
- **Immutability** – configuration beans are immutable after construction; defensive copies in setters.  
- **Thread‑safety** – JNI provider lazily initializes the model without synchronization; parser and mock provider are stateless.  
- **Exception handling** – `IOException` for JNI errors, `IllegalArgumentException` for unknown provider keys.  
- **Factory pattern** – `AiGenerationProviderFactory` centralizes provider creation.  
- **Logging & Debugging** – consistent `@ToString` annotations across all beans for traceability.
