### AiSourceExcludeFilter.java
- H: 1.0
- C: C06E0C2D
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:06:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Filters source file paths using glob patterns to exclude generated or trivial code from indexing.

#### Purpose
- Excludes specific source files from being indexed based on path matching.
- Supports glob-style pattern syntax for flexible exclusion rules.

#### Type
Class, final. Implements no interfaces. Uses @ToString annotation. Generics: List<Pattern>, Collection<String>.

#### Input
- Constructor accepts nullable Collection<String> of glob patterns.
- Method isExcluded takes String relativePath.
- Internal method globToRegex converts String glob to regex pattern.

#### Output
- Returns boolean from isExcluded indicating whether a path should be excluded.
- Compiled List<Pattern> stored in instance field.

#### Core logic
- Compiles glob patterns into regex patterns during initialization.
- Matches input paths against compiled patterns using anchored regex matching.
- Converts glob wildcards (*, **, ?) into equivalent regex expressions.
- Handles special cases like **/ for zero-directory matching.

#### Public API
- AiSourceExcludeFilter(Collection<String>) -> void Initializes filter with glob patterns.
- isExcluded(String) -> boolean Determines if a path should be excluded.

#### Dependencies
java.util.ArrayList, java.util.Collection, java.util.List, java.util.regex.Pattern, lombok.ToString, org.jspecify.annotations.Nullable

#### Exceptions / Errors
- Null or empty glob patterns are safely ignored during initialization.
- No explicit exception throwing beyond standard Java behavior.

#### Concurrency
- Immutable state after construction; no synchronization required.
