### java
- H: 1.0
- C: 2170A0D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:56:59Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Java library that orchestrates AI‑driven source‑code transformations, from configuration and condition evaluation to provider‑agnostic text generation and oversize‑file handling.

#### Purpose
- Central orchestration of AI‑powered source‑code morphing.
- Configurable file selection, fact extraction, and oversized‑file strategies.
- Pluggable AI back‑ends for generating transformation text.

#### Responsibilities
- **Configuration & orchestration** – `SrcMorphConfiguration`, `AiGenerationConfig`.
- **Condition evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact extraction** – `AiFactExtractor`, `AiFactCounter`.
- **Oversize handling** – `AiOversizeStrategy`.
- **AI provider selection** – `AiGenerationProviderFactory`.
- **Local inference** – `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`.
- **Remote inference** – `OpenAiCompatibleProviderSupport`.
- **Completion parsing** – `AiCompletionParser`.

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven‑style settings.
- `AiGenerationConfig` – bean for model run parameters.
- `AiConditionEvaluator` – stateless tree validator/evaluator.
- `AiFactExtractor` – builds deterministic facts block.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationProvider` – interface for generation APIs.
- `AiGenerationProviderFactory` – static factory for provider instances.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible requests.
- `AiCompletionParser` – strips internal thinking blocks.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp options.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated (Maven/CLI) with prompts, models, and rules.
2. **Condition evaluation** – `AiConditionEvaluator` walks an `AiCondition` tree against an `AiFileContext` to decide file inclusion.
3. **Fact extraction** – `AiFactExtractor` consumes `AiFactCounter` list and source text to produce a facts block.
4. **Provider selection** – `AiGenerationProviderFactory.create(...)` resolves a provider key to an `AiGenerationProvider`.
5. **Generation** – Provider (`LlamaCppJniAiGenerationProvider` or `OpenAiCompatibleProviderSupport`) runs chat completion, returns raw text and timing metrics.
6. **Completion cleaning** – `AiCompletionParser.parseCompletion(...)` removes thinking blocks before the final answer is returned.

#### Dependencies
- **Internal** – `net.ladenthin.srcmorph.config` (conditions, facts, calibration), `net.ladenthin.srcmorph.prompt` (prompt definitions), `net.ladenthin.srcmorph.support` (exclude filter).
- **External** – `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok, Jackson (`com.fasterxml.jackson.databind`), `net.ladenthin.llama` (JNI bindings, JSON parser).
- **Provider‑specific** – `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`.

#### Cross‑cutting
- **Thread safety** – `AiConditionEvaluator`, `AiFactExtractor`, provider factories, and support classes are stateless; configuration beans are mutable.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce non‑null labels/patterns and regex syntax.
- **Error handling** – Misconfigurations throw `IllegalArgumentException`; providers throw `IOException` on failures.
- **Immutability** – `AiOversizeStrategy` enum and `LlamaCppJniConfig` are immutable; configuration lists are unmodifiable.
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache token counts and throughput for both local and remote providers.
- **Builder pattern** – `LlamaCppJniConfig.Builder` offers fluent configuration of llama.cpp knobs.
