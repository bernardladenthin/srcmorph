### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:38:08Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition trees against an AiFileContext to determine whether source files match configured include/exclude rules.

#### Purpose
- Evaluates boolean condition trees (and/or/not plus leaf predicates) against file facts.
- Validates condition configuration and reports whether line counting is required.

#### Type
- public final class AiConditionEvaluator; no supertype; no interfaces; stateless.

#### Input
- matches receives AiCondition condition tree and AiFileContext file facts (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).
- validate receives AiCondition condition tree.
- usesLines receives AiCondition condition tree.
- Constructor takes no parameters.

#### Output
- matches returns boolean true if the file facts satisfy the condition tree.
- validate returns void and mutates nothing; throws on invalid configuration.
- usesLines returns boolean true if any node is a lines condition.
- No mutable state or written resources.

#### Core logic
- matches recursively evaluates and groups: all children must match for AND, any child for OR, negated result for NOT.
- matches checks leaf predicates: extensions via filename endsWith, size and lines via AiRangeCondition.contains, modifiedAfter/modifiedBefore via epoch millisecond comparison, pathGlob via AiSourceExcludeFilter.
- matches throws IllegalStateException if a node sets zero or multiple branches (defensive, assumes prior validation).
- validate enforces exactly one branch per node via branchCount, non-empty AND/OR groups, non-empty extensions, size/lines bounds, parseable ISO-8601 instants, and non-blank pathGlob.
- usesLines traverses AND/OR/NOT children and returns true if any node has a non-null lines field.
- parseEpochMilli parses ISO-8601 instants and wraps DateTimeParseException as IllegalArgumentException.

#### Public API
- AiConditionEvaluator() -> AiConditionEvaluator: creates a stateless evaluator instance.
- matches(AiCondition, AiFileContext) -> boolean: returns whether the file facts satisfy the condition tree.
- validate(AiCondition) -> void: throws IllegalArgumentException on any misconfigured node.
- usesLines(AiCondition) -> boolean: returns true if any node requires line count.

#### Dependencies
- java.time.Instant, java.time.format.DateTimeParseException
- java.util.Collections, java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.config.AiCondition, AiConditionGroup, AiRangeCondition, AiFileContext

#### Exceptions / Errors
- validate throws IllegalArgumentException for zero/multiple branches, empty combinator groups, empty extensions, missing size/lines bounds, unparseable instants, blank pathGlob.
- matches throws IllegalStateException if a node has no recognized branch (should not occur after validate).
- parseEpochMilli catches DateTimeParseException and rethrows as IllegalArgumentException with context.
- Null child lists are treated as empty in matches and anyUsesLines.

#### Concurrency
- Stateless with no instance fields; instances are thread-safe and shareable.
