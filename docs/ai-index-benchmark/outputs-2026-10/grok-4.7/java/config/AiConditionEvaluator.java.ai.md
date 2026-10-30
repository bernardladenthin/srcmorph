### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:37:54Z
- G: 
- A: 0.0.0
- X: file
---
Preparing a dense, structured markdown summary of the AiConditionEvaluator.java source file.

The class is a public final evaluator in net.ladenthin.srcmorph.config. It walks AiCondition trees against file facts. Validation fails fast on misconfiguration. A usesLines method reports whether any node needs line counts. The lead blockquote will capture the domain essence.

The class is a plain final class with no extends or implements clause. It uses the @Nullable annotation from org.jspecify.annotations on one parameter. The public API consists of three methods: matches, validate, and usesLines.

The evaluator consumes condition branches including and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, and pathGlob. It reads file facts from AiFileContext such as fileName, sizeBytes, lineCount, lastModifiedEpochMilli, and relativePath. It uses AiRangeCondition.contains and hasBound for range checks, AiSourceExcludeFilter for glob matching, Instant.parse for ISO-8601 dates, and Collections.singletonList. matches returns a boolean. validate throws IllegalArgumentException on misconfiguration. usesLines returns a boolean. matches throws IllegalStateException if it encounters an unvalidated node with no branch.

The class has one public constructor and three public methods, totaling four public members. Private helpers include anyUsesLines, validateGroup, branchCount, matchesExtension, globMatches, and parseEpochMilli.

No near-identical member families exist. The class imports java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, net.ladenthin.srcmorph.support.AiSourceExcludeFilter, and org.jspecify.annotations.Nullable. AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition, IllegalStateException, and IllegalArgumentException are referenced directly without import statements because they reside in the same package.

Validation throws IllegalArgumentException for conditions with no branch, empty extensions, unbounded size or lines, blank pathGlob, empty and/or groups, or invalid ISO-8601 dates. Null children are treated as empty lists, so an empty and group returns true, an empty or group returns false, and anyUsesLines returns false. Null extensions are skipped in matchesExtension. DateTimeParseException is caught and wrapped into IllegalArgumentException. The evaluator is stateless and immutable, requiring no synchronization.

The class declares one private static final constant named ERROR_ONE_BRANCH. It has no instance fields.

The summary will use a lead blockquote followed by sections with `####` headers. No code fences or `###` headings will appear. Each public API entry will follow the format `name(params) -> returnType`, keeping the explanatory clause under six words. The constructor will be listed as `AiConditionEvaluator() -> AiConditionEvaluator`.

The public API entries are now fully specified. matches takes AiCondition and AiFileContext and returns boolean. validate takes AiCondition and returns void. usesLines takes AiCondition and returns boolean. The constructor description is four words. The lead blockquote will capture the evaluator's role in deciding file matches and detecting line-count requirements.

The source is complete and needs no truncation. Every section applies.

Dependencies will be listed as searchable bullets. An exceptions section will follow.

The matches logic is now verified: and requires all children to match, or accepts any child match, not negates the recursive result, extensions check fileName suffixes, size and lines use contains against context values, and modifiedAfter compares lastModifiedEpochMilli against parsed ISO-8601 timestamps.

modifiedBefore compares lastModifiedEpochMilli against parsed ISO-8601 timestamps. pathGlob delegates to AiSourceExcludeFilter using a singleton glob list. validate throws IllegalArgumentException when branchCount is not exactly one, when and/or groups have null or empty children, when extensions are empty, when size or lines lack bounds, when pathGlob is non-null but blank after trimming, and when modifiedAfter or modifiedBefore values fail parsing.

usesLines returns true if the node has a lines bound, otherwise recurses through and/or children via anyUsesLines or checks a not child. branchCount counts nine fields: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, and pathGlob. parseEpochMilli wraps DateTimeParseException into IllegalArgumentException. globMatches creates a new AiSourceExcludeFilter on every call. matchesExtension uses plain endsWith without case folding and skips null extensions.> Evaluates AI source-filter condition trees against file facts and rejects misconfigured nodes before indexing.

#### Purpose
- Evaluates `AiCondition` trees against an `AiFileContext` for AI source selection.
- Validates configuration and reports whether a `lines` leaf requires reading file contents.

#### Type
- `final` class `AiConditionEvaluator`; no superclass, no interfaces, no class annotations.
- Package `net.ladenthin.srcmorph.config`. One private static final error string `ERROR_ONE_BRANCH`.

#### Input
- `AiCondition` tree: exactly one of `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`.
- `AiFileContext` facts: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.
- Nested `AiConditionGroup.getConditions()`, `AiRangeCondition.contains` / `hasBound`.
- ISO-8601 instants via `Instant.parse`; path globs via `AiSourceExcludeFilter`.

#### Output
- `matches` returns whether the file satisfies the tree.
- `validate` mutates nothing; throws on invalid nodes.
- `usesLines` returns whether any node is a `lines` condition.
- `matches` throws `IllegalStateException` if a node sets no branch.

#### Core logic
- `matches` short-circuits `and` (all children; null list matches) and `or` (any child; null list fails).
- `not` negates a recursive `matches` of the child.
- Extension leaf: `fileName.endsWith` any non-null extension string.
- `size` / `lines` leaves delegate to `AiRangeCondition.contains` on bytes or line count.
- `modifiedAfter` requires last-modified strictly after the parsed instant; `modifiedBefore` strictly before.
- `pathGlob` treats a match as `AiSourceExcludeFilter.isExcluded` on `relativePath` for that single glob.
- `validate` requires `branchCount` of 9 fields to equal 1, then checks the active branch only.
- `and`/`or` groups must have a non-null, non-empty child list; each child is validated recursively.
- `extensions` must be non-empty; `size` and `lines` must `hasBound()`; blank `pathGlob` is rejected; dates must parse.
- `usesLines` returns true on a `lines` leaf, else walks `and`/`or` children or the `not` child; other leaves are false.

#### Public API
- `AiConditionEvaluator() -> AiConditionEvaluator` — constructs a stateless evaluator
- `matches(AiCondition, AiFileContext) -> boolean` — tests file against tree
- `validate(AiCondition) -> void` — rejects invalid condition nodes
- `usesLines(AiCondition) -> boolean` — detects any lines leaf

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`, `Collections`, `List`, `Instant`, `DateTimeParseException`, `@Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: not exactly one branch, empty `and`/`or`, empty `extensions`, unbounded `size`/`lines`, blank `pathGlob`, unparseable ISO-8601 instant.
- `DateTimeParseException` is caught in `parseEpochMilli` and wrapped as `IllegalArgumentException` with the bad text.
- `IllegalStateException` from `matches` when no branch is set (`ERROR_ONE_BRANCH`).
- Null child lists are empty, not errors, in `matches` and `anyUsesLines`; null extension strings are skipped.

#### Concurrency
- No instance fields and no synchronization; the evaluator is stateless and safe to share if the tree and `AiFileContext` are not mutated during a call.
