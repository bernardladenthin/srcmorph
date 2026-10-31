### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:35:19Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a deterministic “facts” block summarizing exact counts of regex‑matched elements in a source file.

#### Purpose
- Provides exact, whole‑file counts for AI‑generated summaries.  
- Supplies a header‑prefixed facts line for oversize AI bodies.

#### Type
`public final class AiFactExtractor` – final class, no inheritance, no interfaces.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`  
  - `counters`: list of counters (may be null/empty).  
  - `source`: full source text.  
- `validate(List<AiFactCounter> counters)`  
  - `counters`: list of counters (may be null/empty).  
- `countMatches(String pattern, String source)`  
  - `pattern`: regex string.  
  - `source`: text to search.

#### Output
- `factsBlock` returns a formatted facts string or empty string.  
- `validate` throws `IllegalArgumentException` on misconfiguration.  
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock` iterates counters, skips null or incomplete ones, appends `label: count` entries separated by `; `.  
- `validate` checks each counter for non‑null label/pattern and compiles the pattern, throwing on failure.  
- `countMatches` compiles pattern, uses `Matcher.find()` to count non‑overlapping matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.  
- `validate(List<AiFactCounter>) -> void` – validate counter list.

#### Dependencies
- `java.util.List`  
- `java.util.regex.Matcher`  
- `java.util.regex.Pattern`  
- `java.util.regex.PatternSyntaxException`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for missing label/pattern or invalid regex.  
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless utility class; all methods are thread‑safe.
