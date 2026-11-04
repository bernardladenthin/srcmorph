### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:14:36Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Generates a deterministic “facts” block summarizing exact regex match counts for a source file.

#### Purpose
- Compute exact, whole‑file counts for AI‑generated summaries.  
- Prepend counts to AI body on oversize path.

#### Type
`final class AiFactExtractor` – utility class, no instances.

#### Input
- `List<AiFactCounter> counters` (nullable).  
- `String source` – full source text.

#### Output
- `String` facts block ending with a blank line, or empty string if no counters.

#### Core logic
- Skip null counters or those with null label/pattern.  
- For each valid counter, append `label: count` to a `StringBuilder`.  
- Separate entries with `; `, prefix with `**Facts (exact, whole file):** `, suffix with two newlines.  
- Count matches via `Pattern.compile(pattern).matcher(source).find()` loop.  
- `validate` ensures each counter has non‑null label/pattern and a compilable regex, throwing `IllegalArgumentException` otherwise.

#### Public API
- `factsBlock(List<AiFactCounter>, String) -> String` – build facts block.  
- `validate(List<AiFactCounter>) -> void` – validate counters.

#### Dependencies
- `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` on missing label/pattern or invalid regex.  
- `PatternSyntaxException` wrapped in `IllegalArgumentException`.

#### Concurrency
- Stateless, thread‑safe; all methods are static and use local variables.
