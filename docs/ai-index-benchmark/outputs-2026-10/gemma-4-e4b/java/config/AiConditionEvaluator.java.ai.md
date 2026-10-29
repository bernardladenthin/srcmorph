### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:55:12Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates whether a file context satisfies a complex, structured condition tree composed of logical operators and file properties.

#### Purpose
* Implements logic to determine if an AiFileContext matches an AiCondition tree.
* Provides validation mechanisms to ensure AiCondition trees are correctly configured.

#### Type
final class AiConditionEvaluator

#### Input
* Constructor: No parameters.
* matches(AiCondition condition, AiFileContext context): Takes the condition tree and file context.
* validate(AiCondition condition): Takes the condition tree for validation.
* usesLines(AiCondition condition): Takes the condition tree to check for line usage.

#### Output
* matches(AiCondition condition, AiFileContext context): boolean indicating match status.
* validate(AiCondition condition): Throws IllegalArgumentException on misconfiguration.
* usesLines(AiCondition condition): boolean indicating if line counting is required.

#### Core logic
* matches(): Recursively traverses AiCondition tree:
    * AND: Returns true only if all child conditions match.
    * OR: Returns true if any child condition matches.
    * NOT: Returns true if the child condition does not match.
    * Leaf nodes: Checks specific properties (extensions, size, lines, modified dates, glob) against AiFileContext.
    * Throws IllegalStateException if no branch/leaf is set.
* validate(): Checks that exactly one branch/leaf is set in the root AiCondition.
    * Recursively validates AND/OR groups, ensuring they have children.
    * Validates leaf nodes: checks for non-empty extensions, bounded size/lines, non-blank glob, and parsable dates.
* usesLines(): Recursively checks if any node in the AiCondition tree has a lines condition.

#### Public API
matches(AiCondition condition, AiFileContext context) -> boolean: Determines if context satisfies condition.
validate(AiCondition condition) -> void: Fails fast on misconfigured condition node.
usesLines(AiCondition condition) -> boolean: Reports if any node requires line count.

#### Dependencies
java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, net.ladenthin.srcmorph.support.AiSourceExcludeFilter, org.jspecify.annotations.Nullable, AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition.

#### Exceptions / Errors
* Throws IllegalStateException in matches() if condition lacks a single branch/leaf.
* Throws IllegalArgumentException in validate() for:
    * Incorrect branch count (not exactly one).
    * Empty AND/OR groups.
    * Unbounded size/lines conditions.
    * Unparseable dates (via parseEpochMilli).
    * Blank pathGlob conditions.

#### Concurrency
final class structure suggests immutability and thread-safety for evaluation logic, though no explicit synchronization mechanisms are present.
