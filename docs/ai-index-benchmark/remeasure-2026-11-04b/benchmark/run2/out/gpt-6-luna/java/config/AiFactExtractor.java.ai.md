### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:56:19Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Produces exact whole-source counts for configured regex-based facts and formats them for inclusion with AI-generated summaries.

#### Purpose
- `AiFactExtractor` builds a facts block from `AiFactCounter` labels and regex match counts.
- Validates fact-counter labels and regex patterns before use.

#### Type
- `public final class AiFactExtractor` — class; no supertypes declared.
- `@Nullable` annotates nullable counter-list parameters.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)` — nullable counter list and full source text.
- `validate(List<AiFactCounter> counters)` — nullable counter list.
- Reads each counter’s `getLabel()` and `getPattern()`.

#### Output
- `factsBlock` returns `""` for null, empty, or entirely skipped counters; otherwise returns the formatted facts block ending in a blank line.
- `validate` returns no value; throws `IllegalArgumentException` for missing labels or patterns and invalid regexes.
- No I/O or mutable instance state.

#### Core logic
- `factsBlock` returns `""` when `counters == null || counters.isEmpty()`.
- Iterates counters, skipping null counters and counters with null `getLabel()` or `getPattern()`.
- Appends each `label: count`, separated by `ENTRY_SEPARATOR`; counts use `countMatches(pattern, source)`.
- Returns `""` if no entries were appended; otherwise prefixes `FACTS_HEADER` and appends `FACTS_SUFFIX`.
- `validate` returns immediately for null counters; skips null entries.
- Throws `IllegalArgumentException` if `getLabel()` or `getPattern()` is null.
- Compiles each pattern with `Pattern.compile`; converts `PatternSyntaxException` to `IllegalArgumentException`, preserving the cause.
- `countMatches` compiles the pattern, repeatedly calls `Matcher.find()`, and counts non-overlapping matches, including zero-width matches.

#### Public API
- `FACTS_HEADER: String` — facts block header.
- `LABEL_COUNT_SEPARATOR: String` — separates label and count.
- `ENTRY_SEPARATOR: String` — separates entries.
- `FACTS_SUFFIX: String` — separates facts from following body.
- `factsBlock(List<AiFactCounter>, String) -> String` — builds exact facts block.
- `validate(List<AiFactCounter>) -> void` — validates configured counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for null label, null pattern, or invalid regex; invalid-regex errors wrap `PatternSyntaxException`.
- `factsBlock` skips null counters and counters with null labels or patterns.
- `countMatches` does not catch regex compilation errors.

#### Concurrency
- Pure utility logic with no mutable instance state or I/O; thread safety depends on independent local regex matchers.
