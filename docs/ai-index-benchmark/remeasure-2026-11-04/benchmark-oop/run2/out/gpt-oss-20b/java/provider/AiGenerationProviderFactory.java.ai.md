### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:33Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Instantiates AI generation providers based on a provider key or model configuration.

#### Purpose
- Selects an `AiGenerationProvider` implementation by name.
- Provides a default mock provider when no key is supplied.

#### Type
- `public class AiGenerationProviderFactory`  
  - Modifiers: public, final (via `@ToString`), no superclass.  
  - Fields: `private final Java8CompatibilityHelper compatibilityHelper`.  
  - Constants: `public static final String PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.  
  - Methods: `public AiGenerationProviderFactory()`, `public static @Nullable String effectiveProvider(String, String)`, `public AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)`.

#### Input
- Constructor: none.  
- `effectiveProvider`: `runProvider: String?`, `modelProvider: String?`.  
- `create`: `providerName: String`, `llamaConfig: LlamaCppJniConfig`, `promptSupport: AiPromptSupport`.

#### Output
- `effectiveProvider`: `String?` (trimmed model provider or run provider).  
- `create`: `AiGenerationProvider` instance (Mock, LlamaCppJni, or OpenAiCompatible).  
- Side effect: none.

#### Core logic
- `effectiveProvider` returns `modelProvider.trim()` if present; otherwise `runProvider`.  
- `create` trims the effective provider key; if blank, returns `MockAiGenerationProvider`.  
- Switch on effective key:  
  - `PROVIDER_MOCK` → `MockAiGenerationProvider`.  
  - `PROVIDER_LLAMACPP_JNI` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.  
  - `PROVIDER_OPENAI_COMPATIBLE` → `OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.  
  - Default → `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.  
- `static @Nullable String effectiveProvider(String, String)` → resolves provider key.  
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → creates provider instance.

#### Dependencies
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`  
- `net.ladenthin.srcmorph.provider.AiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`  
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`

#### Exceptions / Errors
- `IllegalArgumentException` thrown when provider key is unrecognised.

#### Concurrency
- No explicit concurrency constructs; instance fields are immutable after construction.
