### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:54:47Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates whether a file context satisfies a complex, structured set of inclusion/exclusion criteria defined by a condition tree.

#### Purpose
* Implements logic to determine if an AiFileContext matches an AiCondition.
* Provides validation methods to ensure AiCondition trees are correctly configured.

#### Type
final class AiConditionEvaluator

#### Input
* Constructor: No parameters.
* matches(AiCondition condition, AiFileContext context): Takes condition tree and file context.
* validate(AiCondition condition): Takes condition tree.
* usesLines(AiCondition condition): Takes condition tree.

#### Output
* matches(...): boolean (true if file matches condition).
* validate(...): void (throws IllegalArgumentException on failure).
* usesLines(...): boolean (true if condition requires line counting).

#### Core logic
* matches(): Recursively traverses AiCondition tree:
    * AND: Returns true only if all child conditions match.
    * OR: Returns true if any child condition matches.
    * NOT: Returns true if the child condition does not match.
    * Leaf nodes: Checks specific context properties (extensions, size, lines, modified dates, glob match).
    * Throws IllegalStateException if condition node lacks exactly one branch/leaf.
* validate(): Ensures AiCondition has exactly one set branch/leaf using branchCount.
    * Recursively validates AND/OR groups, ensuring they have children.
    * Validates leaf nodes for proper configuration (e.g., size/lines must have bounds, glob must not be blank).
* usesLines(): Recursively checks if any node in the tree is a lines condition.
* parseEpochMilli(String isoInstant): Parses ISO-8601 string to epoch milliseconds, throwing IllegalArgumentException on failure.

#### Public API
matches(AiCondition condition, AiFileContext context) -> boolean: Determines if context satisfies condition.
validate(AiCondition condition) -> void: Ensures condition tree is correctly configured.
usesLines(AiCondition condition) -> boolean: Checks if condition requires line content reading.

#### Dependencies
java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, net.ladenthin.srcmorph.support.AiSourceExcludeFilter, org.jspecify.annotations.Nullable, AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition.

#### Exceptions / Errors
* IllegalStateException: Thrown by matches() if condition node lacks exactly one branch/leaf.
* IllegalArgumentException: Thrown by validate() or parseEpochMilli() on misconfiguration or invalid date format.

#### Concurrency
final class structure suggests immutability of state, relying on input parameters for evaluation. No explicit synchronization mechanisms present.
