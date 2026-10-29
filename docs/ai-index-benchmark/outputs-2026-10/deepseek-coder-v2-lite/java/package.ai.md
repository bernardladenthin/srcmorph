### java
- H: 1.0
- C: 0F6C5BA2
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T02:43:39Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.

#### Purpose
- To determine if a file matches a set of conditions defined in an {@link AiCondition} tree.
- To validate the conditions to ensure they are correctly configured.
- To identify if any condition requires line count information.

#### Responsibilities
- **AiConditionEvaluator**:
  - Evaluates condition trees against file contexts.
  - Validates condition trees for correctness.
  - Checks if any condition requires line count information.

#### Key Units
- **AiConditionEvaluator**:
  - **matches**: Recursively evaluates condition trees.
  - **validate**: Ensures conditions are correctly configured.
  - **usesLines**: Checks for line count requirements.

#### Data flow
- **Input**: {@link AiCondition} tree and {@link AiFileContext}.
- **Output**: Boolean result and boolean indicating line count requirement.
- **Process**:
  - `matches` method processes the condition tree.
  - `validate` method ensures conditions are valid.
  - `usesLines` method checks for line count conditions.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`

#### Cross-cutting
- **Shared base types**: Utilizes `java.time` for date handling.
- **Exception handling**: Throws `IllegalArgumentException` for invalid conditions.
- **Threading**: Methods do not rely on shared mutable state.

#### EOF

> Provide a Java implementation for generating AI-driven content using a local llama.cpp JNI binding.
> Support various model configurations and parameters for efficient AI generation.

#### Purpose
- Provide a Java implementation for generating AI-driven content using a local llama.cpp JNI binding.
- Support various model configurations and parameters for efficient AI generation.

#### Responsibilities
- Parses raw LLM completion text to extract the model answer.
- Provides a method for generating text based on an AI generation request.
- Selects and instantiates an AI generation provider based on the given provider name.
- Calculates VAT for invoices by processing request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Key units
- `AiCompletionParser`: Parses raw LLM completion text to extract the model's actual answer from raw completion text by stripping internal reasoning blocks.
- `AiGenerationProvider`: Interface for generating text based on an AI generation request.
- `AiGenerationProviderFactory`: Selects and instantiates an AI generation provider based on the given provider name.
- `OpenAiCompatibleProviderSupport`: Provides support for configuring and interacting with OpenAI-compatible AI generation providers.

#### Data flow
- `AiCompletionParser` processes raw completion text to extract the model's actual answer.
- `AiGenerationProvider` generates text based on an AI generation request.
- `AiGenerationProviderFactory` selects and instantiates an AI generation provider based on the given provider name.
- `OpenAiCompatibleProviderSupport` processes request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Dependencies
- `java.io.IOException`
- `lombok.ToString`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Cross-cutting
- Shared base types/interfaces: `AiGenerationProvider`, `AutoCloseable`
- Common exception/error handling: `IOException`
- Threading/concurrency notes: The class is not thread-safe as it does not modify any shared state.
- Configuration: `LlamaCppJniConfig`, `AiGenerationConfig`
