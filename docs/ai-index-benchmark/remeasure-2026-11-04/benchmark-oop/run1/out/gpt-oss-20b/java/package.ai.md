### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:27:10Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A unified framework for configuring, validating, and executing AI‑driven source‑code indexing and generation across Maven projects.

#### Purpose
- Central AI‑based source‑code processing engine.
- Validates file‑selection, fact extraction, and oversize handling.
- Provides pluggable AI generation back‑ends (local llama.cpp, OpenAI‑compatible, mock).

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`.
- **AI Generation Layer** – `AiGenerationProvider` interface, concrete providers, factory, request/response plumbing.
- **Parsing & Post‑processing** – `AiCompletionParser`, `OpenAiCompatibleProviderSupport`.
- **Model & Timing Management** – `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`, `AiGenerationTimings`.

#### Key units
- `SrcMorphConfiguration` – mutable bean holding directories, model paths, prompt defs, fact groups, Llama settings.
- `AiConditionEvaluator` – stateless tree evaluator for `AiCondition` objects.
- `AiFactExtractor` – static fact‑block generator using regex counters.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationProvider` – interface for generating text with optional timings.
- `AiGenerationProviderFactory` – resolves provider keys, builds `LlamaCppJniConfig`.
- `LlamaCppJniConfig` – immutable 37‑knob config for llama.cpp.
- `LlamaCppJniAiGenerationProvider` – lazy llama.cpp model loader, chat completion, JSON parsing.
- `OpenAiCompatibleProviderSupport` – request builder, endpoint/API‑key validation, timing extraction.
- `AiCompletionParser` – removes Gemma‑4 thinking blocks from raw completions.

#### Data flow
1. **Setup** – Maven plugin/CLI populates `SrcMorphConfiguration`.
2. **File Routing** – `AiConditionEvaluator` checks each file against condition trees; included files proceed.
3. **Fact Generation** – `AiFactExtractor.factsBlock` creates deterministic fact strings per file.
4. **Oversize Handling** – `AiOversizeStrategy.fromConfig` selects strategy for files exceeding model context.
5. **AI Request** – `AiGenerationRequest` (prompt key, source text, header) is passed to `AiGenerationProviderFactory.effectiveProvider`.
6. **Generation** – Provider’s `generate`/`generateWithTimings` executes:
   - *llama.cpp*: build inference params → native `chatComplete` → JSON parse → `AiCompletionParser` → plain text.
   - *OpenAI‑compatible*: build request body → endpoint/API‑key validation → HTTP call → response parse → timing metrics.
7. **Output** – Plain text answer (or `AiGenerationTimings`) returned to caller.

#### Dependencies
- **Internal** – `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`, `org.jspecify.annotations.Nullable`.
- **Configuration** – `AiGenerationConfig.DEFAULT_*` constants, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross‑cutting
- Immutable configs (`LlamaCppJniConfig`), thread‑safe stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Defensive copying and null handling in configuration setters.
- Consistent error handling: `IllegalArgumentException` for misconfigurations, `IOException` for I/O or endpoint issues.
- Timing metrics (`AiGenerationTimings`) capture prompt/cache token counts and throughput; defaulted to zero when unavailable.
- Lazy model loading in `LlamaCppJniAiGenerationProvider`; subsequent calls reuse cached instance.
