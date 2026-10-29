### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:08:24Z
- G: 
- A: 0.0.0
- X: file
---
Here is the markdown summary of the `AiGenerationProviderFactory.java` file:

```markdown
## Purpose
Creates an `AiGenerationProvider` implementation based on the provider name or model configuration.

## Type
Class, final, no extends/implements, no generics.

## Input
- Constructor: none
- `effectiveProvider(String, String)`: runProvider (String), modelProvider (String)
- `create(String, LlamaCppJniConfig, AiPromptSupport)`: providerName (String), llamaConfig (LlamaCppJniConfig), promptSupport (AiPromptSupport)

## Output
- `create(String, LlamaCppJniConfig, AiPromptSupport)`: AiGenerationProvider

## Core Logic
- Determines effective provider name using `effectiveProvider`.
- Returns `MockAiGenerationProvider` if provider is null or empty.
- Returns `MockAiGenerationProvider` for `PROVIDER_MOCK`.
- Returns `LlamaCppJniAiGenerationProvider` for `PROVIDER_LLAMACPP_JNI`.
- Returns `OpenAiCompatibleAiGenerationProvider` for `PROVIDER_OPENAI_COMPATIBLE`.
- Throws `IllegalArgumentException` for unrecognized providers.

## Public API
- `create(String, LlamaCppJniConfig, AiPromptSupport) (1 total)`
- `effectiveProvider(String, String) (1 total)`

## Dependencies
- `Lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.provider.MockAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider`
- `net.ladenthin.srcmorph.provider.LlamaCppJniAiGenerationProvider`

## Exceptions / Errors
- `IllegalArgumentException` for unsupported providers.

## Concurrency
Not explicitly noted, but the class appears to be stateless and thread-safe.
```
