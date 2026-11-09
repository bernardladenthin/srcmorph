### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:31:26Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Converts configured source-file context into AI-generated project index content using selectable local, OpenAI-compatible, or mock generation.

#### Purpose
- Configures source selection, facts, prompts, models, generation behavior, and oversized-file policies.
- Routes generation requests to provider backends and processes completion text and timing data.

#### Responsibilities
- **Run configuration and source preparation:** `SrcMorphConfiguration`, condition evaluation, regex-based fact extraction, and oversized-file strategy parsing.
- **Generation settings:** `AiGenerationConfig` and `LlamaCppJniConfig` define sampling, hardware, caching, reasoning, stop strings, and provider connection options.
- **Provider selection and execution:** `AiGenerationProviderFactory` resolves the backend; providers implement generation, optional timings, and lifecycle closure.
- **Completion and request support:** `AiCompletionParser` cleans generated answers; `OpenAiCompatibleProviderSupport` builds requests and handles endpoint, API-key, error, and timing details.

#### Key units
- `SrcMorphConfiguration` — run-wide source filters, prompts, models, routing rules, facts, and output metadata.
- `AiConditionEvaluator` — validates condition trees and matches file contexts, delegating path globs to `AiSourceExcludeFilter`.
- `AiFactExtractor` — counts configured regex matches in source text and formats a facts block.
- `AiOversizeStrategy` — parses policies for failing, sampling, map-reduce, or deterministic handling.
- `AiGenerationProvider` — pluggable `AutoCloseable` generation contract with optional timing support.
- `AiGenerationProviderFactory` — selects mock, llama.cpp JNI, or OpenAI-compatible generation.
- `LlamaCppJniAiGenerationProvider` — runs local model inference and returns parsed completion text and available metrics.
- `AiCompletionParser` — trims answers and removes a leading Gemma-4 thinking block.
- `OpenAiCompatibleProviderSupport` — prepares chat-completion request data and handles transport safeguards and response metadata.

#### Data flow
- `SrcMorphConfiguration` supplies source scope, filters, prompt/model definitions, fact definitions, and generation settings.
- `AiConditionEvaluator` applies configured conditions to `AiFileContext`; `AiFactExtractor` derives facts from full source text.
- `AiGenerationProviderFactory` resolves the effective provider, defaulting blank selection to mock or choosing a backend by configured provider name.
- The selected provider builds or prepares a request, generates a completion, and returns text with optional timing data; completion parsing trims text and removes a leading thinking block.

#### Dependencies
- Configuration uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, and definitions for prompts, models, fields, facts, and calibration.
- Providers use `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Local inference depends on llama.cpp JNI model and parameter types, `LlamaCppJniProviderSupport`, and response parsing utilities.
- OpenAI-compatible request support uses Jackson, `ChatResponse`, `Usage`, and an injected environment-variable lookup.

#### Cross-cutting
- Configuration types are mostly mutable JavaBeans; `LlamaCppJniConfig` is immutable after construction and built with a single-threaded builder.
- Provider generation exposes `IOException`; an unterminated leading thinking block also produces `IOException`.
- The local model is lazily initialized and cached without synchronization, so concurrent first calls may race.
- OpenAI-compatible support restricts base URLs to HTTP(S) and prevents configured API keys from being sent over plain HTTP to non-loopback hosts.
