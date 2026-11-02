### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:03:47Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata, determining matches and required line counts.

#### Purpose
- Validate AI condition trees for correctness.
- Evaluate file context against conditions.
- Detect if line counting is needed.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).

#### Output
- Boolean results from `matches` and `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on validation errors.

#### Core logic
- `matches`: recursively evaluates logical groups and leaf conditions; uses helper methods for extensions, size, lines, dates, glob.
- `validate`: ensures each node has exactly one branch/leaf; checks bounds, non-empty lists, valid dates, non-blank glob.
- `usesLines`: traverses tree to detect any `lines` condition.
- `validateGroup`: ensures group has children and validates each child.
- `branchCount`: counts non‑null branch/leaf fields.
- `matchesExtension`: checks file name suffix against list.
- `globMatches`: uses `AiSourceExcludeFilter` to test path glob.
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – validate tree structure.
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for misconfigured nodes, invalid dates, empty lists, blank glob.
- `IllegalStateException` if a node sets no or multiple branches/leafs.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
