### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:00:35Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact, whole-file regex match counts for AI fact labels and prepends them as a deterministic facts block to an AI summary.

#### Purpose
- Provides deterministic, model-free counts of source constructs (rows, tables, boolean fields, functions) for AI summarization.
- Validates fact counter configuration and builds a labeled facts block without performing I/O.

#### Type
- `public final class AiFactExtractor`
- Utility class with a private no-op constructor; not instantiable.
- No superclass or interfaces.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`: accepts a nullable list of `AiFactCounter` objects and the full source text as a `String`.
- `validate(List<AiFactCounter> counters)`: accepts a nullable list of `AiFactCounter` objects for configuration validation.
- `countMatches(String pattern, String source)`: accepts a regex pattern string and source text string.
- Consumes `AiFactCounter.getLabel()` and `AiFactCounter.getPattern()` fields.

#### Output
- `factsBlock` returns a `String` facts block ending in `\n\n`, or `""` when no valid counters exist.
- `validate` returns `void` and throws on invalid configuration.
- `countMatches` returns an `int` count of non-overlapping regex matches.
- Mutates a local `StringBuilder` to assemble the facts block.

#### Core logic
- `factsBlock` returns `""` immediately if `counters` is null or empty.
- Iterates over each `AiFactCounter`, skipping null counters or those with null label/pattern.
- Appends `label: count` entries separated by `ENTRY_SEPARATOR` (`; `).
- Prepends `FACTS_HEADER` and appends `FACTS_SUFFIX` to the assembled entries.
- `validate` iterates counters, throwing `IllegalArgumentException` for null label, null pattern, or non-compilable regex.
- `countMatches` compiles the pattern, creates a `Matcher` over the source, and increments a counter for each `matcher.find()` iteration.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` builds the exact facts block for AI input.
- `validate(List<AiFactCounter>) -> void` fails fast on misconfigured fact counters.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` when a counter has a null label, null pattern, or an invalid regex (wrapping `PatternSyntaxException`).
- `factsBlock` and `countMatches` do not throw checked exceptions; null inputs are handled defensively.

#### Concurrency
- Stateless utility class with only static methods and immutable public constants.
- Thread-safe for concurrent use; no shared mutable state.
