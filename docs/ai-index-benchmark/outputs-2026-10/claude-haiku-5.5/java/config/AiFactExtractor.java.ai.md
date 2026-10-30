### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:11Z
- G: 
- A: 0.0.0
- X: file
---
> Builds a whole-file exact count summary ("facts" line) from regex-based counters for AI source summaries.

#### Purpose
- Produces deterministic, model-free `label: count` facts over full source text, prepended to AI summaries on oversize input.
- Validates `AiFactCounter` configuration (label, pattern, regex syntax) to fail fast.

#### Type
- `public final class AiFactExtractor`; utility class with private constructor, not instantiable.
- No extends or implements; no generics.
- Annotations: `@Nullable` (org.jspecify) on parameters.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`: counter list (nullable), full source text.
- `validate(List<AiFactCounter> counters)`: counter list (nullable).
- `countMatches(String pattern, String source)`: regex string, source text.
- Reads `AiFactCounter.getLabel()` and `AiFactCounter.getPattern()`.

#### Output
- `factsBlock` returns `FACTS_HEADER` + `label: count` entries joined by `; ` + `FACTS_SUFFIX`, or `""`.
- `validate` returns void; throws on invalid input.
- `countMatches` returns match count as `int`.
- No I/O, no mutated state.

#### Core logic
- `factsBlock`:
  - Returns `""` when counters are null or empty.
  - Skips counters with null counter, label, or pattern.
  - Appends `label` + `": "` + count for each counter, separated by `"; "`.
  - Returns `""` if nothing appended; otherwise prefixes header and appends trailing `"\n\n"`.
- `validate`:
  - Returns early on null list; skips null counters.
  - Rejects null label or null pattern with `IllegalArgumentException`.
  - Compiles each pattern; wraps `PatternSyntaxException` in `IllegalArgumentException`.
- `countMatches`:
  - Compiles pattern per call, loops `Matcher.find()`, counts matches.
  - Zero-width matches count once per position.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` builds whole-file facts block.
- `validate(List<AiFactCounter> counters) -> void` checks counter label and regex.
- Constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX` (4 public `static final String` fields).

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter` (same package; `getLabel()`, `getPattern()`)

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing label, missing pattern, or invalid regex (cause preserved for regex errors).
- `factsBlock` silently skips null counters, labels, or patterns.
- `factsBlock` with null or empty list returns `""`.
- `countMatches` may throw `PatternSyntaxException` (unchecked) if pattern is invalid and not validated first.

#### Concurrency
- Stateless static methods; no shared mutable state.
- Thread-safe, provided `AiFactCounter` instances are not mutated concurrently.
