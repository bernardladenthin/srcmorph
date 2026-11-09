### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:56:34Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A Maven plugin framework that orchestrates AI‑driven source‑code generation, validation, and file‑selection through configurable prompts, models, and pluggable inference backends.

#### Purpose
- Centralize AI generation parameters and file‑selection logic for the srcmorph Maven plugin.
- Provide a unified, pluggable provider interface for local llama.cpp, mock, and OpenAI‑compatible backends.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` & `AiGenerationConfig` store plugin‑level and per‑step settings.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates file‑selection trees (`AiCondition`, `AiConditionGroup`).
- **Fact Extraction** – `AiFactExtractor` builds deterministic fact blocks from source text.
- **Oversize Handling** – `AiOversizeStrategy` selects how to process files exceeding the model context window.
- **Provider Abstraction** – `AiGenerationProvider` defines the generation contract; `AiGenerationProviderFactory` resolves the concrete implementation.
- **Local Inference** – `LlamaCppJniAiGenerationProvider` loads GGUF models, applies chat templates, and parses completions.
- **Remote Inference** – `OpenAiCompatibleProviderSupport` constructs HTTP requests, validates endpoints, secures API keys, and interprets responses.
- **Parsing Utilities** – `AiCompletionParser` removes internal thinking blocks from raw completions.

#### Key units
- `SrcMorphConfiguration` – plugin‑level settings (directories, prompts, models, llama options).
- `AiGenerationConfig` – mutable bean for a single AI generation step (model path, sampling, retry, stop strings, etc.).
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – utility for counting regex matches and assembling fact blocks.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with parsing logic.
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider key and creates the appropriate provider.
- `LlamaCppJniAiGenerationProvider` – lazy model loading, chat completion, timing extraction.
- `OpenAiCompatibleProviderSupport` – HTTP request construction, endpoint validation, API key handling, timing calculation.
- `LlamaCppJniConfig.Builder` – fluent builder for 37 llama.cpp parameters.
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from raw completions.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated via Maven/JSON/YAML.  
2. **Prompt & Model Setup** – `AiPromptDefinition`/`AiModelDefinition` feed into `AiGenerationConfig`.  
3. **File Selection** – `AiConditionEvaluator` receives a root `AiCondition` and `AiFileContext` to decide inclusion.  
4. **Fact Generation** – `AiFactExtractor` processes source files to produce fact blocks for prompts.  
5. **Oversize Handling** – `AiOversizeStrategy.fromConfig` selects a strategy for large files before generation.  
6. **Provider Selection** – `AiGenerationProviderFactory` resolves the provider key (run or model config) and creates the appropriate provider instance.  
7. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls `model.chatComplete`, parses JSON, extracts text, and optionally timing metrics.  
   - *Remote*: `OpenAiCompatibleProviderSupport` builds a JSON body, validates the endpoint, secures the API key, sends the HTTP request, parses the response, and extracts timing data.  
8. **Post‑processing** – `AiCompletionParser` removes any internal thinking block from the raw completion before returning the final answer.

#### Dependencies
- **Internal** – `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ChatResponseParser`.  
- **External** – `java.time.Instant`, `java.util.regex`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`, `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson.databind` (JSON serialization).  
- **Configuration** – `LlamaCppJniConfig` supplies all runtime knobs; `AiGenerationConfig.DEFAULT_*` values are used in the builder.

#### Cross‑cutting
- Defensive copying in `AiConditionGroup.setConditions`.  
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).  
- Validation throws `IllegalArgumentException` for misconfigurations.  
- Lombok `@ToString` used for diagnostic output across beans.  
- Providers are stateless except for the lazy `LlamaModel`; no explicit synchronization, potential race on first `generate` call.  
- Error handling: `IOException` for missing markers, HTTP errors, or JSON issues; `IllegalArgumentException` for unknown provider keys.  
- Timing metrics captured in `AiGenerationTimings` (prompt/cache tokens, throughput, predicted token counts).  
- `LlamaCppJniConfig` is immutable; builder is single‑threaded.  
- API keys fetched from environment variables; insecure HTTP to non‑loopback hosts is rejected.
