### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:10:04Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates AI file conditions against file context to determine matches, validate structure, and detect line count needs

#### Purpose
- Determines if a file matches a given AI condition tree based on metadata and content constraints  
- Validates condition trees for structural correctness and misconfigurations  
- Detects whether a condition requires line count reading to avoid unnecessary file I/O  

#### Type
final class, no inheritance, no generics, no annotations beyond @Nullable and standard Java types

#### Input
- AiCondition (tree of conditions)  
- AiFileContext (file metadata: size, line count, modification time, path, name)  
- String (ISO-8601 timestamps for modifiedAfter/modifiedBefore)  
- List<String> (file extensions or path glob patterns)  

#### Output
- boolean (true if file matches condition, false otherwise)  
- boolean (true if any condition requires line count, false otherwise)  
- throws IllegalArgumentException on invalid configuration  

#### Core logic
- Recursively traverses condition tree using AND/OR/NOT combinators  
- Validates each node for exactly one active branch (extension, size, lines, etc.)  
- Checks date strings against file modification times using ISO-8601 parsing  
- Matches file extensions via suffix check  
- Matches path patterns using glob filtering via exclude filter  
- Reports line count usage to optimize file reading  

#### Public API
- matches(AiCondition, AiFileContext) → boolean (checks if file satisfies condition)  
- validate(AiCondition) → void (throws error on misconfiguration)  
- usesLines(AiCondition) → boolean (detects if any condition needs line count)  

#### Dependencies
AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition, Instant, DateTimeParseException, Collections, AiSourceExcludeFilter  

#### Exceptions / Errors
- IllegalArgumentException on invalid condition structure (missing branches, empty extensions, unbounded ranges, blank glob)  
- IllegalArgumentException on invalid ISO-8601 date parsing  
- No null pointer exceptions (all null checks performed)  

#### Concurrency
- Immutable state, thread-safe by design due to final class and no shared mutable state  
- No synchronization or concurrent access handling needed
