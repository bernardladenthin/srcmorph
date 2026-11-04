### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:46:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Produces exact, whole-source regex counts for configured code facts and formats them for inclusion with an AI summary.

#### Purpose
- Builds a deterministic facts block from configured `AiFactCounter` entries.
- Validates fact labels and regex patterns so invalid configuration fails fast.

#### Type
- `public final class AiFactExtractor` — class; no supertypes; utility class with a private constructor.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` — accepts nullable counters and the full source text.
- `validate(List<AiFactCounter> counters)` — accepts nullable counters; checks each non-null counter’s label and pattern.

#### Output
- `factsBlock(...)` returns `""` for null or empty counters, or when all entries are skipped; otherwise returns a header, `label: count` entries, and a trailing blank line.
- Counts non-overlapping regex matches across the full source; appends no persistent state and performs no I/O.

#### Core logic
- `factsBlock` returns immediately when `counters == null || counters.isEmpty()`.
- Iterates counters, skipping null counters and counters with null `getLabel()` or `getPattern()`.
- Appends each label, `LABEL_COUNT_SEPARATOR`, and `countMatches(counter.getPattern(), source)`; separates entries with `ENTRY_SEPARATOR`.
- Returns `""` if no entries were appended; otherwise prefixes `FACTS_HEADER` and appends `FACTS_SUFFIX`.
- `validate` returns for null counters and skips null entries; throws `IllegalArgumentException` for a null label or pattern.
- `validate` calls `Pattern.compile(counter.getPattern())`; wraps `PatternSyntaxException` in `IllegalArgumentException`, including the label and syntax error message.
- `countMatches` compiles the pattern, creates a matcher over `source`, and increments a count for each `matcher.find()` result, including zero-width matches.

#### Public API
- `FACTS_HEADER: String` — exact whole-file facts header.
- `LABEL_COUNT_SEPARATOR: String` — separates label and count.
- `ENTRY_SEPARATOR: String` — separates fact entries.
- `FACTS_SUFFIX: String` — separates facts line from following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats exact fact counts.
- `validate(List<AiFactCounter>) -> void` — validates configured fact counters.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` when a non-null counter lacks a label or pattern, or has an invalid regex.
- `factsBlock` skips null counters and counters with null labels or patterns.
- `countMatches` assumes `source` is non-null; no null handling is present.

#### Concurrency
- Pure computation with no I/O, shared mutable state, or synchronization.
