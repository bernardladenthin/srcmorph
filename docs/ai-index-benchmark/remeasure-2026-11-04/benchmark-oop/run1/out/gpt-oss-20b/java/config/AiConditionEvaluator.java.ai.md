### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:21Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Evaluates and validates file‑selection conditions for source‑code indexing.

#### Purpose
- Checks if a file satisfies a tree of `AiCondition` nodes.
- Validates condition trees for correct structure and bounds.
- Determines if line counting is required.

#### Type
- `final class AiConditionEvaluator` – public, no generics, no annotations.

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file facts.
- `validate(AiCondition)` – condition tree.
- `usesLines(AiCondition)` – condition tree.
- Internal helpers: `branchCount(AiCondition)`, `matchesExtension(List<String>, String)`, `globMatches(String, String)`, `parseEpochMilli(String)`.

#### Output
- `matches` returns `boolean` indicating file match.
- `validate` throws `IllegalArgumentException` on misconfiguration.
- `usesLines` returns `boolean` indicating need for line count.
- Helper methods return booleans or parsed epoch milliseconds.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob; throws if node has no branch.
- `validate`: ensures exactly one branch per node, checks non‑empty extensions, bounded size/lines, parses dates, non‑blank glob.
- `usesLines`: traverses tree to detect any `lines` node.
- `validateGroup`: ensures group has at least one child and validates each child.
- `branchCount`: counts non‑null branch/leaf fields.
- `matchesExtension`: checks file name ends with any listed extension.
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

#### Exceptions / Errors
- `IllegalStateException` if a node has no branch.
- `IllegalArgumentException` for:
  - multiple or no branches (`ERROR_ONE_BRANCH`).
  - empty `<extensions>`.
  - unbounded `<size>` or `<lines>`.
  - invalid ISO‑8601 dates.
  - blank `<pathGlob>`.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe by design.
