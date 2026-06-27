### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:13:22Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an `AiGenerationProvider` implementation by name.

#### Purpose
- Instantiate the requested `AiGenerationProvider` based on a provider key.

#### Type
- `class` `AiGenerationProviderFactory` (public) annotated with `@ToString`; holds a `Java8CompatibilityHelper` field.

#### Input
- `String providerName`
- `LlamaCppJniConfig llamaConfig`
- `AiPromptSupport promptSupport`

#### Output
- A newly created `AiGenerationProvider` instance
- Defaults to `MockAiGenerationProvider` when `providerName` is blank or `null`

#### Core logic
- If `providerName` is `null` or blank → return `MockAiGenerationProvider`
- Switch on `providerName`:
  - `"mock"` → return `MockAiGenerationProvider`
  - `"llamacpp-jni"` → return `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`
  - otherwise → throw `IllegalArgumentException("Unsupported AI provider: " + providerName)`

#### Public API
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider` – creates provider instance

#### Dependencies
- `Java8CompatibilityHelper`, `AiPromptSupport`, `LlamaCppJniConfig`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `AiGenerationProvider`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for unrecognized provider names

#### Concurrency
- No threading or immutability concerns shown.
