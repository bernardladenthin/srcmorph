### java/config
- H: 1.0
- C: B56E33BA
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:53:39Z
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
> Configures and evaluates AI-driven source file inclusion rules, extracts deterministic whole-file facts, and defines build behavior for oversized source files.

#### Purpose
- Provides configuration types and evaluation logic for AI-based source filtering, exact fact counting, and oversize-file routing in a source transformation build.

#### Responsibilities
- Boolean condition tree evaluation and validation against file facts (AiConditionEvaluator).
- Grouping child conditions for Maven `<and>` / `<or>` combinators (AiConditionGroup).
- Exact whole-file regex match counting and deterministic facts block generation (AiFactExtractor).
- Case-insensitive parsing of oversize response strategies with `FAIL` as default (AiOversizeStrategy).
- Declares AiGenerationConfig and SrcMorphConfiguration with no summary available.

#### Key units
- AiConditionEvaluator: stateless final class that matches, validates, and inspects AiCondition trees against AiFileContext.
- AiConditionGroup: nullable container for child AiCondition lists bound to Maven `<and>` / `<or>` elements.
- AiFactExtractor: pure utility producing `label: count` facts blocks from AiFactCounter regex matches.
- AiOversizeStrategy: enum with FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC and case-insensitive fromConfig parsing.
- AiGenerationConfig: present in package but no summary provided.
- SrcMorphConfiguration: present in package but no summary provided.

#### Data flow
- AiCondition trees are validated by AiConditionEvaluator.validate, then matched against AiFileContext facts via AiConditionEvaluator.matches to decide file inclusion or exclusion.
- AiConditionGroup supplies child condition lists to AND/OR nodes during condition tree traversal.
- AiFactExtractor.validate checks AiFactCounter regex configuration, then factsBlock emits exact counts for labels over full source text.
- AiOversizeStrategy.fromConfig converts a config token into the build's oversize response, defaulting to FAIL.

#### Dependencies
- Internal: net.ladenthin.srcmorph.config.AiCondition, AiConditionGroup, AiRangeCondition, AiFileContext, AiFactCounter; net.ladenthin.srcmorph.support.AiSourceExcludeFilter.
- External: java.time.Instant, java.time.format.DateTimeParseException, java.util.regex.Pattern/Matcher/PatternSyntaxException, lombok.ToString, org.jspecify.annotations.Nullable.

#### Cross-cutting
- Nullable fields and parameters use org.jspecify.annotations.Nullable.
- Validation methods throw IllegalArgumentException on misconfiguration; AiConditionEvaluator.matches throws IllegalStateException for malformed branch nodes.
- Defensive copying in AiConditionGroup.setConditions; AiConditionEvaluator and AiFactExtractor are stateless and thread-safe.
- AiOversizeStrategy enum constants and final configValue are immutable and thread-safe.
