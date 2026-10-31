### java
- H: 1.0
- C: 194C99AD
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:54:35Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures AI-based source file inclusion and deterministic fact extraction, then morphs source code through pluggable LLM backends (local GGUF, OpenAI-compatible, mock) while sanitizing model reasoning blocks from completions.

#### Purpose
- Configure and evaluate AI-driven source inclusion rules, deterministic whole-file facts, and oversize-file handling.
- Instantiate and invoke pluggable AI generation providers for source-code morphing, stripping leading reasoning blocks from LLM completions.

#### Responsibilities
- Boolean condition tree evaluation and validation against file facts (config).
- Grouping child conditions for Maven `<and>` / `<or>` combinators (config).
- Exact whole-file regex match counting and deterministic facts block generation (config).
- Case-insensitive parsing of oversize response strategies (config).
- Provider contract, lifecycle, and factory-based selection/instantiation (provider).
- Local GGUF execution via llama.cpp JNI (provider).
- OpenAI-compatible request construction without network I/O (provider).
- Completion sanitization by stripping leading thinking/reasoning blocks (provider).

#### Key units
- AiConditionEvaluator: stateless final class matching and validating AiCondition trees against AiFileContext.
- AiConditionGroup: nullable container for child AiCondition lists bound to Maven `<and>` / `<or>` elements.
- AiFactExtractor: pure utility producing `label: count` facts blocks from AiFactCounter regex matches.
- AiOversizeStrategy: enum with FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC and case-insensitive fromConfig parsing.
- AiGenerationConfig and SrcMorphConfiguration: declared configuration types with no behavioral summary provided.
- AiGenerationProvider: interface defining generate, generateWithTimings, and close for AI backends.
- AiGenerationProviderFactory: resolves provider keys and instantiates mock, llama.cpp JNI, or OpenAI-compatible providers.
- LlamaCppJniAiGenerationProvider: final provider wrapping net.ladenthin:llama JNI LlamaModel for local GGUF inference.
- LlamaCppJniConfig: configuration for the llama.cpp JNI provider.
- OpenAiCompatibleProviderSupport: package-private helper for OpenAI-compatible payloads, endpoints, API keys, and timings.
- AiCompletionParser: stateless parser stripping leading Gemma-4 thinking blocks from completion text.

#### Data flow
- AiCondition trees are validated by AiConditionEvaluator.validate, then matched against AiFileContext facts via AiConditionEvaluator.matches to decide file inclusion or exclusion.
- AiConditionGroup supplies child condition lists to AND/OR nodes during condition tree traversal.
- AiFactExtractor.validate checks AiFactCounter regex configuration, then factsBlock emits exact counts for labels over full source text.
- AiOversizeStrategy.fromConfig converts a config token into the build's oversize response, defaulting to FAIL.
- LlamaCppJniConfig and AiPromptSupport are passed to AiGenerationProviderFactory.create to resolve the effective provider key and instantiate an AiGenerationProvider.
- Callers invoke generate or generateWithTimings with an AiGenerationRequest containing prompt key, source file, source text, and current header.
- LlamaCppJniAiGenerationProvider lazily constructs a native LlamaModel, calls chatComplete, then passes the first content through AiCompletionParser to strip thinking blocks.
- OpenAiCompatibleProviderSupport prepares request bodies, endpoints, API keys, and timings for the OpenAI-compatible provider without performing network I/O.
- Results are wrapped in AiGenerationTimings with prompt/cached/completion tokens and decode rates, defaulting to zero rates when timings are absent.

#### Dependencies
- Internal: net.ladenthin.srcmorph.config.AiCondition, AiConditionGroup, AiRangeCondition, AiFileContext, AiFactCounter; net.ladenthin.srcmorph.support.AiSourceExcludeFilter; AiGenerationRequest, AiGenerationTimings, AiPromptSupport, Java8CompatibilityHelper, MockAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, LlamaCppJniProviderSupport.
- External: java.time.Instant, java.time.format.DateTimeParseException, java.util.regex.Pattern/Matcher/PatternSyntaxException, lombok.ToString, org.jspecify.annotations.Nullable; net.ladenthin.llama (LlamaModel, ModelParameters, ChatResponseParser, ChatResponse, Timings, FlashAttn, ReasoningFormat), java.io.IOException, java.util.Objects, java.util.Map, java.util.function.Function, java.lang.AutoCloseable.

#### Cross-cutting
- Nullable fields and parameters use org.jspecify.annotations.Nullable.
- Validation methods throw IllegalArgumentException on misconfiguration; AiConditionEvaluator.matches throws IllegalStateException for malformed branch nodes.
- Defensive copying in AiConditionGroup.setConditions; AiConditionEvaluator and AiFactExtractor are stateless and thread-safe.
- AiOversizeStrategy enum constants and final configValue are immutable and thread-safe.
- Lombok @ToString applied to AiCompletionParser, AiGenerationProviderFactory, LlamaCppJniAiGenerationProvider, and OpenAiCompatibleProviderSupport.
- Null safety via Objects.requireNonNull in constructors and org.jspecify @Nullable on optional returns and parameters.
- IOException declared on generate, generateWithTimings, close, and provider-support methods for I/O or serialization failures.
- IllegalArgumentException thrown by the factory for unrecognized provider keys and by endpoint validation for non-http(s) base URLs.
- AiCompletionParser is stateless and thread-safe; the factory is stateless except an immutable compatibility helper; provider instances hold immutable config and support with only a lazily mutated native model handle.
- LlamaCppJniAiGenerationProvider lazy model initialization is not thread-safe for concurrent first calls and is not designed for concurrent generation on a single instance.
