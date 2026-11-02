### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:01Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Evaluates and validates file‑selection conditions for the source‑morph indexer.

#### Purpose
- Determines if a file satisfies a complex `AiCondition` tree.
- Validates condition trees for correctness.
- Detects whether line counting is required.

#### Type
`final class AiConditionEvaluator` – public, no inheritance, no generics.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).
- `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath).

#### Output
- `boolean matches(AiCondition, AiFileContext)` – true if file matches.
- `void validate(AiCondition)` – throws `IllegalArgumentException` on misconfiguration.
- `boolean usesLines(AiCondition)` – true if any `lines` node present.

#### Core logic
- `matches`: recursively evaluates logical groups; leaf checks use helper methods (`matchesExtension`, `globMatches`, `parseEpochMilli`).
- `validate`: ensures exactly one branch/leaf per node; checks non‑empty lists, bounds, parseable dates, non‑blank glob.
- `usesLines`: depth‑first search for `lines` nodes.
- `validateGroup`: ensures group has children and validates each child.
- `branchCount`: counts non‑null branch/leaf fields.
- `matchesExtension`: file name ends with any listed extension.
- `globMatches`: uses `AiSourceExcludeFilter` to test path glob.
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds, throws `IllegalArgumentException` on failure.

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
  - Multiple or zero branches/leafs.
  - Empty `<extensions>`.
  - Unbounded `<size>` or `<lines>`.
  - Unparseable `<modifiedAfter>`/`<modifiedBefore>`.
  - Blank `<pathGlob>`.

#### Concurrency
- Stateless; all methods are thread‑safe. No shared mutable state.
