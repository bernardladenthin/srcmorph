### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:14Z
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
- Constants: `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX` (public static final String).  
- Private constructor.

#### Input
- `factsBlock(List<AiFactCounter> counters, String source)`  
  - `counters`: nullable list of `AiFactCounter`.  
  - `source`: full source text.  
- `validate(List<AiFactCounter> counters)`  
  - `counters`: nullable list.  
- `countMatches(String pattern, String source)`  
  - `pattern`: regex string.  
  - `source`: text to search.

#### Output
- `factsBlock` returns a formatted facts string or empty string.  
- `validate` throws `IllegalArgumentException` on invalid counters.  
- `countMatches` returns integer match count.

#### Core logic
- `factsBlock`  
  - If counters null/empty → return "".  
  - For each counter: skip if null, label or pattern null.  
  - Append `ENTRY_SEPARATOR` if sb not empty.  
  - Append `label + LABEL_COUNT_SEPARATOR + countMatches(pattern, source)`.  
  - After loop, if sb empty → return "".  
  - Return `FACTS_HEADER + sb + FACTS_SUFFIX`.  
- `validate`  
  - If counters null → return.  
  - For each counter: skip if null.  
  - Throw `IllegalArgumentException` if label null.  
  - Throw if pattern null.  
  - Try `Pattern.compile(pattern)`; on `PatternSyntaxException` wrap and rethrow.  
- `countMatches`  
  - Compile pattern, create matcher on source.  
  - Iterate `matcher.find()` counting matches.  
  - Return count.

#### Public API
- `factsBlock(List<AiFactCounter> counters, String source) -> String` – build facts block.  
- `validate(List<AiFactCounter> counters) -> void` – validate counters.

#### Dependencies
- `java.util.List`  
- `java.util.regex.Matcher`  
- `java.util.regex.Pattern`  
- `java.util.regex.PatternSyntaxException`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiFactCounter`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for missing label/pattern or invalid regex.  
- `PatternSyntaxException` caught and wrapped in `IllegalArgumentException`.

#### Concurrency
- All methods are static and stateless; thread‑safe.
