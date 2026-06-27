### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:34:26Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an `AiGenerationProvider` implementation by name.

#### Purpose
- Factory for creating instances of different AI generation providers based on the provided name.

#### Type
public class AiGenerationProviderFactory

#### Core logic
- Determines which `AiGenerationProvider` to create based on the `providerName` parameter.
- Supports two provider types: "mock" and "llamacpp-jni".
- Returns a new instance of the selected provider or throws an exception if the provider is not recognized.

#### Public API
- `create(final String providerName, final LlamaCppJniConfig llamaConfig, final AiPromptSupport promptSupport) -> AiGenerationProvider`: Creates an `AiGenerationProvider` for the given provider name.
