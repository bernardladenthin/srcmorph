### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:21Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Computes deterministic, language‑agnostic fact counts from source text using regex counters.

#### Purpose
- Generates a facts block summarizing exact counts of language constructs.
- Validates fact counter configurations.

#### Type
- `final class AiFactExtractor` (class, final)  
  - No supertypes.  
  - Annotations: none.

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
- `factsBlock` returns a string: `FACTS_HEADER + entries + FACTS_SUFFIX` or empty string.  
- `validate` throws `IllegalArgumentException` on invalid counters.  
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`  
  - Skip null counters or those with null label/pattern.  
  - Append `ENTRY_SEPARATOR` between entries.  
  - For each counter: `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)`.  
  - Prepend `FACTS_HEADER`, append `FACTS_SUFFIX`.  
- `validate`  
  - For each counter: check non‑null label and pattern.  
  - Compile pattern with `Pattern.compile`; catch `PatternSyntaxException` and wrap in `IllegalArgumentException`.  
- `countMatches`  
  - Compile pattern, create matcher, iterate `matcher.find()` counting matches.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` – build facts block.  
- `validate(List<AiFactCounter> counters) -> void` – validate counters.  
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
- `validate` throws `IllegalArgumentException` for missing label/pattern or invalid regex.  
- `countMatches` may throw `PatternSyntaxException` if pattern compilation fails (handled in `validate`).

#### Concurrency
- All methods are static and stateless; thread‑safe.
