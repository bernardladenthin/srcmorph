### AiFactExtractor.java
- H: 1.0
- C: 149025B2
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:10:14Z
- G: 
- A: 0.0.0
- X: file
---
> Computes exact, whole-file regex match counts for AI-generated summaries to ensure deterministic and model-free fact reporting

#### Purpose
- Generates a deterministic facts block from regex-based counters  
- Ensures exact, non-estimated counts of code elements (e.g., functions, tables) across the full source  

#### Type
class, final, public static methods only; private helper methods  
implements none; extends none  
generics: none  
key annotations: @Nullable, org.jspecify.annotations  

#### Input
- List of {@link AiFactCounter} objects (null or empty allowed)  
- Full source text string to search across  
- Each counter provides: label (string) and regex pattern (string)  

#### Output
- A formatted string block starting with "**Facts (exact, whole file):**"  
- Contains entries like "label: count; label: count" separated by "; "  
- Ends with "\n\n" (blank line)  
- Returns empty string if no valid counters or no matches  

#### Core logic
- Skips counters with null label or pattern during processing  
- Validates all patterns at build time using regex compilation  
- Counts non-overlapping regex matches in full source using Matcher.find()  
- Builds facts string with consistent formatting and header/suffix  
- Returns empty string if no valid counters or no matches  

#### Public API
- factsBlock(List<AiFactCounter>, String) → String (builds facts block from counters and source)  
- validate(List<AiFactCounter>) → void (validates counter labels and patterns)  
- countMatches(String, String) → int (counts non-overlapping regex matches)  

#### Dependencies
AiFactCounter, java.util.List, java.util.regex.Pattern, java.util.regex.Matcher, java.lang.String, org.jspecify.annotations.Nullable  

#### Exceptions / Errors
- Throws IllegalArgumentException if:  
  - A counter lacks a label or pattern  
  - A regex pattern is invalid (syntax error)  
- No null pointer exceptions; null inputs are handled gracefully  

#### Concurrency
- Immutable state; no shared mutable state  
- Thread-safe by design due to pure, stateless operations  
- No synchronization needed; safe for concurrent use
