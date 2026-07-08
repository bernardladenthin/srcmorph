### AiFactExtractor.java
- H: 1.0
- C: C5F253BD
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:50:43Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 7; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 1; field declarations (w/ modifier): 4

> Generates a deterministic “facts” block with exact counts of regex matches for configured fact counters, to prepend to AI summaries.

#### Purpose
- Compute exact counts of regex matches in source code.
- Produce a formatted facts header for AI output.

#### Type
Class, final, public, no inheritance.

#### Input
- `List<AiFactCounter> counters` (nullable, may be empty).
- `String source` (full source text).
- `AiFactCounter` fields: `label`, `pattern`.

#### Output
- `String` facts block (header + `label: count` entries + blank line) or empty string.
- Side effect: none (pure calculation).

#### Core logic
- If counters null/empty → return empty.
- Iterate counters:
  - Skip null counter or missing label/pattern.
  - Append `label: count` to `StringBuilder`, separating entries with `ENTRY_SEPARATOR`.
  - `countMatches(pattern, source)` counts non‑overlapping regex matches.
- If no valid entries → return empty.
- Prepend `FACTS_HEADER` and append `FACTS_SUFFIX`.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` – build facts block.
- `validate(List<AiFactCounter> counters) -> void` – ensure counters have labels, patterns, and compile.

#### Dependencies
- `java.util.List`
- `java.util.regex.Matcher`
- `java.util.regex.Pattern`
- `java.util.regex.PatternSyntaxException`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` if counter missing label/pattern or regex fails to compile.
- `PatternSyntaxException` caught and rethrown as `IllegalArgumentException`.

#### Concurrency
- Thread‑safe: all methods are static and stateless; no shared mutable state.
