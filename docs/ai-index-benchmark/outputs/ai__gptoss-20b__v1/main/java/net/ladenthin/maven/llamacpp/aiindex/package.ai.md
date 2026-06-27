### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: 83E61DEB
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:36:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Enables Maven to generate index fields by selecting AI prompts and running llama.cpp locally or via a mock, producing clean text for documentation.

#### Purpose
- Configure AI prompts, models, and context for Maven index generation.
- Execute AI generation using llama.cpp JNI or deterministic mock.

#### Responsibilities
- **Configuration** – Parse `<aiFieldGeneration>` and `<aiModelDefinition>` XML elements, expose `AiFieldGenerationConfig`, `AiGenerationConfig`, and `AiModelDefinition`.  
- **Selection** – `AiFieldGenerationSelector` picks the first matching config for a file’s extension.  
- **Provider** – `AiGenerationProviderFactory` creates a provider (`MockAiGenerationProvider` or `LlamaCppJniAiGenerationProvider`).  
- **Parsing** – `AiCompletionParser` extracts the final answer from raw completions.  
- **Prompt building** – `AiPromptSupport` (used by JNI provider) creates prompt strings for the model.

#### Key units
- **AiFieldGenerationConfig** – mutable bean: prompt key, model key, optional file‑extension filters.  
- **AiGenerationConfig** – concrete AI parameters: model path, context, temperature, etc.; drives the generation engine.  
- **AiModelDefinition** – reusable definition referenced by key; converted by `AiModelDefinitionSupport`.  
- **AiGenerationProvider** – interface for generating text; default temperature/close logic.  
- **AiGenerationProviderFactory** – selects provider implementation based on key.  
- **LlamaCppJniAiGenerationProvider** – builds prompt, configures `InferenceParameters`, calls `model.chatCompleteText`, parses result.  
- **MockAiGenerationProvider** – deterministic summary for tests.  
- **AiCompletionParser** – removes Gemma‑4 thinking blocks, returns clean string.

#### Data flow
1. Maven reads `<aiFieldGeneration>` & `<aiModelDefinition>` → creates config objects.  
2. `AiModelDefinitionSupport` builds a read‑only map `definitionKey → AiGenerationConfig`.  
3. During run, `AiFieldGenerationSelector` selects the applicable `AiFieldGenerationConfig` for a source file.  
4. Selected config’s model key resolves to an `AiGenerationConfig` via the support map.  
5. `AiGenerationProviderFactory.create` returns the provider.  
6. Caller submits `AiGenerationRequest` (prompt key, source, header).  
7. JNI provider builds prompt, wraps in chat messages, configures `InferenceParameters`, invokes `model.chatCompleteText`.  
8. Raw completion is parsed by `AiCompletionParser` → cleaned answer returned.

#### Dependencies
- Java collections (`List`, `Map`, etc.).
- Lombok `@ToString` for logging.
- `org.jspecify.annotations.Nullable` for optional fields.
- `net.ladenthin.llama` model, parameters, and Pair classes.
- Native JNI library via `LlamaCppJniConfig`.
- Internal helpers: `Java8CompatibilityHelper`, `AiPromptSupport`, `AiGenerationRequest`.

#### Cross-cutting
- **Immutability**: Config objects are immutable after construction; mutable setters expose defensive copies.  
- **Thread‑safety**: JNI provider performs lazy, unsynchronized initialization; parser and mock provider are stateless.  
- **Exception handling**: `IOException` for JNI failures, `IllegalArgumentException` for unknown provider keys.  
- **Factory pattern**: Centralizes provider creation, simplifying extension.  
- **Logging**: Consistent `@ToString` annotations across beans.
