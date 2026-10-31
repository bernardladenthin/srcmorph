### java
- H: 1.0
- C: 93B74ADC
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:18:49Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures and executes AI-driven source-file documentation runs by selecting files, budgeting generation, and routing to local or remote LLMs.

#### Purpose
- Central configuration and pluggable generation backends for `srcmorph` AI summarization runs bindable from Maven / JSON / YAML.

#### Responsibilities
- Run configuration: scope, filters, prompts/models/field routing, facts, fallback, version headers, force/planOnly via `SrcMorphConfiguration`.
- File selection: `AiCondition` trees over `AiFileContext` — extensions, size/lines ranges, modified dates, path globs; `<and>`/`<or>` grouping.
- Generation tuning: single-step LLM knobs — context/trimming, sampling, cache/GPU, remote provider, DRY, `AiCalibration`.
- Oversize handling: `FAIL` / `SAMPLE` / `MAP_REDUCE` / `DETERMINISTIC` routing with token parsing; deterministic regex-count `FACTS` block.
- Provider routing: `mock` / `llamacpp-jni` / `openai-compatible` backends for `AiGenerationRequest` header replacement.
- Request/response plumbing: prompt building, local GGUF inference, remote chat-completions JSON, thinking-block stripping, timings.

#### Key units
- `SrcMorphConfiguration` — mutable JavaBean for entire run; directories, filters, providers, prompts, models, routing, fallback.
- `AiGenerationConfig` — mutable JavaBean for one generation step; sampling, budgeting, caching, GPU, remote, DRY.
- `AiConditionEvaluator` — stateless evaluator; `matches`, `validate`, `usesLines`.
- `AiConditionGroup` — `List<AiCondition>` holder for `<and>`/`<or>` Maven binding.
- `AiFactExtractor` — static regex utility; `factsBlock`, `validate`, `countMatches`.
- `AiOversizeStrategy` — enum with `configValue`, `fromConfig`; default `FAIL`.
- `AiGenerationProvider` — pluggable backend contract; `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` — backend router; `create`, `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` — local GGUF executor via `LlamaModel.chatComplete`.
- `LlamaCppJniConfig` + `Builder` — immutable 41-field value object seeded from `AiGenerationConfig.DEFAULT_*`.
- `AiCompletionParser` — thinking-block stripper; `parseCompletion`.
- `OpenAiCompatibleProviderSupport` — offline remote helper; `requestBody`, `endpoint`, `apiKey`, `timings`, `errorMessage`.

#### Data flow
- `SrcMorphConfiguration` supplies scope + `fieldGenerations` routing + `factDefinitions` + fallback to engine.
- `AiConditionEvaluator.matches` filters `AiFileContext` (fileName, relativePath, sizeBytes, lineCount, lastModified); `usesLines` signals file reads.
- Oversized sources branch by `AiOversizeStrategy.fromConfig`; `DETERMINISTIC` prepends `AiFactExtractor.factsBlock` counts.
- `AiGenerationRequest` (promptKey, sourceFile, sourceText) → `AiGenerationProviderFactory.create` → selected provider.
- Local path: prompts + inference params → `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser.parseCompletion` → `String` / `AiGenerationTimings`.
- Remote path: systemPrompt/userMessage + sampling fields → Jackson JSON body → `baseUrl + /chat/completions` → `ChatResponse`/`Usage` → `AiGenerationTimings`.

#### Dependencies
- Internal: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiFactDefinition`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`.
- External: `java.time.Instant`, `java.util.regex.Pattern/Matcher`, `java.io.File`, `java.net.URL`, Jackson `ObjectMapper/ArrayNode/ObjectNode`, `net.ladenthin.llama` — `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`, `Usage`, Lombok `@ToString/@EqualsAndHashCode`, JSpecify `@Nullable`.

#### Cross-cutting
- Mutable non-synchronized JavaBeans with `DEFAULT_*` constants, null-coalescing setters, unmodifiable list views; assume populate-then-read-only.
- Stateless thread-safe utilities (`AiConditionEvaluator`, `AiFactExtractor`) vs unsynchronized lazy `LlamaModel` init and `Builder`; immutable `AiOversizeStrategy`, `LlamaCppJniConfig`; shared static `ObjectMapper MAPPER`.
- Fail-fast validation: `IllegalArgumentException` on bad conditions/facts/oversize tokens/unknown provider/non-http(s) `baseUrl`; `IllegalStateException` on unvalidated match; `IOException` on provider failure/truncated thinking block.
- Null/blank-tolerant routing: empties mean unset/vacuous; blank provider → mock; blank `remoteModel/reasoningEffort/apiKeyEnv` omitted; sentinel `-1` keep default, `0` disable.
- Security/conventions: cleartext API-key refusal except loopback `localhost/127.x/[::1]`; tri-state `chatTemplateEnableThinking` defers to template; `<|channel>thought` block stripped before persistence; Lombok `@Exclude` on native handles.
