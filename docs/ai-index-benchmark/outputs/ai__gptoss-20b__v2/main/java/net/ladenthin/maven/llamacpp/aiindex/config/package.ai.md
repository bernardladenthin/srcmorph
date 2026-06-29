### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:19:54Z
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
> Configures and maps AI field‑generation steps for Maven‑based projects, allowing field‑specific prompts, model definitions, and extension‑based selection.

#### Purpose
- Central AI configuration provider for Maven Llama‑Cpp plugin.
- Supplies prompt‑model mappings, file‑extension filtering, and runtime generation settings.

#### Responsibilities
- **Prompt‑Model mapping** – `AiFieldGenerationConfig` holds prompt key ↔ model key.
- **Extension selection** – `AiFieldGenerationSelector` resolves applicable config per file.
- **Runtime generation parameters** – `AiGenerationConfig` and `AiModelDefinition` expose model path, context, temperature, etc.
- **Definition lookup** – `AiModelDefinitionSupport` validates and retrieves `AiGenerationConfig` for a given key.

#### Key units
- **AiFieldGenerationConfig** – mutable DTO for a single field‑generation step; defensive copies of file extensions.
- **AiFieldGenerationSelector** – static helper returning the first matching or fallback config for a filename.
- **AiGenerationConfig** – JavaBean holding generation hyper‑parameters; exposes unmodifiable stop‑string list.
- **AiModelDefinition** – mutable model definition mirroring `AiGenerationConfig`; used by plugin configuration.
- **AiModelDefinitionSupport** – constructs a lookup map of key → `AiGenerationConfig`; throws on missing keys.
- **AiGenerationKind** – enum defining FILE_SUMMARY or PACKAGE_SUMMARY scopes (unused in current flow).

#### Data flow
1. Maven injects `AiFieldGenerationConfig` instances via setters.
2. `AiFieldGenerationSelector.selectForFileName()` chooses the config matching a source file’s extension.
3. `AiModelDefinitionSupport` builds a map from `AiModelDefinition` keys to `AiGenerationConfig`.
4. The chosen `AiGenerationConfig` drives the Llama‑Cpp AI provider during field generation.

#### Dependencies
- Internal: `AiFieldGenerationConfig`, `AiModelDefinition`, `AiGenerationConfig`, `AiFieldGenerationSelector`.
- External: Lombok (`@ToString`), Java Collections (`ArrayList`, `Collections`, `List`, `Map`, `HashMap`), `org.jspecify.annotations.Nullable`, `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition`.

#### Cross-cutting
- Defensive copying of mutable collections and returning unmodifiable views.
- Null handling: `null` or empty file extensions treated as fallbacks.
- Thread safety: selector uses local variables; other classes are mutable without synchronization.
- Lombok `@ToString` for diagnostics but no `equals`/`hashCode`.
- Consistent use of JavaBean getters/setters for Maven injection.
