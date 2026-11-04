### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:07Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates and validates file‑selection conditions against file metadata, determining matches, required line counts, and validating configuration.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext`.
- Validates condition trees for correct structure and bounds.
- Reports whether line counting is needed for a condition.

#### Type
- `final class AiConditionEvaluator`  
  - extends `Object`  
  - fields: `private static final String ERROR_ONE_BRANCH`  
  - constructor: `public AiConditionEvaluator()` (no-op)

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file facts.  
- `validate(AiCondition)` – condition tree.  
- `usesLines(AiCondition)` – condition tree.  
- `branchCount(AiCondition)` – condition node.  
- `matchesExtension(List<String>, String)` – extensions list, file name.  
- `globMatches(String, String)` – glob pattern, relative path.  
- `parseEpochMilli(String)` – ISO‑8601 instant string.

#### Output
- `matches` returns `boolean` indicating file match.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `usesLines` returns `boolean` if any `lines` condition exists.  
- `branchCount` returns `int` count of non‑null branches/leaves.  
- `matchesExtension` returns `boolean`.  
- `globMatches` returns `boolean`.  
- `parseEpochMilli` returns `long` epoch milliseconds.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, then leaf checks (`extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).  
- `validate`: ensures exactly one branch/leaf per node; validates groups, extensions, size/lines bounds, date parsing, non‑blank glob.  
- `usesLines`: checks for `lines` leaf or recurses into `and`, `or`, `not`.  
- `anyUsesLines`: helper iterating children list.  
- `validateGroup`: ensures group has at least one child; recursively validates each child.  
- `branchCount`: counts non‑null branch/leaf fields.  
- `matchesExtension`: iterates extensions, checks `fileName.endsWith`.  
- `globMatches`: uses `AiSourceExcludeFilter` to test exclusion.  
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds, throws `IllegalArgumentException` on failure.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – check file matches condition.  
- `validate(AiCondition) -> void` – validate condition tree.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`  
- `java.util.Collections`, `java.util.List`  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException` if a node has no branch/leaf.  
- `IllegalArgumentException` for invalid configuration: missing branch, empty extensions, unbounded size/lines, invalid dates, blank glob.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe by design.
