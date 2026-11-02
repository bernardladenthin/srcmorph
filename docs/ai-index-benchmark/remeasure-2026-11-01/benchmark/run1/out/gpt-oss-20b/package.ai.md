### ai
- H: 1.0
- C: 8A065534
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:30:01Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A Maven‑based AI code‑generation framework that evaluates source files against configurable rules, extracts deterministic facts, and delegates text generation to pluggable local or remote AI back‑ends.

#### Purpose
- Orchestrate AI‑driven source‑code analysis and generation within a Maven build.
- Provide a unified, configurable pipeline from file selection to AI output.

#### Responsibilities
- **Configuration & Validation** – runtime beans for directories, providers, prompts, models, and field rules.
- **Rule Evaluation** – stateless tree validator for source‑file conditions.
- **Fact Extraction** – deterministic fact blocks from regex counters.
- **Prompt & Model Handling** – building chat requests from prompt definitions.
- **AI Generation** – pluggable providers (local llama.cpp JNI, OpenAI‑compatible HTTP).
- **Output Cleaning & Timing** – stripping internal reasoning and capturing metrics.

#### Key units
- `SrcMorphConfiguration` – bean for directories, providers, prompts, models, field rules.
- `AiGenerationConfig` – per‑generation settings (model path, context, temperature, GPU layers, caching, stop strings, dry‑run).
- `AiOversizeStrategy` – enum for handling oversized files (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – builds deterministic fact blocks from regex counters.
- `AiPromptSupport` – merges prompt definitions with facts and source context.
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider keys to concrete providers.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI provider with lazy model load and timing extraction.
- `OpenAiCompatibleProviderSupport` – remote API support with endpoint/key validation and timing.
- `AiCompletionParser` – removes internal reasoning blocks from raw completions.

#### Data flow
1. **Setup** – `SrcMorphConfiguration` populated via Maven, JSON/YAML, or code.
2. **File Routing** – `AiConditionEvaluator` filters source files.
3. **Fact Block** – `AiFactExtractor` produces deterministic facts.
4. **Prompt Construction** – `AiPromptSupport` builds chat requests.
5. **Generation Request** – sent to `AiGenerationProviderFactory`.
6. **Provider Selection** – factory resolves key → `LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport`.
7. **AI Call** – provider builds request, invokes native or HTTP API, parses response, extracts text and timings.
8. **Post‑processing** – `AiCompletionParser` removes reasoning; result returned.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `LlamaCppJniConfig`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`, `org.jspecify.annotations`, Lombok (`@ToString`, `@EqualsAndHashCode`).
- **Configuration Sources** – Maven, JSON/YAML, code.

#### Cross‑cutting
- Immutability & thread‑safety in provider configs and factory; stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Validation throws `IllegalArgumentException`/`IllegalStateException`; providers throw `IOException`.
- Timing metrics captured by `AiGenerationTimings`.
- Builder pattern for `LlamaCppJniConfig`; mutable beans for runtime setup.
- Local provider’s lazy model load is not synchronized; remote provider is stateless and thread‑safe.
