### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:34:29Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Java framework that orchestrates AI‑driven source‑code generation, from configuration and rule evaluation to provider‑agnostic model execution and response handling.

#### Purpose
- Centralize AI‑powered code transformation workflows.
- Manage configuration, file‑selection logic, fact extraction, and oversized‑file strategies.
- Abstract multiple AI back‑ends (local llama.cpp, OpenAI‑compatible services) and provide unified request/response handling.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`, `AiConditionEvaluator`.
- **Rule & Fact Processing** – `AiConditionGroup`, `AiFactExtractor`.
- **Provider Management** – `AiGenerationProviderFactory`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`.
- **Response Parsing** – `AiCompletionParser`.
- **Performance & Error Handling** – `AiGenerationTimings`, exception handling across providers.

#### Key units
- `SrcMorphConfiguration` – mutable bean aggregating paths, filters, AI provider, model definitions, prompts, field rules, fact groups.
- `AiGenerationConfig` – immutable bean with provider parameters (model path, context size, sampling, GPU/offload, cache, DRY, etc.).
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) selecting oversize handling logic.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees against `AiFileContext`.
- `AiFactExtractor` – builds deterministic facts block from regex counters.
- `AiGenerationProviderFactory` – resolves provider key and instantiates `AiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI implementation.
- `OpenAiCompatibleProviderSupport` – HTTP client for OpenAI‑compatible APIs.
- `AiCompletionParser` – strips internal reasoning blocks from LLM outputs.
- `LlamaCppJniConfig` – immutable builder‑style record for llama.cpp parameters.
- `AiGenerationTimings` – captures latency metrics for local and remote calls.

#### Data flow
1. **Setup**: `SrcMorphConfiguration` is populated (Maven, JSON/YAML, or programmatically).
2. **Rule Validation**: `AiConditionEvaluator.validate` checks the condition tree; `AiConditionEvaluator.evaluate` applies it to each file via `AiFileContext`.
3. **Fact Header**: `AiFactExtractor.factsBlock` generates a deterministic facts block for oversized files.
4. **Oversize Decision**: `AiOversizeStrategy.fromConfig` selects handling logic for files exceeding the model context window.
5. **Provider Selection**: `AiGenerationProviderFactory.create` resolves the effective provider key and returns an `AiGenerationProvider`.
6. **Request Construction**: Caller builds an `AiGenerationRequest` (prompt key, source file, text, header).
7. **Generation**:
   - *Local*: `LlamaCppJniAiGenerationProvider.generate` loads the GGUF model, runs `chatComplete`, parses JSON, returns text and timings.
   - *Remote*: `OpenAiCompatibleProviderSupport` builds the HTTP body, sends the request, parses `ChatResponse`, and records timings.
8. **Post‑processing**: `AiCompletionParser.parseCompletion` removes reasoning blocks, yielding the final answer.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- **External**: `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`, `net.ladenthin.llama` (model, args, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`.

#### Cross‑cutting
- **Thread safety**: `AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`, and immutable config objects (`LlamaCppJniConfig`) are stateless and thread‑safe.
- **Validation**: Structural checks in `AiConditionEvaluator.validate` and regex validation in `AiFactExtractor.validate`.
- **Immutability**: `AiGenerationConfig` exposes unmodifiable lists for DRY breakers and stop strings.
- **Error handling**: `IllegalArgumentException` for misconfigurations; `IllegalStateException` for node conflicts; `IOException` for provider‑level issues.
- **Performance metrics**: `AiGenerationTimings` captures latency for both local and remote providers, enabling profiling and optimization.
