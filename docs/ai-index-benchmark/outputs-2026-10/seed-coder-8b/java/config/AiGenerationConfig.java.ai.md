### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:12:09Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for a single model, balancing performance and creativity.

#### Purpose
Mutable configuration for AI generation, bridging Maven config and AI provider implementations.

#### Type
Public class with Lombok-generated toString.

#### Input
Constructor (no-op), setters for all fields, various getter methods.

#### Output
Getter methods, setters for all fields, builder pattern alternatives.

#### Core logic
Handles AI generation parameters, including model path, sampling, caching, and hardware optimization.

#### Public API
setModelPath(String), setContextSize(int), ..., getCalibration(AiCalibration) -> AiCalibration

#### Dependencies
java.util, lombok, jspecify.annotations

#### Exceptions / Errors
No specific exceptions, relies on input validation.

#### Concurrency
Thread-safe due to immutable defaults and synchronized setters.
