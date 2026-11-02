### java/provider
- H: 1.0
- C: 038F6632
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:06:08Z
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
> A modular AI‑generation framework that abstracts multiple back‑ends (mock, llama.cpp, OpenAI‑compatible) and provides a clean API for generating text, parsing responses, and measuring performance.

#### Purpose
- Offer a pluggable text‑generation service for source‑code transformation tools.
- Hide provider‑specific details behind a common interface.
- Provide utilities for request construction, response parsing, and timing.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface and factory for concrete implementations.
- **Configuration** – immutable `LlamaCppJniConfig` for llama.cpp settings.
- **Response handling** – `AiCompletionParser` strips reasoning blocks from LLM outputs.
- **OpenAI compatibility** – `OpenAiCompatibleProviderSupport` builds requests, resolves endpoints, validates API keys, and extracts timings.

#### Key units
- `AiGenerationProvider` (interface) – contract for `generate` and `generateWithTimings`.
- `AiGenerationProviderFactory` – resolves provider key and instantiates the appropriate provider.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference, lazy model loading, JSON parsing, timing extraction.
- `LlamaCppJniConfig` – immutable record of all llama.cpp runtime knobs.
- `OpenAiCompatibleProviderSupport` – JSON request builder, endpoint resolver, API key guard, success checker, error formatter, timing calculator.
- `AiCompletionParser` – removes Gemma‑4 style chain‑of‑thought blocks from completions.

#### Data flow
1. **Request creation** – caller builds an `AiGenerationRequest` (prompt key, source text, header).
2. **Provider selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a key; `create` returns a concrete provider.
3. **Generation** – provider’s `generate` or `generateWithTimings` is invoked.
   - For llama.cpp: build inference parameters → native chat completion → JSON → `ChatResponseParser` → extract content → `AiCompletionParser` → return text/timings.
   - For OpenAI‑compatible: `OpenAiCompatibleProviderSupport.requestBody` builds JSON → HTTP call → response parsing → timing extraction.
4. **Post‑processing** – `AiCompletionParser` ensures only the final answer is exposed.

#### Dependencies
- **Internal** – `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`.
- **External** – `net.ladenthin.llama` (native llama.cpp bindings), Jackson `ObjectMapper`, `org.jspecify.annotations.Nullable`, Lombok.
- **Runtime** – environment variables for API keys, optional remote model URLs.

#### Cross‑cutting
- **Immutability** – `LlamaCppJniConfig` and most support classes are immutable, thread‑safe.
- **Error handling** – `IOException` for I/O or configuration errors; `IllegalArgumentException` for unsupported provider names.
- **Timing measurement** – `AiGenerationTimings` captures prompt, cached, and completion token counts and decode rate.
- **Configuration resolution** – provider key can come from run or model config; defaults to mock if unspecified.
