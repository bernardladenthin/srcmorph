### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:56:25Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an `AiGenerationProvider` implementation by name string within the Llama.cpp AI indexing domain.

#### Purpose
- Instantiates either a `MockAiGenerationProvider` or `LlamaCppJniAiGenerationProvider` based on provider key.
- Validates provider names and handles default fallback behavior for blank inputs.

#### Type
public class AiGenerationProviderFactory; extends Object; implements none; annotated with @ToString.

#### Input
- `providerName`: String (key identifier, defaults to "mock" if null/blank).
- `llamaConfig`: `LlamaCppJniConfig` object for JNI provider configuration.
- `promptSupport`: `AiPromptSupport` object for prompt lookup support.

#### Output
- Returns an `AiGenerationProvider` instance (`MockAiGenerationProvider` or `LlamaCppJniAiGenerationProvider`).
- Throws `IllegalArgumentException` if `providerName` is unrecognized.

#### Core logic
- Checks if `providerName` is null or blank; if so, instantiates `MockAiGenerationProvider`.
- Switches on `providerName`: returns `MockAiGenerationProvider` for "mock".
- Switches on `providerName`: instantiates `LlamaCppJniAiGenerationProvider` with provided `llamaConfig` and `promptSupport`.
- Throws exception in default case for unsupported names.

#### Public API
`create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`

#### Dependencies
`AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiPromptSupport`.

#### Exceptions / Errors
- Throws `IllegalArgumentException` with message "Unsupported AI provider: " + providerName.

#### Concurrency
No concurrency indicators; uses standard single-threaded instantiation logic.
