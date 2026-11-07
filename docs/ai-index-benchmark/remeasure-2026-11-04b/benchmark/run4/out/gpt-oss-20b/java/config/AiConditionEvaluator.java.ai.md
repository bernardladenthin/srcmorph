### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:14Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates and validates file‑matching conditions against file metadata, supporting logical combinators, size, line count, modification dates, extensions, and path glob patterns.

#### Purpose
- Implements condition tree evaluation for file indexing.
- Validates condition syntax and bounds.
- Detects if line counting is required.

#### Type
- `final class AiConditionEvaluator`  
  - extends `Object`  
  - annotations: none  
  - fields: `private static final String ERROR_ONE_BRANCH`  
  - methods: 10 (3 public, 7 private)

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file facts.  
- `validate(AiCondition)` – condition tree.  
- `usesLines(AiCondition)` – condition tree.  
- `AiCondition` fields accessed: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`.  
- `AiFileContext` methods: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.  
- `AiRangeCondition` methods: `contains(long)`, `hasBound()`.  
- `AiSourceExcludeFilter` constructor and `isExcluded(String)`.

#### Output
- `matches` returns `boolean` indicating file matches condition.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `usesLines` returns `boolean` indicating line count needed.  
- `parseEpochMilli` returns epoch milliseconds or throws `IllegalArgumentException`.  
- `globMatches` returns `boolean`.  
- `matchesExtension` returns `boolean`.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not` groups; otherwise checks leaf conditions: extensions, size, lines, modifiedAfter/Before, pathGlob.  
- `validate`: ensures exactly one branch/leaf per node; validates groups, extensions list non‑empty, size/lines bounds, ISO‑8601 dates, non‑blank glob.  
- `usesLines`: recursively checks for `lines` leaf; uses helper `anyUsesLines` on child lists.  
- `validateGroup`: ensures group has at least one child; recursively validates each child.  
- `branchCount`: counts non‑null branch/leaf fields in a node.  
- `matchesExtension`: iterates extensions, checks `fileName.endsWith`.  
- `globMatches`: creates `AiSourceExcludeFilter` with single glob, calls `isExcluded`.  
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds, throws `IllegalArgumentException` on parse error.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – validate condition syntax.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalStateException` in `matches` when node has no branch/leaf.  
- `IllegalArgumentException` in `validate` for: wrong branch count, empty extensions, missing size/lines bounds, invalid dates, blank glob.  
- `IllegalArgumentException` in `parseEpochMilli` for invalid ISO‑8601 instant.

#### Concurrency
- No explicit synchronization; all methods are stateless and thread‑safe.
