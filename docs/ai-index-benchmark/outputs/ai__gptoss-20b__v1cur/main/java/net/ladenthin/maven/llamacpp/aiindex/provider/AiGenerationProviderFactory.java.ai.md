### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-07-02T23:12:20Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 3; TODO/FIXME: 0; @Override: 0; methods (approx): 2; constructors: 1; field declarations (w/ modifier): 1

> Instantiates AI generation providers (mock or llama.cpp JNI) based on a name.

#### Purpose
- Factory for `AiGenerationProvider` instances.
- Chooses implementation according to provider key.

#### Type
- Class `AiGenerationProviderFactory` (public, final by default, no annotations except `@ToString`).

#### Input
- `String providerName` – key (“mock” or “llamacpp-jni”).
- `LlamaCppJniConfig llamaConfig` – config for JNI provider.
- `AiPromptSupport promptSupport` – prompt lookup for providers that need it.
- Internal `Java8CompatibilityHelper` for blank checks.

#### Output
- Returns a new `AiGenerationProvider` instance.
- Throws `IllegalArgumentException` for unsupported names.

#### Core logic
- If `providerName` is null or blank → return `MockAiGenerationProvider`.
- Switch on `providerName`:
  - `"mock"` → `MockAiGenerationProvider`.
  - `"llamacpp-jni"` → `LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport)`.
  - Default → throw `IllegalArgumentException`.

#### Public API
- `AiGenerationProviderFactory()` → constructor, no side effects.
- `AiGenerationProvider create(String, LlamaCppJniConfig, AiPromptSupport) → new provider instance` – selects and creates provider.

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`
- `lombok.ToString`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `LlamaCppJniConfig`

#### Exceptions / Errors
- `IllegalArgumentException` when provider name is unsupported.

#### Concurrency
- Stateless; thread‑safe.
