### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:55:14Z
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
> A modular AI generation framework that supports local llama.cpp inference, OpenAI‑compatible APIs, and mock providers, with configurable request building, response parsing, and timing metrics.

#### Purpose
- Provide a pluggable text‑generation engine for source‑code transformation tasks.
- Offer local, cloud‑compatible, and test‑mode backends with unified request/response handling.

#### Responsibilities
- **Provider Factory** – resolves and instantiates the correct `AiGenerationProvider` based on run‑time or model configuration.
- **Provider Implementations** – generate text and optionally timing data:
  - `LlamaCppJniAiGenerationProvider` for local GGUF inference.
  - `OpenAiCompatibleAiGenerationProvider` for HTTP chat‑completion endpoints.
  - `MockAiGenerationProvider` for deterministic testing.
- **Request/Response Handling** – build JSON bodies, validate URLs, fetch API keys, parse raw completions, and extract metrics.
- **Configuration** – immutable `LlamaCppJniConfig` exposes all llama.cpp knobs and defaults.

#### Key units
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – `create`, `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` – lazy GGUF model loading, chat completion, timing extraction.
- `OpenAiCompatibleProviderSupport` – request body construction, endpoint normalization, API key resolution, error formatting, timing calculation.
- `LlamaCppJniConfig` – immutable builder‑based config with 37 knobs.
- `AiCompletionParser` – strips model‑internal thinking blocks from raw text.

#### Data flow
1. **Factory** receives provider key, llama config, and prompt support → returns a concrete provider.
2. **Provider** receives `AiGenerationRequest` (prompt key, source file, source text, header).
3. **Request body** built by provider support → sent to local model or HTTP endpoint.
4. **Response** parsed:
   - Local: `chatComplete` → JSON → `ChatResponseParser` → `ChatResponse`.
   - Remote: HTTP body → `ChatResponseParser`.
5. **Completion text** extracted via `AiCompletionParser`.
6. **Timings** computed from inference metadata or usage stats.
7. **Result** returned as `String` or `AiGenerationTimings`.

#### Dependencies
- **Internal**: `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationRequest`, `AiGenerationTimings`.
- **Local provider**: `net.ladenthin.llama` (model, parameters, JSON parsing).
- **Remote provider**: Jackson `ObjectMapper`, `ChatResponse`, `Usage`.
- **Utilities**: `lombok` annotations, `org.jspecify.annotations.Nullable`, `java.net.URL`, `java.util.regex.Pattern`.

#### Cross-cutting
- **Immutability**: `LlamaCppJniConfig` and provider support classes are immutable; thread‑safe by design.
- **Error handling**: `AiCompletionParser` throws `IOException` on malformed thinking blocks; provider support throws `IOException` for key/URL issues.
- **Timing metrics**: `generateWithTimings` in providers returns `AiGenerationTimings` with prompt/cache throughput and predicted token rates.
- **Configuration knobs**: 37 parameters in `LlamaCppJniConfig` allow fine‑grained control over model loading, GPU usage, caching, and sampling.
