### java
- H: 1.0
- C: 603ABF3C
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:38:12Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures AI-assisted source documentation and routes text-generation requests to mock, local llama.cpp, or OpenAI-compatible models.

#### Purpose
- Defines source-selection, generation, model-routing, and oversized-file settings for AI documentation workflows.
- Provides provider implementations that turn generation requests into text, with optional timing and token metrics.

#### Responsibilities
- **Configuration and source facts:** `SrcMorphConfiguration` and `AiGenerationConfig` hold run-wide and per-model settings; condition utilities evaluate file-selection rules, and fact utilities count configured regex matches.
- **Oversized-source policy:** `AiOversizeStrategy` parses configured choices for failing, sampling, map-reduce, or deterministic output.
- **Provider selection and contract:** `AiGenerationProviderFactory` chooses an implementation; `AiGenerationProvider` defines generation, timing, and resource-close operations.
- **Generation backends:** `LlamaCppJniAiGenerationProvider` handles local GGUF-backed inference; OpenAI-compatible support builds remote chat-completions requests; the mock provider supplies the fallback backend.
- **Completion handling:** `AiCompletionParser` strips a leading Gemma-compatible thinking block and trims the answer.

#### Key units
- `SrcMorphConfiguration` — JavaBean settings for source filters, prompts, model definitions, routing, fallbacks, and generated-document metadata.
- `AiGenerationConfig` — per-model inference and provider options, including sampling, prompt limits, hardware/cache settings, reasoning, and stopping.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate condition trees against `AiFileContext` facts; store Maven-bindable condition groups.
- `AiFactExtractor` — validates regex counters and formats exact whole-source match counts.
- `AiOversizeStrategy` — maps configuration tokens to oversized-source policy values, defaulting to `FAIL`.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define the backend contract and resolve the configured provider, defaulting to mock when unset.
- `LlamaCppJniConfig` — immutable, builder-created configuration for local and OpenAI-compatible generation options.
- `LlamaCppJniAiGenerationProvider` — initializes and runs local llama.cpp inference, returning parsed completion text and available metrics.
- `OpenAiCompatibleProviderSupport` — prepares chat-completions JSON and handles endpoint, API-key, HTTP-error, and timing utilities.
- `AiCompletionParser` — removes a leading reasoning block and returns the remaining answer.

#### Data flow
- `SrcMorphConfiguration` supplies source filters, prompts, model definitions, routing, and fallback settings to callers.
- Condition trees are validated and evaluated against file context; configured fact-counter patterns are applied to source text and formatted as a facts block.
- The factory resolves provider configuration and creates a backend for an `AiGenerationRequest`.
- The local backend builds inference settings from `LlamaCppJniConfig`, invokes llama.cpp chat completion, and parses the response; OpenAI-compatible support builds non-streaming chat requests.
- `generateWithTimings` returns metrics where available; the interface default wraps generated text with zero timing values.

#### Dependencies
- Internal configuration and request types include `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptDefinition`, `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Provider integrations include `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, and `OpenAiCompatibleAiGenerationProvider`.
- External dependencies include llama.cpp JNI types, Jackson, Java collections/regex/I/O/network APIs, Lombok, and JSpecify annotations.

#### Cross-cutting
- Configuration classes are mutable JavaBeans; `LlamaCppJniConfig` is immutable. Utilities for condition evaluation, fact extraction, and oversize parsing are stateless or immutable.
- Validation is explicit for condition trees and fact-counter regexes; configuration setters do not generally validate settings.
- Defaults and null handling are common: null generation lists reset to empty lists, and blank provider selection falls back to mock.
- Generation and lifecycle operations declare `IOException`; unknown nonblank oversize tokens throw `IllegalArgumentException`.
- llama.cpp initialization and cached-handle access are unsynchronized; no provider thread-safety guarantee is stated.
- OpenAI-compatible API keys are permitted over HTTPS or to recognized loopback hosts and rejected for other cleartext endpoints.
