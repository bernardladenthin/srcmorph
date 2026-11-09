### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:18:17Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Enables AI‑powered source‑code generation and routing for the srcmorph engine.

#### Purpose
- Centralize AI configuration, validation, and orchestration for source‑code transformations.
- Provide pluggable AI back‑ends (local llama.cpp, OpenAI‑compatible, mock) with prompt handling, timing, and response parsing.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionEvaluator`, `AiFactExtractor`.
- **File Selection & Oversize Handling** – `AiConditionGroup`, `AiOversizeStrategy`.
- **AI Provider Management** – `AiGenerationProviderFactory`, `AiGenerationProvider` implementations.
- **Prompt & Response Processing** – `OpenAiCompatibleProviderSupport`, `AiCompletionParser`.
- **Local Inference** – `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`.

#### Key units
- `SrcMorphConfiguration` – mutable bean for runtime settings.
- `AiGenerationConfig` – mutable bean for AI provider options.
- `AiConditionEvaluator` – stateless validator of `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact blocks.
- `AiOversizeStrategy` – enum for oversize actions.
- `AiGenerationProviderFactory` – resolves provider keys to concrete providers.
- `AiGenerationProvider` (interface) – `generate`, `generateWithTimings`, `close`.
- `MockAiGenerationProvider` – test stub.
- `LlamaCppJniAiGenerationProvider` – local llama.cpp inference.
- `OpenAiCompatibleProviderSupport` – OpenAI‑compatible request builder and parser.
- `AiCompletionParser` – strips reasoning blocks from completions.
- `LlamaCppJniConfig` – immutable config for llama.cpp parameters.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` loaded from Maven/CLI/JSON/YAML → `AiGenerationConfig`.
2. **File Selection** – `AiConditionEvaluator` evaluates `AiCondition` trees via `AiConditionGroup` to filter source files.
3. **Oversize Handling** – `AiOversizeStrategy` determines truncation or sampling for files exceeding context limits.
4. **Fact Generation** – `AiFactExtractor` counts regex matches and prepends a facts block to the prompt.
5. **Provider Selection** – `AiGenerationProviderFactory.create` selects a provider based on config keys.
6. **Prompt Construction** – provider (or `OpenAiCompatibleProviderSupport`) obtains system/user prompts via `AiPromptSupport`.
7. **Generation** – provider’s `generate` or `generateWithTimings` produces AI text (local or OpenAI).
8. **Post‑processing** – `AiCompletionParser.parseCompletion` removes internal reasoning blocks before returning the final answer.

#### Dependencies
- **Internal** – `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `LlamaCppJniConfig`.
- **External** – `java.time`, `java.util`, `java.io`, `java.util.regex`, Lombok (`@ToString`), `org.jspecify.annotations`, `net.ladenthin.llama`, `com.fasterxml.jackson`, `java.net`.

#### Cross‑cutting
- **Stateless utilities** – `AiConditionEvaluator`, `AiFactExtractor` are thread‑safe, no shared mutable state.
- **Immutability** – `LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport` are immutable and thread‑safe.
- **Error handling** – `IllegalArgumentException` for malformed configs or URLs; `IOException` for I/O or response parsing errors.
- **Timing metrics** – `AiGenerationTimings` captures prompt, cache, and prediction times; providers expose `generateWithTimings`.
- **Resource management** – `AiGenerationProvider` extends `AutoCloseable`; local provider disposes native model on `close`.
- **Configuration beans** – `SrcMorphConfiguration` and `AiGenerationConfig` are plain JavaBeans, not thread‑safe.
