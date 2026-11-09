### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:58:02Z
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
> Provides a pluggable AI generation layer that supports local llama.cpp models, OpenAI‑compatible APIs, and mock back‑ends, exposing a unified request/response contract and optional timing metrics.

#### Purpose
- Abstract AI text generation behind a single interface.
- Allow runtime selection of provider (local, remote, mock) via configuration.
- Offer clean parsing of model responses and optional performance telemetry.

#### Responsibilities
- **Provider abstraction** – `AiGenerationProvider` interface defines generation and timing APIs.
- **Factory orchestration** – `AiGenerationProviderFactory` resolves provider keys and instantiates the correct implementation.
- **Local model handling** – `LlamaCppJniAiGenerationProvider` loads a GGUF model lazily, runs chat completions, parses responses, and reports timings.
- **Remote API support** – `OpenAiCompatibleProviderSupport` builds request bodies, validates endpoints, secures API keys, and interprets OpenAI‑compatible responses.
- **Configuration** – `LlamaCppJniConfig` holds all runtime knobs for the llama.cpp provider.
- **Response cleaning** – `AiCompletionParser` strips Gemma‑4 thinking blocks from raw completions.

#### Key units
- `AiGenerationProvider` – interface for generating text and timings.
- `AiGenerationProviderFactory` – resolves provider key → concrete provider.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp implementation.
- `OpenAiCompatibleProviderSupport` – helper for OpenAI‑compatible HTTP interactions.
- `LlamaCppJniConfig` – immutable config builder for llama.cpp.
- `AiCompletionParser` – removes internal reasoning blocks from completions.

#### Data flow
1. **Request creation** – caller builds an `AiGenerationRequest` (prompt key, source file/text, header).
2. **Provider selection** – `AiGenerationProviderFactory.create(...)` chooses the provider based on run‑level or model‑level keys.
3. **Generation** – provider’s `generate(...)` or `generateWithTimings(...)` is invoked.
   - *Local*: `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel`, runs `chatComplete`, parses `ChatResponse`, cleans text via `AiCompletionParser`, and returns result or timings.
   - *Remote*: `OpenAiCompatibleProviderSupport` builds JSON body, validates endpoint, secures API key, sends HTTP request, parses `ChatResponse`, and extracts timings.
4. **Response handling** – caller receives plain text or `AiGenerationTimings` (text + token counts, rates).

#### Dependencies
- **Internal** – `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `LlamaCppJniConfig`.
- **External** – `net.ladenthin.llama` (native model, JSON parser), `com.fasterxml.jackson` (JSON), `org.jspecify` (nullable annotations), `lombok` (annotations).
- **Runtime** – native llama.cpp library, HTTP client (used by OpenAI support), environment variables for API keys.

#### Cross-cutting
- **Immutability** – `LlamaCppJniConfig` and support classes are immutable; thread‑safe by design.
- **Error handling** – `IOException` propagated for I/O or parsing failures; `IllegalArgumentException` for misconfiguration.
- **Timing metrics** – default implementation in `AiGenerationProvider` returns zeroed metrics; concrete providers override to supply real data.
- **Configuration resolution** – `effectiveProvider` logic centralizes provider key precedence (run vs. model level).
- **Response parsing** – `AiCompletionParser` ensures consistent output by stripping internal reasoning blocks across providers.
