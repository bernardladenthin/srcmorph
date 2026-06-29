### AiTimeSupport.java
- H: 1.0
- C: 27F1E50B
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:07:49Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Formats timestamps for AI index metadata files with second-level precision.

#### Purpose
- Provides timestamp formatting for AI metadata headers.
- Supports consistent datetime representation in .ai.md files.

#### Type
class AiTimeSupport final

#### Input
- Constructor: no parameters
- Method `formatInstant`: Instant instant

#### Output
- Return type: String
- Side effect: none
- State mutation: none

#### Core logic
- Truncates an Instant to second precision using ChronoUnit.SECONDS
- Formats the truncated Instant into an ISO-8601 string

#### Public API
- `formatInstant(final Instant instant) -> String` format timestamp

#### Dependencies
- java.time.Instant
- java.time.format.DateTimeFormatter
- java.time.temporal.ChronoUnit
- lombok.ToString

#### Exceptions / Errors
- None explicitly handled or thrown
- Null input may cause NullPointerException

#### Concurrency
- No concurrency concerns; stateless utility class
- Immutable formatter constant used safely across threads
