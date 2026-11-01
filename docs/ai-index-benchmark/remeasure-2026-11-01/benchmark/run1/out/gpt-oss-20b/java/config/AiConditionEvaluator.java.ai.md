### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:24:25Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI‑condition trees against file metadata, determining matches, validating structure, and detecting line‑count usage.

#### Purpose
- Evaluate `AiCondition` trees against `AiFileContext`.
- Validate condition tree structure and bounds.
- Detect if any node requires line count.

#### Type
`public final class AiConditionEvaluator` – class, final, no generics, no annotations.

#### Input
- `AiCondition` tree (root node).
- `AiFileContext` providing file facts (name, size, lines, last modified, relative path).

#### Output
- `boolean` from `matches` and `usesLines`.
- `void` from `validate` (throws on error).
- Internal state: none mutated.

#### Core logic
- `matches`: recursively walk tree; handle `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`; throw if node invalid.
- `validate`: ensure exactly one branch/leaf per node; validate groups, bounds, date parsing, non‑blank glob.
- `usesLines`: recursively check for any `lines` node.
- Helper methods: `anyUsesLines`, `validateGroup`, `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – check if file satisfies condition.
- `validate(AiCondition) -> void` – ensure condition tree is well‑formed.
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException` if a node has no branch/leaf.
- `IllegalArgumentException` for invalid configuration: missing bounds, empty extensions, unparseable dates, blank glob, empty group.

#### Concurrency
- Stateless; thread‑safe. No shared mutable state.
