# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open.

## Open

### Audit of 2026-11-01 (PR #261), in this order

Six reviews of the codebase found the items below; each package is one commit with its tests, and a
finished package is deleted here. The design goal is a clean state, not backward compatibility:
behaviour may change where the old one was wrong, and the CHANGELOG says so.

2. **An incomplete answer is never a summary.** The provider fails on a 2xx answer without choices,
   HTML, `{"error": ...}`, `content: null`, a reasoning-only answer and `finish_reason: length` with
   nothing written; `<think>` blocks are stripped like Gemma's markers. `AiGenerationResult` carries
   whether it is complete; an incomplete file, package or project summary is not written (an older
   one stays), `GenerateResult` counts it, the summary line names it and the run fails at its end;
   `onIncomplete: fail | retryOnce`. mapReduce drops empty partials with a warning; the package
   `Contents` fallback is written only without a model; a project whose overview is missing is
   regenerated. Replaces the "cut-off summary is kept" item below.
3. **Provider robustness.** Retries for 408, 429, 5xx and timeouts (`Retry-After`, else exponential
   backoff with jitter, `maxRetries` per model, waiting on a `Condition`); none for 400/401/403/404/413.
   A remote context overflow (400/413 with the provider's overflow text) is a context-overflow error,
   so the budget shrink applies to remote models too. `apiKeyEnv` trimmed and checked before the plan;
   loopback detection case-insensitive and for full IPv6. One failing file does not end the run; the
   run fails at its end.
4. **Engine correctness.** `maxInputChars` per file, not cached from the first file's name;
   `onOversize: fail` enforced at run time; the package and project phases take exactly one rule
   (`examples/config_All.json`); a subtree outside `baseDirectory`, on another drive or missing is an
   error; a prompt larger than the window fails before the run; extensions match with their dot and,
   on Windows, ignoring case; colliding output paths are an error; the dead `llama*` fallback values
   are not logged as run values. The incremental check compares the source checksum and `aiVersion`
   only, so a fresh clone or a version bump regenerates nothing.
7. **Tests and the PIT gate.** `LlamaCppJniConfig$Builder` and `AiIndexPlan$Entry` on the gate; the
   pure logic of `AiFieldGenerationSupport` (overflow backoff, reduce batching) and `SourceFileIndexer`
   (time estimate, line count) moved to gated support classes; mock-provider tests of `sample`,
   `mapReduce` and `deterministic` from plan to written file; CRLF sources through indexer and chunker.
6. **CLI, plugin, docs.** The plugin descriptor states Maven 3.6.3 and Java 8 (asserted in
   plugin-it); relative CLI paths resolve against the configuration file's directory; the example pom
   and READMEs say which release the examples need; the CLI prints its usage and fails without
   arguments, `--help`; the plugin and CLI READMEs, the CLAUDE.md version table and stale TODO
   references match the code; plugin-it covers an incremental second run, the condition tree,
   `templateFile`, inline `<facts>` with `<ignore>` and `srcmorph:help`; a small demo tree under
   `examples/` so the smoke test can assert a file count; a `check` goal and CLI command (plan, model
   files, GGUF header, `baseUrl`, `apiKeyEnv`, profile files, no model load).
9. **Complete mapReduce and a run record.** mapReduce reduces every chunk in stages instead of a
   silent sample (`sample` only when chosen), and the facts line states the coverage; a JSON record
   per run with tokens, finish reason, model and prompt/facts hashes per file; the plan shows tokens
   per file and directory.
8. **Benchmark and measurement.** `score.py`: code fences out of the name recall, common words only in
   backticks, annotated getters, fewer false "invented" names, a `failed` column; the model card
   re-scored. `run.py` validates its options and records `maxChunks`, hashes, version, tokens and the
   per-request cost; `search.py` keys its cache by prompt and backs off; `large.py` writes its facts
   beside the corpus and plants names in the middle of the dump. Then three runs each of gpt-oss-20b
   and GPT-6 Luna on the language, benchmark and large corpora.

### Other open items

- **Use jllama's `ChatStreamAssembler` instead of srcmorph's own stream assembly.** The `stream`
  setting of the `openai-compatible` provider needs a Server-Sent Events body assembled into a chat
  completion; java-llama.cpp has that as `net.ladenthin.llama.json.ChatStreamAssembler`
  (bernardladenthin/java-llama.cpp#595), but in no release yet, so
  `OpenAiCompatibleProviderSupport.assembleStream` and its constants are a copy, marked
  `@ReplaceWithLibrary`. When `net.ladenthin:llama` is raised to the release that carries it,
  `ReplaceWithLibraryTest` fails: let `completionJson` call `ChatStreamAssembler.parseStream`
  (`toCompletionJson`, or hand its `ChatResponse` on directly) and turn its `InvalidStreamException`
  into the `IOException` the provider throws; delete the marked code, its tests in
  `OpenAiCompatibleProviderSupportTest`, this item, and, being the last marker, `ReplaceWithLibrary`
  with its test. The stub-server tests in `OpenAiCompatibleAiGenerationProviderTest` stay and prove the
  switch.

- **A cut-off summary is kept as if it were done.** When a generation hits `maxOutputTokens`, the
  provider only logs a WARN and the truncated `.ai.md` counts as up to date, so an incremental run
  regenerates it only after the source changes. The model-card benchmark hit this with reasoning
  models, whose reasoning tokens count against the budget (`docs/ai-index-benchmark/MODEL-CARD.md`,
  findings 1 and 3). Options: mark the header so the next run retries it, fail the file under an
  `onTruncated` setting, or retry once with a larger budget. Same place: the OpenAI-compatible
  provider reuses `LlamaCppJniProviderSupport.warnOnTruncatedAnswer`, so its warning is logged under
  the llama.cpp class's logger name.

- **No GPU module is launched in CI.** `smoke-jar` builds `examples/build-your-own-jar` with the CPU
  natives only and runs `Plan`; a GitHub-hosted runner has no CUDA/ROCm/SYCL/OpenVINO device, and
  only a real generation loads the native library. Closing this needs hardware: a self-hosted runner,
  or a manual pre-release pass on one GPU box per backend (`build.sh -Dgpu=<classifier>`, then a
  `GenerateFileIndex` with `gpuLayers` set). **`cuda13-windows-x86-64` has had that pass** (2026-10-23,
  `llama 6.0.0-SNAPSHOT`, still as a fat jar): on an RTX 3070 Laptop a real `GenerateFileIndex` through
  the CLI *and* through `srcmorph:generate` ran at 3474 t/s prefill / 86.8 t/s decode with ggml
  registering `[CUDA, RPC, CPU]`, against 63 / 7.1 with `[RPC, CPU]` on the CPU jar. **Still
  unlaunched: `rocm`, `sycl`, `openvino` and `vulkan`** — no AMD, Intel or second GPU here. Vulkan is
  reachable on any NVIDIA box and is the cheapest one to add next.

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory (declared in `srcmorph/pom.xml`, the only reactor module with a jqwik test dependency).

- **`@VisibleForTesting` audit.** Nothing is annotated, and the inventory has two kinds of package-private members that must not be lumped together. **Production API, not candidates:** the four of `provider.LlamaCppJniProviderSupport` (`buildChatTemplateKwargs`, `buildInferenceParameters`, `warnOnTruncatedAnswer`, `logPromptCacheReuse`) and the static `lazyMode`/`cacheType` are package-private because `LlamaCppJniAiGenerationProvider` calls them (they were moved out of it so the PIT gate reaches them); their visibility serves the provider, the tests only ride along. **Test seams, the actual candidates:** `document.AiMdDocumentCodec.read(List)` and the `String write(AiMdDocument)` overload (their Javadoc says so; production goes through the `Path` overloads), `prompt.AiPromptPreparationSupport.trimSourceAtLineBreak` (called only inside its own class and by `AiPromptPreparationSupportTest`), and the three mojos' `build*Configuration()` (called only by their own `execute()` and by `MojoConfigurationMappingTest`). Guava is not a dependency, so closing this means either a project-local marker annotation on the second group (there is precedent: `support.ConvertToRecord`) or recording that the convention is not adopted here. Decide and act rather than re-auditing.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.
