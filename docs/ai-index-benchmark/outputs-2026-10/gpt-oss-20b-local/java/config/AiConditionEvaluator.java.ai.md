### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:06:39Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata, determining matches and required line counts.

#### Purpose
- Validate AI condition trees for correctness.  
- Evaluate condition trees against file context.  
- Detect if a condition tree requires line counting.

#### Type
- `final class AiConditionEvaluator` (public).  
- No inheritance or interfaces.

#### Input
- `AiCondition` trees (branches: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob).  
- `AiFileContext` providing fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath.

#### Output
- `boolean` from `matches` and `usesLines`.  
- Exceptions (`IllegalArgumentException`, `IllegalStateException`) on validation or malformed nodes.

#### Core logic
- `matches`: recursively walk the tree; for each node type, apply the corresponding check (AND, OR, NOT, extension match, size/lines range, date comparison, glob exclusion).  
- `validate`: ensure exactly one branch/leaf per node; validate group children; check bounds for size/lines; parse dates; reject blank glob.  
- `usesLines`: recursively search for any `lines` node to decide if line counting is needed.  
- Helper methods: `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – throw if tree is malformed.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`.  
- `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` for invalid configuration (missing bounds, blank glob, bad date).  
- `IllegalStateException` if a node has no or multiple branches/leafs.

#### Concurrency
- Stateless; thread‑safe as no mutable shared state.
