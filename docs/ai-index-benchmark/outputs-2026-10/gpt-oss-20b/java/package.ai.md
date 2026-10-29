### java
- H: 1.0
- C: DA147B25
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:53:30Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A unified, configuration‑driven AI engine that validates source‑code files, extracts deterministic facts, and orchestrates text generation via pluggable local or cloud providers for code transformation tasks.

#### Purpose
- Centralize AI‑based source‑code processing configuration and orchestration.
- Provide a pluggable generation subsystem for local, cloud, or mock AI back‑ends.

#### Responsibilities
- **Configuration Management** – load, validate, and expose runtime settings (`SrcMorphConfiguration`).
- **File Selection & Fact Extraction** – evaluate logical conditions (`AiConditionEvaluator`) and prepend deterministic fact blocks (`AiFactExtractor`) for oversized files.
- **Oversize Handling** – decide on failure, sampling, map‑reduce, or deterministic processing (`AiOversizeStrategy`).
- **AI Generation** – build chat requests, select providers, execute local or remote inference, parse responses, and report timings (`AiGenerationProvider` hierarchy).

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean holding directories, provider keys, model paths, and generation defaults.
- `AiConditionEvaluator` / `AiConditionGroup` – stateless tree validator/evaluator for file‑selection logic.
- `AiFactExtractor` – produces a deterministic facts header from `AiFactCounter` lists.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationProvider` interface – contract for `generate` and `generateWithTimings`.
- `AiGenerationProviderFactory` – resolves provider key to concrete implementation.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference with lazy model loading and native timing extraction.
- `OpenAiCompatibleProviderSupport` – helper for building OpenAI‑compatible requests and computing throughput.
- `AiCompletionParser` – strips internal reasoning blocks from raw completions.
- `LlamaCppJniConfig` – immutable configuration holder for the llama.cpp provider.

#### Data flow
1. **Configuration Load** → `SrcMorphConfiguration` populated via binding frameworks.  
2. **File Selection** → `AiConditionEvaluator` evaluates `AiCondition` tree against `AiFileContext`.  
3. **Fact Generation** (if oversized) → `AiFactExtractor.factsBlock` creates deterministic facts header.  
4. **Prompt Construction** → facts header + user prompt → `AiGenerationRequest`.  
5. **Provider Resolution** → `AiGenerationProviderFactory` selects concrete provider.  
6. **Generation** → provider builds chat request (via `OpenAiCompatibleProviderSupport` or native logic), sends it, receives raw completion.  
7. **Response Parsing** → `AiCompletionParser` extracts clean answer.  
8. **Timings** → `AiGenerationTimings` (token counts, throughput) returned alongside the answer.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationRequest`, `AiGenerationTimings`.  
- **External**: `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations`, `lombok.ToString`, `net.ladenthin.llama` (model, JNI), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`.  
- **AI Providers**: configured via `AiGenerationConfig` (model path, remote URL, API key env, etc.).

#### Cross‑cutting
- **Thread Safety**: `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `LlamaCppJniConfig`, and provider support classes are stateless/immutable and thread‑safe.  
- **Validation & Error Handling**: `AiConditionEvaluator.validate`, `AiFactExtractor.validate` enforce well‑formed inputs; misconfigurations raise `IllegalArgumentException`; runtime failures propagate `IOException` or `IllegalStateException`.  
- **Immutability**: `AiConditionGroup` defensively copies collections; `AiOversizeStrategy` is an enum.  
- **Timing & Metrics**: all providers expose `generateWithTimings`; local provider extracts native timings, remote provider computes decode rate from usage data.  
- **Resource Management**: providers implement `AutoCloseable`; local provider releases native `LlamaModel` on `close`.  
- **Configuration Defaults**: `AiGenerationConfig` supplies sensible defaults for all AI parameters; `SrcMorphConfiguration` provides defaults for directories, provider, and model settings.
