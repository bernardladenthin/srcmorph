### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:32:56Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code morphing by evaluating file‑selection rules, extracting deterministic facts, and delegating generation to pluggable LLM providers.

#### Purpose
- Centralize AI‑powered source‑morphing configuration and orchestration.
- Provide a pluggable generation subsystem for local and cloud LLMs.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates directories, file filters, provider details, and prompt/model definitions.
- **Rule Evaluation & Oversize Routing** – `AiConditionEvaluator` and `AiOversizeStrategy` decide which files are processed and how oversized files are handled.
- **Fact Extraction** – `AiFactExtractor` builds deterministic fact blocks from source text using regex counters.
- **Provider Abstraction** – `AiGenerationProviderFactory` selects and instantiates the appropriate `AiGenerationProvider` (mock, llama.cpp JNI, OpenAI‑compatible).
- **Request & Response Handling** – `OpenAiCompatibleProviderSupport` builds JSON payloads, validates endpoints, and parses HTTP responses; `AiCompletionParser` cleans LLM outputs.

#### Key units
- `SrcMorphConfiguration` – mutable bean holding all runtime settings.
- `AiConditionEvaluator` – stateless validator/evaluator for logical condition trees.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) controlling oversized‑file handling.
- `AiFactExtractor` – utility that compiles fact counters into a formatted block.
- `AiGenerationProvider` (interface) – contract for `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – resolves the concrete provider implementation.
- `LlamaCppJniAiGenerationProvider` – llama.cpp JNI implementation with lazy model loading and timing extraction.
- `OpenAiCompatibleAiGenerationProvider` – HTTP chat completion wrapper.
- `AiCompletionParser` – strips internal thinking blocks from completions.
- `OpenAiCompatibleProviderSupport` – JSON builder, endpoint/key validation, error formatting, timing calculation.

#### Data flow
1. **Load Configuration** – `SrcMorphConfiguration` is populated from Maven, CLI, or JSON/YAML.
2. **File Selection** – `AiConditionEvaluator` checks each file against the configured `AiCondition` tree; oversized files are routed per `AiOversizeStrategy`.
3. **Fact Generation** – `AiFactExtractor` processes source text with `AiFactCounter` regexes to produce a facts block.
4. **Provider Selection** – `AiGenerationProviderFactory.create` chooses the provider based on runtime or model config.
5. **Generation** – The chosen provider’s `generate` method consumes an `AiGenerationRequest` (prompt key, source file, text) and returns raw completion.
6. **Post‑processing** – `AiCompletionParser.parseCompletion` removes thinking blocks; the cleaned text is persisted or used for transformation.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- **External** – `java.time`, `java.util.regex`, `org.jspecify.annotations`, Lombok, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (native bindings), `com.fasterxml.jackson` (JSON), HTTP client for OpenAI provider.

#### Cross‑cutting
- **Thread Safety** – All utilities (`AiConditionEvaluator`, `AiFactExtractor`, `OpenAiCompatibleProviderSupport`) are stateless; configuration objects (`LlamaCppJniConfig`) are immutable.
- **Validation & Error Handling** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce correctness; provider failures surface as `IOException`; malformed responses are wrapped with descriptive messages.
- **Performance** – `LlamaCppJniAiGenerationProvider` lazily loads the model; timing extraction is built into the provider interface.
- **Extensibility** – New providers can be added by implementing `AiGenerationProvider` and registering in the factory; new fact counters or conditions are added via configuration beans.
