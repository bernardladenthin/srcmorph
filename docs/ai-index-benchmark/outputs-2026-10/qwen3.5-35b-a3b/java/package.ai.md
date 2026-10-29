### java
- H: 1.0
- C: 086884D0
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:39:20Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Manages AI-driven source code indexing by filtering files based on conditions and generating text via pluggable inference backends.

#### Purpose
*   Validates and evaluates `AiCondition` trees for file filtering during indexing.
*   Defines contracts and implementations for pluggable AI text generation services.

#### Responsibilities
*   Evaluate `AiCondition` trees against `AiFileContext` to determine file matches.
*   Resolve and instantiate `AiGenerationProvider` implementations via factory.
*   Execute local inference using `LlamaCppJniAiGenerationProvider` with native bindings.
*   Construct HTTP payloads and validate security for `OpenAiCompatibleProviderSupport`.
*   Manage generation parameters and performance metrics via `LlamaCppJniConfig` and `AiGenerationTimings`.

#### Key units
*   `AiConditionEvaluator`: Evaluates file filtering conditions against `AiFileContext`.
*   `AiConditionGroup`: Container for child `AiCondition` instances for logical combination.
*   `AiCondition`: Condition tree node (referenced in dependencies).
*   `AiRangeCondition`: Size/lines bounds (referenced in dependencies).
*   `AiGenerationProvider`: Interface for text generation with `generate` and `close` methods.
*   `AiGenerationProviderFactory`: Resolves provider keys and creates `AiGenerationProvider` instances.
*   `LlamaCppJniAiGenerationProvider`: Implements inference using `LlamaModel` native handle.
*   `LlamaCppJniConfig`: Immutable value object with 41 generation parameters.
*   `OpenAiCompatibleProviderSupport`: Stateless utility for JSON request construction and security checks.
*   `AiCompletionParser`: Referenced by `LlamaCppJniAiGenerationProvider` for parsing logic.
*   `AiGenerationRequest`: Input parameter containing prompt key, source file, and text.
*   `AiGenerationTimings`: Wrapper for performance metrics and token counts.

#### Data flow
*   Input `AiCondition` tree + `AiFileContext` -> `AiConditionEvaluator.matches()` returns boolean.
*   Factory resolves provider key from run/model configuration.
*   Provider instance receives `AiGenerationRequest` and configuration.
*   Local provider loads `LlamaModel` lazily and invokes `chatComplete()`.
*   External support serializes request to JSON and validates API key transport security.
*   Output returns generated `String` and `AiGenerationTimings` metrics.

#### Dependencies
*   Internal: `AiCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiPromptSupport`, `AiGenerationConfig`.
*   External: `net.ladenthin.llama` for native model bindings and JSON parsing.
*   External: `com.fasterxml.jackson.databind` for JSON serialization.
*   External: `java.time.Instant`, `java.util.List`, `org.jspecify.annotations.Nullable`, `lombok`.

#### Cross-cutting
*   `AiConditionEvaluator` is stateless and thread-safe.
*   `LlamaCppJniConfig` and support classes use final fields for immutability.
*   `AutoCloseable` interface ensures native handle cleanup.
*   `OpenAiCompatibleProviderSupport` blocks API keys over non-HTTPS unless loopback.
*   Validation throws `IllegalArgumentException` for invalid condition structure or keys.
*   `DateTimeParseException` caught and wrapped in `IllegalArgumentException`.
