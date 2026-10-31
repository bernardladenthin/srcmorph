### java/config
- H: 1.0
- C: 2F6CC29D
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:36:30Z
- G: 
- A: 0.0.0
- X: package
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [SrcMorphConfiguration.java](SrcMorphConfiguration.java.ai.md)
---
> Indexes source files and generates AI-assisted metadata for each file within a project.

#### Purpose
- Indexes source files and generates AI-assisted metadata for each file within a project.
- Bridges Maven configuration, JSON/YAML config, and plain Java code binding surfaces.

#### Responsibilities
- Evaluates and validates AI condition trees against file contexts to determine matches.
- Computes deterministic whole-file regex counts of structural elements for AI fact extraction.
- Carries all parameters for a single AI generation step between Maven configuration and AI provider implementations.
- Selects how oversized source files are handled during AI routing rules.
- Holds every parameter a srcmorph run needs as a root mutable JavaBean.

#### Key units
- `AiConditionEvaluator`: validates and matches AI condition trees against file contexts.
- `AiConditionGroup`: container for child AI conditions used as and/or combinators.
- `AiFactExtractor`: computes deterministic whole-file regex counts for AI fact extraction.
- `AiGenerationConfig`: mutable configuration object for AI generation steps.
- `AiOversizeStrategy`: enum selecting oversized source file handling behavior.
- `SrcMorphConfiguration`: root mutable JavaBean for srcmorph run parameters.

#### Data flow
- `SrcMorphConfiguration` supplies AI definitions, prompt definitions, and generation provider settings.
- `AiConditionEvaluator` uses `AiCondition` trees and `AiFileContext` to determine file matches.
- `AiFactExtractor` uses `AiFactCounter` definitions and source text to compute structural counts.
- `AiGenerationConfig` carries model path, sampling parameters, and retry policy for AI generation steps.

#### Dependencies
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `java.time.Instant`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Cross-cutting
- Shared `@Nullable` and `@ToString` annotations across configuration classes.
- `IllegalArgumentException` used for validation failures in evaluators and extractors.
- Mutable JavaBeans used for configuration objects, not thread-safe by design.
- No concurrency or threading notes in any file.
