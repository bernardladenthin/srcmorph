### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:07:55Z
- G: 
- A: 0.0.0
- X: file
---
Here is the markdown summary of the Java source file AiFactExtractor.java:

```markdown
## Output

### Purpose
Calculates deterministic, language-agnostic "facts" blocks from a list of AiFactCounter objects for a given source text.

### Type
Class, final, no modifiers, no extends or implements.

### Input
- List<AiFactCounter> counters
- String source

### Output
- String factsBlock (ending in a blank line), or "" when there is nothing to report

### Core Logic
- Validate counters for non-null label and compilable pattern
- Build facts block string by iterating over counters
  - Append label and count of matches for each counter
  - Use countMatches method to count non-overlapping matches of pattern in source
- Return FACTS_HEADER + facts string + FACTS_SUFFIX

### Public API
- factsBlock(List<AiFactCounter>, String) -> String
- validate(List<AiFactCounter>) -> void
- countMatches(String, String) -> int

### Dependencies
- java.util.List
- java.util.regex.Pattern
- java.util.regex.PatternSyntaxException
- org.jspecify.annotations.Nullable

### Exceptions / Errors
- IllegalArgumentException for missing label/pattern or invalid regex

### Concurrency
Not applicable, no threading or synchronization involved.
```
