### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:31:59Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact fact counts from source code to prepend to AI summaries.

#### Purpose
- Provides deterministic fact extraction from source code for AI summary generation.
- Generates a facts block of label: count entries for whole-file exact counts.

#### Type
- final public class AiFactExtractor; no subclasses; no interfaces; generic List<AiFactCounter>; @Nullable annotations on parameters.

#### Input
- Private no-arg constructor.
- factsBlock(List<AiFactCounter> counters, String source): builds facts block.
- validate(List<AiFactCounter> counters): validates counters.
- countMatches(String pattern, String source): private, counts non-overlapping matches.

#### Output
- factsBlock returns String (facts block ending with blank line, or empty string).
- validate returns void.
- countMatches returns int (match count).

#### Core logic
- Validate counters for null label/pattern and compilable regex.
- Iterate counters, skip null or invalid, append label, separator, and match count.
- Compile pattern and count non-overlapping matches with Matcher.
- Return header + built block or empty string.

#### Public API
- factsBlock(counters, source) -> String: builds facts block for counters over source.
- validate(counters) -> void: validates counters for null label/pattern and valid regex.
- FACTS_HEADER..FACTS_SUFFIX (4 constants)

#### Dependencies
- java.util.List
- java.util.regex.Matcher
- java.util.regex.Pattern
- java.util.regex.PatternSyntaxException
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- IllegalArgumentException thrown by validate for missing label/pattern or invalid regex.
- Null counters are skipped, not thrown.

#### Concurrency
- No shared mutable state; no synchronization required; thread-safe by immutability.
