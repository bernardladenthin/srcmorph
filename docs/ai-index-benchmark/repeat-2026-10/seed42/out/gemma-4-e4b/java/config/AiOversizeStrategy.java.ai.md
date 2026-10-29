### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:26Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling source files exceeding the context window size of a routed model.

#### Purpose
* Enumerates distinct strategies for managing oversized source files during processing.

#### Type
enum AiOversizeStrategy;

#### Input
* Method parameter: String value in fromConfig(String).
* Injected dependency: None.

#### Output
* Return type: AiOversizeStrategy in fromConfig(String).
* Side effect: Throws IllegalArgumentException on invalid configuration.

#### Core logic
* AiOversizeStrategy.fromConfig(value):
    * Checks for null or blank input value, returning DEFAULT (FAIL).
    * Iterates through all enum values, performing case-insensitive comparison against the trimmed input.
    * Throws IllegalArgumentException if no matching strategy is found.

#### Public API
configValue() -> String: Returns the configuration token for the strategy.
fromConfig(String value) -> AiOversizeStrategy: Parses config string to strategy.

#### Dependencies
org.jspecify.annotations.Nullable

#### Exceptions / Errors
Throws IllegalArgumentException if input string does not match any defined strategy.
Handles null or blank input by returning AiOversizeStrategy.DEFAULT.

#### Concurrency
None apparent; enum usage is inherently thread-safe.
