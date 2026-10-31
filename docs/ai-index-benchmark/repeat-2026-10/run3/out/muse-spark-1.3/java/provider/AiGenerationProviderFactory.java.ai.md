### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:22:03Z
- G: 
- A: 0.0.0
- X: file
---
> Selects AI text-generation backend (mock, llama.cpp JNI, OpenAI-compatible server) for code-morphing runs.
#### Purpose
- Factory selecting and instantiating AiGenerationProvider by provider key.
- Resolves model-level vs run-level provider override via effectiveProvider.
#### Type
- class public, @ToString, no extends/implements, no generics.
#### Input
- Constructor: no parameters, no injected dependencies.
- effectiveProvider(runProvider String nullable, modelProvider String nullable): run generationProvider and model provider strings.
- create(providerName String, llamaConfig LlamaCppJniConfig, promptSupport AiPromptSupport): run provider key, model config with provider(), prompt lookup support.
- Consumed field: compatibilityHelper Java8CompatibilityHelper for isBlank check.
#### Output
- effectiveProvider -> String nullable: trimmed modelProvider if non-empty else runProvider.
- create -> AiGenerationProvider: new MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider.
#### Core logic
- effectiveProvider prefers modelProvider when non-null and non-blank after trim, otherwise falls back to runProvider.
- create computes effective key via effectiveProvider(providerName, llamaConfig.provider()).
- Blank/null effective key defaults to MockAiGenerationProvider deterministic model-free provider.
- Switch dispatches PROVIDER_MOCK to MockAiGenerationProvider, PROVIDER_LLAMACPP_JNI to LlamaCppJniAiGenerationProvider, PROVIDER_OPENAI_COMPATIBLE to OpenAiCompatibleAiGenerationProvider.
- Unknown key rejects with IllegalArgumentException.
#### Public API
- AiGenerationProviderFactory() -> void: default no-op constructor.
- effectiveProvider(runProvider, modelProvider) -> String: resolves model-over-run provider key.
- create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider: instantiates provider by key.
#### Dependencies
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- AiGenerationProvider, MockAiGenerationProvider, LlamaCppJniAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, LlamaCppJniConfig
#### Exceptions / Errors
- IllegalArgumentException on unsupported AI provider name in create, includes effective key.
- Null/blank handling: null/blank effective provider defaults to mock; null modelProvider falls back to runProvider; empty/whitespace modelProvider falls back to runProvider.
