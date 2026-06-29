### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:19:11Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Factory selects and instantiates an AI generation provider implementation by name for integration into the Llama.cpp AI index system.

#### Purpose
*   Instantiates `AiGenerationProvider` instances for `mock` or `llamacpp-jni` modes within the AI index module.
*   Validates provider names and returns configured `LlamaCppJniAiGenerationProvider` or `MockAiGenerationProvider`.

#### Type
Class `AiGenerationProviderFactory` with `@ToString` annotation; final field `Java8CompatibilityHelper`.

#### Input
Constructor takes no parameters; `create` method accepts `String providerName`, `LlamaCppJniConfig llamaConfig`, and `AiPromptSupport promptSupport`.

#### Output
Returns `AiGenerationProvider` instance (`MockAiGenerationProvider` or `LlamaCppJniAiGenerationProvider`); throws `IllegalArgumentException` on invalid names.

#### Core logic
*   Initializes internal `Java8CompatibilityHelper` for string validation.
*   Checks if `providerName` is null or blank to default to `mock` mode.
*   Switches on `providerName` string value to instantiate specific provider types.
*   Throws error for unrecognized provider keys in `llamacpp-jni` AI index context.

#### Public API
`create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider`: creates provider instance.

#### Dependencies
`AiGenerationProvider`, `AiPromptSupport`, `LlamaCppJniConfig`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `Java8CompatibilityHelper`.

#### Exceptions / Errors
Throws `IllegalArgumentException` when `providerName` is unrecognized or invalid string.

#### Concurrency
No concurrency concerns; uses single-threaded instantiation logic with immutability for internal helpers.
