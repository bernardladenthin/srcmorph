### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:30Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Evaluates and validates file‑selection conditions against file metadata.

#### Purpose
- Provides matching logic for `AiCondition` trees.
- Validates condition trees for correctness.
- Determines if line counting is required.

#### Type
- `final class AiConditionEvaluator`  
  - extends `Object`  
  - no annotations

#### Input
- `AiCondition condition` (root of condition tree)  
- `AiFileContext context` (file facts)  
- `List<String> extensions` (file extensions)  
- `AiRangeCondition size/lines` (size or line bounds)  
- `String modifiedAfter/modifiedBefore` (ISO‑8601 timestamps)  
- `String pathGlob` (glob pattern)

#### Output
- `boolean` from `matches`, `usesLines`  
- `void` from `validate` (throws `IllegalArgumentException` on error)  
- `long` from `parseEpochMilli` (epoch milliseconds)

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, and leaf conditions; throws if node invalid.  
- `validate`: ensures exactly one branch/leaf per node; checks non‑empty lists, bounds, and parsable dates.  
- `usesLines`: traverses tree to detect any `lines` condition.  
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
- `IllegalStateException` in `matches` if node has no branch/leaf.  
- `IllegalArgumentException` in `validate` for misconfigured nodes, empty lists, missing bounds, or unparsable dates.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe.
