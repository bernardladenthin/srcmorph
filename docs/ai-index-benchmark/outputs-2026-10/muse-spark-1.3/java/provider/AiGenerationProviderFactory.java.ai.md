### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:15:04Z
- G: 
- A: 0.0.0
- X: file
---
> Selects AI text-generation backend for source-code morphing by provider name.
#### Purpose
- Factory selecting/instantiating AiGenerationProvider implementation by name.
- Centralizes provider-key constants and model-over-run provider resolution.
#### Type
- public class AiGenerationProviderFactory, Lombok @ToString, no extends/implements.
#### Input
- runProvider String run-level generationProvider, nullable.
- modelProvider String model definition provider, nullable/empty.
- providerName String run provider key, nullable/blank defaults to mock.
- llamaConfig LlamaCppJniConfig model configuration, exposes provider().
- promptSupport AiPromptSupport passed to LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider.
- compatibilityHelper Java8CompatibilityHelper field for isBlank check.
#### Output
- effectiveProvider -> @Nullable String trimmed modelProvider or runProvider.
- create -> AiGenerationProvider new MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider.
#### Core logic
- effectiveProvider prefers trimmed modelProvider when non-null/non-empty, else runProvider.
- create resolves effective key via effectiveProvider(providerName, llamaConfig.provider()).
- Blank/null effective key defaults to MockAiGenerationProvider deterministic model-free provider.
- Switch dispatches PROVIDER_MOCK to MockAiGenerationProvider, PROVIDER_LLAMACPP_JNI to LlamaCppJniAiGenerationProvider, PROVIDER_OPENAI_COMPATIBLE to OpenAiCompatibleAiGenerationProvider.
- LlamaCppJniAiGenerationProvider loads real GGUF model via llama.cpp JNI.
- OpenAiCompatibleAiGenerationProvider targets OpenAI-compatible chat-completions server (OpenRouter, llama-server, NativeServer).
#### Public API
- AiGenerationProviderFactory() -> constructor, creates new factory.
- effectiveProvider(runProvider, modelProvider) -> String, resolves model-over-run key.
- create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider, instantiates selected provider.
- PROVIDER_MOCK -> String, selects MockAiGenerationProvider.
- PROVIDER_LLAMACPP_JNI -> String, selects llama.cpp JNI provider.
- PROVIDER_OPENAI_COMPATIBLE -> String, selects OpenAI-compatible provider.
#### Dependencies
- lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable.
- AiGenerationProvider, MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, LlamaCppJniConfig.
#### Exceptions / Errors
- IllegalArgumentException on unsupported AI provider name, includes effective key.
- Null/blank handling: null/blank effective provider falls back to mock, not error.
