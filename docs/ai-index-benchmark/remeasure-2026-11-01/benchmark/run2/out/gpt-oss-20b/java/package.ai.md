### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:48:13Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework for configuring, validating, and executing AI‑driven source‑code transformations in a Maven‑based code‑morphing tool.

#### Purpose
- Central AI configuration and execution engine for source‑code morphing.
- Validates transformation rules, handles oversized files, and orchestrates AI generation.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates AI provider, model, prompt, and fact settings.
- **Rule Validation & Evaluation** – `AiConditionEvaluator` checks and evaluates filter trees against file contexts.
- **Fact Extraction** – `AiFactExtractor` builds deterministic facts headers for oversized files.
- **Oversize Handling** – `AiOversizeStrategy` defines modes (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- **AI Generation** – `AiGenerationProvider` hierarchy supplies text from local llama.cpp, OpenAI‑compatible APIs, or mocks, with timing and response parsing.

#### Key units
- `SrcMorphConfiguration` – mutable bean for all runtime AI settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – deterministic facts block generator.
- `AiOversizeStrategy` – enum of oversize handling modes.
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – resolves provider keys to concrete providers.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference with lazy model loading.
- `OpenAiCompatibleProviderSupport` – remote API request builder, endpoint/key resolution, timing extraction.
- `LlamaCppJniConfig` – immutable config for local inference.
- `AiCompletionParser` – strips internal reasoning blocks from LLM output.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated via Maven/JSON/YAML or code.
2. **Routing** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees against `AiFileContext` to decide file processing.
3. **Fact Generation** – For oversized files, `AiFactExtractor` creates a facts header using configured regex counters.
4. **Provider Selection** – `AiGenerationProviderFactory` resolves the provider key (run‑level or model‑level) and returns an `AiGenerationProvider`.
5. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` loads the GGUF model, runs chat completion, parses JSON, extracts text and timing, then passes through `AiCompletionParser`.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds the JSON body, resolves endpoint/key, sends HTTP request, receives `ChatResponse`, extracts text and timing, then passes through `AiCompletionParser`.
6. **Output** – `AiGenerationTimings` (text, token counts, throughput) is returned to the caller for further transformation.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `ChatResponseParser`, `ChatResponse`, `Usage`, `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.
- **External** – Lombok (`@ToString`), JSPECIFY (`@Nullable`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` JNI bindings, Jackson (`com.fasterxml.jackson`), Java standard libraries (`java.util`, `java.time`, `java.io`, `java.util.regex`, `java.net.URL`).

#### Cross-cutting
- **Thread safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless; `LlamaCppJniConfig` and provider support classes are immutable.
- **Validation** – `AiConditionEvaluator.validate` ensures logical groups have children; `AiFactExtractor.validate` checks regex patterns.
- **Error handling** – Misconfigurations raise `IllegalArgumentException`/`IllegalStateException`; IO or HTTP errors propagate as `IOException`.
- **Timing metrics** – `AiGenerationTimings` captures generated text, token counts, and throughput; providers expose `generateWithTimings`.
- **Prompt templating** – `AiPromptSupport` supplies system/user messages; providers delegate template construction to it.
- **Logging & warnings** – Local provider logs truncation and cache reuse; remote provider validates loopback key usage.
