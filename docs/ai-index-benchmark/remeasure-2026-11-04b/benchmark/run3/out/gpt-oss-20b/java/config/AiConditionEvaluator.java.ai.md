### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:04Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates and validates file‑selection conditions against file metadata.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext`.
- Validates condition trees for correct structure and values.
- Detects if a condition tree requires line counting.

#### Type
- `final class AiConditionEvaluator`  
  - extends `Object`  
  - no generics  
  - annotations: none  
  - imports: `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations.Nullable`

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file context.  
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
- `usesLines` returns `boolean`.  
- `anyUsesLines` returns `boolean`.  
- `validateGroup` throws `IllegalArgumentException` if group invalid.  
- `branchCount` returns `int`.  
- `matchesExtension` returns `boolean`.  
- `globMatches` returns `boolean`.  
- `parseEpochMilli` returns `long` epoch milliseconds.

#### Core logic
- `matches`: recursively evaluates `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`; uses helper methods for each leaf.  
- `validate`: ensures exactly one branch/leaf per node; validates each specific leaf type (non‑empty extensions, bounded size/lines, parseable dates, non‑blank glob); delegates group validation to `validateGroup`.  
- `usesLines`: checks for `lines` leaf; recurses into `and`, `or`, `not` groups.  
- `anyUsesLines`: iterates child list, returns true if any child uses lines.  
- `validateGroup`: ensures group has at least one child; validates each child recursively.  
- `branchCount`: counts non‑null branch/leaf fields in a node.  
- `matchesExtension`: true if file name ends with any non‑null extension.  
- `globMatches`: uses `AiSourceExcludeFilter` to test exclusion.  
- `parseEpochMilli`: parses ISO‑8601 instant to epoch milliseconds; throws `IllegalArgumentException` on parse error.

#### Public API
- `AiConditionEvaluator()` -> constructor, no parameters.  
- `matches(AiCondition, AiFileContext) -> boolean` – evaluate condition.  
- `validate(AiCondition) -> void` – validate tree.  
- `usesLines(AiCondition) -> boolean` – check line usage.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations.Nullable`, `net.ladenthin.srcmorph.config.AiCondition`, `net.ladenthin.srcmorph.config.AiConditionGroup`, `net.ladenthin.srcmorph.config.AiRangeCondition`, `net.ladenthin.srcmorph.config.AiFileContext`.

#### Exceptions / Errors
- `IllegalStateException` in `matches` when node has no branch/leaf.  
- `IllegalArgumentException` in `validate`, `validateGroup`, `branchCount` (via error messages).  
- `IllegalArgumentException` in `parseEpochMilli` on invalid ISO‑8601 instant.

#### Concurrency
- No explicit synchronization or concurrency constructs; all methods are stateless and thread‑safe.
