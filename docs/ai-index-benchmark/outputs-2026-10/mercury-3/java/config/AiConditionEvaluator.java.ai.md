### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:10Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition file-matching trees against file facts so the source indexer only reads files that satisfy include/exclude rules.

#### Purpose
- Evaluates condition trees (and/or/not + leaf predicates) against an AiFileContext to decide whether a source file should be included or excluded.
- Validates that each condition node sets exactly one branch/leaf and that the configuration is well-formed.
- Tells the indexer whether any node needs a line count, so file contents are read only when necessary.

#### Type
- Final class `AiConditionEvaluator`
- Public no-arg constructor

#### Input
- `matches(AiCondition condition, AiFileContext context)` — condition tree and file facts (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath)
- `validate(AiCondition condition)` — condition tree to validate
- `usesLines(AiCondition condition)` — condition tree to inspect
- `AiCondition` getters: getAnd, getOr, getNot, getExtensions, getSize, getLines, getModifiedAfter, getModifiedBefore, getPathGlob
- `AiConditionGroup` getter: getConditions
- `AiRangeCondition` method: contains, hasBound
- `AiFileContext` methods: fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath
- `AiSourceExcludeFilter` constructed with a singleton list containing a glob pattern

#### Output
- `matches` returns boolean true if the file satisfies the condition tree.
- `validate` returns void; throws IllegalArgumentException on any misconfiguration.
- `usesLines` returns boolean true if any node is a lines condition.
- `parseEpochMilli` converts an ISO-8601 instant string to epoch milliseconds.
- Side effect: `AiSourceExcludeFilter.isExcluded` computes a glob match.

#### Core logic
- `matches` recursively walks the tree: and requires all children match; or requires any child match; not negates the child; leaves are checked by type (extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `validate` first checks branchCount(condition) == 1, then dispatches on the set branch: and/or groups must have at least one child; extensions must be non-empty; size and lines need a bound; dates must parse; glob must not be blank. Recursion validates children.
- `usesLines` returns true when any node sets lines; it short-circuits on the first found lines condition and recurses through groups and not nodes.
- `branchCount` counts how many of the nine branch/leaf fields are non-null.
- `matchesExtension` accepts a file if any extension string is non-null and the file name ends with it.
- `globMatches` delegates to `AiSourceExcludeFilter.isExcluded`.
- `parseEpochMilli` parses with `Instant.parse`; on DateTimeParseException it wraps in IllegalArgumentException.

#### Public API
- `AiConditionEvaluator() -> void` — constructs evaluator
- `matches(AiCondition, AiFileContext) -> boolean` — tests file against condition tree
- `validate(AiCondition) -> void` — fails fast on misconfigured node
- `usesLines(AiCondition) -> boolean` — reports if any node needs line count

#### Dependencies
- java.time.Instant
- java.time.format.DateTimeParseException
- java.util.Collections
- java.util.List
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter
- org.jspecify.annotations.Nullable
- AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition

#### Exceptions / Errors
- IllegalArgumentException thrown when branchCount != 1, group has no children, extensions empty, size/lines unbounded, glob blank, or ISO-8601 date unparseable.
- IllegalStateException thrown in `matches` when condition node sets no branch/leaf.
- DateTimeParseException caught inside `parseEpochMilli` and rethrown as IllegalArgumentException.

#### Concurrency
- Stateless immutable evaluator; no mutable fields; safe for concurrent use.
- Local variables only; no shared state.
