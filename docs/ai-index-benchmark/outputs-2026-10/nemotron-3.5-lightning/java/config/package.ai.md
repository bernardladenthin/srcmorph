### java/config
- H: 1.0
- C: D38DF5EA
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:10:28Z
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
> This package enables AI-aware source file selection, generation parameter tuning, and oversized-file strategy management for build-time code transformation.

#### Purpose
- The package provides AI-aware source file configuration, condition evaluation, and generation parameter management for build-time code transformation.

#### Responsibilities
- AI condition trees are walked and validated against file context to determine file inclusion matches
- Logical combinators (<and>/<or>) group child AiCondition instances for configuration-level criteria
- Exact regex-based fact blocks are generated from pattern counters over source text for AI documentation
- AI generation parameters are configured and mutated as mutable JavaBeans for provider integration
- Oversized source files are assigned handling strategies (fail, sample, mapReduce, deterministic) for build-time processing

#### Key units
- AiConditionEvaluator: final class that walks AiCondition trees and validates file inclusion matches against AiFileContext
- AiConditionGroup: class that holds child AiCondition instances as logical <and>/<or> combinators
- AiFactExtractor: final utility class that generates regex-based fact blocks from pattern counters over source text
- AiGenerationConfig: mutable JavaBeans configuration object with 91 public members for AI generation steps
- AiOversizeStrategy: enum defining FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC strategies for files exceeding model context windows

#### Data flow
- AiCondition trees and AiFileContext enter AiConditionEvaluator.matches/validate; output boolean match or void IllegalArgumentException
- Source text and AiFactCounter lists enter AiFactExtractor.factsBlock; output String facts block or empty string
- Config parameters flow into AiGenerationConfig setters; output instance with populated fields for AI provider consumption
- Config strings enter AiOversizeStrategy.fromConfig; output strategy instance, DEFAULT for null/blank, or IllegalArgumentException for unrecognized values
- AiConditionGroup conditions feed into AiConditionEvaluator for tree walking and match evaluation

#### Dependencies
- Internal: AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition, AiFactCounter, AiCalibration
- External java: java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, java.util.ArrayList, java.util.regex.Pattern, java.util.regex.Matcher, java.util.regex.PatternSyntaxException
- Annotations: org.jspecify.annotations.Nullable, lombok.ToString
- Support: net.ladenthin.srcmorph.support.AiSourceExcludeFilter, net.ladenthin.srcmorph.config.AiCalibration, net.ladenthin.srcmorph.config

#### Cross-cutting
- @Nullable annotation governs null-handling across AiFactExtractor, AiGenerationConfig, AiOversizeStrategy
- IllegalArgumentException thrown by validators for misconfiguration (AiConditionEvaluator, AiFactExtractor, AiOversizeStrategy)
- Defensive copying in AiConditionGroup.setConditions to prevent external mutation of conditions list
- Thread-safety: AiConditionEvaluator and AiFactExtractor are stateless and thread-safe; AiGenerationConfig is fully mutable, not safe for concurrent access; AiConditionGroup has mutable state not thread-safe by default; AiOversizeStrategy is immutable enum, safe for concurrent access
- All validators default null/blank inputs to safe states (DEFAULT for AiOversizeStrategy, empty string for factsBlock)
