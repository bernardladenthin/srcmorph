### java
- H: 1.0
- C: C3D438BB
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T12:40:11Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Provides AI condition evaluation, fact extraction, generation configuration, oversize handling, and configuration management for source morphing operations.

#### Purpose
- Evaluates and validates AiCondition trees against AiFileContext for srcmorph source morphing.
- Determines file match status, line-count needs, and fact counts from source code.
- Configures model paths, sampling settings, and resource limits for single-step AI generation.
- Defines strategies for source files that exceed the context window of their routed model.

#### Responsibilities
- Condition evaluation and validation: AiConditionEvaluator walks AiCondition trees, checks matches, validates, and reports line-count requirements.
- Condition grouping: AiConditionGroup holds child AiCondition instances with defensive copying and Maven plugin framework item binding support.
- Fact extraction: AiFactExtractor generates a facts block of label: count entries from source code.
- Generation configuration: AiGenerationConfig holds mutable parameters for a single AI generation step; SrcMorphConfiguration centralizes all srcmorph parameters.
- Oversize handling: AiOversizeStrategy selects abort, head sampling, chunked summarization, or deterministic emission strategies.
- LLM parsing and generation: AiCompletionParser strips thinking blocks from raw completion text; AiGenerationProvider, LlamaCppJniAiGenerationProvider, Mock, OpenAiCompatibleProviderSupport provide pluggable backends.
- Provider factory and configuration: AiGenerationProviderFactory selects provider implementations; LlamaCppJniConfig is an immutable configuration holder; OpenAiCompatibleProviderSupport provides offline request/response utilities.

#### Key units
- AiConditionEvaluator: final class that evaluates and validates AiCondition trees against AiFileContext.
- AiConditionGroup: container for child AiCondition objects with defensive copying.
- AiFactExtractor: final class computing deterministic fact counts from source code.
- AiGenerationConfig: mutable configuration class for a single AI generation step.
- AiOversizeStrategy: enum defining strategies for oversized source files.
- SrcMorphConfiguration: JavaBean holding all configuration parameters for srcmorph runs.
- AiCompletionParser: extracts model answer from raw completion text and strips thinking blocks.
- AiGenerationProvider: interface for text generation, extends AutoCloseable.
- AiGenerationProviderFactory: factory selecting provider implementations by name.
- LlamaCppJniAiGenerationProvider: final class implementing AiGenerationProvider and AutoCloseable using JNI.
- LlamaCppJniConfig: immutable configuration with generation knobs and model path.
- OpenAiCompatibleProviderSupport: offline request/response utilities for local OpenAI-compatible providers.

#### Data flow
- Source code is analyzed by AiFactExtractor to produce a facts block of label: count entries.
- AiCondition trees are evaluated by AiConditionEvaluator using AiFileContext, yielding match status and line-count needs.
- AiConditionGroup stores child conditions for and/or combinators.
- AiGenerationConfig and SrcMorphConfiguration supply runtime parameters for AI generation steps.
- AiOversizeStrategy determines the action when a file exceeds the model context window.
- Raw completion text is passed to AiCompletionParser.parseCompletion(String) -> cleaned answer, then to AiGenerationProvider.generate(request) -> completion text.
- Provider selection via AiGenerationProviderFactory.create(providerName, llamaConfig, promptSupport) -> provider instance; LlamaCppJniConfig provides parameters; OpenAiCompatibleProviderSupport builds JSON requestBody and timing info.

#### Dependencies
- Internal: java.time.Instant, java.time.format.DateTimeParseException; java.util.Collections, List; net.ladenthin.srcmorph.support.AiSourceExcludeFilter; org.jspecific.annotations.Nullable; org.jspecify.annotations.Nullable; java.io.File; lombok.ToString; net.ladenthin.srcmorph.prompt.AiPromptDefinition; net.ladenthin.srcmorph.config.AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition, AiGenerationConfig.
- External modules: net.ladenthin.srcmorph.config (AiCondition, AiFileContext, AiConditionGroup, AiGenerationConfig), net.ladenthin.srcmorph.support (AiSourceExcludeFilter), org.jspecific (annotations), org.jspecify (annotations), lombok.
- Provider package external: net.ladenthin.srcmorph.document (AiGenerationRequest), net.ladenthin.srcmorph.prompt (AiPromptSupport), net.ladenthin.srcmorph.support (Java8CompatibilityHelper), net.ladenthin.llama.value (ChatResponse, Usage), com.fasterxml.jackson.databind (ObjectMapper, ArrayNode, ObjectNode), java.io, java.util, java.net.

#### Cross-cutting
- Stateless final classes (AiConditionEvaluator, AiOversizeStrategy) are thread-safe by immutability.
- Mutable beans (AiGenerationConfig, SrcMorphConfiguration) are not thread-safe; state managed by Maven plugin framework.
- Defensive copying used in AiConditionGroup.setConditions and AiFactExtractor.
- Null handling: @Nullable annotations on parameters; null-safe defaults for config values.
- No checked exceptions in most classes; IllegalArgumentException for invalid configurations or malformed dates.
- Provider side: stateless design, no shared mutable state; immutable configuration after construction; lazy model loading in LlamaCppJniAiGenerationProvider; null input treated as empty string; null config throws NullPointerException; thread-safety via stateless factory/provider.
