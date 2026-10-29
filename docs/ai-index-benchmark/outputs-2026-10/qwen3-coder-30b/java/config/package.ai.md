### java/config
- H: 1.0
- C: 693B5E2E
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:20:07Z
- G: 
- A: 0.0.0
- X: package
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [SrcMorphConfiguration.java](SrcMorphConfiguration.java.ai.md)
---
> Configures and evaluates AI-driven code generation and analysis workflows, managing conditions, facts, and generation parameters across source files.

#### Purpose
- Defines and evaluates AI conditions for file inclusion and processing.
- Configures AI generation parameters and oversize handling strategies.
- Extracts structured facts from source code for deterministic analysis.

#### Responsibilities
- **Condition Evaluation**: Evaluates AI conditions against file metadata to determine inclusion in processing.
- **Configuration Management**: Holds and manages AI generation settings and model parameters.
- **Fact Extraction**: Counts regex-patterned elements in source code for structured analysis.
- **Oversize Handling**: Defines strategies for managing source files exceeding context limits.
- **Configuration Binding**: Supports Maven, JSON/YAML, and Java binding for AI code generation parameters.

#### Key units
- `AiConditionEvaluator`: Evaluates AI conditions against file metadata.
- `AiConditionGroup`: Groups child AI conditions for logical operations.
- `AiFactExtractor`: Extracts structured facts from source code using regex patterns.
- `AiGenerationConfig`: Configures AI generation parameters for model inference.
- `AiOversizeStrategy`: Defines strategies for oversized source file handling.
- `SrcMorphConfiguration`: Central configuration object for srcmorph AI workflows.

#### Data flow
- AI conditions are evaluated against file metadata to filter source files.
- Configuration parameters guide AI model behavior during code generation.
- Source code is analyzed using regex patterns to extract structured facts.
- Oversize strategies determine how to handle files exceeding context limits.
- Configuration data flows from Maven/YAML/Java into `SrcMorphConfiguration` for unified processing.

#### Dependencies
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`: For glob pattern matching.
- `java.time.Instant`, `java.time.format.DateTimeParseException`: For timestamp handling.
- `java.util.regex.Pattern`, `java.util.regex.Matcher`: For regex-based fact counting.
- `lombok.ToString`: For configuration object string representations.
- `org.jspecify.annotations.Nullable`: For nullability annotations.

#### Cross-cutting
- Null safety via `@Nullable` annotations and defensive copying.
- Immutable enums (`AiOversizeStrategy`) for thread-safe strategy definitions.
- Static, stateless methods for utility operations like regex matching and validation.
- Default values and validation in configuration classes to ensure safe usage.
- Lombok-generated `@ToString` for readable configuration objects.
