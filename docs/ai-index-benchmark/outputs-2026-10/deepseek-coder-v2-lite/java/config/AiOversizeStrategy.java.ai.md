### AiOversizeStrategy.java
- H: 1.0
- C: A01D1DA1
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:27:41Z
- G: 
- A: 0.0.0
- X: file
---
> Defines strategies for handling oversized source files in a build process, based on configuration settings.

#### Purpose
- Defines strategies for handling oversized source files in a build process.
- Provides a default strategy for handling oversized files.

#### Type
- Enum class.
- Modifiers: None.
- Extends: None.
- Implements: None.
- Key Generics: None.
- Notable Annotations: None.

#### Input
- None.

#### Output
- None.

#### Core logic
- Defines four strategies for handling oversized files:
  1. **FAIL**: Abort the build, requiring user intervention to configure a larger-context model.
  2. **SAMPLE**: Trim the source to the window and summarize the head in a single call.
  3. **MAP_REDUCE**: Split the file into window-sized chunks at line boundaries, summarize each chunk, and then combine the summaries.
  4. **DETERMINISTIC**: Emit a deterministic body (metadata + head/tail sample) without using AI analysis.

#### Public API
- `configValue() -> String`: Returns the configuration token for the strategy.
- `fromConfig(String value) -> AiOversizeStrategy`: Parses the configuration value and returns the corresponding strategy.

#### Dependencies
- `org.jspecify.annotations.Nullable`: For nullable parameter handling.

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if the provided configuration value does not match any known strategy.

#### Concurrency
- The enum values are thread-safe as they are constants.

#### Counts
- 1 Enum: `AiOversizeStrategy`
- 4 Enum Values: `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`
- 1 Method: `fromConfig(String value)`
- 1 Field: `configValue`
