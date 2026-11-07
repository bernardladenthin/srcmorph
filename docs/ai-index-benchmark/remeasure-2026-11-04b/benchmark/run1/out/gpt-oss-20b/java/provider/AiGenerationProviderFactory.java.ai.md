### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:43Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Selects an `AiGenerationProvider` implementation by name or model configuration.
- Provides default mock provider when no key is supplied.

#### Type
- `public class AiGenerationProviderFactory`  
  - Modifiers: public, final (implicit), @ToString  
  - Supertype: java.lang.Object  
  - Fields:  
    - `private final Java8CompatibilityHelper compatibilityHelper` – instance of helper for blank checks.  
  - Methods:  
    - `public AiGenerationProviderFactory()` – no‑op constructor.  
    - `public static @Nullable String effectiveProvider(String runProvider, String modelProvider)` – returns provider key.  
    - `public AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` – factory method.

#### Input
- `create` parameters:  
  - `providerName` – run’s provider key (may be null/blank).  
  - `llamaConfig` – model configuration providing `provider()` method.  
  - `promptSupport` – lookup support for prompts.  
- `effectiveProvider` parameters: `runProvider`, `modelProvider`.

#### Output
- `create` returns an instance of `AiGenerationProvider` (one of `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).  
- `effectiveProvider` returns a trimmed provider key or null.

#### Core logic
- `effectiveProvider`:  
  - If `modelProvider` is null or empty after trim, return `runProvider`; else return trimmed `modelProvider`.  
- `create`:  
  - Compute `effective` via `effectiveProvider`.  
  - If `effective` is null or blank (checked by `compatibilityHelper.isBlank`), return new `MockAiGenerationProvider`.  
  - Switch on `effective`:  
    - `PROVIDER_MOCK` → `new MockAiGenerationProvider()`.  
    - `PROVIDER_LLAMACPP_JNI` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
    - `PROVIDER_OPENAI_COMPATIBLE` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.  
    - default → throw `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.  
- `static @Nullable String effectiveProvider(String runProvider, String modelProvider)` → resolves provider key.  
- `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` → creates provider instance.

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` when provider key is unrecognised.

#### Concurrency
- No explicit concurrency constructs; factory is stateless except for a single helper instance.
