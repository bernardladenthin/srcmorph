### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:15:36Z
- G: 
- A: 0.0.0
- X: file
---
> Selects AI text-generation backend (mock, llama.cpp JNI, OpenAI-compatible server) for model runs.
#### Purpose
- Factory for `AiGenerationProvider` instances by provider key.
- Resolves run-level vs model-level provider override via `effectiveProvider`.
#### Type
- class `AiGenerationProviderFactory`, Lombok `@ToString`.
#### Input
- `providerName` run-level `generationProvider` key, nullable/blank.
- `llamaConfig` `LlamaCppJniConfig` with `provider()` model override plus model/server config.
- `promptSupport` `AiPromptSupport` prompt lookup passed to `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- `runProvider`, `modelProvider` strings for `effectiveProvider`, nullable/empty allowed.
- `compatibilityHelper` `Java8CompatibilityHelper` for blank check.
#### Output
- `AiGenerationProvider`: `MockAiGenerationProvider` on blank/mock, `LlamaCppJniAiGenerationProvider` on `llamacpp-jni`, `OpenAiCompatibleAiGenerationProvider` on `openai-compatible`.
- `effectiveProvider` returns trimmed `modelProvider` or fallback `runProvider`, nullable/empty if neither names one.
#### Core logic
- `effectiveProvider` prefers non-empty trimmed model `provider` over run `generationProvider`.
- `create` computes effective key then switches on `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.
- Blank/null effective key defaults to deterministic model-free `MockAiGenerationProvider`.
- Unknown key rejects with `IllegalArgumentException`.
#### Public API
- `AiGenerationProviderFactory()` -> constructor, no-op factory init.
- `effectiveProvider(runProvider, modelProvider)` -> String, resolves model-over-run provider key.
- `create(providerName, llamaConfig, promptSupport)` -> AiGenerationProvider, instantiates provider by key.
- `PROVIDER_MOCK` -> String, key `mock`.
- `PROVIDER_LLAMACPP_JNI` -> String, key `llamacpp-jni`.
- `PROVIDER_OPENAI_COMPATIBLE` -> String, key `openai-compatible`.
#### Dependencies
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- `AiGenerationProvider`, `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniConfig`
#### Exceptions / Errors
- `IllegalArgumentException` on unsupported AI provider name in `create`.
- Null/blank-tolerant: null/empty/whitespace provider falls back to mock, not NPE.
