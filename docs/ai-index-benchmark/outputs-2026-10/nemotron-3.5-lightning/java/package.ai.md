### java
- H: 1.0
- C: 086884D0
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:11:23Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides AI‑aware source file selection, generation parameter tuning, oversized‑file strategy management, and pluggable AI text generation and completion parsing for srcmorph document workflows.

#### Purpose
- Enables AI‑aware source file configuration, condition evaluation, and generation parameter management for build‑time code transformation.
- Supplies pluggable AI text generation and completion parsing for srcmorph document workflows, including local GGUF model inference and OpenAI‑compatible model interactions.

#### Responsibilities
- AI condition trees are walked and validated against file context to determine file inclusion matches.
- Logical combinators (<and>/<or>) group child AiCondition instances for configuration‑level criteria.
- Exact regex‑based fact blocks are generated from pattern counters over source text.
- AI generation parameters are configured and mutated as mutable JavaBeans for provider integration.
- Oversized source files are assigned handling strategies (fail, sample, mapReduce, deterministic) for build‑time processing.
- AI completion parsing strips thinking blocks from LLM outputs; null input yields empty string, missing end marker throws IOException.
- Provider contract defines generate/generateWithTimings/close; factory resolves effective provider key and dispatches to concrete back‑ends.
- Llama.cpp JNI backend runs GGUF inference with lazy native model initialization.
- OpenAI‑compatible infrastructure builds JSON request bodies, resolves endpoints, validates API keys, and extracts generation timings.

#### Key units
- AiConditionEvaluator: final class that walks AiCondition trees and validates file inclusion matches against AiFileContext.
- AiConditionGroup: class that holds child AiCondition instances as logical <and>/<or> combinators.
- AiFactExtractor: final utility class that generates regex‑based fact blocks from pattern counters over source text.
- AiGenerationConfig: mutable JavaBeans configuration object with many public members for AI generation steps.
- AiOversizeStrategy: enum defining FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC strategies for files exceeding model context windows.
- AiCompletionParser: parses raw LLM completion text; returns empty string for null input, strips thinking blocks, throws IOException if thinking block started without end marker.
- AiGenerationProvider: generation contract interface extending AutoCloseable with generate/generateWithTimings/close.
- AiGenerationProviderFactory: creates providers by name (PROVIDER_MOCK, PROVIDER_LLAMACPP_JNI, PROVIDER_OPENAI_COMPATIBLE); effectiveProvider resolves key.
- LlamaCppJniAiGenerationProvider: runs GGUF model inference via llama.cpp; native LlamaModel handle lazily initialized on first generate call.
- OpenAiCompatibleProviderSupport: final class providing non‑network infrastructure for OpenAI‑compatible model interactions; builds JSON request bodies, resolves endpoints, validates API keys.
- AiGenerationRequest, AiGenerationTimings: data types carrying generation parameters and token/rate metrics.

#### Data flow
- AiCondition trees and AiFileContext enter AiConditionEvaluator.matches/validate; output boolean match or IllegalArgumentException.
- Source text and AiFactCounter lists enter AiFactExtractor.factsBlock; output String facts block or empty string.
- Config parameters flow into AiGenerationConfig setters; output instance with populated fields for AI provider consumption.
- Config strings enter AiOversizeStrategy.fromConfig; output strategy instance (DEFAULT for null/blank, IllegalArgumentException for unrecognized values).
- AiConditionGroup conditions feed into AiConditionEvaluator for tree walking and match evaluation.
- Client code calls AiGenerationProviderFactory.create(providerName, config, promptSupport) → effective key resolution → concrete provider instance.
- Provider's generate(AiGenerationRequest) executes backend inference: LlamaCppJni performs lazy native init then GGUF inference; OpenAiCompatible builds JSON request body, resolves endpoint, validates API key, and processes response with timing extraction.
- AiCompletionParser.parseCompletion(rawText) independently processes raw LLM output: null→empty, trim, strip thinking blocks if present, throw IOException if block started without end marker.
- AiGenerationTimings flows through generateWithTimings with prompt/cached/completion tokens and decode rate.

#### Dependencies
- Internal: AiCondition, AiFileContext, AiConditionGroup, AiRangeCondition, AiFactCounter, AiCalibration, AiCompletionParser, AiGenerationProvider, AiGenerationProviderFactory, LlamaCppJniConfig, OpenAiCompatibleProviderSupport, MockAiGenerationProvider, OpenAiCompatibleAiGenerationProvider, AiGenerationRequest, AiGenerationTimings, AiPromptSupport, Java8CompatibilityHelper.
- External java: java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List, java.util.ArrayList, java.util.regex.Pattern, java.util.regex.Matcher, java.util.regex.PatternSyntaxException.
- External modules: lombok.ToString, org.jspecify.annotations.Nullable, com.fasterxml.jackson.databind.ObjectMapper, net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Usage, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper.
- No SQL tables; all Java types and infrastructure.

#### Cross‑cutting
- @Nullable annotation governs null‑handling across AiFactExtractor, AiGenerationConfig, AiOversizeStrategy, AiCompletionParser.
- IllegalArgumentException thrown by validators for misconfiguration (AiConditionEvaluator, AiFactExtractor, AiOversizeStrategy).
- Defensive copying in AiConditionGroup.setConditions to prevent external mutation of conditions list.
- Thread‑safety: AiConditionEvaluator and AiFactExtractor are stateless and thread‑safe; AiGenerationConfig is fully mutable, not safe for concurrent access; AiConditionGroup has mutable state not thread‑safe by default; AiOversizeStrategy is immutable enum, safe for concurrent access; providers control their own synchronization.
- All validators default null/blank inputs to safe states (DEFAULT for AiOversizeStrategy, empty string for factsBlock).
- @ToString Lombok annotation on classes/fields (with @ToString.Exclude where needed).
- IOException thrown for missing thinking blocks, invalid endpoints, unset API keys, non‑loopback key transmission, and non‑2xx HTTP statuses.
- Switch/dispatch on effective provider key for backend dispatch.
- Constructor patterns requiring non‑null config and promptSupport; final fields where applicable.
- Default no‑op close() and delegating generateWithTimings implementations.
- JSON serialization via ObjectMapper for request bodies.
- Pattern‑based endpoint validation (http(s) scheme, trailing slash stripping).
- Loopback host checking (localhost, 127.x.x.x, [::1]) for API key transmission safety.
