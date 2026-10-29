### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:40:00Z
- G: 
- A: 0.0.0
- X: file
---
> Carries all per-model AI inference parameters (sampling, context, GPU offload, KV-cache, DRY, provider routing) as a mutable JavaBean between the Maven plugin configuration layer and llama.cpp / OpenAI-compatible provider implementations.

#### Purpose
- Mutable configuration POJO holding every tunable for a single AI generation step in the srcmorph indexing pipeline.
- Populated by the Maven plugin framework via setters; consumed by `LlamaCppJniAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`.

#### Type
- `public class AiGenerationConfig` (no extends/implements).
- `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- 45 private instance fields; 42 `public static final` constants.

#### Input
- All 45 fields set via public setters (Maven plugin property injection).
- `setCalibration(AiCalibration)` accepts an optional per-machine timing calibration object.
- `setDrySequenceBreakers(List<String>)` and `setStopStrings(List<String>)` accept nullable lists.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` accept `@Nullable String` with null-to-default coercion.

#### Output
- 45 getters returning the configured values (primitives, `String`, `@Nullable Boolean`, `@Nullable AiCalibration`, `List<String>`).
- `getDrySequenceBreakers()` and `getStopStrings()` return `Collections.unmodifiableList(...)` views.
- No side effects; pure data carrier.

#### Core logic
- No business logic — a flat JavaBean with default values baked into field initializers.
- Key design decisions encoded in defaults:
  - `DEFAULT_THREADS = Math.min(4, availableProcessors())` — avoids ggml spinning-barrier pathology on ≤4-core machines.
  - `DEFAULT_MAX_INPUT_CHARS` auto-calculated from `contextSize × charsPerToken − maxOutputTokens − promptOverhead − safetyMargin` when `charsPerToken > 0`; falls back to the constant otherwise.
  - `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING = null` — tri-state: null omits the kwarg, true/false forward it.
  - `DEFAULT_SEED = -1` — random per request; a fixed seed makes re-index diffs reviewable (not bit-reproducible across thread counts/backends).
  - `DEFAULT_SWA_FULL = true` + `DEFAULT_CACHE_REUSE = 256` — enables cross-request KV prefix reuse for multi-file batch indexing.
  - `DEFAULT_FLASH_ATTN = false` — leaves llama.cpp's `auto` default in force; must be `true` to use a quantized V cache.
  - `DEFAULT_DRY_PENALTY_LAST_N = -1` is a srcmorph sentinel (not forwarded); the provider must guard against sending it to the binding.
  - `DEFAULT_API_KEY_ENV = ""` — no key sent unless a model explicitly names an env var, preventing accidental key leakage to custom endpoints.

#### Public API
- `AiGenerationConfig()` — no-arg constructor, applies all defaults.
- 45 getter/setter pairs covering all 45 fields (listed below by field name). Each getter returns the field; each setter assigns it (with null-coercion on String/list fields).

Getter/setter pairs (45 total, indexed 1–45):
1. `modelPath` — `getModelPath() -> @Nullable String` / `setModelPath(@Nullable String)`
2. `contextSize` — `getContextSize() -> int` / `setContextSize(int)`
3. `maxOutputTokens` — `getMaxOutputTokens() -> int` / `setMaxOutputTokens(int)`
4. `temperature` — `getTemperature() -> float` / `setTemperature(float)`
5. `threads` — `getThreads() -> int` / `setThreads(int)`
6. `charsPerToken` — `getCharsPerToken() -> int` / `setCharsPerToken(int)`
7. `maxInputChars` — `getMaxInputChars() -> int` / `setMaxInputChars(int)`
8. `warnOnTrim` — `isWarnOnTrim() -> boolean` / `setWarnOnTrim(boolean)`
9. `topP` — `getTopP() -> float` / `setTopP(float)`
10. `topK` — `getTopK() -> int` / `setTopK(int)`
11. `minP` — `getMinP() -> float` / `setMinP(float)`
12. `topNSigma` — `getTopNSigma() -> float` / `setTopNSigma(float)`
13. `repeatPenalty` — `getRepeatPenalty() -> float` / `setRepeatPenalty(float)`
14. `chatTemplateEnableThinking` — `getChatTemplateEnableThinking() -> @Nullable Boolean` / `setChatTemplateEnableThinking(@Nullable Boolean)`
15. `cachePrompt` — `isCachePrompt() -> boolean` / `setCachePrompt(boolean)`
16. `swaFull` — `isSwaFull() -> boolean` / `setSwaFull(boolean)`
17. `cacheReuse` — `getCacheReuse() -> int` / `setCacheReuse(int)`
18. `gpuLayers` — `getGpuLayers() -> int` / `setGpuLayers(int)`
19. `seed` — `getSeed() -> int` / `setSeed(int)`
20. `cpuMoeLayers` — `getCpuMoeLayers() -> int` / `setCpuMoeLayers(int)`
21. `cpuFfnLayers` — `getCpuFfnLayers() -> int` / `setCpuFfnLayers(int)`
22. `kvUnifiedPerSlot` — `getKvUnifiedPerSlot() -> int` / `setKvUnifiedPerSlot(int)`
23. `lazyMode` — `getLazyMode() -> String` / `setLazyMode(@Nullable String)`
24. `repeatLastN` — `getRepeatLastN() -> int` / `setRepeatLastN(int)`
25. `cacheTypeK` — `getCacheTypeK() -> String` / `setCacheTypeK(@Nullable String)`
26. `cacheTypeV` — `getCacheTypeV() -> String` / `setCacheTypeV(@Nullable String)`
27. `flashAttn` — `isFlashAttn() -> boolean` / `setFlashAttn(boolean)`
28. `batchSize` — `getBatchSize() -> int` / `setBatchSize(int)`
29. `ubatchSize` — `getUbatchSize() -> int` / `setUbatchSize(int)`
30. `threadsBatch` — `getThreadsBatch() -> int` / `setThreadsBatch(int)`
31. `mainGpu` — `getMainGpu() -> int` / `setMainGpu(int)`
32. `devices` — `getDevices() -> String` / `setDevices(String)`
33. `provider` — `getProvider() -> String` / `setProvider(String)`
34. `remoteModel` — `getRemoteModel() -> String` / `setRemoteModel(String)`
35. `baseUrl` — `getBaseUrl() -> String` / `setBaseUrl(String)`
36. `apiKeyEnv` — `getApiKeyEnv() -> String` / `setApiKeyEnv(String)`
37. `reasoningEffort` — `getReasoningEffort() -> String` / `setReasoningEffort(String)`
38. `reasoningBudgetTokens` — `getReasoningBudgetTokens() -> int` / `setReasoningBudgetTokens(int)`
39. `dryMultiplier` — `getDryMultiplier() -> float` / `setDryMultiplier(float)`
40. `dryBase` — `getDryBase() -> float` / `setDryBase(float)`
41. `dryAllowedLength` — `getDryAllowedLength() -> int` / `setDryAllowedLength(int)`
42. `dryPenaltyLastN` — `getDryPenaltyLastN() -> int` / `setDryPenaltyLastN(int)`
43. `drySequenceBreakers` — `getDrySequenceBreakers() -> List<String>` (unmodifiable) / `setDrySequenceBreakers(@Nullable List<String>)`
44. `stopStrings` — `getStopStrings() -> List<String>` (unmodifiable) / `setStopStrings(@Nullable List<String>)`
45. `calibration` — `getCalibration() -> @Nullable AiCalibration` / `setCalibration(@Nullable AiCalibration)`

#### Dependencies
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.config.AiCalibration` (referenced field type)
- Javadoc references: `net.ladenthin.llama.parameters.ModelParameters`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `InferenceParameters`

#### Exceptions / Errors
- No exceptions thrown or caught.
- `@SuppressWarnings("NullAway.Init")` suppresses NullAway initialization warnings on nullable fields.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` coerce `null` to the corresponding `DEFAULT_*` constant.
- `setDrySequenceBreakers`, `setStopStrings` coerce `null` to a new empty `ArrayList`.

#### Concurrency
- Not thread-safe; mutable JavaBean managed by identity.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable views but do not synchronize; concurrent mutation via setters is unsafe.
- No `volatile`, `synchronized`, or `AtomicReference` usage.
