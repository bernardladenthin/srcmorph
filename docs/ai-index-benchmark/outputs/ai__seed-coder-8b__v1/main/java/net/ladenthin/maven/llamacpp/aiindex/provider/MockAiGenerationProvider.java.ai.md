### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:21:16Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides mock AI generation summaries for testing purposes

#### Purpose
- Implements a deterministic AI generation provider
- Returns mock summaries for testing

#### Type
class public implements AiGenerationProvider @ToString

#### Input
- AiGenerationRequest object with source file information

#### Output
- String containing mock summary

#### Core logic
1. Extracts filename from source file path
2. Generates mock summary string

#### Public API
- MockAiGenerationProvider() -> constructor
- generate(AiGenerationRequest) -> String throws IOException

#### Dependencies
- java.io.IOException
- java.nio.file.Path
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest

#### Exceptions
- Throws IOException if file operations fail
