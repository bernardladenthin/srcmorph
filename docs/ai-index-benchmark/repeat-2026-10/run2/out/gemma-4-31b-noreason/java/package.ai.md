### java
- H: 1.0
- C: 5BF387FF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:59:16Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Orchestrates AI-driven source code analysis by managing LLM provider integrations and the configuration logic for file filtering, metadata extraction, and inference strategies.

#### Purpose
* Abstract and provide a pluggable framework for local (GGUF/JNI) and remote (OpenAI-compatible) LLM backends.
* Define and evaluate the configuration schema for AI generation runs, including file selection and deterministic fact extraction.

#### Responsibilities
* **AI Provider Orchestration**: Resolving and instantiating LLM backends via a factory pattern and managing the lifecycle of local/remote inference.
* **Configuration & Filtering**: Evaluating boolean logic trees to filter source files and managing engine parameters via Maven/JSON/YAML.
* **Deterministic Analysis**: Using regex-based extraction to supplement AI prompts with hard counts of code elements.
* **Inference Management**: Handling context window overflows via defined oversize strategies and cleaning model outputs (e.g., removing chain-of-thought blocks).

#### Key units
* `AiGenerationProvider`: Interface defining the contract for text generation and performance timing.
* `AiGenerationProviderFactory`: Resolves the effective provider implementation based on configuration.
* `LlamaCppJniAiGenerationProvider`: Local inference implementation utilizing `LlamaModel` JNI bindings.
* `OpenAiCompatibleProviderSupport`: Utility for managing JSON requests and secure API key transmission for remote services.
* `SrcMorphConfiguration`: Root JavaBean containing all engine parameters and AI definitions.
* `AiConditionEvaluator`: Logic engine that validates `AiCondition` trees against `AiFileContext`.
* `AiFactExtractor`: Utility that applies `AiFactCounter` regexes to source text to generate a "facts block".
* `AiCompletionParser`: Logic for stripping `<thought>` blocks from AI responses.
* `AiOversizeStrategy`: Enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling files exceeding context limits.

#### Data flow
1. **Setup**: `SrcMorphConfiguration` and `AiGenerationConfig` are loaded from Maven/JSON.
2. **Selection**: `AiConditionEvaluator` filters files based on `AiCondition` trees.
3. **Augmentation**: `AiFactExtractor` scans source code via regex to produce deterministic metadata.
4. **Execution**: `AiGenerationProviderFactory` instantiates a provider; an `AiGenerationRequest` is sent to the LLM (local via JNI or remote via API).
5. **Refinement**: `AiCompletionParser` cleans the raw response, returning the final text and `AiGenerationTimings`.

#### Dependencies
* **External**: `net.ladenthin.llama` (JNI bindings), `com.fasterxml.jackson` (JSON), `lombok`, `jspecify`.
* **Internal**: `AiPromptSupport` (prompt rendering), `AiPromptDefinition`, `AiModelDefinition`.

#### Cross-cutting
* **Configuration Patterns**: Mix of mutable JavaBeans for framework compatibility (Maven) and Immutable Builders for thread-safe provider settings (`LlamaCppJniConfig`).
* **Security**: Enforcement of HTTPS for remote API keys unless targeting loopback addresses.
* **Validation**: Pre-execution `validate()` methods in evaluators and extractors to catch regex or format errors.
* **Performance**: Standardized measurement of token throughput and latency using `AiGenerationTimings`.
