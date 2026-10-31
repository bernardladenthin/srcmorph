### java
- H: 1.0
- C: 2B1E2C14
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:55:39Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework for configuring, evaluating, and executing AI‑driven source‑code generation within a Maven‑style build process.

#### Purpose
- Centralize AI generation configuration and runtime execution for srcmorph.
- Provide pluggable AI backends and response handling.

#### Responsibilities
- **Configuration & Evaluation** – load and validate AI generation settings, evaluate file‑selection conditions, extract deterministic facts.
- **Provider Management** – resolve and instantiate AI generation providers (local llama.cpp or OpenAI‑compatible), manage provider lifecycles.
- **Response Parsing** – clean and parse LLM outputs, especially Gemma‑4 thinking blocks.
- **Runtime Execution** – orchestrate generation requests, collect timings, and return generated text.

#### Key units
- `SrcMorphConfiguration` – mutable bean holding all runtime settings (paths, provider, prompts, models, routing rules).
- `AiGenerationConfig` – AI provider parameters (model path, sampling, caching, GPU/CPU settings).
- `AiOversizeStrategy` – enum of oversize handling modes (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`, `DEFAULT`).
- `AiConditionEvaluator` – stateless validator and matcher for `AiCondition` trees against `AiFileContext`.
- `AiFactExtractor` – builds a deterministic facts block from regex counters.
- `AiGenerationProvider` – interface for AI generation services.
- `AiGenerationProviderFactory` – resolves and creates providers based on keys.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp JNI provider.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible APIs.
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from raw completions.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.
2. **Routing** – `AiConditionEvaluator` checks each source file against configured `AiCondition` trees to decide inclusion.
3. **Fact Generation** – `AiFactExtractor` produces a deterministic facts string for selected files.
4. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the provider key.
5. **Generation** – `AiGenerationRequest` is sent to the chosen provider:
   - *Local*: `LlamaCppJniAiGenerationProvider` loads the GGUF model, runs chat completion, parses JSON, cleans output with `AiCompletionParser`, returns `AiGenerationTimings`.
   - *OpenAI‑compatible*: `OpenAiCompatibleProviderSupport` builds the request, sends HTTP, parses response, returns `AiGenerationTimings`.
6. **Output** – Generated text and timing metrics are returned to the caller for further processing.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (model, args, JSON parser), `com.fasterxml.jackson` (JSON), `org.jspecify` (nullable annotations), `lombok`.
- **Runtime** – JNI for llama.cpp, HTTP client for OpenAI‑compatible calls.

#### Cross‑cutting
- Immutable configuration (`LlamaCppJniConfig`) and stateless parsers (`AiCompletionParser`) ensure thread safety.
- Providers expose `close()` for resource cleanup; local provider releases native model.
- Validation and error handling are centralized in `AiConditionEvaluator` and `AiFactExtractor`, throwing `IllegalArgumentException` on misconfiguration.
- Timing metrics (`AiGenerationTimings`) are consistently returned across providers for performance monitoring.
