### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:01:56Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI filter conditions against file metadata, determining matches and required line counts.

#### Purpose
- Validates `AiCondition` trees for correctness.  
- Evaluates whether a file satisfies a condition tree.  
- Detects if line counting is needed.

#### Type
`public final class AiConditionEvaluator` – class, final.

#### Input
- `AiCondition` trees (branches, leaves).  
- `AiFileContext` (file name, size, line count, last modified, relative path).  
- Condition strings (ISO‑8601 dates, glob patterns).  
- List of extensions.

#### Output
- `boolean` from `matches`, `usesLines`.  
- Exceptions (`IllegalArgumentException`, `IllegalStateException`).  
- No state mutation.

#### Core logic
- `matches`: recursive traversal of `and`, `or`, `not` groups; leaf checks for extensions, size, lines, dates, glob.  
- `validate`: ensures exactly one branch/leaf per node; checks bounds on ranges, non‑empty lists, valid dates, non‑blank glob.  
- `usesLines`: recursively searches for any `lines` leaf to decide if file contents must be read.  
- Helper methods: `branchCount`, `validateGroup`, `matchesExtension`, `globMatches`, `parseEpochMilli`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – throw if misconfigured.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`.  
- `java.util.Collections`, `java.util.List`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` for invalid configuration or parsing errors.  
- `IllegalStateException` if a node has no branch/leaf.

#### Concurrency
- Stateless; thread‑safe.  
- No shared mutable state.
