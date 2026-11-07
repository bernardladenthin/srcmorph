### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:51:31Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates and validates file‑selection conditions for the source‑morph indexer.

#### Purpose
- Traverses `AiCondition` trees to determine file matches.
- Validates condition syntax and bounds.
- Detects if line counting is required.

#### Type
- `final class AiConditionEvaluator` (class)  
  - Extends `Object`.  
  - No annotations.

#### Input
- `matches(AiCondition, AiFileContext)` – condition tree, file facts.  
- `validate(AiCondition)` – condition tree.  
- `usesLines(AiCondition)` – condition tree.  
- Dependencies: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `Instant`, `DateTimeParseException`, `Collections`, `List`.

#### Output
- `matches` → `boolean` (file matches).  
- `validate` → void (throws `IllegalArgumentException` on error).  
- `usesLines` → `boolean` (needs line count).  
- `parseEpochMilli` → `long` (epoch ms).  
- `matchesExtension` → `boolean`.  
- `globMatches` → `boolean`.

#### Core logic
- `matches`:  
  - If `and` group → all children must match.  
  - If `or` group → any child must match.  
  - If `not` group → negated child match.  
  - `extensions` → file name ends with any listed extension.  
  - `size` → `AiRangeCondition.contains(sizeBytes)`.  
  - `lines` → `AiRangeCondition.contains(lineCount)`.  
  - `modifiedAfter` → file lastModified > parsed epoch.  
  - `modifiedBefore` → file lastModified < parsed epoch.  
  - `pathGlob` → `AiSourceExcludeFilter.isExcluded(relativePath)` (true if excluded).  
  - Else throw `IllegalStateException(ERROR_ONE_BRANCH + condition)`.

- `validate`:  
  - Ensure exactly one branch/leaf (`branchCount == 1`).  
  - For groups: `validateGroup`.  
  - For `extensions`: non‑empty list.  
  - For `size`/`lines`: at least one bound.  
  - For `modifiedAfter`/`modifiedBefore`: parse ISO‑8601.  
  - For `pathGlob`: non‑blank.

- `usesLines`: recursively checks for `lines` in node or any child group.

- `validateGroup`: ensures group has ≥1 child, validates each child.

- `branchCount`: counts non‑null branch/leaf fields.

- `matchesExtension`: iterates extensions, checks `fileName.endsWith(extension)`.

- `globMatches`: creates `AiSourceExcludeFilter` with single glob, calls `isExcluded`.

- `parseEpochMilli`: `Instant.parse(isoInstant).toEpochMilli()`, wraps `DateTimeParseException` in `IllegalArgumentException`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – checks file against condition.  
- `validate(AiCondition) -> void` – validates condition tree.  
- `usesLines(AiCondition) -> boolean` – detects line‑count requirement.

#### Dependencies
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`  
- `java.time.Instant`  
- `java.time.format.DateTimeParseException`  
- `java.util.Collections`  
- `java.util.List`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalStateException` if condition node has no branch/leaf.  
- `IllegalArgumentException` for invalid configuration (missing bounds, empty lists, blank glob, bad ISO instant).  
- `DateTimeParseException` wrapped in `IllegalArgumentException` by `parseEpochMilli`.

#### Concurrency
- Stateless; no shared mutable state. Thread‑safe by design.
