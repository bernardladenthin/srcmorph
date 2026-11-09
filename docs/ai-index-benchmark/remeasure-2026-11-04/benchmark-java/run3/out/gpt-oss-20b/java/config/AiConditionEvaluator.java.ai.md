### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:32Z
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
`final class AiConditionEvaluator` – public class, no inheritance, no interfaces.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` providing file name, size, line count, last modified epoch, relative path.

#### Output
- `boolean` from `matches`, `usesLines`.
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on invalid trees or parsing errors.

#### Core logic
- `matches`: recursively evaluates and/or/not groups; delegates to leaf checks (`matchesExtension`, `size.contains`, `lines.contains`, timestamp comparisons, glob matching).
- `validate`: ensures exactly one branch/leaf per node; checks non‑empty extensions, bounded ranges, parsable dates, non‑blank glob; recursively validates groups.
- `usesLines`: traverses tree to find any `lines` condition; short‑circuits on first match.
- Helper methods: `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

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
- `IllegalArgumentException` for misconfigured nodes, empty extensions, unbounded ranges, invalid dates, blank glob.
- `IllegalStateException` if a node sets more than one branch/leaf.

#### Concurrency
- Stateless; all methods are thread‑safe. No shared mutable state.
