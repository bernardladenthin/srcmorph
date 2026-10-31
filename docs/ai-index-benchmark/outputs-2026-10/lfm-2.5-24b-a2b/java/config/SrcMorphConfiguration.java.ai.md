### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:43:00Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all configuration parameters for srcmorph runs, including AI generation settings, file filtering, and output paths.

#### Purpose
- Mutable JavaBean that centralizes every parameter a srcmorph execution needs, independent of how it is bound (Maven @Parameter, JSON/YAML config, or direct Java construction).

#### Type
- public class, no inheritance; annotated with @ToString (Lombok) and @SuppressWarnings.

#### Input
- No constructor arguments; defaults applied; state populated via setters from mojos, config files, or callers.

#### Output
- No return values; mutates internal fields; provides read/write access via getters and setters.

#### Core logic
- Configuration holder: stores defaults and user-supplied values; no computation or transformation beyond defaulting.

#### Public API
- 23 getter/setter pairs (getBaseDirectory..getProjectName) (23 total)

#### Dependencies
- java.io.File, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.prompt.AiPromptDefinition
- net.ladenthin.srcmorph.config.AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition, AiGenerationConfig

#### Exceptions / Errors
- No explicit exceptions thrown; nullable fields may be null; no checked error conditions.

#### Concurrency
- No synchronization; mutable bean not thread-safe; intended for single-threaded engine construction.
