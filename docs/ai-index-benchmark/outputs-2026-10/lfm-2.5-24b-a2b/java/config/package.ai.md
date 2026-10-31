### java/config
- H: 1.0
- C: A3C47B7D
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T12:32:52Z
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
> This package provides AI condition evaluation, fact extraction, generation configuration, oversize handling, and configuration management for source morphing operations.

#### Purpose
- Evaluates and validates AiCondition trees against an AiFileContext.
- Determines file match status and whether line counting is needed.
- Computes exact fact counts from source code for AI summary generation.
- Configures model path, sampling settings, and resource limits for a single AI generation step.
- Defines strategies for source files that exceed the context window of their routed model.
- Centralizes all configuration parameters for srcmorph runs.

#### Responsibilities
- Condition evaluation and validation: AiConditionEvaluator walks AiCondition trees, checks matches, validates, and reports line-count requirements.
- Condition grouping: AiConditionGroup holds child AiCondition instances and supports Maven plugin framework item binding.
- Fact extraction: AiFactExtractor generates a facts block of label: count entries from source code.
- Generation configuration: AiGenerationConfig holds mutable parameters for a single AI generation step.
- Oversize handling: AiOversizeStrategy selects abort, head sampling, chunked summarization, or deterministic emission strategies.
- Configuration management: SrcMorphConfiguration is a JavaBean centralizing all srcmorph parameters.

#### Key units
- AiConditionEvaluator: final class that evaluates and validates AiCondition trees against AiFileContext.
- AiConditionGroup: container for child AiCondition objects with defensive copying.
- AiFactExtractor: final class computing deterministic fact counts from source code.
- AiGenerationConfig: mutable configuration class for a single AI generation step.
- AiOversizeStrategy: enum defining strategies for oversized source files.
- SrcMorphConfiguration: JavaBean holding all configuration parameters for srcmorph runs.

#### Data flow
- Source code is analyzed by AiFactExtractor to produce a facts block of label: count entries.
- AiCondition trees are evaluated by AiConditionEvaluator using AiFileContext, yielding match status and line-count needs.
- AiConditionGroup stores child conditions for and/or combinators.
- AiGenerationConfig and SrcMorphConfiguration provide runtime parameters for AI generation steps.
- AiOversizeStrategy determines the action when a file exceeds the model context window.

#### Dependencies
- Internal: java.time.Instant, java.time.format.DateTimeParseException; java.util.Collections, List; net.ladenthin.srcmorph.support.AiSourceExcludeFilter; org.jspecific.annotations.Nullable; org.jspecify.annotations.Nullable; java.io.File; lombok.ToString; net.ladenthin.srcmorph.prompt.AiPromptDefinition; net.ladenthin.srcmorph.config.AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition, AiGenerationConfig.
- External modules: net.ladenthin.srcmorph.config (AiCondition, AiFileContext, AiConditionGroup, AiGenerationConfig), net.ladenthin.srcmorph.support (AiSourceExcludeFilter), org.jspecific (annotations), org.jspecify (annotations), lombok.

#### Cross-cutting
- Stateless final classes (AiConditionEvaluator, AiOversizeStrategy) are thread-safe by immutability.
- Mutable beans (AiGenerationConfig, SrcMorphConfiguration) are not thread-safe; state managed by Maven plugin framework.
- Defensive copying used in AiConditionGroup.setConditions and AiFactExtractor.
- Null handling: @Nullable annotations on parameters; null-safe defaults for config values.
- No checked exceptions in most classes; IllegalArgumentException for invalid configurations or malformed dates.
