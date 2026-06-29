### main/java/net/ladenthin/maven
- H: 1.0
- C: 192118FD
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:40:49Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [llamacpp/](llamacpp/package.ai.md)
---
> Generates AI‑driven index fields for Maven projects using llama.cpp or a mock provider, converting configured prompts into clean, model‑produced text for documentation.

#### Purpose
- Automate generation of index documentation fields via AI prompts.
- Support deterministic testing with a mock provider alongside live llama.cpp execution.

#### Responsibilities
- **Configuration** – parse XML `<aiFieldGeneration>` & `<aiModelDefinition>` into beans (`AiFieldGenerationConfig`, `AiGenerationConfig`, `AiModelDefinition`).  
- **Selection** – `AiFieldGenerationSelector` chooses the applicable config per source file extension.  
- **Provider Creation** – `AiGenerationProviderFactory` supplies either `LlamaCppJniAiGenerationProvider` or `MockAiGenerationProvider`.  
- **Prompt Building & Parsing** – `AiPromptSupport` constructs prompts; `AiCompletionParser` extracts clean answers from raw completions.  

#### Key units
- **AiFieldGenerationConfig** – immutable bean with prompt key, model key, and optional file‑extension filters.  
- **AiGenerationConfig** – concrete AI settings (model path, context size, temperature).  
- **AiModelDefinition** – reusable definition referenced by key; mapped by `AiModelDefinitionSupport`.  
- **AiGenerationProvider** – interface for generating text; implemented by JNI and mock providers.  
- **LlamaCppJniAiGenerationProvider** – builds chat messages, configures `InferenceParameters`, calls `model.chatCompleteText`.  
- **MockAiGenerationProvider** – deterministic summaries for unit tests.  
- **AiCompletionParser** – strips Gemma‑4 thinking blocks, returns clean string.  
- **AiFieldGenerationSelector** – matches file extensions to the first matching `AiFieldGenerationConfig`.  

#### Data flow
1. Maven loads `<aiFieldGeneration>` and `<aiModelDefinition>` XML → constructs config objects.  
2. `AiModelDefinitionSupport` creates a read‑only map `definitionKey → AiGenerationConfig`.  
3. During execution, `AiFieldGenerationSelector` selects the config applicable to the source file.  
4. Selected config’s model key resolves to an `AiGenerationConfig`.  
5. `AiGenerationProviderFactory.create` returns the appropriate provider.  
6. Caller issues `AiGenerationRequest` (prompt key, source, header).  
7. JNI provider builds prompt, configures `InferenceParameters`, invokes `model.chatCompleteText`.  
8. `AiCompletionParser` cleans the raw completion → final answer returned.

#### Dependencies
- Java collections (`List`, `Map`), Lombok `@ToString`, `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.llama` model and parameter classes.  
- Native JNI library via `LlamaCppJniConfig`.  
- Internal helpers: `Java8CompatibilityHelper`, `AiPromptSupport`, `AiGenerationRequest`.

#### Cross-cutting
- **Immutability** – configuration beans immutable after construction; defensive copies in setters.  
- **Thread‑safety** – JNI provider lazily initializes the model without synchronization; parser and mock provider are stateless.  
- **Exception handling** – `IOException` for JNI errors, `IllegalArgumentException` for unknown provider keys.  
- **Factory pattern** – centralizes provider creation in `AiGenerationProviderFactory`.  
- **Logging & Debugging** – consistent `@ToString` annotations across all beans for traceability.
