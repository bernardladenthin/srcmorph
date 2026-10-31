### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:09:34Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Computes exact, whole-file counts of structured elements (e.g., rows, tables, functions) from regex patterns and embeds them as a deterministic "Facts" header for AI summaries.

#### Purpose
- Generates a concise facts block with label:count entries for exact structural counts.
- Validates configuration to reject null labels/patterns and malformed regexes.
- Counts non-overlapping regex matches across the entire source text.

#### Type
- Class `AiFactExtractor`, `final`, not instantiable (private constructor).
- Uses `java.util.regex` types (`Pattern`, `Matcher`).
- Imports `java.util.List`, `java.util.regex` utilities, `org.jspecify.annotations.Nullable`.

#### Input
- `counters`: `List<AiFactCounter>` (may be null or empty).
- `source`: `String` full source text.
- `pattern`: `String` regex pattern (from `AiFactCounter`).
- In `validate`: `counters` list (may be null).

#### Output
- `factsBlock(...)`: `String` formatted as `**Facts (exact, whole file): label1: count1; label2: count2; …\n\n` or `""` when no valid counters.
- `validate(...)`: void; throws `IllegalArgumentException` on misconfiguration.
- `countMatches(...)`: `int` count of non-overlapping matches.
- Internal `StringBuilder` used to build the header string.

#### Core logic
- `factsBlock`:
  - Returns `""` if `counters` is null or empty.
  - Iterates counters, skips null counters, null label, or null pattern.
  - Appends each `label: count` entry separated by `; ` using `ENTRY_SEPARATOR`.
  - Prefixes with `FACTS_HEADER` and suffixes with `FACTS_SUFFIX` (`\n\n`).
  - Returns empty string if no valid entries after filtering.
- `validate`:
  - Returns early if `counters` is null.
  - For each counter, skips null counter.
  - Checks `label` and `pattern` are non-null; throws `IllegalArgumentException` if null.
  - Attempts `Pattern.compile(pattern)`; catches `PatternSyntaxException` and rethrows `IllegalArgumentException` with label context.
- `countMatches`:
  - Compiles `pattern` with `Pattern.compile`.
  - Uses `Matcher` to find all non-overlapping matches via `matcher.find()` loop.
  - Counts each match occurrence; returns total count.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String`: builds facts header string; returns empty string for no valid counters.
- `validate(List<AiFactCounter> counters) -> void`: validates counters; throws `IllegalArgumentException` for null label/pattern or invalid regex.
- `countMatches(String pattern, String source) -> int`: counts non-overlapping regex matches in source.

> Note: `countMatches` is `private static`; only `factsBlock` and `validate` are public static.

#### Dependencies
- `java.util.List`
- `java.util.regex.Pattern`
- `java.util.regex.Matcher`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `AiFactCounter` (assumed defined elsewhere; referenced via getter methods)

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` when:
  - `counter.getLabel()` is null.
  - `counter.getPattern()` is null.
  - `Pattern.compile` fails (`PatternSyntaxException` caught and rethrown with message including label).
- `PatternSyntaxException` caught internally in `validate`; not exposed.
- `counters` may be null; handled safely in both methods.
- `source` may be empty or null? (Not validated; `Pattern.compile` and `matcher.find` handle empty source safely).

#### Concurrency
- No shared state; all methods are stateless and pure.
- Thread-safe by default; no mutable instance fields.

#### Lead summary (already provided in blockquote)
> Computes exact, whole-file counts of structured elements (e.g., rows, tables, functions) from regex patterns and embeds them as a deterministic "Facts" header for AI summaries.
