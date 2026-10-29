### java
- H: 1.0
- C: 6E4AB334
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:48:15Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> AI configuration and provider orchestration for source-aware generation, selecting eligible files, setting model and prompt policy, and executing local or OpenAI-compatible inference.

#### Purpose
- Defines the policy and metadata used to decide which source files are eligible for AI generation and how oversized inputs are handled.
- Executes generation requests through a provider abstraction that supports native llama.cpp/JNI and OpenAI-compatible HTTP backends.

#### Responsibilities
- Configuration and validation — `AiCondition`, `AiConditionGroup`, `AiConditionEvaluator`, `AiGenerationConfig`, and `SrcMorphConfiguration` define file filters, model/prompt settings, and run-scoped policy.
- Source facts and filtering — `AiFactExtractor`, `AiFactCounter`, and `AiSourceExcludeFilter` summarize metadata and evaluate inclusion rules against file context.
- Oversized-input policy — `AiOversizeStrategy` normalizes config values such as `sample` and `mapReduce` into handling strategies for large files.
- Provider selection and execution — `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, and `OpenAiCompatibleProviderSupport` choose and run the active backend.
- Completion cleanup — `AiCompletionParser` strips leading reasoning markers so final output is stored cleanly.

#### Key units
- `SrcMorphConfiguration` — root mutable JavaBean for scan scope, file filters, generation provider, model prompts, and llama.cpp defaults.
- `AiGenerationConfig` — per-request generation settings for model, prompt, sampling, cache, batching, provider, and calibration.
- `AiConditionEvaluator` — validates condition-tree structure and evaluates whether a file matches configured `AiCondition` logic and range constraints.
- `AiConditionGroup` — nullable group container for `and`/`or` condition children with defensive-copy semantics.
- `AiFactExtractor` — compiles regex counters and emits compact `label: count` facts blocks used by prompts.
- `AiOversizeStrategy` — enum policy for `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling of oversized inputs.
- `AiGenerationProvider` — contract for generation, timing, and close behavior across providers.
- `AiGenerationProviderFactory` — resolves the effective provider key and instantiates the correct backend.
- `LlamaCppJniAiGenerationProvider` — lazy-loads the native model, builds inference parameters, and returns parsed completion text and timings.
- `OpenAiCompatibleProviderSupport` — prepares HTTP chat-completions requests, validates endpoint/key configuration, and formats errors and timing metadata.
- `AiCompletionParser` — removes leading Gemma reasoning markers before final answer text is returned.
- `AiGenerationRequest` — carrier for prompt key, source file, source text, and header context.
- `AiGenerationTimings` — timing metadata for prompt/prediction duration and token usage.

#### Data flow
- Configuration assembly — callers populate `SrcMorphConfiguration` and nested `AiGenerationConfig` objects via setters and JavaBean-style binding.
- File eligibility evaluation — `AiCondition` trees are validated by `AiConditionEvaluator.validate(...)` and matched against `AiFileContext` values such as filename, extension, size, line count, modified time, and relative path.
- Facts generation — configured counters and source text are passed to `AiFactExtractor.factsBlock(...)`, producing a compact facts summary for prompt construction.
- Oversize handling — `AiOversizeStrategy.fromConfig(...)` resolves config tokens to the canonical strategy used for oversized files.
- Provider selection — `AiGenerationProviderFactory.create(...)` chooses the backend, defaulting to `MockAiGenerationProvider` when needed.
- Native inference — `LlamaCppJniAiGenerationProvider.generate(...)` builds inference parameters from `AiPromptSupport`, loads a native `LlamaModel`, and executes `chatComplete(...)`.
- HTTP generation — `OpenAiCompatibleProviderSupport.requestBody(...)` creates the chat-completions payload, validates URL/key requirements, and converts HTTP results into errors or timing metadata.
- Final output cleanup — `ChatResponseParser` and `AiCompletionParser.parseCompletion(...)` strip reasoning markers before returning final answer text and timings.

#### Dependencies
- Internal collaborations:
  - `config` types feed `provider` execution: `SrcMorphConfiguration` and `AiGenerationConfig` shape provider input; `AiGenerationRequest` carries prompt and source context.
  - `AiConditionEvaluator` depends on `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, and `AiSourceExcludeFilter`.
  - `AiFactExtractor` consumes regex fact counters and source text.
  - Provider code depends on `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, and `Java8CompatibilityHelper`.
- External modules / libraries:
  - `java.time.Instant`, `java.util.*`, `java.io.File`, `java.net.URL`, `java.util.regex.*`, `java.util.function.Function`
  - `org.jspecify.annotations.Nullable`
  - Lombok `@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`
  - Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`
  - Maven/Plexus-style JavaBean binding assumptions
  - Native `net.ladenthin.llama.*` interfaces for `LlamaModel`, `ChatResponse`, `Timings`, and model parameters
  - `llama.cpp` runtime configuration surfaced in `SrcMorphConfiguration`

#### Cross-cutting
- Mutable configuration pattern — `AiGenerationConfig`, `SrcMorphConfiguration`, and `AiConditionGroup` are writable JavaBeans; no synchronization or immutability is enforced.
- Validation-first design — structural checks, regex validation, URL scheme checks, key/host validation, and explicit `IllegalArgumentException` / `IllegalStateException` failures are used consistently.
- Null and blank normalization — provider names, config strings, and environment values are trimmed and defaulted; blank inputs fall back to mock or default values.
- Defensive copying and unmodifiable views — `AiConditionGroup.setConditions(...)` copies input collections; config list getters expose unmodifiable views.
- Stateless utility behavior — `AiConditionEvaluator` and `AiFactExtractor` function as stateless validators/formatters, with explicit error handling rather than central exception handling.
- Lazy native lifecycle — `LlamaCppJniAiGenerationProvider` caches a single `LlamaModel` and closes it only when present; no explicit locking is used.
- Timing and diagnostics — provider methods return zeroed `AiGenerationTimings` when unavailable and truncate HTTP error bodies before formatting failures; config classes use Lombok `@ToString` for readable introspection.
