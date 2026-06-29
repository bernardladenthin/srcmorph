### AiFieldGenerationSupport.java
- H: 1.0
- C: 8B242F21
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:09:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI-powered field documentation by processing configuration lists, preparing prompts, and handling retry logic for failed generations.

#### Purpose
- Processes AI field generation requests.
- Manages prompt preparation, retries, and trim warnings.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString.

#### Input
- Constructor: Log, AiGenerationProvider, AiPromptPreparationSupport, AiModelDefinitionSupport.
- Method processFieldGenerations: List<AiFieldGenerationConfig>, Path, String, String, AiMdHeader.
- Dependencies: AiFieldGenerationConfig, AiGenerationConfig, AiGenerationRequest, AiPreparedPrompt.

#### Output
- Return type: AiGenerationResult.
- Side effects: Log warnings for trimming and empty outputs; log retry attempts at INFO level.

#### Core logic
- Iterates over field generation configurations.
- Prepares prompts using AiPromptPreparationSupport.
- Truncates source text if needed, logs warning when configured.
- Generates AI output via AiGenerationProvider.
- Retries up to maxRetries times with increasing temperature for blank outputs.
- Logs detailed calculations of max input characters per prompt configuration.

#### Public API
- processFieldGenerations(fieldGenerations, contextFile, contextType, sourceText, baseHeader) -> AiGenerationResult  
  Generates and accumulates AI field documentation.

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig
- net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationResult
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPreparedPrompt
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- org.apache.maven.plugin.logging.Log

#### Exceptions / Errors
- Throws IOException if generation provider fails.
- IllegalArgumentException if AI definition key is not found.

#### Concurrency
- No explicit concurrency handling. Uses thread-safe Map cache for maxInputChars.
