### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:03:07Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files exceeding a model's context window during processing.

#### Purpose
* Enumerates predefined strategies for managing oversized source files.
* Provides a mechanism to parse configuration strings into specific oversize handling strategies.

#### Type
enum AiOversizeStrategy;

#### Input
* `fromConfig(String value)`: Configured string value (nullable).

#### Output
* `fromConfig(String value)`: AiOversizeStrategy instance.
* `configValue()`: String representing the strategy's configuration token.

#### Core logic
* `fromConfig`: Checks for null or blank input; if so, returns `DEFAULT` (FAIL). Iterates through all `AiOversizeStrategy` values, comparing the trimmed input string case-insensitively against each strategy's `configValue`. Throws `IllegalArgumentException` if no match is found.

#### Public API
configValue() -> String: Returns the strategy's configuration token.
fromConfig(String value) -> AiOversizeStrategy: Parses config string to strategy.

#### Dependencies
org.jspecify.annotations.Nullable

#### Exceptions / Errors
* Throws IllegalArgumentException: If `value` is non-blank and does not match any defined strategy.
* Handles null/blank input by returning AiOversizeStrategy.DEFAULT.

#### Concurrency
No explicit concurrency mechanisms present; enum instances are inherently thread-safe.
