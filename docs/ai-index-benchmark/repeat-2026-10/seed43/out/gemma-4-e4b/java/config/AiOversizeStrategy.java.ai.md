### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:56:12Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files exceeding the context window limit of a routed model.

#### Purpose
* Enumerates distinct strategies for managing oversized source files during processing.
* Provides a mechanism to parse configuration strings into specific oversized file handling behaviors.

#### Type
enum AiOversizeStrategy;

#### Input
* `fromConfig(String value)`: Configured string value (`@Nullable`).

#### Output
* `configValue()`: Returns the strategy's configuration string.
* `fromConfig(String value)`: Returns an `AiOversizeStrategy` instance.

#### Core logic
* `fromConfig`: Checks input `value` for null or blank; returns `DEFAULT` if so. Iterates through `AiOversizeStrategy` values, comparing case-insensitively against the trimmed input; throws `IllegalArgumentException` if no match is found.

#### Public API
configValue() -> String: Returns the strategy's configuration token.
fromConfig(String value) -> AiOversizeStrategy: Parses config string to strategy.

#### Dependencies
org.jspecify.annotations.Nullable

#### Exceptions / Errors
* Throws IllegalArgumentException if `fromConfig` receives a non-blank, unrecognized strategy string.
* Handles null or blank input in `fromConfig` by returning `DEFAULT`.

#### Concurrency
No explicit concurrency mechanisms present; enum instance usage is inherently thread-safe for state access.
