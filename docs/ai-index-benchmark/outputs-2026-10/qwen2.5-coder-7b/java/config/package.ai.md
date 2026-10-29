### java/config
- H: 1.0
- C: 7F3BDD4D
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:18:10Z
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
> Manages AI-related configurations, including condition evaluation, fact extraction, and generation settings.

#### Purpose
- Manages AI-related configurations.
- Handles condition evaluation and validation.
- Extracts facts from source code.
- Configures AI generation parameters.

#### Responsibilities
- Condition evaluation and validation.
- Fact extraction from source code.
- Configuration management for AI generation.

#### Key units
- `AiConditionEvaluator` - Evaluates and validates `AiCondition` trees.
- `AiFactExtractor` - Computes a deterministic "facts" block from `AiFactCounter`s.
- `AiGenerationConfig` - Manages configuration parameters for AI generation.
- `AiOversizeStrategy` - Configures handling strategies for oversized source files.
- `SrcMorphConfiguration` - Holds configuration parameters for `srcmorph` runs.

#### Data flow
- Input: `AiCondition` trees, `AiFileContext`, `AiFactCounter`s, and configuration settings.
- Output: `boolean` for condition matches, `String` for facts block, and configuration parameters.

#### Dependencies
- `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `File`, `List`, `String`, `int`, `float`, `long`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- `@Nullable` annotations for handling potential null values.
- Defensive copying of input collections to prevent null pointer exceptions.
- No explicit concurrency handling.
