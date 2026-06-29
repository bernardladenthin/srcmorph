### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:51:06Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Mock AI generation provider for testing purposes, returning deterministic mock summaries.

#### Purpose
- Mock implementation of `AiGenerationProvider` for testing
- Returns a fixed mock summary based on the input file name

#### Type
class public implements AiGenerationProvider @ToString

#### Input
- `AiGenerationRequest` object containing source file information

#### Output
- String containing mock summary

#### Core logic
1. Extract file name from request
2. Generate mock summary string with file name

#### Public API
- `MockAiGenerationProvider() -> void` Constructs the provider
- `generate(AiGenerationRequest) -> String` Generates mock summary

#### Dependencies
- `AiGenerationRequest`
- `Path`
- `IOException`

#### Exceptions / Errors
- May throw `IOException` if file operations fail
