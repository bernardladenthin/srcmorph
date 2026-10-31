### java
- H: 1.0
- C: 0925CF25
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:48:27Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures and executes the AI-driven source documentation pipeline for the srcmorph Maven plugin, selecting source files via condition trees, handling oversize files with deterministic fact extraction, and generating `.ai.md` summaries through pluggable local llama.cpp or remote OpenAI-compatible LLM backends.

#### Purpose
- Bind Maven plugin XML to AI inference parameters, file-selection rules, and oversize strategies for automated `.ai.md` generation
- Provide pluggable, AutoCloseable AI generation backends (local GGUF via JNI, remote OpenAI-compatible HTTP, mock) with completion parsing, security enforcement, and token-throughput metrics

#### Responsibilities
- **Configuration binding & file filtering** (`config/`): Map Maven XML to `SrcMorphConfiguration` and `AiGenerationConfig`; evaluate `AiCondition` AND/OR/NOT trees against `AiFileContext` via `AiConditionEvaluator` to include/exclude files by extension, size, lines, modification date, and path-glob
- **Oversize & deterministic handling** (`config/`): Route oversize files via `AiOversizeStrategy` (`FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC`); prepend exact regex match-count blocks from `AiFactExtractor` on deterministic paths
- **Provider abstraction & factory** (`provider/`): Define `AiGenerationProvider` contract (`generate`, `generateWithTimings`, `close`); resolve backends via `AiGenerationProviderFactory` keys (`mock`, `llamacpp-jni`, `openai-compatible`) with model-level override
- **Local & remote inference** (`provider/`): Execute local GGUF chat completions via `LlamaCppJniAiGenerationProvider` (JNI `LlamaModel`, lazy `ModelParameters` from `LlamaCppJniConfig`); POST to OpenAI-compatible `/chat/completions` via `OpenAiCompatibleProviderSupport` with HTTPS/loopback API-key enforcement
- **Completion post-processing** (`provider/`): Strip Gemma-4 thinking blocks via `AiCompletionParser`; map token usage to `AiGenerationTimings` decode rates

#### Key units
- `SrcMorphConfiguration` — top-level Maven XML-bound JavaBean (23 fields) holding directories, file filters, execution mode, provider, and prompt/model/fact definitions
- `AiGenerationConfig` — per-step inference JavaBean (45 setters, 42 `DEFAULT_*` constants) for model path, context size, sampling, GPU offload, KV cache, DRY, stop strings, and remote provider settings
- `AiConditionEvaluator` — stateless boolean evaluator walking `AiCondition`/`AiConditionGroup` trees against `AiFileContext` facts to decide file inclusion
- `AiOversizeStrategy` — enum (`FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC`, default `FAIL`) routing files exceeding the model context window
- `AiFactExtractor` — utility computing deterministic `factsBlock` regex match-count strings from `AiFactCounter` patterns
- `AiGenerationProvider` — AutoCloseable backend interface defining `generate`, `generateWithTimings` → `AiGenerationTimings`, and `close`
- `AiGenerationProviderFactory` — factory resolving provider keys (`mock`, `llamacpp-jni`, `openai-compatible`) with model-over-run precedence
- `LlamaCppJniAiGenerationProvider` — local GGUF inference backend via `net.ladenthin:llama` JNI with lazy `ModelParameters` construction and `chatComplete` execution
- `LlamaCppJniConfig` — immutable 41-field value object with fluent `Builder` defaulting to `AiGenerationConfig.DEFAULT_*`
- `OpenAiCompatibleProviderSupport` — remote API helper serializing chat-completion JSON, enforcing HTTPS/loopback API-key security, and computing usage-based timings
- `AiCompletionParser` — stateless parser stripping Gemma-4 `<|channel|>thought` thinking blocks from raw completions

#### Data flow
- Maven plugin XML → `SrcMorphConfiguration` setters → `AiConditionEvaluator.matches(AiCondition, AiFileContext)` selects files (lazy line-count reads when `usesLines`) → oversize check via `AiOversizeStrategy.fromConfig` → `AiFactExtractor.factsBlock` prepends deterministic counts when applicable → `AiGenerationProviderFactory.create()` resolves provider (model-level override) → `AiGenerationRequest` (prompt key, source text, header) → local path: `LlamaCppJniConfig` → `ModelParameters` → `LlamaModel.chatComplete` → `ChatResponse`; remote path: `OpenAiCompatibleProviderSupport.requestBody()` → HTTPS POST `/chat/completions` → `ChatResponse` → `AiCompletionParser.parseCompletion()` strips thinking → `generate()`/`generateWithTimings()` returns text + `AiGenerationTimings` (token counts, decode rates) → `.ai.md` output

#### Dependencies
- Internal cross-package: `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`/`AiPromptDefinition`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`/`Java8CompatibilityHelper`/`ConvertToRecord`
- Internal same-package: `config` ↔ `provider` via `AiGenerationConfig` (feeds `LlamaCppJniConfig` defaults and factory routing); `AiGenerationTimings`, `MockAiGenerationProvider`, `LlamaCppJniProviderSupport`, `OpenAiCompatibleAiGenerationProvider`
- External: `net.ladenthin:llama` JNI (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `ChatResponseParser`, `FlashAttn`, `ReasoningFormat`), Jackson Databind (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok, `org.jspecify.annotations.Nullable`, Maven plexus reflection
- JDK: `java.util.regex`, `java.time.Instant`, `java.net.URL`, `java.io.IOException`/`File`, `java.util.*`

#### Cross-cutting
- Null-tolerance: `@Nullable` throughout; null inputs reset to `DEFAULT_*` constants or empty lists; defensive copies on write, unmodifiable views on read
- Immutability split: immutable value objects (`LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser`, factory) vs mutable single-threaded JavaBeans (`SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionGroup`); `LlamaCppJniAiGenerationProvider` explicitly single-threaded with unsynchronized lazy model init
- Builder pattern: `LlamaCppJniConfig.Builder` null-coalescing setters; `builder(String)` enforces non-null model path via `Objects.requireNonNull`
- Security: `OpenAiCompatibleProviderSupport.apiKeyFor()` blocks cleartext API keys to non-loopback HTTP hosts (`localhost`, `127.x.x.x`, `[::1]`); `IOException` for security violations and unterminated thinking blocks
- Fail-fast validation: `IllegalArgumentException` from `AiConditionEvaluator.validate`/`AiFactExtractor.validate` on invalid ranges, blank globs, bad regex, unparseable ISO-8601 dates
- Size-bound semantics: `minFileSizeBytes` exclusive lower bound, `maxFileSizeBytes` inclusive upper bound (`<=0` disables)
- Lombok `@ToString` on beans for build-log diagnostics; `@ToString.Exclude` on sensitive provider fields; `@EqualsAndHashCode`/`@ConvertToRecord` on config
