### java
- H: 1.0
- C: 44C305C9
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:39:13Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Indexes source files and generates AI-assisted metadata for each file within a project.

#### Purpose
- Indexes source files and generates AI-assisted metadata for each file within a project.
- Bridges Maven configuration, JSON/YAML config, and plain Java code binding surfaces.

#### Responsibilities
- Evaluates and validates AI condition trees against file contexts to determine matches.
- Computes deterministic whole-file regex counts of structural elements for AI fact extraction.
- Carries all parameters for a single AI generation step between Maven configuration and AI provider implementations.
- Selects how oversized source files are handled during AI routing rules.
- Holds every parameter a srcmorph run needs as a root mutable JavaBean.
- Supplies AI text generation backends for the srcmorph project, including local inference and remote chat-completions providers.
- Parses raw LLM completions to extract final model answers for storage.

#### Key units
- `AiConditionEvaluator`: validates and matches AI condition trees against file contexts.
- `AiConditionGroup`: container for child AI conditions used as and/or combinators.
- `AiFactExtractor`: computes deterministic whole-file regex counts for AI fact extraction.
- `AiGenerationConfig`: mutable configuration object for AI generation steps.
- `AiOversizeStrategy`: enum selecting oversized source file handling behavior.
- `SrcMorphConfiguration`: root mutable JavaBean for srcmorph run parameters.
- `AiGenerationProvider`: interface defining `generate` and `generateWithTimings` methods for AI text generation.
- `AiGenerationRequest`: request object containing prompt, source file, source text, and current header.
- `AiGenerationTimings`: timings object reporting token and rate metrics for generation.
- `AiCompletionParser`: class stripping Gemma-4 thinking blocks from LLM completions to extract the final answer.
- `LlamaCppJniAiGenerationProvider`: class implementing local LLM inference via llama.cpp JNI.
- `LlamaCppJniConfig`: immutable builder-based configuration for llama.cpp JNI parameters.
- `OpenAiCompatibleProviderSupport`: class handling request body, endpoint, API key, status check, error message, and timings for OpenAI-compatible providers.
- `AiGenerationProviderFactory`: class selecting and instantiating AI generation providers by name.

#### Data flow
- `SrcMorphConfiguration` supplies AI definitions, prompt definitions, and generation provider settings.
- `AiConditionEvaluator` uses `AiCondition` trees and `AiFileContext` to determine file matches.
- `AiFactExtractor` uses `AiFactCounter` definitions and source text to compute structural counts.
- `AiGenerationConfig` carries model path, sampling parameters, and retry policy for AI generation steps.
- `AiGenerationProviderFactory` resolves the effective provider key between run-level and model-level configuration, then instantiates the matching provider.
- `LlamaCppJniAiGenerationProvider` lazily loads the model, builds inference parameters from `LlamaCppJniConfig`, calls the model, and parses the response through `AiCompletionParser` and `ChatResponseParser`.
- `OpenAiCompatibleProviderSupport` builds the chat-completions JSON body, resolves the API key, checks the host, and computes timings from the response.
- `AiCompletionParser` strips thinking blocks from raw completion text to extract the final model answer for storage.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.srcmorph.config.AiCondition`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `net.ladenthin.srcmorph.config.AiModelDefinition`, `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`, `net.ladenthin.srcmorph.config.AiFactDefinition`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `ChatResponseParser`, `ModelParameters`, `ReasoningFormat`, `FlashAttn`, `ChatResponse`, `Timings`, `MockAiGenerationProvider`.
- External: `com.fasterxml.jackson.databind.ObjectMapper`, `com.fasterxml.jackson.databind.node.ArrayNode`, `com.fasterxml.jackson.databind.node.ObjectNode`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`, `java.time.Instant`, `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.io.IOException`, `java.net.URL`, `java.util.Objects`, `java.util.function.Function`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Shared `@Nullable` and `@ToString` annotations across configuration and provider classes.
- `IllegalArgumentException` used for validation failures in evaluators and extractors.
- `IOException` thrown by generation and parsing methods on underlying failures.
- Mutable JavaBeans used for configuration objects, not thread-safe by design.
- Immutable value objects (`LlamaCppJniConfig`) for thread-safe configuration.
- Builder pattern for complex configuration (`LlamaCppJniConfig.Builder`).
- Stateless classes after construction for testability.
- No concurrency or threading notes in any file.
