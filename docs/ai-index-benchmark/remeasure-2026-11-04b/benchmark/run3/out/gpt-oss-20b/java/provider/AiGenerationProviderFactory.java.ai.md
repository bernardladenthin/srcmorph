### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:45Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Creates an AI generation provider instance based on a provider key or model configuration.

#### Purpose
- Instantiates an `AiGenerationProvider` implementation.
- Resolves the effective provider key from run or model configuration.

#### Type
- `public class AiGenerationProviderFactory`  
  - modifiers: public  
  - supertypes: none  
  - annotations: `@ToString`  
  - fields: `private final Java8CompatibilityHelper compatibilityHelper`  
  - constants: `public static final String PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`  
  - methods: `public AiGenerationProviderFactory()`, `public static @Nullable String effectiveProvider(String, String)`, `public AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)`

#### Input
- Constructor: none.
- `effectiveProvider`: `runProvider: String?`, `modelProvider: String?`.
- `create`: `providerName: String`, `llamaConfig: LlamaCppJniConfig`, `promptSupport: AiPromptSupport`.

#### Output
- `effectiveProvider`: `String?` (trimmed model provider or run provider).
- `create`: `AiGenerationProvider` instance (Mock, LlamaCppJni, or OpenAiCompatible).

#### Core logic
- `effectiveProvider`: return `modelProvider` trimmed if present; else `runProvider`.
- `create`:
  1. Compute `effective = effectiveProvider(providerName, llamaConfig.provider())`.
  2. If `effective` is null or blank → return `new MockAiGenerationProvider()`.
  3. Switch on `effective`:
     - `PROVIDER_MOCK` → `new MockAiGenerationProvider()`.
     - `PROVIDER_LLAMACPP_JNI` → `new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
     - `PROVIDER_OPENAI_COMPATIBLE` → `new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport)`.
     - default → throw `IllegalArgumentException("Unsupported AI provider: " + effective)`.

#### Public API
- `AiGenerationProviderFactory()` → constructs factory.
- `static @Nullable String effectiveProvider(String, String)` → resolves provider key.
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport)` → creates provider instance.

#### Dependencies
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `create` throws `IllegalArgumentException` for unsupported provider names.

#### Concurrency
- No explicit concurrency constructs; instance field `compatibilityHelper` is immutable.
