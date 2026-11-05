# srcmorph production-code review: packages, naming, null-safety

Read-only review of `srcmorph/src/main/java/net/ladenthin/srcmorph/` (96 classes in 7 packages),
`srcmorph-cli/src/main/java` and `srcmorph-maven-plugin/src/main/java`, state of 2026-11-05
(`1.5.0-SNAPSHOT`). Paths below are relative to `srcmorph/src/main/java/net/ladenthin/srcmorph/`
unless they start with a module name. Policy: `workspace/policies/code-quality-todos.md` sections 2 and 3.

## Summary

| Section | High | Medium | Low |
|---|---|---|---|
| 1. Package hierarchy | 1 | 4 | 4 |
| 2. Class and method naming | 3 | 7 | 5 |
| 3. Null-safety precision | 3 | 4 | 6 |

**Should the package restructure happen now? Yes, but a limited one.** Reasons:

- The CLI and the plugin import only `config` (4 classes), `engine` (7 classes) and one static method,
  `OpenAiCompatibleAiGenerationProvider.withoutUserInfo`. A move inside indexer/document/prompt/support
  therefore breaks no known consumer. External library users are possible, since all 7 packages are
  exported (`module-info.java:77-83`), but none is known.
- The declared layering has two real leaks: `config -> prompt`, and a hidden `document -> config` edge
  that ArchUnit cannot see. `config` is also a kitchen sink (22 classes, 3 concerns).
- `main` sits between releases, and breaking changes are allowed.
- What it costs: the 85 FQNs in PIT's `targetClasses` (`srcmorph/pom.xml:695ff`), the module-info
  exports (+3 packages), the `layeredArchitecture` rule, the test tree (it mirrors the packages 1:1),
  the spotbugs-exclude entries and CLAUDE.md.
- Leave `provider`, `engine` and `prompt` structurally as they are.
- Do it on a `main` with no open PR touching the core, because every moved file conflicts.
- Drop the `Ai` prefix only in that same pass, or not at all; doing it piecemeal is the expensive option.

---

## 1. Package hierarchy

**P1 (high): `config` is a kitchen sink.** 22 classes with three concerns:
- (a) 14 beans (13 Jackson-bound, plus `AiGenerationConfig`), `SrcMorphConfigurationFileLoader` and `AiModelDefinitionSupport`.
- (b) Routing logic: `AiConditionEvaluator` (454 lines), `AiFieldGenerationSelector`, `AiFileContext`.
- (c) A regex fact-counting engine: `AiFactExtractor` (414 lines), `AiFactDefinitionLoader` (318),
  `AiFactDefinitionSupport`.

(b) and (c) are runtime logic over files, not configuration. Only one code edge runs from a bean to the
logic: `SrcMorphConfiguration.java:58` takes its default from `AiConditionEvaluator.CONTENT_HEAD_CHARS`.
Every other reference is Javadoc.

**P2 (medium): the `config -> prompt` edge exists only because a bean sits in `prompt`.**
- `SrcMorphConfiguration.java:9,63` imports `prompt.AiPromptDefinition`, a Jackson bean like every
  other one in `config`.
- To allow this, `CoreArchitectureTest.java:137` lets Config access Prompt. So the order CLAUDE.md
  states ("prompt above config") is not the order that is actually enforced.

**P3 (medium): a hidden `document -> config` edge.**
- `document/AiMdHeaderSupport.java:10,130` reads `config.AiFactExtractor.FACTS_HEADER`
  (`AiFactExtractor.java:53`).
- That field is a compile-time constant, so javac inlines it and ArchUnit, which reads bytecode, never
  sees the edge.
- The rule (`CoreArchitectureTest.java:134-141`) allows Document to access only Support. The source
  violates it.

**P4 (medium): `document` holds generation I/O that is not part of the `.ai.md` format.**
- `AiGenerationRequest` is used by prompt, provider and indexer. `AiGenerationResult` is used only by
  indexer.
- `AiGenerationRequest.currentHeader()` (`document/AiGenerationRequest.java:82`) is never read by a
  provider or a prompt. It is only passed on (`prompt/AiPromptPreparationSupport.java:64,77`,
  `indexer/AiFieldGenerationSupport.java:401`), and it is the only reason prompt and provider depend
  on document.
- *Uncertain:* whether it is kept on purpose for a future prompt that reads the previous header.

**P5 (medium): `indexer` mixes the walkers and the plan with a self-contained generation pipeline.**
- `AiFieldGenerationSupport` (1133 lines) plus `AiMapReduceSupport`, `AiTokenWindow`,
  `AiOverflowBackoff`, `AiInputWindowCalculator` and `AiRouteEstimator` form a closed cluster.
- The cluster has no code edge back to the walkers; the only reference is a comment at
  `AiFieldGenerationSupport.java:1101`. The walkers, `AiIndexPlan` and `AiCalibrationRunner` call into
  it. That is a clean sub-package seam.

**P6 (low): `support` holds four domain classes with a single consumer each.**
- `AiSourceChunker` and `AiDeterministicSummary`: used only by `AiFieldGenerationSupport`.
- `AiGenerationTimeEstimator`: a fitted timing model, used by indexer and `GenerateEngine.java:228`.
- `AiProgressBar`: used only by `GenerateEngine.java:306`.
- None of them is a foundation helper.

**P7 (low): a generic helper is nested inside the fact engine.**
- The package-private `AiFactExtractor.ReadBudget` (`AiFactExtractor.java:329`) limits how much a
  regex may read.
- `AiConditionEvaluator.java:153-155` reuses it for `contentMatches`.
- Once `config` is split, it has to become a public class in `support`.

**P8 (low): URL masking is reached through a provider class.**
- `withoutUserInfo` (`provider/OpenAiCompatibleAiGenerationProvider.java:265`) is called by the CLI
  (`srcmorph-cli/.../cli/Main.java:256`), by `engine/EngineSupport.java:394` and by
  `engine/ListModelsEngine.java:118`.
- It is a string utility and belongs in `support`.

**P9 (low): every package is exported, although most are de facto internal.**
- `module-info.java:77-83` exports all 7 packages, including `support` (`Java8CompatibilityHelper`,
  `ReplaceWithLibrary`).
- Not exporting `support` and `indexer` would make the real surface (config + engine) explicit.
- Optional: unknown external consumers would break.

**Fine as is:**
- `engine`: reports and records next to their engines is cohesive.
- `prompt`, once P2 and P4 are applied.
- `document`, once P4 is applied: pure `.ai.md` model plus codecs.
- `provider` (18 classes) stays flat. The `LlamaCppJni*` / `OpenAiCompatible*` prefixes already group it,
  and the `*ProviderSupport` classes and `RequestRateLimiter` are package-private already. Splitting it
  into `provider.llamacpp` / `provider.openai` would create a root-to-sub cycle: the factory creates both
  providers, and both implement the root interface. `noPackageCycles` slices at the top level
  (`CoreArchitectureTest.java:82`), so it would not catch that cycle.
- CLI and plugin packages: fine.

### Target tree

```
net.ladenthin.srcmorph
├── engine               AggregatePackagesEngine, AggregateProjectEngine, CalibrateEngine, GenerateEngine,
│                        ListModelsEngine, CalibrationReport, ModelListReport, RunRecord, GenerateResult,
│                        SrcMorphException, EngineSupport, AiProgressBar*
├── indexer              SourceFileIndexer, PackageIndexer, ProjectIndexer, AiIndexPlan, AiIndexOutcome,
│                        AiIndexTally, AiHeaderHashes, AiCalibrationRunner, AiCalibrationMeasurement
├── indexer.generation   AiFieldGenerationSupport, AiMapReduceSupport, AiTokenWindow, AiOverflowBackoff,
│                        AiInputWindowCalculator, AiRouteEstimator, AiGenerationResult*,
│                        AiGenerationTimeEstimator*, AiSourceChunker*, AiDeterministicSummary*
├── document             AiMdDocument, AiMdHeader, AiMdDocumentCodec, AiMdHeaderCodec, AiMdHeaderSupport,
│                        AiMdChildEntryLineFormatter, AiMdLeadExtractor
├── provider             (unchanged, 18 classes)
├── prompt               AiPromptSupport, AiPromptPreparationSupport, AiPreparedPrompt,
│                        AiPromptTemplateLoader, AiGenerationRequest*
├── routing              AiConditionEvaluator*, AiFieldGenerationSelector*, AiFileContext*
├── facts                AiFactExtractor*, AiFactDefinitionLoader*, AiFactDefinitionSupport*
├── config               SrcMorphConfiguration, SrcMorphConfigurationFile, SrcMorphConfigurationFileLoader,
│                        AiModelDefinition, AiGenerationConfig, AiModelDefinitionSupport,
│                        AiFieldGenerationConfig, AiCondition, AiConditionGroup, AiRangeCondition,
│                        AiFactCounter, AiFactDefinition, AiCalibration, AiRegenerateOn,
│                        AiIncompleteStrategy, AiOversizeStrategy, AiPromptDefinition*
└── support              AiChecksumSupport, AiPathSupport, AiTimeSupport, AiGlob, AiSourceLines, AiShebang,
                         AiSourceExcludeFilter, AiProfileFileSupport, AiGeneratorVersion,
                         Java8CompatibilityHelper, ConvertToRecord, ReplaceWithLibrary,
                         ReadBudget* (from AiFactExtractor), UrlUserInfo* (withoutUserInfo)
(* = moved or extracted; names here are the current ones, the renames are in section 2)
```

The tree uses the current names. Section 2's renames could also make `SummaryGenerator` the reason to
call the sub-package `indexer.generation`.

**Layering:**

```
engine -> indexer(..) -> {provider, document, routing} -> prompt -> facts -> config -> support
```

- `provider -> prompt, config`
- `document -> facts` (for `FACTS_HEADER`)
- `routing -> facts, config`
- `prompt -> config`
- These are all acyclic, so the `layeredArchitecture` rule can drop the `Config -> Prompt` exception.

### Moves implied

| # | Move | Reason | Breaks |
|---|---|---|---|
| M1 | `prompt.AiPromptDefinition` -> `config` | bean among beans; removes `config -> prompt` (P2) | library FQN |
| M2 | `config.AiConditionEvaluator`, `AiFieldGenerationSelector`, `AiFileContext` -> `routing` | routing logic, not config (P1); move `CONTENT_HEAD_CHARS` to `SrcMorphConfiguration` (`:58`) so no bean depends on it | library FQN |
| M3 | `config.AiFactExtractor`, `AiFactDefinitionLoader`, `AiFactDefinitionSupport` -> `facts` | fact engine, not config (P1); turns the inlined `document -> config` edge into an allowed `document -> facts` (P3) | library FQN |
| M4 | `AiFactExtractor.ReadBudget` -> `support.ReadBudget` (public) | shared by routing and facts after M2 and M3 (P7) | none (package-private today) |
| M5 | `document.AiGenerationRequest` -> `prompt`, dropping `currentHeader` | its users are prompt, provider and indexer; drops `prompt/provider -> document` (P4) | library FQN + constructor |
| M6 | `document.AiGenerationResult` -> `indexer.generation` | only the generation pipeline produces it (P4) | library FQN |
| M7 | `AiFieldGenerationSupport`, `AiMapReduceSupport`, `AiTokenWindow`, `AiOverflowBackoff`, `AiInputWindowCalculator`, `AiRouteEstimator` -> `indexer.generation` | closed cluster, no back-edge (P5) | library FQN |
| M8 | `support.AiSourceChunker`, `AiDeterministicSummary`, `AiGenerationTimeEstimator` -> `indexer.generation` | single-consumer domain logic (P6) | library FQN |
| M9 | `support.AiProgressBar` -> `engine` | single consumer `GenerateEngine` (P6) | library FQN |
| M10 | `withoutUserInfo` -> `support` (e.g. `UrlUserInfo.mask`) | string utility the CLI reaches through a provider (P8) | library API (CLI updated) |

None of the moves touches a configuration-file key or the plugin's goals or properties.

---

## 2. Class and method naming

**N1 (high): `provider/LlamaCppJniConfig` -> `ProviderConfig` (or `ModelConfig`).** No move.
- It is the config of all three providers: `OpenAiCompatibleAiGenerationProvider(LlamaCppJniConfig, ...)`
  at `provider/OpenAiCompatibleAiGenerationProvider.java:142`, and the factory at
  `provider/AiGenerationProviderFactory.java:61` with a parameter named `llamaConfig`.
- It carries `baseUrl`, `apiKeyEnv`, `stream`, `requestsPerMinute` and `maxRetries`.
- Its Javadoc, "Immutable configuration for the llama.cpp JNI provider" (`:16`), is stale.
- Rename with it: `LlamaCppJniConfigFactory.fromGenerationConfig` -> `ProviderConfig.from(AiGenerationConfig)`,
  and `EngineSupport.resolveLlamaCppJniConfig` (`engine/EngineSupport.java:278`) -> `resolveProviderConfig`.
- Breaks: library API.

**N2 (high): `indexer/AiFieldGenerationSupport` -> `SummaryGenerator`.** Rides with M7.
- It is the largest class (1133 lines): it generates one body per rule, with single-shot, map-reduce,
  sample, deterministic and overflow-retry paths.
- `*Support` hides all of that.
- The class Javadoc (`:36-48`) is stale: it says "Iterates over a list of ... entries", but `generate`
  takes one rule, and it is used by all three walkers, not two.
- Breaks: library FQN.

**N3 (high): `srcmorph-maven-plugin/.../mojo/AbstractAiIndexMojo` -> `AbstractSrcMorphMojo`.** No move.
- "AiIndex" is the plugin's pre-rename name; CLAUDE.md:276 keeps it only as a historical name.
- Breaks: the FQN of an abstract class only. Goals, properties and the descriptor stay the same.

**N4 (medium): `config/AiFieldGenerationConfig` -> `AiRule` (or `RoutingRule`); `AiFieldGenerationSelector` -> `RuleSelector`.**
The selector rename rides with M2.
- The class Javadoc already calls it "One routing rule" (`:11`). "Field generation" dates from per-field
  generation, which no longer exists.
- Rename the class now (breaks the library API).
- Rename the keys `fieldGenerations` / `packageFieldGenerations` / `projectFieldGenerations` only with a
  deliberate config-schema break: that breaks every configuration file.

**N5 (medium): `document/AiMdHeader` single-letter accessors.** No move.
- `h() c() d() t() g() a() x() p() n() m() k()` (`:207-311`) leak the on-disk keys into the Java API.
  Callers read things like `header.p(), header.n()` (`indexer/SourceFileIndexer.java:339`).
- Proposed: `headerVersion()`, `checksum()`, `sourceDate()`, `generatedAt()`, `generatorVersion()`,
  `aiVersion()`, `nodeType()`, `promptHash()`, `factsHash()`, `modelHash()`, `childrenHash()`.
- The codec keeps the letters. Breaks: library API only; the file format is unchanged.

**N6 (medium): `*Support` names that hide a role.** Each rides with its package where one moves.

| Current | Proposed | Note |
|---|---|---|
| `config/AiModelDefinitionSupport` | `ModelRegistry` (`getConfig(key)` -> `config(key)`) | |
| `prompt/AiPromptSupport` | `PromptCatalog` | |
| `prompt/AiPromptPreparationSupport` | `PromptPreparer` | |
| `facts/AiFactDefinitionSupport` | `FactGroups` | M3 |
| `document/AiMdHeaderSupport` | `AiMdRewritePolicy` (`shouldWrite` -> `shouldRewrite`) | it reads the target file, which the name does not hint at |
| `AiMapReduceSupport` | `MapReduceMath` | M7 |

- Keep `LlamaCppJniProviderSupport` / `OpenAiCompatibleProviderSupport`: they are package-private, and
  the split is documented as load-bearing for the PIT gate (CLAUDE.md:615-624).
- Keep `EngineSupport` (package-private).
- `Java8CompatibilityHelper` names exactly what it is; keep it.

**N7 (medium): the `Ai` prefix.**
- It is on 65 of 96 core classes. It carries meaning only in `AiMd*`, the `.ai.md` format name
  (7 classes); keep those.
- On `AiGlob`, `AiShebang`, `AiSourceLines`, `AiPathSupport`, `AiTimeSupport`, `AiChecksumSupport`,
  `AiProgressBar`, `AiIndexTally`, `AiRangeCondition`, `AiFileContext` and others, nothing is AI.
- Drop it only in the same pass as the moves, since each moved FQN breaks anyway.
- Watch the collisions:
  - `AiCondition` -> `Condition` clashes with `java.util.concurrent.locks.Condition`
    (`provider/RequestRateLimiter.java:15`).
  - `AiPathSupport` -> `Paths` clashes with `java.nio.file.Paths`, imported in 4 files; use `SourcePaths`.
- Breaks: library API everywhere.

**N8 (medium): three `Entry` types in one signature.**
- `engine/GenerateEngine.java:379-386` takes an `AiIndexPlan.Entry` and a `Map.Entry<String,String>`, and
  returns a `RunRecord.Entry`.
- The `Map.Entry` is used as a (promptHash, factsHash) pair, from `SourceFileIndexer.recordedHashes`
  (`indexer/SourceFileIndexer.java:337`) and `GenerateEngine.recordedHeader` (`:448`).
- Proposed: `AiIndexPlan.Entry` -> `Route`, `RunRecord.Entry` -> `FileRecord`, and a small value type
  `RecordedHashes`. No move.

**N9 (medium): `SrcMorphConfiguration.pluginVersion` -> `generatorVersion`.**
- It is srcmorph's own version, for the CLI and the plugin alike (`config/SrcMorphConfiguration.java:70,484`;
  `support/AiGeneratorVersion.java:15`). It is the header's `G`.
- `RunRecord(String pluginVersion, ...)` (`engine/RunRecord.java:61`) changes with it.
- Breaks: the config key `pluginVersion`.

**N10 (medium): three 40-knob classes for one model.**
- `config/AiModelDefinition` (bound, 985 lines), `config/AiGenerationConfig` (mutable copy with
  `modelPath` resolved, 1396 lines; copied field by field at `AiModelDefinitionSupport.java:145ff`) and
  `provider/LlamaCppJniConfig` (immutable, 1102 lines; copied again in `LlamaCppJniConfigFactory`).
- The name `AiGenerationConfig` does not say "resolved definition".
- The better fix is to remove the middle class, so that `AiModelDefinitionSupport` builds the immutable
  `ProviderConfig` (N1) directly; otherwise rename it `ResolvedModel`.
- Breaks: library API.

**N11 (low): `provider/AiGenerationTimings` holds the generated text** (`text()`, `:69`).
Proposed: `TimedGeneration`.

**N12 (low): one constant name, two values.** `UNKNOWN_CONTEXT_LENGTH` is `-1` in
`provider/GgufModelInfo.java:25` but `0` in `provider/RemoteModel.java:22`.

**N13 (low): verbs.**
- `take*` reads and resets: `AiGenerationProvider.takeLoadNanos()` (`provider/AiGenerationProvider.java:63`)
  and `AiFieldGenerationSupport.takeUsage()` (`:227`). "Take" conveys consumption, as in `BlockingQueue.take`,
  and both are documented, so they are fine.
- No `get*`, `is*` or `has*` with a side effect was found outside the beans; I grepped all of them.
- `AiPromptPreparationSupport.getBasePromptLength` (`prompt/AiPromptPreparationSupport.java:101`) is a
  computed value, not a property; `basePromptLength` would fit better.

**N14 (low): CLI names.**
- `Main.logConfigurationTransformation()` (`srcmorph-cli/.../Main.java:242`) logs the echo -> `logConfigurationEcho`.
- `configurationToYAML` (`:156`) vs `configurationToJson` (`:143`) -> `configurationToYaml`.
- `Main.readString` / `fromJson` / `fromYaml` (`:82,94,106`) are public but used only by tests.
- `CCommand.GenerateFileIndex` vs the goal `generate`: leave it, since renaming breaks `command:` values.

**N15 (low): stale plugin package Javadoc.**
`srcmorph-maven-plugin/.../net/ladenthin/maven/srcmorph/package-info.java:6-7` says "alongside Java
source trees via llama.cpp".

---

## 3. Null-safety precision (ranked by how likely a caller is misled)

Background: the loader's mappers set `Nulls.SKIP` for values and `Nulls.FAIL` for list items
(`config/SrcMorphConfigurationFileLoader.java:134`). So a key set to nothing keeps its field default,
and `- ` list items are refused. A key that is **absent** still leaves a field without a default null.
Exactly eight such non-null-typed fields exist; they are hidden by class-level `NullAway.Init`:

- `AiFactCounter.label`, `AiFactCounter.pattern`
- `AiFactDefinition.key`
- `AiFieldGenerationConfig.promptKey`, `AiFieldGenerationConfig.aiDefinitionKey`
- `AiModelDefinition.key`
- `AiPromptDefinition.key`
- `SrcMorphConfiguration.baseDirectory`

**S1 (high): `AiFieldGenerationConfig.getPromptKey()` / `getAiDefinitionKey()` (`config/AiFieldGenerationConfig.java:40,46,163,181`) are typed non-null but are null for every skip rule, by design.**
- The code knows this:
  - `AiFieldGenerationSelector.java:145` checks `== null`.
  - `engine/EngineSupport.java:132-139` collects them into a `List<@Nullable String>`, commented
    "a skip rule's prompt".
  - `EngineSupport.java:316-317` checks `aiDefinitionKey == null`.
  - `prompt/AiPromptTemplateLoader.java:84` takes a `Collection<@Nullable String>`.
- Fix: make both getters `@Nullable`; optionally add non-null accessors on a validated route rule.
- Breaks: library API.

**S2 (high): `SrcMorphConfiguration.getBaseDirectory()` (`config/SrcMorphConfiguration.java:49,79`) has no default and is null on `new SrcMorphConfiguration()`.**
- `engine/GenerateEngine.java:118` and `engine/AggregatePackagesEngine.java:93` dereference it, so a
  programmatic caller who forgets the setter gets an NPE.
- The loader guards it (`SrcMorphConfigurationFileLoader.java:300-302`, `configuredBase != null`).
- Fix: either `@Nullable` plus a `SrcMorphException` in the engines, or a default (`new File(".")`).
- Breaks: library API.

**S3 (high): `LlamaCppJniConfig.modelPath()` is `""` for "no model file" (a remote model).**
- `provider/LlamaCppJniConfigFactory.java:36-37` does `modelPath == null ? "" : modelPath`, although the
  source getter `AiGenerationConfig.getModelPath()` is precisely `@Nullable`.
- A caller cannot tell "unset" apart from a path.
- Fix: `@Nullable String modelPath()`, and the builder argument becomes optional.
- Breaks: library API.

**S4 (medium): definition keys use a `requireNonNull` workaround.**
- The non-null `getKey()` of `AiModelDefinition` (`:40,96`), `AiPromptDefinition` (`prompt/AiPromptDefinition.java:36,45`)
  and `AiFactDefinition` (`:37,50`) is checked with `Objects.requireNonNull` at
  `AiModelDefinitionSupport.java:91`, `prompt/AiPromptSupport.java:69` and `AiFactDefinitionSupport.java:59`.
- The engines then catch `NullPointerException` to report a configuration error (`EngineSupport.java:143,163,255`).
- Fix: `@Nullable` getters and an explicit `IllegalArgumentException`; stop catching NPE.
- Breaks: library API.

**S5 (medium): `AiFactCounter.getLabel()` / `getPattern()` (`config/AiFactCounter.java:51,54,70,88`) are non-null but checked for null.**
- `AiFactExtractor.validate` checks them at `:155,166`, and `isReported` at `:193`.
- `factsHash` (`:399-401`) appends them unchecked; it relies on prior validation, otherwise it hashes
  `"null"`.
- Fix: `@Nullable` getters. Breaks: library API.

**S6 (medium): `AiGenerationProviderFactory.effectiveProvider` (`provider/AiGenerationProviderFactory.java:43-44`) is `@Nullable` in and out, but every caller passes non-null.**
- The callers are `EngineSupport.java:322`, `ListModelsEngine.java:110` and the factory's own `:62`.
  Each passes `getGenerationProvider()` / `provider()`, both non-null.
- Tightening it removes the dead `provider != null` (`EngineSupport.java:327`) and
  `effective == null` (`:63`) checks.
- Related: `DEFAULT_PROVIDER = ""` (`AiGenerationConfig.java:372`) is a sentinel for "inherit the run's
  provider"; `@Nullable` would read better.

**S7 (medium): `ProjectIndexer` takes four jointly-optional parameters (`indexer/ProjectIndexer.java:162-171`).**
- The provider, the rule, the prompt support and the model support are each `@Nullable`, but they only
  mean something together. Passing three of the four silently disables the overview (`:184-198`).
- Fix: one `@Nullable` overview parameter object.
- `SourceFileIndexer.classify` (`:256-260`, two jointly-nullable parameters) has the same shape.
- Breaks: library API.

**S8 (low): setters with a non-null parameter that guard null.**
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl` and `setApiKeyEnv` take `final String`
  and then test `x == null ? DEFAULT : x`, in `AiGenerationConfig.java:1090-1163` and
  `AiModelDefinition.java:683-756`.
- The sibling setters `setLazyMode` / `setCacheTypeK` / `setCacheTypeV` declare `@Nullable` (`:924,961,980`).
- Fix: make them consistent with `@Nullable`.

**S9 (low): list elements are typed non-null but guarded.**
- `AiFieldGenerationSelector.java:46,109`, `AiFactExtractor.java:151,206`, `AiFactDefinitionSupport.java:99`,
  `EngineSupport.java:215,313,352`, `SourceFileIndexer.java:153,466,486`.
- `Nulls.FAIL` already keeps null items out of file input, so the guards only serve programmatic callers.
- Fix: either drop the guards, or type `List<@Nullable T>`.

**S10 (low): `GgufModelInfo` sentinels.**
- `modelName` and `failure` use `""` and `contextLength` uses `-1` (`provider/GgufModelInfo.java:25-49`).
- Callers test the sentinels (`EngineSupport.java:457,467`).
- `@Nullable` / `OptionalLong` would read better. They are documented, so this is low.

**S11 (low): sentinels that are fine to keep.**
- `""` in `AiMdHeader` `p/n/m/k` (`document/AiMdHeader.java:128-131`), `AiFactExtractor.factsHash`
  (`:393-396`) and `AiGenerationUsage.finishReason` (`:22,76`) mirrors the on-disk "not recorded" state.
- `<= 0 disables` in `AiRangeCondition` (`:10-11`) is the configuration-file semantics; changing it
  would break config meaning.

**S12 (low): superfluous class-level `NullAway.Init`.**
- `AiCalibration.java:22`, `AiConditionGroup.java:20` and `AiGenerationConfig.java:22` have no
  uninitialized non-null field.
- The suppression only disables the check for a future field; remove it.

**S13 (low): `AiPromptTemplateLoader.load(@Nullable List)` returns `@Nullable`.**
- `prompt/AiPromptTemplateLoader.java:60` returns null exactly when its input is null.
- It is used only by tests; production calls the two-argument overload (`EngineSupport.java:142`).
  Drop the overload.

Checked and precise:
- `SrcMorphConfigurationFile.getSrcMorph()`: `Nulls.SKIP` keeps its default.
- The plugin's `@Parameter` fields: `projectVersion` is non-null and always injected.
- `AiGenerationResult.incompleteReason()`
- `RemoteModel` prices
- `AiShebang.interpreter`
