### ai
- H: 1.0
- C: 3148BC75
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:57:00Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A framework that routes source files to AI models, extracts deterministic facts, and orchestrates local or remote AI generation with configurable strategies.

#### Purpose
- Central configuration and routing for AI‑driven source‑code transformations.  
- Abstract AI generation providers and manage request/response handling.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates paths, providers, prompts, models, and fact extraction rules.  
- **File Selection & Fact Extraction** – `AiConditionEvaluator` and `AiFactExtractor` determine which files to process and build deterministic fact blocks.  
- **Oversize Handling** – `AiOversizeStrategy` selects actions for files exceeding model context limits.  
- **Provider Abstraction** – `AiGenerationProvider` interface and `AiGenerationProviderFactory` resolve concrete backends.  
- **Local & Remote Generation** – `LlamaCppJniAiGenerationProvider` (local inference) and `OpenAiCompatibleProviderSupport` (API calls) perform text generation.  
- **Response Cleanup** – `AiCompletionParser` removes internal reasoning blocks from LLM outputs.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean holding all runtime settings.  
- `AiConditionEvaluator` – stateless validator/evaluator for file‑selection trees.  
- `AiFactExtractor` – static utility for deterministic fact block generation.  
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies.  
- `AiGenerationProvider` – interface defining `generate` and `generateWithTimings`.  
- `AiGenerationProviderFactory` – resolves provider keys to concrete implementations.  
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference provider with lazy model loading.  
- `OpenAiCompatibleProviderSupport` – builds and sends chat‑completion requests to OpenAI‑compatible APIs.  
- `AiCompletionParser` – strips chain‑of‑thought blocks from LLM responses.  
- `LlamaCppJniConfig` – immutable config holder for llama.cpp options.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated from Maven, JSON/YAML, or code.  
2. **Routing** – `AiConditionEvaluator` checks each file against `AiCondition` trees to decide inclusion.  
3. **Fact Generation** – `AiFactExtractor` counts regex matches in selected files and produces a facts block.  
4. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a provider key; `create` returns the corresponding `AiGenerationProvider`.  
5. **Generation** – Provider’s `generate` (or `generateWithTimings`) is invoked:  
   - Local: `LlamaCppJniAiGenerationProvider` builds inference parameters, calls `chatComplete`, parses JSON, extracts text via `AiCompletionParser`, and returns or wraps in `AiGenerationTimings`.  
   - Remote: `OpenAiCompatibleProviderSupport` builds request body, validates endpoint, secures API key, sends HTTP request, parses `ChatResponse`, and computes timings.  
6. **Result Handling** – Caller receives plain text or `AiGenerationTimings`; errors propagate as `IOException`.

#### Dependencies
- **Internal** – `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `MockAiGenerationProvider`, `LlamaCppJniProviderSupport`.  
- **External** – `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON serialization), `java.net`, `java.io`, `org.jspecify.annotations`, Lombok.  
- **Configuration** – `LlamaCppJniConfig` derived from `AiGenerationConfig`.

#### Cross-cutting
- **Thread safety** – Providers are stateless or immutable; local provider’s lazy model loading is not synchronized, potentially creating multiple models concurrently.  
- **Error handling** – `IOException` signals missing or malformed responses, key issues, or endpoint problems; `IllegalArgumentException` for unknown provider names.  
- **Timing metrics** – `AiGenerationTimings` captures prompt/cache tokens, TPS, and predicted tokens for performance analysis.  
- **Configuration immutability** – `LlamaCppJniConfig` is a record‑style immutable holder, ensuring safe concurrent reads.
