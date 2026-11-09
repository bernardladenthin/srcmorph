### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:03:19Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑based framework that configures, evaluates, and drives AI‑powered source‑code generation and transformation, supporting multiple back‑ends and runtime decision‑making for file selection and context handling.

#### Purpose
- Centralize AI generation settings and runtime decision logic for source‑code transformation.
- Abstract multiple AI back‑ends (mock, llama.cpp JNI, OpenAI‑compatible) into a unified provider API.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` and `AiGenerationConfig` expose mutable beans for Maven, JSON/YAML, or programmatic use.
- **Condition Evaluation & Fact Extraction** – `AiConditionEvaluator`, `AiConditionGroup`, `AiFactExtractor`, and related counters compute deterministic facts and decide file inclusion.
- **Oversize Handling** – `AiOversizeStrategy` selects how to process files exceeding the model context window.
- **Provider Abstraction** – `AiGenerationProvider`, `AiGenerationProviderFactory`, and concrete providers (`LlamaCppJniAiGenerationProvider`, OpenAI‑compatible support) generate text from prompts.
- **Request & Timing Support** – `AiPromptSupport`, `AiCompletionParser`, and `AiGenerationTimings` manage prompt rendering, post‑processing, and metric collection.

#### Key units
- `SrcMorphConfiguration` – runtime parameters (directories, providers, prompts, models, etc.).
- `AiGenerationConfig` – per‑step AI generation settings (model path, sampling, retry, stop strings).
- `AiConditionEvaluator` – stateless validator/matcher for `AiCondition` trees.
- `AiFactExtractor` – builds fact blocks from `AiFactCounter` configurations.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationProvider` – interface for text generation.
- `AiGenerationProviderFactory` – resolves provider key and constructs provider.
- `LlamaCppJniAiGenerationProvider` – local GGUF inference via llama.cpp JNI.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible HTTP calls.
- `AiCompletionParser` – strips internal thinking blocks from completions.
- `AiGenerationTimings` – holds generated text and timing metrics.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated (Maven plugin, JSON/YAML, or code).
2. **Condition Setup** – `AiCondition` trees are defined within the configuration.
3. **File Selection** – `AiConditionEvaluator` receives a file’s `AiFileContext` and evaluates the tree to decide inclusion and whether line counting is needed.
4. **Oversize Decision** – If a file exceeds the context window, `AiOversizeStrategy` determines the handling path.
5. **Fact Extraction** – `AiFactExtractor` processes source text with configured `AiFactCounter`s to produce a facts block for AI prompts.
6. **Request Creation** – Client builds an `AiGenerationRequest` (prompt key, source file/text, header).
7. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the key; `create` returns the chosen provider.
8. **Generation** – Provider’s `generate` or `generateWithTimings` is invoked:
   - **llama.cpp**: lazy model load → inference → JSON parse → optional timing extraction.
   - **OpenAI‑compatible**: build JSON body → HTTP call → parse response → timing extraction.
9. **Post‑processing** – `AiCompletionParser` removes internal thinking blocks if present.
10. **Result** – `AiGenerationTimings` (or plain text) returned to caller.

#### Dependencies
- **Internal** – `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationRequest`.
- **External** – `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), Maven plugin APIs, `net.ladenthin.llama` (model, parameters, JSON parser), Jackson `ObjectMapper`.

#### Cross‑cutting
- Defensive copying in `AiConditionGroup` and `SrcMorphConfiguration`.
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Immutable configuration builders (`LlamaCppJniConfig`).
- Lazy initialization of llama.cpp model (not thread‑safe; external coordination required).
- Consistent null handling and error reporting (`IllegalArgumentException`, `IllegalStateException`, `IOException`).
- Environment‑based credential lookup for OpenAI‑compatible provider.
- Logging of truncated answers and prompt‑cache reuse.
