### java
- H: 1.0
- C: 7EEE2668
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:54:44Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> srcmorph integrates AI-driven file processing and generation with pluggable backends for local GGUF models and OpenAI-compatible APIs.

#### Purpose
The package orchestrates AI-augmented source code transformations, providing condition-based file selection, deterministic fact extraction, and pluggable generation backends (local GGUF via llama.cpp JNI and OpenAI-compatible APIs) for model inference.

#### Responsibilities
- **AI condition evaluation and validation**: Walks condition trees (`and`, `or`, `not`) to determine file inclusion; validates configuration and detects line-count dependencies.
- **Deterministic facts computation**: Extracts fact blocks from source text using regex counter configurations.
- **AI generation configuration**: Bridges Maven/JSON/YAML binding to provider implementations via mutable JavaBeans.
- **Oversize strategy management**: Determines handling for files exceeding model context windows.
- **Pluggable AI provider orchestration**: Selects and instantiates generation backends (mock, LlamaCppJni, OpenAI-compatible); manages request construction, API key resolution, and completion parsing.
- **Completion parsing**: Strips thinking blocks from LLM output to extract clean model answers.

#### Key units
- **AiConditionEvaluator** (final class): Evaluates and validates AI condition trees against file context; walks `and`/`or`/`not` combinators and leaf conditions (extensions, size, lines, date, glob).
- **AiConditionGroup** (class): Container for child `AiCondition` instances; facilitates Maven plugin XML binding.
- **AiFactExtractor** (final utility class): Computes deterministic facts blocks from `AiFactCounter` configurations; counts non-overlapping regex matches.
- **AiGenerationConfig** (class): Mutable JavaBean carrying all parameters for a single AI generation step; bridges Maven to AI provider implementations.
- **AiOversizeStrategy** (enum): Determines behavior when a source file exceeds its routed model's context window (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- **SrcMorphConfiguration** (class): Root mutable JavaBean centralizing all parameters a srcmorph run needs; designed for binding from Maven, JSON/YAML, or direct Java construction.
- **AiCompletionParser**: Strips leading Gemma-4 thinking blocks from raw LLM completion text to extract the model answer.
- **AiGenerationProvider** (interface): Pluggable AI backend defining `generate(AiGenerationRequest)` and `generateWithTimings(AiGenerationRequest)`.
- **AiGenerationProviderFactory**: Selects and instantiates an `AiGenerationProvider` implementation by name; supports mock, LlamaCppJni, and OpenAI-compatible providers.
- **LlamaCppJniAiGenerationProvider**: Implementation of `AiGenerationProvider` that runs local GGUF models through llama.cpp JNI bindings; lazily loads and caches a native `LlamaModel`.
- **LlamaCppJniConfig**: Immutable value class with thirty-seven fields controlling model loading, generation settings, and provider interaction; built via a mutable builder pattern.
- **OpenAiCompatibleProviderSupport**: Provides pure logic for OpenAI-compatible model requests: constructing JSON request bodies, resolving endpoint URLs, looking up API keys, and computing generation timings.

#### Data flow
1. **Condition evaluation**: `AiConditionEvaluator.matches()` walks a condition tree (`AiCondition`/`AiConditionGroup`) against an `AiFileContext`, applying combinator logic (`and`/`or`/`not`) and leaf checks to produce a boolean inclusion decision.
2. **Validation & line-detection**: The evaluator provides `validate()` (fails fast on misconfiguration) and `usesLines()` (recursively detects if any node needs file contents).
3. **Facts extraction**: `AiFactExtractor.factsBlock()` iterates `AiFactCounter` configurations, counts regex matches via `countMatches()` across source text, and builds a `FACTS_HEADER`/`FACTS_SUFFIX` delimited string.
4. **Generation pipeline**: `SrcMorphConfiguration` is populated and feeds into `AiGenerationConfig`; routing rules reference `AiOversizeStrategy` to handle oversize files; `AiFactExtractor` processes source before generation.
5. **Provider selection**: `AiGenerationProviderFactory.create()` receives a provider name and config; returns the appropriate `AiGenerationProvider` instance.
6. **Text generation**: Client calls `provider.generate(request)`; for LlamaCppJni, the provider lazily initializes a native `LlamaModel`, builds chat template kwargs, executes native chat completion, and parses the response via `AiCompletionParser`.

#### Dependencies
- **Internal collaborations**: `AiCondition`, `AiFileContext`, `AiConditionGroup`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`.
- **External modules**: `java.time`, `java.time.format`, `java.util.regex`, `java.util.Collections`, `java.util.List`, `java.util.ArrayList`, `org.jspecify.annotations.Nullable`, `lombok.ToString`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.llama.LlamaModel`, `net.ladenthin.llama.args.FlashAttn`, `net.ladenthin.llama.args.ReasoningFormat`, `net.ladenthin.llama.json.ChatResponseParser`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Timings`, `net.ladenthin.llama.value.Usage`, `com.fasterxml.jackson.databind.ObjectMapper`.
- **Configuration bindings**: Maven plexus reflection, Jackson/YAML mapping expectations; field names mirror Maven `@Parameter` names.

#### Cross-cutting
- **Nullability**: `@Nullable` widely used on list-returning getters, setter parameters, and optional fields; null semantics vary (omit kwarg, reset to default, reset to empty).
- **Immutability / mutability**: `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless/pure/enum with no mutable state. `AiConditionGroup` has mutable `conditions` field accessed via getter/setter. `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans designed for identity-based management and population through setters.
- **Error handling**: `IllegalArgumentException` thrown for misconfigured condition nodes, unparseable ISO instants, blank pathGlob, missing counter labels/patterns, and unknown oversize strategy values; `PatternSyntaxException` caught and rethrown as `IllegalArgumentException` in `AiFactExtractor`.
- **Concurrency**: Stateless methods (`AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`) are thread-safe. Mutable beans (`AiGenerationConfig`, `SrcMorphConfiguration`) are not thread-safe by design and intended for single-threaded population/management.
- **API key handling**: `OpenAiCompatibleProviderSupport` resolves API keys from environment variables and enforces that keys for plain HTTP endpoints must be to loopback hosts, throwing `IOException` otherwise.
