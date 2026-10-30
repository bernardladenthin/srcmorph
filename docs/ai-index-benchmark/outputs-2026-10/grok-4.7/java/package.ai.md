### java
- H: 1.0
- C: 64C7F162
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:45:59Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
Synthesizing a package summary from the provided per-file summaries.

The package configures and evaluates which sources a srcmorph run indexes, how oversized files are treated, and which model, sampling, and exact-fact settings drive `.ai.md` generation. It binds Maven configuration to runtime behavior, using numeric sentinels for defaults and environment-variable names for API keys. The domain covers both local llama.cpp and OpenAI-compatible backends.> Configures which sources a srcmorph run indexes and drives local llama.cpp or OpenAI-compatible generation of cleaned `.ai.md` completion text, including oversize policy and exact fact counts.

#### Purpose
- Holds Maven-bindable run settings for source filters, model routing, llama.cpp and OpenAI-compatible knobs, header metadata, and exact whole-file facts.
- Selects a generation backend, runs an `AiGenerationRequest`, and returns cleaned answer text plus optional timings.

#### Responsibilities
- Run configuration: `SrcMorphConfiguration` stores roots, excludes, size bounds, provider, prompts, models, field routing, facts, llama fallbacks, and `.ai.md` header versions.
- Per-model settings: `AiGenerationConfig` and immutable `LlamaCppJniConfig` carry sampling, KV cache, GPU/CPU layers, DRY, stop strings, remote endpoint, and optional `AiCalibration`.
- Source selection and oversize policy: `AiConditionEvaluator` matches condition trees against file facts; `AiOversizeStrategy` parses `<onOversize>` into abort, head-sample, map-reduce, or model-free deterministic output.
- Exact facts: `AiFactExtractor` turns `AiFactCounter` regexes into a deterministic `label: count` block prepended when counters remain after skips.
- Backend selection and execution: `AiGenerationProviderFactory` resolves `mock`, `llamacpp-jni`, or `openai-compatible`; JNI loads a GGUF `LlamaModel`; remote support builds chat-completions JSON without opening a socket.
- Answer cleanup: `AiCompletionParser` strips a leading Gemma-4 thinking block before storage.

#### Key units
- `SrcMorphConfiguration` — mutable shared bean for one srcmorph run, bindable from Maven or plain Java.
- `AiGenerationConfig` — one model definition’s inference, trim, stop, DRY, and endpoint settings.
- `AiConditionEvaluator` — walks `AiCondition` trees against `AiFileContext`; fail-fast `validate`; `usesLines` reports whether contents must be read.
- `AiConditionGroup` — `conditions` list so Maven binds nested `<and>`/`<or>` children.
- `AiOversizeStrategy` — `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; blank `<onOversize>` defaults to `FAIL`.
- `AiFactExtractor` — static `factsBlock` / `validate` for whole-file regex counts.
- `AiGenerationProvider` — closeable contract: `generate`, default `generateWithTimings`, no-op `close`.
- `AiGenerationProviderFactory` — `effectiveProvider` prefers a model provider over the run key; `create` instantiates mock, JNI, or OpenAI-compatible providers.
- `LlamaCppJniAiGenerationProvider` — local GGUF chat via llama.cpp JNI; maps `Timings` into `AiGenerationTimings`.
- `LlamaCppJniConfig` / `Builder` — 41-field immutable config; only path is `builder(modelPath)` then fluent setters and `build`.
- `OpenAiCompatibleProviderSupport` — package-private JSON body, `/chat/completions` URL, loopback-only cleartext API key, error excerpt, and timings.
- `AiCompletionParser` — `parseCompletion` drops a leading `<|channel>thought` … `<channel|>` block.

#### Data flow
- A caller populates `SrcMorphConfiguration` (provider, `aiDefinitions` as `AiModelDefinition`, `fieldGenerations`, `factDefinitions`, llama fallbacks, excludes, size bounds).
- Relative `modelPath` / `llamaModelPath` resolve against `modelDirectory`; empty `fieldGenerations` falls back to llama numeric fields and `llamaModelPath`.
- `AiConditionEvaluator` tests `fileName`, `sizeBytes`, `lineCount`, `lastModifiedEpochMilli`, and `relativePath`; `AiOversizeStrategy.fromConfig` decides abort, head-sample, map-reduce, or metadata plus head/tail.
- `AiFactExtractor.factsBlock` can prepend exact counts to an oversize AI summary.
- Run and model provider strings enter `effectiveProvider`; a null or blank key becomes `MockAiGenerationProvider`.
- `create` passes `LlamaCppJniConfig` and `AiPromptSupport` into the JNI or OpenAI-compatible provider; an `AiGenerationRequest` (prompt key, source file, source text, header) is the generation input.
- JNI path: lazy `ModelParameters` and `LlamaModel.chatComplete`, then `ChatResponseParser`, truncation/cache logs, then `AiCompletionParser`.
- Remote path: support builds `messages`, sampling, optional seed/stop/reasoning, and a guarded endpoint; the socket-sending provider is not described in these summaries.
- `generate` returns non-null text; `generateWithTimings` adds prompt/cache/predicted counts and rates, zeroed when timings are missing.

#### Dependencies
- Config types: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`, `AiPromptDefinition`.
- Generation types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig` defaults.
- Local binding: `LlamaModel`, `ModelParameters`, `ChatResponse`, `ChatResponseParser`, `Timings`, `FlashAttn`, `ReasoningFormat`, `LlamaCppJniProviderSupport`, `LlamaCppJniAiGenerationProvider`.
- Remote JSON: Jackson `ObjectMapper` / `ObjectNode`; `Usage`; `Function` environment lookup for `apiKeyEnv`.
- Named providers: `llamacpp-jni`, `openai-compatible`, `mock`; default provider `"mock"`; default remote base `https://openrouter.ai/api/v1`.
- JDK and helpers: `Instant`, `Pattern`/`Matcher`, `File`, `List`/`ArrayList`/`Collections`; `Java8CompatibilityHelper`; Lombok; `@Nullable`; `@ConvertToRecord`.

#### Cross-cutting
- Provider keys: `PROVIDER_MOCK` (`"mock"`), `PROVIDER_LLAMACPP_JNI` (`"llamacpp-jni"`), `PROVIDER_OPENAI_COMPATIBLE` (`"openai-compatible"`). Unrecognized keys throw `IllegalArgumentException`.
- Maven JavaBeans on mutable config holders: no-arg constructors, setters, Lombok `@ToString`; `equals`/`hashCode` omitted on `AiGenerationConfig`; `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Fail-fast: `IllegalArgumentException` on bad conditions, unparseable ISO-8601 dates, unknown `<onOversize>`, invalid fact patterns, and bad provider keys; `matches` throws `IllegalStateException` if no branch is set; generation failures surface as `IOException`.
- Sentinels: `-1` and empty strings often mean “do not forward / leave binding default”; `0` often means disabled; size bounds `<= 0` disable or unlimit file filters. `apiKeyEnv` stores an env-var name, never the key.
- Mutability split: run config beans are mutable, unsynchronized, and not thread-safe; `LlamaCppJniConfig` fields are final with unmodifiable list views. Evaluator, fact extractor, parser, and support are stateless after construction.
- Concurrency gap: JNI `LlamaModel` lazy init is unsynchronized and not volatile; `Builder` is not thread-safe; support’s static `MAPPER` is shared with no locks in that class.
