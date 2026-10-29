### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:08:26Z
- G: 
- A: 0.0.0
- X: file
---
> Generates exact, whole-file counts of regex-patterned elements from a list of AI fact counters for use in deterministic code analysis.

#### Purpose
- Produces deterministic counts of structured elements in source code.
- Validates and formats AI fact counters for use in code analysis workflows.

#### Type
Final class extending no types; implements no interfaces. Uses generics for `List<AiFactCounter>` and `@Nullable` annotations.

#### Input
- `counters`: List of `AiFactCounter` objects, may be null or empty.
- `source`: Full source text as a `String` for regex matching.
- `pattern`: Regular expression string to compile and match.
- `counter`: Individual `AiFactCounter` instance with label and pattern fields.

#### Output
- `factsBlock`: Formatted string with counts prefixed by `FACTS_HEADER` and suffixed by `FACTS_SUFFIX`.
- `validate`: Throws `IllegalArgumentException` for invalid counter configurations.
- `countMatches`: Returns integer count of non-overlapping regex matches.

#### Core logic
- Validates input `counters` list for nulls, missing labels, or invalid patterns.
- Builds a facts string by iterating over valid counters and counting matches.
- Skips invalid or null counters during facts block construction.
- Uses `Pattern.compile` and `Matcher.find` to count non-overlapping matches.

#### Public API
- `factsBlock(counters, source) -> String`: Constructs a facts block from counters and source.
- `validate(counters) -> void`: Validates counter configurations.
- `countMatches(pattern, source) -> int`: Counts regex matches in source.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter`

#### Exceptions / Errors
- Throws `IllegalArgumentException` when a counter lacks a label or pattern, or when a pattern is invalid.
- Handles `PatternSyntaxException` during regex compilation.

#### Concurrency
- No concurrency concerns; all methods are static and stateless.
