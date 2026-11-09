### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:58:07Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑style framework that orchestrates AI‑driven source‑code generation, selecting files, extracting deterministic facts, and delegating prompt generation to pluggable AI back‑ends (local llama.cpp, OpenAI‑compatible APIs, or mocks).

#### Purpose
- Orchestrate AI code‑generation jobs from source trees.
- Provide flexible file‑selection, fact extraction, and model configuration.

#### Responsibilities
- **Configuration & Runtime** – `SrcMorphConfiguration` and `AiGenerationConfig` hold all plugin parameters and per‑model flags.
- **File Selection** – `AiConditionEvaluator` validates `AiCondition` trees; `AiConditionGroup` supplies child conditions.
- **Fact Extraction** – `AiFactExtractor` builds deterministic fact blocks from source text.
- **Oversize Handling** – `AiOversizeStrategy` defines how to process files that exceed model context limits.
- **AI Provider Abstraction** – `AiGenerationProvider` interface, `AiGenerationProviderFactory`, and concrete providers (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `MockAiGenerationProvider`).
- **Response Cleaning** – `AiCompletionParser` removes internal reasoning blocks from completions.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for Maven plugin parameters.
- `AiGenerationConfig` – mutable holder for model‑specific generation flags.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies.
- `AiConditionGroup` – defensive container for child `AiCondition` objects.
- `AiGenerationProvider` – interface for generating text and timings.
- `AiGenerationProviderFactory` – resolves provider key → concrete provider.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible HTTP interactions.
- `LlamaCppJniConfig` – immutable config builder for llama.cpp.
- `AiCompletionParser` – removes Gemma‑4 thinking blocks from raw completions.

#### Data flow
1. Maven plugin populates `SrcMorphConfiguration` → `AiGenerationConfig`.
2. `AiConditionEvaluator` filters source files against `AiCondition` trees.
3. For each selected file, `AiFactExtractor.factsBlock` generates fact strings.
4. `AiOversizeStrategy.fromConfig` selects handling for large files.
5. `AiGenerationProviderFactory.create(...)` resolves the runtime provider.
6. Provider generates text (`generate` or `generateWithTimings`), optionally returning `AiGenerationTimings`.
7. `AiCompletionParser` cleans the raw completion before returning to the caller.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`.
- **External** – Lombok (`@ToString`), `org.jspecify.annotations.Nullable`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (native model, JSON parser), `com.fasterxml.jackson` (JSON), HTTP client for OpenAI support, native llama.cpp library.
- **Runtime** – environment variables for API keys, native libraries for llama.cpp.

#### Cross‑cutting
- **Immutability & Thread‑Safety** – `AiConditionEvaluator`, `AiFactExtractor`, `LlamaCppJniConfig`, and provider support classes are stateless/immutable; mutable configuration classes are not thread‑safe.
- **Validation & Error Handling** – `validate` methods throw `IllegalArgumentException`; structural errors throw `IllegalStateException`; I/O or parsing failures propagate `IOException`.
- **Timing Metrics** – `AiGenerationProvider` supplies default zeroed metrics; concrete providers override with real timings.
- **Configuration Resolution** – `effectiveProvider` logic centralizes provider key precedence (run vs. model level).
- **Response Parsing** – `AiCompletionParser` ensures consistent output by stripping internal reasoning blocks across providers.
