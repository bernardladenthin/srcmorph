### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:39:50Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Evaluates and validates file‑selection conditions against file metadata, determining matches, required line counts, and validating configuration.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext`.
- Validates condition trees for correct structure and bounds.
- Reports whether line counting is needed.

#### Type
- `final class AiConditionEvaluator`  
  - extends `Object`  
  - no interfaces  
  - field: `private static final String ERROR_ONE_BRANCH`  
  - constructor: `public AiConditionEvaluator()` (no-op)

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file facts.  
- `validate(AiCondition)` – condition tree.  
- `usesLines(AiCondition)` – condition tree.  
- `anyUsesLines(List<AiCondition>)` – optional list of child conditions.  
- `validateGroup(AiConditionGroup, String)` – group and label.  
- `branchCount(AiCondition)` – condition node.  
- `matchesExtension(List<String>, String)` – extensions list, file name.  
- `globMatches(String, String)` – glob pattern, relative path.  
- `parseEpochMilli(String)` – ISO‑8601 instant string.

#### Output
- `matches` returns `boolean` indicating file match.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `usesLines` returns `boolean` if any `lines` condition exists.  
- `anyUsesLines` returns `boolean`.  
- `validateGroup` throws `IllegalArgumentException` on empty group.  
- `branchCount` returns `int` count of non‑null branches/leaves.  
- `matchesExtension` returns `boolean`.  
- `globMatches` returns `boolean`.  
- `parseEpochMilli` returns `long` epoch milliseconds.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, and leaf conditions (`extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).  
- `validate`: ensures exactly one branch/leaf per node, checks non‑empty lists, bounds on ranges, parses dates, trims glob.  
- `usesLines`: traverses tree to detect any `lines` condition.  
- `anyUsesLines`: helper for list traversal.  
- `validateGroup`: ensures group has at least one child and validates each child.  
- `branchCount`: counts non‑null branch/leaf fields.  
- `matchesExtension`: checks file name ends with any non‑null extension.  
- `globMatches`: uses `AiSourceExcludeFilter` to test path exclusion.  
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds, throws on failure.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition tree.  
- `validate(AiCondition) -> void` – validate configuration.  
- `usesLines(AiCondition) -> boolean` – check for line count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`  
- `java.util.Collections`, `java.util.List`  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException` in `matches` if node has no branch/leaf.  
- `IllegalArgumentException` in `validate`, `validateGroup`, `branchCount`, `parseEpochMilli` for misconfigurations or invalid dates.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe by design.
