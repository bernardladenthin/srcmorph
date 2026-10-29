### java
- H: 1.0
- C: FDEB87CD
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:55:18Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Orchestrates AI-driven source code analysis by integrating LLM providers with configurable file filtering and deterministic metric extraction.

#### Purpose
* Provide a pluggable framework for AI text generation and source code analysis.
* Manage the lifecycle and configuration of local and remote AI models.

#### Responsibilities
* **AI Provider Orchestration**: Abstracts LLM backends (Local GGUF via JNI or remote OpenAI-compatible APIs) and manages their instantiation via a factory.
* **Configuration & Tuning**: Centralizes engine settings, AI hyperparameters (sampling, KV cache), and hardware-specific tuning (GPU offloading).
* **Context & File Evaluation**: Filters source files using recursive logical condition trees (AND/OR/NOT) and manages oversize file strategies.
* **Deterministic Analysis**: Supplements AI summaries with quantitative "facts" extracted via regex.
* **Response Processing**: Cleans LLM outputs by stripping chain-of-thought reasoning blocks.

#### Key units
* `AiGenerationProvider`: Interface defining the contract for text generation and performance timing.
* `AiGenerationProviderFactory`: Resolves and creates specific provider implementations (Mock, LlamaCppJni, OpenAI).
* `LlamaCppJniAiGenerationProvider`: Handles local model execution using native `LlamaModel` handles.
* `SrcMorphConfiguration`: Root JavaBean aggregating all engine and AI settings.
* `AiConditionEvaluator`: Logic engine for matching `AiFileContext` against `AiCondition` trees.
* `AiFactExtractor`: Utility that applies `AiFactCounter` regexes to generate deterministic code facts.
* `AiCompletionParser`: Extracts final answers by removing `<thought>` blocks from LLM responses.
* `AiOversizeStrategy`: Enum defining handling for files exceeding context limits (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).

#### Data flow
1. **Setup**: `SrcMorphConfiguration` is initialized; `AiGenerationProviderFactory` instantiates the chosen AI provider.
2. **Filtering**: `AiConditionEvaluator` determines if a file should be analyzed based on metadata.
3. **Preprocessing**: `AiFactExtractor` scans code for regex matches to produce a facts string; `AiOversizeStrategy` handles files exceeding context limits.
4. **Generation**: `AiGenerationRequest` is sent to the provider, which uses `AiPromptSupport` to render the prompt and calls the native model or remote API.
5. **Post-processing**: Raw output is cleaned by `AiCompletionParser` and returned alongside `AiGenerationTimings`.

#### Dependencies
* **Internal**: `AiPromptSupport`, `AiPromptDefinition`, `AiModelDefinition`, `AiGenerationRequest`, `AiGenerationTimings`.
* **External**: `net.ladenthin.llama` (JNI bindings), `Jackson` (JSON), `Lombok`, `java.util.regex`.

#### Cross-cutting
* **Validation**: Configuration integrity is ensured via `validate()` methods in evaluators and extractors.
* **Resource Management**: Providers implement `AutoCloseable` for native memory release.
* **Security**: API keys are restricted from transmission over plain HTTP (except loopback).
* **Configuration Patterns**: Mix of mutable JavaBeans for binding and immutable Builder patterns for provider configs.
