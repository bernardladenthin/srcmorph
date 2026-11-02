### java
- H: 1.0
- C: 55485E7D
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:43:32Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑based AI source‑code transformation engine that selects files, extracts deterministic facts, and orchestrates local or remote LLM generation with fine‑grained configuration.

#### Purpose
- Provide a configurable AI‑powered source‑code morphing pipeline.
- Validate file‑level conditions, handle oversized inputs, and generate deterministic fact headers.
- Abstract multiple AI back‑ends (local llama.cpp, OpenAI‑compatible APIs, mocks) with consistent request/response handling and timing metrics.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `AiGenerationConfig`.
- **File Selection & Fact Extraction** – evaluate `AiCondition` trees against `AiFileContext`, count regex matches, build facts blocks.
- **Oversize Handling** – decide between FAIL, SAMPLE, MAP_REDUCE, or DETERMINISTIC strategies.
- **Provider Abstraction** – `AiGenerationProvider`, `AiGenerationProviderFactory`, concrete providers (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Request/Response Processing** – `AiCompletionParser`, `OpenAiCompatibleProviderSupport`, `AiPromptSupport`.
- **Timing & Metrics** – `AiGenerationTimings` captures token counts, TPS, and predicted metrics.

#### Key units
- **`SrcMorphConfiguration`** – mutable bean holding paths, providers, prompts, models, field rules, and facts.
- **`AiConditionEvaluator`** – stateless validator/evaluator for `AiCondition` trees.
- **`AiFactExtractor`** – builds deterministic facts header from regex counters.
- **`AiOversizeStrategy`** – enum of oversize handling modes.
- **`AiGenerationConfig`** – JavaBean with AI provider parameters for a run.
- **`AiGenerationProvider`** – interface for generating text and optional timings.
- **`AiGenerationProviderFactory`** – resolves provider keys and creates instances.
- **`LlamaCppJniConfig`** – immutable llama.cpp settings.
- **`LlamaCppJniAiGenerationProvider`** – local inference provider using `LlamaModel`.
- **`OpenAiCompatibleProviderSupport`** – helper for OpenAI‑compatible chat‑completion calls.
- **`AiCompletionParser`** – removes leading thinking blocks from raw completions.

#### Data flow
1. **Load configuration** → `SrcMorphConfiguration` (Maven plugin, Jackson, or code).
2. **Validate conditions** → `AiConditionEvaluator.validate()`.
3. **Match files** → `AiConditionEvaluator.matches()` decides processing eligibility.
4. **Handle oversize** → `AiOversizeStrategy` selects strategy; if deterministic, `AiFactExtractor.factsBlock()` creates header.
5. **Build request** → `AiGenerationRequest` (prompt key, source text, header).
6. **Select provider** → `AiGenerationProviderFactory.effectiveProvider()` → concrete provider.
7. **Generate** → provider’s `generate()` or `generateWithTimings()`.
   - *Local*: load `LlamaModel`, call `chatComplete`, parse JSON, extract text and timing.
   - *Remote*: build HTTP body, resolve endpoint, send request, parse response, extract timing.
8. **Post‑process** → `AiCompletionParser.parseCompletion()` removes any leading thinking block.
9. **Persist or return** transformed source.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External**: `java.time`, `java.util.regex`, `lombok`, `org.jspecify.annotations`, `net.ladenthin.llama` (JNI model, parameters, JSON parser), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `java.util`.
- **Provider‑specific**: llama.cpp JNI libraries, OpenAI‑compatible HTTP APIs, mock implementations.

#### Cross‑cutting
- **Thread‑Safety**: `AiConditionEvaluator`, `AiFactExtractor`, `LlamaCppJniConfig`, and provider support classes are stateless/immutable and thread‑safe.
- **Validation & Defaults**: Configuration beans expose `validate()`; defaults are applied when fields are `null` or empty.
- **Error Handling**: Configuration errors → `IllegalArgumentException`/`IllegalStateException`; parsing errors → `PatternSyntaxException`, `DateTimeParseException`; provider errors → `IOException`, `IllegalArgumentException`.
- **Timing Instrumentation**: `AiGenerationTimings` records prompt/cache tokens, TPS, and predicted metrics for both local and remote providers.
- **Logging & Warnings**: Providers log truncation, cache reuse; `AiCompletionParser` warns on malformed thinking blocks.
