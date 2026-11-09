### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:00:52Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates and validates file‑selection conditions against file metadata, determining matches and required line counting.

#### Purpose
- Evaluate `AiCondition` trees against `AiFileContext`.
- Validate condition trees for correct structure and bounds.
- Detect if any condition requires line counting.

#### Type
- `public final class AiConditionEvaluator`  
  - extends `Object`  
  - no generics  
  - annotations: none  
  - fields: `private static final String ERROR_ONE_BRANCH`  
  - constructor: `public AiConditionEvaluator()` (no-op)

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
- `validateGroup` throws `IllegalArgumentException` on empty group.  
- `branchCount` returns `int`.  
- `matchesExtension` returns `boolean`.  
- `globMatches` returns `boolean`.  
- `parseEpochMilli` returns `long` epoch milliseconds.

#### Core logic
- `matches`:  
  - If `and` group present, recursively `matches` all children; return `true` if all true.  
  - If `or` group present, recursively `matches` any child; return `true` if any true.  
  - If `not` present, return negated `matches` of child.  
  - If `extensions` present, call `matchesExtension`.  
  - If `size` present, call `size.contains(context.sizeBytes())`.  
  - If `lines` present, call `lines.contains(context.lineCount())`.  
  - If `modifiedAfter` present, compare `context.lastModifiedEpochMilli()` > parsed epoch.  
  - If `modifiedBefore` present, compare `<` parsed epoch.  
  - If `pathGlob` present, call `globMatches`.  
  - Else throw `IllegalStateException(ERROR_ONE_BRANCH + condition)`.

- `validate`:  
  - Ensure `branchCount(condition) == 1`; else throw `IllegalArgumentException`.  
  - For each possible branch:  
    - `<and>`/`<or>`: call `validateGroup`.  
    - `<not>`: recursively `validate`.  
    - `<extensions>`: non‑empty list.  
    - `<size>`/`<lines>`: must have bound (`hasBound()`).  
    - `<modifiedAfter>`/`<modifiedBefore>`: parse epoch.  
    - `<pathGlob>`: non‑blank after trim.

- `usesLines`:  
  - If `lines` present, return `true`.  
  - Else recurse into `and`, `or`, `not` groups via `anyUsesLines`.

- `anyUsesLines`: iterate children; return `true` if any child `usesLines`.

- `validateGroup`: ensure at least one child; recursively `validate` each child.

- `branchCount`: count non‑null branch/leaf fields (`and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).

- `matchesExtension`: iterate extensions; return `true` if any non‑null extension matches file name suffix.

- `globMatches`: instantiate `AiSourceExcludeFilter` with single glob; call `isExcluded(relativePath)`.

- `parseEpochMilli`: `Instant.parse(isoInstant).toEpochMilli()`; on `DateTimeParseException` throw `IllegalArgumentException` with message.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – check file matches condition.  
- `validate(AiCondition) -> void` – validate condition tree.  
- `usesLines(AiCondition) -> boolean` – detect line‑count requirement.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalStateException` in `matches` when node has no branch.  
- `IllegalArgumentException` in `validate`, `validateGroup`, `branchCount` checks, and `parseEpochMilli` on invalid ISO‑8601.

#### Concurrency
- No explicit synchronization or threading; all methods are stateless and thread‑safe.
