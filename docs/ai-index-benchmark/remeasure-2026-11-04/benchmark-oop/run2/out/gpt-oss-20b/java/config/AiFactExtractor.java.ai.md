### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:39:56Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Provides a facts block for AI summaries.
- Counts non‑overlapping regex matches across entire source.

#### Type
- `final class AiFactExtractor` (class, final)  
  - No supertypes.  
  - No annotations.

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
- `factsBlock` returns a string: facts header + `label: count` entries separated by `; `, ending with two newlines, or empty string if no counters or all skipped.  
- `validate` throws `IllegalArgumentException` on missing label/pattern or invalid regex.  
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`:  
  1. Return empty if counters null/empty.  
  2. Iterate counters; skip null or missing label/pattern.  
  3. Append `label: count` using `countMatches`.  
  4. Prepend `FACTS_HEADER`, append `FACTS_SUFFIX`.  
- `validate`:  
  1. Return if counters null.  
  2. For each counter: check non‑null label/pattern.  
  3. Compile pattern; on `PatternSyntaxException` wrap in `IllegalArgumentException`.  
- `countMatches`:  
  1. Compile pattern, create matcher on source.  
  2. Loop `matcher.find()` counting matches.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.  
- `validate(List<AiFactCounter>) -> void` – validate counters.  
- `FACTS_HEADER: String` – facts header.  
- `LABEL_COUNT_SEPARATOR: String` – label/count separator.  
- `ENTRY_SEPARATOR: String` – entry separator.  
- `FACTS_SUFFIX: String` – suffix after facts block.

#### Dependencies
- `java.util.List`  
- `java.util.regex.Matcher`  
- `java.util.regex.Pattern`  
- `java.util.regex.PatternSyntaxException`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for missing label/pattern or invalid regex.  
- `PatternSyntaxException` caught and wrapped.

#### Concurrency
- All methods are static and stateless; thread‑safe.
