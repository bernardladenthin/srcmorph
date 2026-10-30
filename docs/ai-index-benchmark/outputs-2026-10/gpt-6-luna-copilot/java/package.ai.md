### java
- H: 1.0
- C: 64C7F162
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:37:17Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects eligible source files, configures AI-assisted summary generation, and produces text through local or OpenAI-compatible providers.

#### Purpose
- Define source eligibility, generation settings, prompts, routing, facts, and run metadata.
- Generate text through selectable AI backends and support completion cleanup.

#### Responsibilities
- **Configuration and eligibility:** Store run and generation settings; evaluate nested file conditions and format configured source facts.
- **Provider selection and generation:** Resolve provider overrides and generate text using mock, llama.cpp JNI, or OpenAI-compatible backends.
- **Inference support:** Configure local model inference and build remote chat-completion requests, including endpoint, key, error, and timing handling.
- **Completion cleanup:** Remove a leading Gemma-4 reasoning block and trim completion text.

#### Key units
- `SrcMorphConfiguration` — run-level source scope, generation, fallback, and output metadata.
- `AiGenerationConfig` — settings for one AI generation step.
- `AiConditionEvaluator` — evaluates `AiCondition` trees against `AiFileContext`.
- `AiFactExtractor` — validates fact counters and formats source-text match counts.
- `AiOversizeStrategy` — parses policies for oversized files.
- `AiGenerationProvider` — generation, timing, and close contract.
- `AiGenerationProviderFactory` — resolves provider settings and creates supported backends.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp chat completion.
- `LlamaCppJniConfig` — immutable inference and provider settings with a builder.
- `OpenAiCompatibleProviderSupport` — constructs and validates compatible chat-completion data.
- `AiCompletionParser` — cleans completion text.

#### Data flow
- `AiCondition` trees and `AiFileContext` feed eligibility evaluation; configured `AiFactCounter` entries and source text produce a formatted facts block.
- Run and model settings determine the provider; providers receive `AiGenerationRequest` and return completion text, optionally with `AiGenerationTimings`.
- The local backend runs configured inference and parses its response; OpenAI-compatible support builds request data and derives endpoint, key, error, and timing information.

#### Dependencies
- Internal types include `AiFileContext`, `AiCondition`, `AiFactCounter`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Path-glob evaluation uses `AiSourceExcludeFilter`; local inference uses llama.cpp JNI types and `LlamaCppJniProviderSupport`.
- Remote support uses Jackson and OpenAI-compatible response and usage types.

#### Cross-cutting
- Generation APIs declare `IOException`; providers implement `AutoCloseable`.
- Mutable configuration objects generally lack thread-safety guarantees; the llama.cpp provider lazily caches its model with unsynchronized initialization.
- Provider configuration uses final fields; its builder is not thread-safe.
