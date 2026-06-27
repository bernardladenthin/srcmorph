### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:12:20Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Selects and instantiates an `AiGenerationProvider` implementation by name.

#### Type
- Class, final, no extends/implements, no generics.

#### Input
- `providerName` (String), `llamaConfig` (LlamaCppJniConfig), `promptSupport` (AiPromptSupport).

#### Output
- `AiGenerationProvider` instance.

#### Core Logic
- Returns `MockAiGenerationProvider` if `providerName` is null or blank.
- Returns `LlamaCppJniAiGenerationProvider` for "llamacpp-jni".
- Throws `IllegalArgumentException` for unsupported providers.

#### Public API
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider` selects provider based on name.

#### Dependencies
- `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiPromptSupport`.

#### Exceptions / Errors
- Throws `IllegalArgumentException` for unsupported providers.

#### Concurrency
- No explicit concurrency notes.
