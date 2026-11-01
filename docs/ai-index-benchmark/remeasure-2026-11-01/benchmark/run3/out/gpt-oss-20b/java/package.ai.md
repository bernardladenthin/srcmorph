### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:08:13Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven‑based AI source‑generation engine that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings for code‑generation tasks.

#### Purpose
- Unified, mutable configuration for AI‑powered source morphing.
- Validate and evaluate AI filter trees against file metadata.
- Extract deterministic fact blocks from source files.
- Define oversize handling strategies for model context limits.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration`, `AiGenerationConfig`, and related data classes expose all runtime parameters.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical condition trees (`AiCondition`, `AiConditionGroup`).
- **Fact Extraction** – `AiFactExtractor` builds fact blocks from regex counters.
- **Oversize Strategy** – `AiOversizeStrategy` enumerates handling modes for oversized source files.
- **Provider Abstraction** – `AiGenerationProvider` interface and concrete implementations (local, mock, OpenAI‑compatible).
- **Factory & Configuration** – `AiGenerationProviderFactory`, `LlamaCppJniConfig`.
- **Local Inference** – `LlamaCppJniAiGenerationProvider` with llama.cpp JNI integration.
- **OpenAI‑compatible Support** – `OpenAiCompatibleProviderSupport` for request/response handling.
- **Completion Parsing** – `AiCompletionParser` to strip internal reasoning blocks.

#### Key units
- `SrcMorphConfiguration` – central mutable config holder.
- `AiGenerationConfig` – AI model and prompt‑generation parameters.
- `AiConditionEvaluator` – recursive evaluator for AI filter trees.
- `AiFactExtractor` – static utility for deterministic fact counting.
- `AiOversizeStrategy` – enum for oversize handling strategies.
- `AiGenerationProvider` – interface for generate/close operations.
- `AiGenerationProviderFactory` – resolves provider key, creates provider instances.
- `LlamaCppJniConfig` – immutable runtime knobs for llama.cpp.
- `LlamaCppJniAiGenerationProvider` – local inference provider.
- `OpenAiCompatibleProviderSupport` – OpenAI‑compatible request/response handling.
- `AiCompletionParser` – removes `THINKING_BLOCK_*` markers from raw completions.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated (Maven, JSON/YAML, or code) and supplies `AiGenerationConfig` and other AI definitions.  
2. **File Selection** – `AiConditionEvaluator` receives an `AiCondition` tree and `AiFileContext` for each source file, returning a match flag and whether line counts are needed.  
3. **Fact Extraction** – For matched files, `AiFactExtractor.factsBlock` generates a deterministic fact string using configured `AiFactCounter`s.  
4. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a provider key; `create` instantiates the provider.  
5. **Request Creation** – `AiGenerationRequest` (prompt, source file, header) is passed to the chosen provider.  
6. **Local Provider** – `LlamaCppJniAiGenerationProvider` builds inference parameters via `AiPromptSupport`, loads `LlamaModel`, runs `chatComplete`, parses JSON with `ChatResponseParser`, and returns text/metrics.  
7. **OpenAI Provider** – `OpenAiCompatibleProviderSupport` serialises request body, resolves endpoint, secures API key, sends HTTP call, receives `ChatResponse`, and computes `AiGenerationTimings`.  
8. **Post‑processing** – `AiCompletionParser.parseCompletion` strips reasoning blocks before persisting the answer.

#### Dependencies
- **Internal** – `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiFactDefinition`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`, Lombok annotations, `org.jspecify.annotations`.

#### Cross‑cutting
- **Thread safety** – Evaluator and extractor utilities are stateless; configuration objects are mutable but intended for single‑threaded use.  
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce structural correctness and regex syntax.  
- **Immutability** – `LlamaCppJniConfig` and provider support classes are immutable, thread‑safe.  
- **Error handling** – Validation errors surface as `IllegalArgumentException` or `IllegalStateException`; I/O or parsing errors throw `IOException`.  
- **Timing metrics** – `AiGenerationTimings` captures token counts and throughput for both local and OpenAI providers.  
- **Logging & warnings** – providers log cache reuse and truncation warnings.  
- **API key security** – `OpenAiCompatibleProviderSupport.apiKeyFor` blocks sending keys over non‑loopback HTTP.
