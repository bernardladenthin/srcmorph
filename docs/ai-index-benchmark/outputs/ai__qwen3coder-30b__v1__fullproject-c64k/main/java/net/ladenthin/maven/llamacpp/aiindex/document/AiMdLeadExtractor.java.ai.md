### AiMdLeadExtractor.java
- H: 1.0
- C: 650CFCE3
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:08:36Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Extracts the first meaningful line from .ai.md document bodies, stripping blockquote markers to produce navigable package summaries.

#### Purpose
- Parses markdown document bodies to isolate descriptive leads.
- Supports deterministic, marker-aware lead extraction for index generation.

#### Type
class public final
implements no interfaces
generics no type bounds
annotations @ToString

#### Input
- Constructor: no parameters
- Method extractLead: String body (required, not null)
- Field compatibilityHelper: Java8CompatibilityHelper instance

#### Output
- Method extractLead returns String (trimmed lead line or empty string)
- Side effect: none
- State mutation: none

#### Core logic
- Splits input body into lines using newline delimiter
- Skips blank lines until first non-blank line is found
- Trims first non-blank line
- Checks if trimmed line starts with blockquote marker (>)
- Strips marker and trims result if present; otherwise returns trimmed line
- Returns empty string if no non-blank line exists

#### Public API
extractLead(body) -> String extracts the lead line from a markdown body

#### Dependencies
- Java8CompatibilityHelper
- String (JDK)

#### Exceptions / Errors
- Throws NullPointerException if body is null
- No explicit error handling for invalid input beyond null check

#### Concurrency
- No concurrency concerns; stateless and immutable operation
- Thread-safe due to lack of shared mutable state
