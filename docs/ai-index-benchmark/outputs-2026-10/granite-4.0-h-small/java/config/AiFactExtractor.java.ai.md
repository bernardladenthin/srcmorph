### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:52:32Z
- G: 
- A: 0.0.0
- X: file
---
> Computes deterministic, language-agnostic "facts" from regex counters for AI summarization.

#### Purpose
- Generates a structured facts block for AI-assisted code summarization.
- Counts non-overlapping regex matches across the entire source file.
- Provides exact, whole-file metrics (e.g., rows, tables, functions) to complement AI estimates.

#### Type
- Class: `AiFactExtractor`
- Final, utility class (no instantiation)
- Static methods only

#### Input
- `counters`: `List<AiFactCounter>` - configured fact counters (may be null or empty)
- `source`: `String` - full source text to search against

#### Output
- `String` - the facts block prefixed with `FACTS_HEADER` and suffixed with `FACTS_SUFFIX`, or empty string if no counters

#### Core logic
1. Validate non-null, non-empty `counters` and each counter's `label` and `pattern`.
2. For each valid counter:
   - Compile its regex pattern.
   - Count non-overlapping matches in `source`.
   - Append `label: count` to `StringBuilder`.
3. Concatenate all `label: count` entries with `ENTRY_SEPARATOR`.
4. Prepend `FACTS_HEADER` and append `FACTS_SUFFIX`.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String`: Builds the facts block.
- `validate(List<AiFactCounter>) -> void`: Validates counter configuration.
- `countMatches(String, String) -> int`: Counts regex matches in source.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if any counter has a null `label` or `pattern`, or if a pattern is invalid.

#### Concurrency
- Purely functional, no shared state, thread-safe.
