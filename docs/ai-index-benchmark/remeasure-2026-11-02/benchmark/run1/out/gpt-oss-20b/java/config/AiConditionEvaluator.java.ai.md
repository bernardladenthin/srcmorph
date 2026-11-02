### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:30:31Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Evaluates and validates file‑selection conditions for the source‑morph indexer.

#### Purpose
- Checks if a file satisfies an `AiCondition` tree.
- Validates condition trees for correctness.
- Detects if line counting is required.

#### Type
`final class AiConditionEvaluator` – public, no inheritance, no generics.

#### Input
- `AiCondition` tree (root node).
- `AiFileContext` providing file facts (name, size, lines, last modified, relative path).

#### Output
- `boolean matches(...)` – true if file matches condition.
- `boolean usesLines(...)` – true if any `lines` condition exists.
- `void validate(...)` – throws `IllegalArgumentException` on misconfiguration.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, and leaf conditions (`extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`). Uses helper methods `matchesExtension`, `globMatches`, `parseEpochMilli`.
- `validate`: ensures each node has exactly one branch/leaf; validates groups (`and`/`or`) for non‑empty children; checks bounds on `size`/`lines`; parses dates; trims `pathGlob`.
- `usesLines`: recursively scans tree for any `lines` leaf; helper `anyUsesLines` iterates children.

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
- `IllegalStateException` if a node has no branch/leaf.
- `IllegalArgumentException` for:
  - Multiple or no branches/leafs.
  - Empty `<extensions>`.
  - Unbounded `<size>` or `<lines>`.
  - Invalid ISO‑8601 dates.
  - Blank `<pathGlob>`.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
