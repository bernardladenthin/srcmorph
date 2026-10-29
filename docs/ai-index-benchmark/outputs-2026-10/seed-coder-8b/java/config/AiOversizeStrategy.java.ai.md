### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:13:03Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling oversized source files in routing rules.

#### Purpose
- Enumerates strategies for oversized file processing
- Provides default behavior and configuration parsing

#### Type
- Enum
- Public
- Final

#### Input
- String config values for each strategy
- Nullable String for fromConfig method

#### Output
- String config tokens
- AiOversizeStrategy enum values
- Throws IllegalArgumentException for invalid inputs

#### Core logic
- Maps config strings to enum values
- Handles default case for null/empty inputs
- Validates input against known strategies

#### Public API
- `FAIL("fail") -> AiOversizeStrategy`
- `SAMPLE("sample") -> AiOversizeStrategy`
- `MAP_REDUCE("mapReduce") -> AiOversizeStrategy`
- `DETERMINISTIC("deterministic") -> AiOversizeStrategy`
- `configValue() -> String`
- `fromConfig(String) -> AiOversizeStrategy`

#### Dependencies
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- Throws IllegalArgumentException for unknown strategies

#### Concurrency
- Thread-safe due to enum nature
