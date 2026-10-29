### java
- H: 1.0
- C: 38DDAD4D
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:05:53Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package orchestrates AI-driven source code generation and summarization by managing configuration schemas, validating file inclusion rules, and executing inference via local Llama.cpp or remote OpenAI-compatible backends.

#### Purpose
*   Centralizes configuration schemas, validation logic, and execution strategies for AI-driven code generation and summarization.
*   Orchestrates pluggable AI inference engines supporting local Llama.cpp JNI bindings and remote OpenAI-compatible APIs.

#### Responsibilities
*   **Configuration Management**: Defines and validates parameters for AI generation runs, including model routing, file filters, prompt definitions, and sampling settings.
*   **Condition Evaluation**: Implements complex logical rule trees (`AiCondition`) to determine which source files are included or excluded from processing.
*   **Fact Extraction**: Generates deterministic metadata blocks containing regex match counts and file context facts prepended to AI summaries.
*   **Oversize Handling**: Selects strategies (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for processing source files exceeding model context windows.
*   **Provider Abstraction**: Manages pluggable backends for local Llama.cpp inference and remote OpenAI-compatible APIs, handling native bindings, JSON serialization, and API key validation.
*   **Text Processing**: Extracts clean answer text by stripping internal reasoning blocks (`THINKING_BLOCK_START_MARKER`) from raw completions.
*   **Metrics Collection**: Tracks token counts, throughput rates, and wall-clock time for generation requests.

#### Key units
*   `SrcMorphConfiguration`: Central mutable JavaBean holding all parameters for a srcmorph AI code generation run, unifying Maven Mojo fields, JSON/YAML configs, and direct instantiation.
*   `AiGenerationConfig`: Stores mutable configuration for a single AI generation step including model paths, sampling strategies, KV cache optimization, and hardware offloading.
*   `AiConditionEvaluator`: Evaluates and validates AI condition trees against file contexts to determine file inclusion/exclusion based on complex logical rules.
*   `AiFactExtractor`: Generates a deterministic, language-agnostic "facts" block containing exact regex match counts from a source file, prepended to AI summaries.
*   `LlamaCppJniAiGenerationProvider`: Implements native inference with lazy model loading and GPU configuration via Llama.cpp JNI bindings.
*   `OpenAiCompatibleProviderSupport`: Handles JSON serialization, endpoint construction, API key retrieval, and HTTP status parsing for remote providers.
*   `AiCompletionParser`: Extracts clean answer text by removing `THINKING_BLOCK_START_MARKER`/`END_MARKER` sequences from raw completion strings.
*   `AiGenerationProviderFactory`: Resolves provider keys and instantiates `Mock`, `LlamaCppJni`, or `OpenAiCompatible` providers based on configuration.

#### Data flow
*   Configuration parameters are bound into `SrcMorphConfiguration` and `AiGenerationConfig` objects, serving as the single source of truth for the generation engine.
*   `AiConditionEvaluator` consumes `AiCondition` trees and `AiFileContext` facts to produce boolean match results, validating structural correctness first.
*   `AiFactExtractor` takes `AiFactCounter` definitions and source text to produce a formatted facts block string.
*   `AiGenerationRequest` (prompt key, source text) enters the pipeline, where `AiGenerationProviderFactory` resolves the effective provider key and instantiates the backend.
*   Execution branches: Local providers load GGUF models and execute `chatComplete()`, while remote providers serialize requests to JSON and send to endpoints.
*   Post-processing strips reasoning blocks via `AiCompletionParser`, and `AiGenerationTimings` aggregates token counts and decode rates.

#### Dependencies
*   **Internal**: `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `ChatResponse`, `Usage`.
*   **External Modules**: `LlamaCppJniProviderSupport` (native bindings), `ObjectMapper` (JSON), `Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`.
*   **Native**: `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`.
*   **Configuration**: `AiCalibration`, `AiPromptDefinition`.

#### Cross-cutting
*   **Validation**: `AiConditionEvaluator` enforces strict rules (exactly one branch/leaf, non-empty groups, bounded ranges, valid ISO-8601 dates, non-blank globs) before execution.
*   **Null Safety**: Extensive use of `org.jspecify.annotations.Nullable` for string and list fields to handle optional configuration values safely.
*   **Concurrency**: `AiConditionEvaluator` and `AiOversizeStrategy` are thread-safe (stateless/immutable), while `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans requiring external synchronization if accessed concurrently.
*   **Security**: `OpenAiCompatibleProviderSupport` enforces HTTPS or loopback-only connections for API keys.
*   **Reasoning Support**: `AiCompletionParser` and `LlamaCppJniConfig` support `chat_template_enable_thinking` and reasoning effort/budget parameters.
*   **Dry Run Mode**: `LlamaCppJniConfig` supports `dryMultiplier`, `dryBase`, and `dryAllowedLength` for simulation without inference.
*   **Caching**: `LlamaCppJniConfig` manages `cachePrompt`, `cacheReuse`, and `cacheTypeK/V` for KV cache optimization.
