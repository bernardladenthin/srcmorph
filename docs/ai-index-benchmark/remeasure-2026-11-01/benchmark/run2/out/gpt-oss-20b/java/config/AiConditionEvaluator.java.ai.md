### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:45:50Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata, determining matches and required line counts.

#### Purpose
- Validates `AiCondition` trees for correct structure and bounds.  
- Evaluates whether a file satisfies a condition tree.  
- Detects if any condition requires line counting.

#### Type
`public final class AiConditionEvaluator` – class, final, no generics, no annotations.

#### Input
- `AiCondition` trees (branches: and/or/not, leaves: extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).  
- `AiFileContext` providing file name, size, line count, last modified epoch, relative path.

#### Output
- Boolean match results.  
- Boolean indicating line‑count usage.  
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid configuration or parsing errors.

#### Core logic
- `matches`: recursively evaluates logical groups (`and`, `or`, `not`) and leaf conditions.  
- `validate`: ensures each node has exactly one branch/leaf, checks bounds on ranges, parses dates, non‑blank glob.  
- `usesLines`: traverses tree to find any `lines` leaf.  
- `validateGroup`: ensures group has at least one child and validates each child.  
- `branchCount`: counts non‑null branch/leaf fields.  
- `matchesExtension`: checks file name suffix against allowed extensions.  
- `globMatches`: uses `AiSourceExcludeFilter` to test path glob.  
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds, throws on failure.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – validate tree structure.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`  
- `java.util.Collections`, `java.util.List`  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`

#### Exceptions / Errors
- `IllegalArgumentException` for misconfigured nodes, empty bounds, invalid dates, blank glob.  
- `IllegalStateException` if a node sets no branch/leaf.  
- `DateTimeParseException` wrapped in `IllegalArgumentException` by `parseEpochMilli`.

#### Concurrency
- Stateless; all methods are thread‑safe. No shared mutable state.
