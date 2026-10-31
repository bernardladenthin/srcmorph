### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:23:22Z
- G: 
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider by name, defaulting to a mock when none is specified.

#### Purpose
- Factory for creating AI generation providers from a named key.
- Resolves effective provider between run-level and model-level configuration.

#### Type
- Class `AiGenerationProviderFactory`, final, `@ToString`.
- Implements no interface; uses `@Nullable` and `@Mojo`-style annotations from jspecify and Lombok.

#### Input
- `providerName` (String): run-level provider key, may be null or blank.
- `llamaConfig` (LlamaCppJniConfig): model configuration, provides `provider()`.
- `promptSupport` (AiPromptSupport): prompt lookup support.
- `compatibilityHelper` (Java8CompatibilityHelper): internal blank-check helper.

#### Output
- `AiGenerationProvider` instance: mock, LlamaCppJni, or OpenAI-compatible.
- Throws `IllegalArgumentException` for unrecognized provider names.

#### Core logic
- `effectiveProvider` prefers model-level provider over run-level provider.
- `create` defaults to `MockAiGenerationProvider` when no effective provider is set.
- Switch dispatches on provider key to instantiate the matching provider.

#### Public API
- `AiGenerationProviderFactory()` -> constructor, no-op.
- `effectiveProvider(String, String) -> @Nullable String`, resolves effective provider key.
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider`, instantiates provider.

#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `LlamaCppJniConfig`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `OpenAiCompatibleAiGenerationProvider`

#### Exceptions / Errors
- `IllegalArgumentException` thrown for unsupported provider names.
- Null handling via `@Nullable` and blank checks.

#### Concurrency
- No concurrency notes in source.
