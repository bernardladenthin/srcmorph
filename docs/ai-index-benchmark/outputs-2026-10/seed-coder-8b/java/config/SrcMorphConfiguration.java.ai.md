### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:13:22Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI-driven source code transformations for projects

#### Purpose
Configurable parameters for AI-powered code modifications

#### Type
public class with Lombok @ToString annotation

#### Input
Constructor, setters for all fields, various getter methods

#### Output
Getter methods, potentially modified state through setters

#### Core logic
Holds configuration for AI generation, file processing, and output settings

#### Public API
100+ getter and setter methods (getBaseDirectory, setBaseDirectory, ..., getProjectName, setProjectName)

#### Dependencies
java.io.File, java.util.List, lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptDefinition, org.jspecify.annotations.Nullable

#### Exceptions / Errors
No explicit exceptions, but null values allowed for optional fields

#### Concurrency
Not thread-safe, mutable state
