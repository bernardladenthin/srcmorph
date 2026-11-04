### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:49:30Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Provides a pluggable AI text‑generation layer for source‑code transformation, supporting local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable prompts, timing metrics, and robust response parsing.

#### Purpose
- Supply generated code snippets for transformation requests.
- Offer timing diagnostics for performance tuning.
- Abstract over multiple AI back‑ends.

#### Responsibilities
- **Provider selection** – `AiGenerationProviderFactory` resolves a provider key to a concrete implementation.
- **Generation logic** – `AiGenerationProvider` implementations (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`) produce text and optional metrics.
- **Configuration** – `LlamaCppJniConfig` holds all llama.cpp runtime knobs.
- **Prompt handling** – `AiPromptSupport` (not shown) supplies prompt templates.
- **Response parsing** – `AiCompletionParser` extracts the final answer from raw completions.
- **OpenAI compatibility** – `OpenAiCompatibleProviderSupport` builds request payloads, validates endpoints, secures API keys, and interprets responses.

#### Key units
- `AiGenerationProviderFactory` – resolves provider names.
- `AiGenerationProvider` interface – contract for generation and timing.
- `LlamaCppJniAiGenerationProvider` – native llama.cpp inference, lazy model loading, token‑count metrics.
- `OpenAiCompatibleProviderSupport` – JSON request builder, endpoint validation, key handling, error formatting, timing calculation.
- `AiCompletionParser` – strips internal reasoning blocks from LLM output.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp parameters.

#### Data flow
1. **Request creation** – caller builds an `AiGenerationRequest` (prompt key, source file, text, header).
2. **Provider resolution** – `AiGenerationProviderFactory.effectiveProvider` selects a key; `create` returns the matching provider.
3. **Generation** – provider’s `generate` (or `generateWithTimings`) receives the request.
   - For llama.cpp: builds inference parameters via `AiPromptSupport`, runs `LlamaModel.chatComplete`, parses JSON, logs cache usage, and parses completion with `AiCompletionParser`.
   - For OpenAI‑compatible: `OpenAiCompatibleProviderSupport` builds the request body, validates the endpoint, retrieves the API key, sends the HTTP call, parses the `ChatResponse`, and computes timings.
4. **Output** – provider returns the generated text (and optionally `AiGenerationTimings`).

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `lombok`.
- **Optional**: environment variable lookup for API keys, OpenAI‑compatible HTTP client (not shown).

#### Cross-cutting
- **Thread safety**: `AiCompletionParser` is stateless; providers are generally stateless but may cache a native model (single‑threaded). Factory is stateless.
- **Error handling**: `IOException` for missing closing markers, provider failures, or HTTP errors; `IllegalArgumentException` for unsupported provider names.
- **Configuration immutability**: `LlamaCppJniConfig` is immutable; builder pattern used for construction.
- **Timing metrics**: `AiGenerationTimings` captures text, token counts, and throughput; default zeros when unavailable.
- **Prompt caching**: `LlamaCppJniAiGenerationProvider` logs cache reuse and warns on truncation.
