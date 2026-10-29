### AiGenerationProviderFactory.java
- H: 1.0
- C: E2DEA5E5
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:03:02Z
- G: 
- A: 0.0.0
- X: file
---
> Factory selects and instantiates an AI generation provider implementation based on configuration keys and model settings.

#### Purpose
*   Instantiates specific AI generation providers (Mock, Llama.cpp JNI, OpenAI-compatible) based on provider keys.
*   Resolves the effective provider key by prioritizing model configuration over run configuration.

#### Type
*   Kind: `public class`
*   Modifiers: `final` (implied by no setters), `@ToString`
*   Extends: `Object`
*   Implements: None
*   Annotations: `@lombok.ToString`

#### Input
*   `String providerName`: Run's provider key (defaults to mock if blank/null).
*   `LlamaCppJniConfig llamaConfig`: Model configuration containing the model's provider key.
*   `AiPromptSupport promptSupport`: Prompt lookup support for providers requiring it.

#### Output
*   `AiGenerationProvider`: Returns a newly instantiated provider instance.
*   `String`: Returns resolved provider key via `effectiveProvider` method.
*   Side Effect: Throws `IllegalArgumentException` if provider name is unsupported.

#### Core logic
*   Resolves the effective provider key by checking model configuration first, then falling back to run configuration.
*   Defaults to `MockAiGenerationProvider` if the resolved key is null or blank.
*   Instantiates `MockAiGenerationProvider` for the mock provider key.
*   Instantiates `LlamaCppJniAiGenerationProvider` for the Llama.cpp JNI provider key.
*   Instantiates `OpenAiCompatibleAiGenerationProvider` for the OpenAI-compatible provider key.
*   Throws `IllegalArgumentException` if the provider key is not recognized.

#### Public API
*   `AiGenerationProviderFactory()` -> `void` (no-op constructor)
*   `String effectiveProvider(String runProvider, String modelProvider)` -> `String` (resolves provider key)
*   `AiGenerationProvider create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)` -> `AiGenerationProvider` (instantiates provider)

#### Dependencies
*   `net.ladenthin.srcmorph.provider.AiGenerationProvider`
*   `net.ladenthin.srcmorph.prompt.AiPromptSupport`
*   `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
*   `org.jspecify.annotations.Nullable`
*   `lombok.ToString`
*   `MockAiGenerationProvider`
*   `LlamaCppJniAiGenerationProvider`
*   `OpenAiCompatibleAiGenerationProvider`
*   `LlamaCppJniConfig`

#### Exceptions / Errors
*   `IllegalArgumentException`: Thrown when the provider name is not recognized.
*   Null handling: `effectiveProvider` returns null if both inputs are null or empty.

#### Concurrency
*   Thread-safe: No shared mutable state; uses immutable configuration objects.
*   Synchronization: None required.
