### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:01:56Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates whether a file context satisfies a complex, structured set of inclusion/exclusion criteria defined by a condition tree.

#### Purpose
* Implements logic to determine if an AiFileContext matches an AiCondition.
* Provides validation mechanisms for ensuring AiCondition trees are correctly configured.

#### Type
final class AiConditionEvaluator

#### Input
* Constructor: No parameters.
* Method `matches`: AiCondition condition, AiFileContext context.
* Method `validate`: AiCondition condition.
* Method `usesLines`: AiCondition condition.

#### Output
* Method `matches`: boolean.
* Method `validate`: Throws IllegalArgumentException on misconfiguration.
* Method `usesLines`: boolean.

#### Core logic
* `matches`: Recursively traverses the AiCondition tree (AND, OR, NOT) to check for matches against AiFileContext properties (extensions, size, lines, modification time, path glob).
* `validate`: Ensures the AiCondition node sets exactly one branch/leaf, and validates sub-groups/conditions recursively.
* `usesLines`: Recursively checks the AiCondition tree to determine if any node requires line counting.
* `parseEpochMilli`: Converts ISO-8601 instant string to epoch milliseconds, throwing IllegalArgumentException on parsing failure.

#### Public API
matches(AiCondition, AiFileContext) -> boolean: Determines if context satisfies condition.
validate(AiCondition) -> void: Fails fast on misconfigured condition node.
usesLines(AiCondition) -> boolean: Reports if lines condition is present.

#### Dependencies
java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, net.ladenthin.srcmorph.support.AiSourceExcludeFilter, org.jspecify.annotations.Nullable, AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition.

#### Exceptions / Errors
* Throws IllegalStateException in `matches` if no branch is set.
* Throws IllegalArgumentException in `validate` if branch count is not one or if specific conditions are misconfigured (e.g., empty glob, unparseable date).
* Throws IllegalArgumentException in `parseEpochMilli` for invalid ISO-8601 instants.

#### Concurrency
No explicit concurrency mechanisms present; class is final and methods appear to operate on immutable inputs or local state.
