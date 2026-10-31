### java
- H: 1.0
- C: 9B496A0B
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:11:36Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Java framework that orchestrates AI‑driven source‑code transformations, providing configurable routing, fact extraction, and a pluggable generation backend for the srcmorph tool.

#### Purpose
- Central configuration and orchestration of AI‑based source‑code morphing.
- Unified AI generation interface supporting local, cloud, and mock providers.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`.
- **Routing & Fact Generation** – evaluate file‑level conditions, build deterministic fact blocks, decide oversize handling.
- **AI Generation Abstraction** – `AiGenerationProvider`, `AiGenerationProviderFactory`, provider implementations (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleProviderSupport`, `MockAiGenerationProvider`).
- **Request/Response Handling** – build prompts, parse responses, strip internal reasoning, capture timings.

#### Key units
- `SrcMorphConfiguration` – mutable bean for a full srcmorph run.
- `AiGenerationConfig` – per‑generation parameters (model, temperature, etc.).
- `AiConditionEvaluator` – stateless validator/evaluator for `AiConditionGroup` trees.
- `AiFactExtractor` – static utility to produce fact strings from regex counters.
- `AiOversizeStrategy` – enum (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider keys and builds instances.
- `LlamaCppJniConfig` – immutable config builder for llama.cpp.
- `LlamaCppJniAiGenerationProvider` – local JNI provider implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible APIs.
- `AiCompletionParser` – removes internal reasoning blocks from model output.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated from Maven/JSON/YAML or code.
2. **File Routing** – `AiConditionEvaluator` checks `AiConditionGroup` trees to decide inclusion and line‑count needs.
3. **Fact Generation** – `AiFactExtractor.factsBlock` creates deterministic fact strings when required.
4. **Oversize Handling** – `AiOversizeStrategy` dictates how oversized files are processed before generation.
5. **Generation Request** – `AiGenerationRequest` (prompt key, source text, header) is passed to a provider.
6. **Provider Selection** – `AiGenerationProviderFactory` chooses the concrete provider based on config.
7. **Generation** –  
   - *Local*: provider builds chat prompt, calls `LlamaModel.chatComplete`, parses JSON, extracts answer via `AiCompletionParser`.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds HTTP request, sends it, parses `ChatResponse`, extracts answer.  
8. **Timing** – `generateWithTimings` wraps raw text with `AiGenerationTimings`, including token counts and decode rates.
9. **Output** – cleaned answer string (or timings object) returned to the caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `ChatResponseParser`, `ChatResponse`, `LlamaModel`, `LlamaCppJniConfig`.
- **External** – Lombok, `org.jspecify.annotations`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (JNI), Jackson (`com.fasterxml.jackson`), `java.net`, `java.io`.
- **Configuration Classes** – `AiPromptDefinition`, `AiModelDefinition`, `AiFactDefinition`, `AiFieldGenerationConfig`, `AiCalibration`.

#### Cross‑cutting
- **Immutability & Thread‑Safety** – `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`, `LlamaCppJniConfig` are stateless/immutable; providers are stateless except for the lazy‑loaded native model.
- **Validation & Error Handling** – `validate` methods throw `IllegalArgumentException`; IO and JSON errors are wrapped in `IOException`; misconfigurations raise `IllegalStateException`.
- **Logging & Diagnostics** – Lombok `@ToString` provides readable state dumps; `AiGenerationTimings` captures token counts and decode rates for performance analysis.
- **Configuration Normalization** – setters coerce nulls to defaults and expose unmodifiable lists.
