### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:30:43Z
- G: 
- A: 0.0.0
- X: file
---
> Produces exact whole-source regex counts for configured facts, formatted for inclusion alongside an AI-generated summary.

#### Purpose
- `AiFactExtractor` builds a deterministic facts block from configured `AiFactCounter` entries.
- Validates counter labels and regex patterns before use.

#### Type
- `AiFactExtractor` — `final` class; utility class with a private constructor.

#### Input
- `factsBlock(List<AiFactCounter>, String)` consumes nullable counters and the source text to search.
- `validate(List<AiFactCounter>)` consumes nullable counters.

#### Output
- `factsBlock` returns an empty string when no entries can be reported; otherwise returns formatted `label: count` entries followed by a blank line.
- Public string constants define the header and separators.

#### Core logic
- `factsBlock` skips null lists, empty lists, and counters with null entries, labels, or patterns.
- Compiles each pattern and counts non-overlapping matches across the full source using `Matcher.find()`.
- Formats entries as `label: count`, separated by `; `, and prefixes the facts header.
- `validate` skips null lists and null counter entries; rejects missing labels or patterns and invalid regex syntax.

#### Public API
- `FACTS_HEADER` — facts-line header constant.
- `LABEL_COUNT_SEPARATOR` — label/count separator constant.
- `ENTRY_SEPARATOR` — entry separator constant.
- `FACTS_SUFFIX` — trailing blank-line separator constant.
- `factsBlock(List<AiFactCounter>, String) -> String` — formats whole-source fact counts.
- `validate(List<AiFactCounter>) -> void` — validates counter configuration.

#### Dependencies
- `AiFactCounter`
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for missing labels, missing patterns, or invalid regex syntax; invalid-regex errors retain the `PatternSyntaxException` cause.
- `factsBlock` compiles patterns during counting; invalid patterns can throw `PatternSyntaxException`.
- A null `source` is not handled and can cause a `NullPointerException`.

#### Concurrency
- Stateless utility; no shared mutable state or synchronization.
