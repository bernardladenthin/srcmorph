### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:49:37Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code transformation by evaluating file‑selection rules, extracting deterministic facts, and delegating generation to pluggable AI back‑ends.

#### Purpose
- Centralize configuration for source‑morph runs.
- Route files to AI providers based on conditions.
- Generate transformed code snippets with timing diagnostics.

#### Responsibilities
- **Configuration Management** – load, validate, and expose runtime parameters (`SrcMorphConfiguration`).
- **Condition Evaluation** – decide file inclusion via `AiConditionEvaluator` and logical groups.
- **Fact Extraction** – count deterministic regex‑based facts (`AiFactExtractor`).
- **Oversize Handling** – policy for files exceeding context limits (`AiOversizeStrategy`).
- **AI Generation** – abstract provider selection, prompt handling, response parsing, and metrics (`AiGenerationProviderFactory`, `AiGenerationProvider` implementations).

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/Jackson binding.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – utility for deterministic fact counting.
- `AiOversizeStrategy` – enum of oversize handling policies.
- `AiGenerationProviderFactory` – resolves provider keys.
- `AiGenerationProvider` interface – contract for generation and timing.
- `LlamaCppJniAiGenerationProvider` – native llama.cpp inference.
- `OpenAiCompatibleProviderSupport` – OpenAI‑compatible HTTP client.
- `MockAiGenerationProvider` – test stub.
- `AiCompletionParser` – strips internal reasoning from LLM output.
- `LlamaCppJniConfig` – immutable config holder for llama.cpp parameters.

#### Data flow
1. **Load configuration** → `SrcMorphConfiguration`.
2. **Evaluate file conditions** → `AiConditionEvaluator` → inclusion decision.
3. **Extract facts** → `AiFactExtractor` → facts block.
4. **Resolve provider** → `AiGenerationProviderFactory` → concrete `AiGenerationProvider`.
5. **Generate text** → provider’s `generate` (or `generateWithTimings`) → `AiCompletionParser` → final snippet.
6. **Return result** with optional `AiGenerationTimings`.

#### Dependencies
- **Internal**: `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `Java8CompatibilityHelper`.
- **External**: `net.ladenthin.llama` (model, parameters, JSON), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`, `lombok`, `org.jspecify.annotations.Nullable`.
- **Optional**: environment variable lookup for API keys, OpenAI‑compatible HTTP client.

#### Cross‑cutting
- **Thread safety** – stateless utilities; providers may cache a single native model.
- **Error handling** – `IOException` for I/O or parsing errors; `IllegalArgumentException` for unsupported providers.
- **Immutability** – `LlamaCppJniConfig` and `AiOversizeStrategy` are immutable; configuration beans are mutable but not thread‑safe.
- **Timing metrics** – `AiGenerationTimings` captures token counts, throughput, and timestamps.
- **Prompt caching** – `LlamaCppJniAiGenerationProvider` logs cache reuse and warns on truncation.
