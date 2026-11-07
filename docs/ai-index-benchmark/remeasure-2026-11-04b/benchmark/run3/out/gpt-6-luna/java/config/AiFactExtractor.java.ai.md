### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:04:51Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Produces exact whole-source counts for configured regex-based code facts, formatted for inclusion alongside an AI-generated summary.

#### Purpose
- Builds a deterministic facts block from `AiFactCounter` labels and regex patterns.
- Validates configured counters and regexes before use.

#### Type
- `public final class AiFactExtractor` — utility class; no supertypes declared.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` — nullable counter list and full source text.
- `validate(List<AiFactCounter> counters)` — nullable counter list.

#### Output
- `factsBlock(...)` returns `""` when no counters are reportable; otherwise returns the facts header, `label: count` entries, and trailing blank line.
- `validate(...)` returns no value; rejects counters with missing labels or patterns, and invalid regexes.

#### Core logic
- `factsBlock` returns `""` when `counters` is `null` or empty.
- Iterates counters, skipping `null` counters and counters with `null` labels or patterns.
- Appends each label and `countMatches(counter.getPattern(), source)`, separating entries with `ENTRY_SEPARATOR`.
- Returns `""` if no entries were appended; otherwise prefixes `FACTS_HEADER` and appends `FACTS_SUFFIX`.
- `validate` returns for a `null` list; skips `null` counters, checks `getLabel()` and `getPattern()`, then compiles each pattern with `Pattern.compile`.
- Private `countMatches(pattern, source)` uses `Pattern.compile(pattern).matcher(source)` and counts each non-overlapping `matcher.find()` result, including zero-width matches.
- Formatting constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.

#### Public API
- `FACTS_HEADER: String` — exact-facts line prefix.
- `LABEL_COUNT_SEPARATOR: String` — separates label and count.
- `ENTRY_SEPARATOR: String` — separates entries.
- `FACTS_SUFFIX: String` — separates facts from following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats whole-source fact counts.
- `validate(List<AiFactCounter>) -> void` — validates counter labels and regexes.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` when a counter has a `null` label or pattern, or when its pattern is invalid; invalid-regex errors retain the `PatternSyntaxException` as cause.
- `factsBlock` skips counters with `null` labels or patterns.
- `countMatches` compiles patterns during counting; invalid patterns can throw `PatternSyntaxException`.

#### Concurrency
- Pure utility logic with no I/O or mutable shared state.
