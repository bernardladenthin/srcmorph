### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:34:24Z
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
> A framework for configuring, selecting, and executing AI text‑generation back‑ends—whether local llama.cpp or OpenAI‑compatible services—while parsing responses, handling errors, and measuring performance.

#### Purpose
- Provide a pluggable AI generation layer for source‑code transformation tasks.
- Abstract model configuration, request construction, and result parsing across multiple providers.

#### Responsibilities
- **Provider selection & instantiation** – `AiGenerationProviderFactory` resolves the effective provider key and creates the appropriate `AiGenerationProvider`.
- **Local inference** – `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, runs chat completions, and reports timings.
- **Remote inference** – `OpenAiCompatibleProviderSupport` builds HTTP requests, validates endpoints, secures API keys, and interprets responses.
- **Response cleaning** – `AiCompletionParser` strips internal reasoning blocks from LLM outputs.
- **Configuration** – `LlamaCppJniConfig` holds all llama.cpp parameters in an immutable, builder‑style record.

#### Key units
- `AiGenerationProviderFactory` – resolves provider key, creates `AiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider` – implements `AiGenerationProvider` for llama.cpp JNI.
- `OpenAiCompatibleProviderSupport` – constructs OpenAI‑compatible chat‑completion requests and parses responses.
- `AiCompletionParser` – extracts final answer from raw completion text.
- `LlamaCppJniConfig` – immutable config record for llama.cpp provider.

#### Data flow
1. **Configuration**: `LlamaCppJniConfig` built via builder, passed to factory.
2. **Provider creation**: `AiGenerationProviderFactory.create(...)` selects provider key → returns `AiGenerationProvider`.
3. **Request**: Caller builds `AiGenerationRequest` (prompt key, source file, text, header).
4. **Generation**:
   - *Local*: `LlamaCppJniAiGenerationProvider.generate(...)` loads model, runs `chatComplete`, parses JSON, returns text and timings.
   - *Remote*: `OpenAiCompatibleProviderSupport` builds JSON body, endpoint, API key, sends HTTP request, parses `ChatResponse`, computes `AiGenerationTimings`.
5. **Post‑processing**: `AiCompletionParser.parseCompletion(...)` removes thinking blocks, yielding clean answer.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (model, args, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `lombok`.
- **Provider‑specific**: `MockAiGenerationProvider` for testing, `OpenAiCompatibleAiGenerationProvider` for OpenAI‑compatible APIs.

#### Cross‑cutting
- Immutable configuration objects (`LlamaCppJniConfig`) ensure thread safety.
- `AiCompletionParser` is stateless and reusable across providers.
- Error handling: `IOException` for missing keys, malformed responses, or abrupt reasoning blocks.
- Timing metrics exposed via `AiGenerationTimings` for both local and remote providers.
- Provider factory uses `effectiveProvider` to merge run‑level and model‑level keys.
