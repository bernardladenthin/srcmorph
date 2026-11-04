### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:32:39Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Evaluates and validates file‑selection conditions for the source‑morph indexer.

#### Purpose
- Checks if a file satisfies an `AiCondition` tree.
- Validates condition trees for correctness.
- Detects if line counting is required.

#### Type
`final class AiConditionEvaluator` – public, no inheritance, no interfaces.

#### Input
- `AiCondition` tree (root node).
- `AiFileContext` providing file facts (name, size, lines, timestamps, path).

#### Output
- `boolean` from `matches` and `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid trees or parsing errors.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, and leaf conditions (`extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`). Uses helper methods for extensions, glob matching, and epoch parsing.
- `validate`: ensures each node sets exactly one branch/leaf; checks non‑empty lists, bounds, and valid ISO‑8601 dates.
- `usesLines`: traverses tree to find any `lines` condition, enabling lazy line counting.
- `validateGroup`: ensures combinator groups have at least one child and validates each child.
- `branchCount`: counts non‑null branch/leaf fields to enforce single‑branch rule.
- `matchesExtension`: checks file name suffix against allowed extensions.
- `globMatches`: uses `AiSourceExcludeFilter` to test path glob.
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds, throws on failure.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.
- `validate(AiCondition) -> void` – throw if tree invalid.
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` for misconfigured nodes, empty lists, missing bounds, blank glob, or invalid ISO‑8601.
- `IllegalStateException` if a node sets more than one branch/leaf.

#### Concurrency
- Stateless; all methods are thread‑safe. No shared mutable state.
