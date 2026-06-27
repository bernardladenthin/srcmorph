### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:29:28Z
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
> Provides a framework for configuring, selecting, and mapping AI model settings used by a Maven plugin to generate index fields.

#### Purpose
- Manage AI model and prompt definitions for Maven-based index field generation.
- Resolve appropriate configuration per source file and convert definitions into executable AI settings.

#### Responsibilities
- **Configuration beans** – `AiFieldGenerationConfig`, `AiGenerationConfig`, `AiModelDefinition`.
- **Selection logic** – `AiFieldGenerationSelector` chooses the config that matches a file’s extension.
- **Definition support** – `AiModelDefinitionSupport` builds a key‑to‑config lookup for model definitions.
- **Scope identification** – `AiGenerationKind` distinguishes file‑level from package‑level AI generation.

#### Key units
- **AiFieldGenerationConfig** – mutable bean holding prompt key, model key, and optional file‑extension filters.
- **AiFieldGenerationSelector** – selects the first matching config for a file name, falls back to a generic config.
- **AiGenerationConfig** – holds concrete AI generation parameters (model path, context size, temperature, etc.) and is used by the AI engine.
- **AiModelDefinition** – Maven‑plugin bean that defines reusable AI model settings, referenced by key.
- **AiModelDefinitionSupport** – constructs a read‑only map from definition key to `AiGenerationConfig`; validates keys.
- **AiGenerationKind** – enum marking generation scope (`FILE_SUMMARY`, `PACKAGE_SUMMARY`).

#### Data flow
1. Maven plugin reads `<aiFieldGeneration>` and `<aiModelDefinition>` XML elements → creates `AiFieldGenerationConfig` and `AiModelDefinition` instances.
2. `AiModelDefinitionSupport` converts each `AiModelDefinition` into an `AiGenerationConfig` and stores in a lookup map.
3. During execution, `AiFieldGenerationSelector` receives a file name and a list of `AiFieldGenerationConfig` objects and returns the applicable config.
4. The selected config supplies the prompt key and model definition key, which are resolved to an `AiGenerationConfig` via `AiModelDefinitionSupport`.
5. The resulting `AiGenerationConfig` drives the AI generation engine to produce index fields.

#### Dependencies
- Standard Java collections (`ArrayList`, `HashMap`, `List`, `Map`, `Collections`).
- Lombok `@ToString` for readable `toString` implementations.
- `org.jspecify.annotations.Nullable` to denote optional values.
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper` used by `AiModelDefinitionSupport`.
- Maven plugin configuration context (implicit).

#### Cross-cutting
- Defensive copying of collections in setters; unmodifiable views in getters to preserve immutability of exposed data.
- Null‑aware logic: file‑extension lists may be `null` to indicate “no restriction”.
- No validation of numeric ranges; defaults mirror `AiGenerationConfig` constants.
- Thread‑unsafe state in configuration classes; `AiModelDefinitionSupport` is immutable after construction.
- Reuse of the same `@ToString` annotation across all beans for consistent logging.
