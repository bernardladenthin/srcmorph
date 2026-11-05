# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open.

## Open

### Enterprise kinds of profile v3 (2026-11-04)

- **PR #269** (branch `feat/profiles-v3-kinds-and-safety`): CI green but the Claude review action;
  the owner merges, then this line goes.
- The plugin's self-test profile routes only the kinds measured on this repository; the new ones are
  routed in `examples/copilot-api-gateway/srcmorph-luna.yaml` and measured on the enterprise corpus
  (`docs/ai-index-benchmark/remeasure-2026-11-04b/`), which is written for the benchmark. Once
  measured on a corpus of real projects, the self-test can take them too.
- gpt-oss-20b named `ringbuf.c`'s headers instead of `malloc` and `memcpy` in two of six card runs
  after the path line came into the user message (fallback prompt, none before): watch it in the
  next round; with three more runs it is variation or a sentence for the fallback prompt.

### TypeScript measurement of 2026-11-05 (internal; the data stays out of the repository), open items

Fixed on PR #269: the finish reason `error`, the TypeScript rule, the time estimate's basis. These
wait for a decision:

- **gpt-oss-20b runs away on a big TypeScript file now and then; a larger budget does not help.** The
  three files cut at 4096 tokens ran again at 16384 with the oop prompt, two runs per model: every
  finished answer took 1229 to 1618 tokens (Luna's six, gpt-oss's five), and gpt-oss's sixth ran
  to 16384 on a file it summarized in the other run. So 4096 stays: a larger budget only makes a
  runaway dearer (72 s and four times the tokens). What would save the file is `onIncomplete:
  retryOnce`; decide whether `run.py` (whose budget and strategy the published numbers were measured
  with) or the examples take it.
- **TypeScript has no fact counters**: exports, classes, interfaces, functions and arrow consts would
  count as `kotlin-facts` does. Not written yet, because the recall is already 98 to 100 % with the
  oop prompt and a counter changes nothing the scores read.
- **`package.json` varies with gpt-oss-20b**: 45, 13, 28 and 15 of its 45 participants, commands,
  tools and dependencies in four runs (Luna 43 to 45 each time), with the fallback prompt. A rule for
  manifests (`package.json`, maybe `pyproject.toml`) with the ops prompt and counters for scripts and
  dependencies would be the next thing to measure.

### Reviews of 2026-11-04 (PR #269 at 98c6b77, the delta and the whole repository), open items

Two reviews, verified against the code; fixed on the branch except these, which wait for a decision:

- The read budget of fact and `contentMatches` patterns counts the matcher's `charAt` calls, which
  may differ between JDK versions; `ProfileFactPatternsLinearityTest` runs on JDK 25 only (not
  measured). Running it on the oldest supported JDK too would show it.
- In the plugin, `G` records the version of the user's project, not the plugin's
  (`AbstractAiIndexMojo`); check whether that is meant.
- `${...}` in a configuration value has no escape, so a literal `${user.home}` in an inline prompt is
  always replaced; the rate limiter keeps a window per server and limit, so two models on one server
  with 20 and 10 requests a minute send up to 30 (its Javadoc says so; decide whether one window per
  server is meant).

### Audit of 2026-11-01 (PR #261), in this order

Six reviews of the codebase found the items below; each package is one commit with its tests, and a
finished package is deleted here. The design goal is a clean state, not backward compatibility:
behaviour may change where the old one was wrong, and the CHANGELOG says so.

8. **Benchmark and measurement.** `score.py` and the tools are done and `scores/` is re-scored; the
   runs are done: [`remeasure-2026-11-01/`](docs/ai-index-benchmark/remeasure-2026-11-01/README.md)
   (hosted: gpt-oss-20b 99.4 / 99.8 / 78.1 %, GPT-6 Luna 99.8 / 100 / 97.9 % on the language, card and
   large corpora; locally every large-file run fails at least one file), and in finding 7 of the
   card. What is still open from the runs:
   - **gpt-oss-20b's map-reduce loses the middle of a file.** Since the map and reduce prompts it
     names the catalog's planted middle element in every run, the enum's middle area in none of
     the 4096-token runs (once at 16384; Luna in every run); the partial summaries are not kept, so where it is lost is not known.
     Keeping the partials (beside the run record, on request) would show it.

### Verification of 2026-11-01 (eleven reviews of b74aa6b), open items

Eleven parallel reviews (the whole PR, then config, fact counters, indexer, provider, engine, plugin,
CLI and examples, docs, benchmark tooling, tests and gates) checked the PR at b74aa6b; every item
below was verified against the code. Fixed and deleted from here: the chunker's repeated chunks
behind a long line, the overflow retry that cut a fitting source behind the rule's back, a provider's
unchecked error ending the run, the run record lost on an abort, the documentation drift, relative
paths against the working directory, Calibrate's XML block, the unchecked package and project
rules, an unknown provider name, `All` with `planOnly`, keys set to nothing, the echoed strategy,
the resent POST after a read timeout, the checksum of non-UTF-8 files.

**Decisions taken** (2026-11-01): D1 the package and project rules default to `mapReduce`, done;
D2 the card notes the scorer change and finding 7 the re-measurement, the re-render waits for the next
round of models, done; D4 the Java names keep counting from prose, the asymmetry is documented in
`score.py`, done; D5 the header records the prompt and facts hashes a summary was written with, done.
Open from them:

- **D3. The self-test's Granite tiers for big XML**: decided on 2026-11-02 by three runs each
  (`remeasure-2026-11-02/`): `big-xml` (H-Tiny) takes `file-body-xml` (an 870 KB catalog: 7 of 8
  names against 4), `huge-xml` (H-1B, six sampled chunks) too (a 1.15 MB catalog: a short summary
  against a file failed at the output limit). Open: a large POM for `big-pom` / `huge-pom`, which
  keep the generic prompt unmeasured. Done before: a
  cut part summary is kept; the family rules name no analysis channel; the big tier map-reduces with
  H-Tiny over 64K; map and reduce calls name their input (`mapPromptKey: map-chunk`,
  `reducePromptKey: reduce-parts`, `Chunk i of n:`), the reduce reads the exact facts and, for a
  sample, a note saying so; a local model's windows are counted in tokens.

### Review of 2026-11-02 (seven reviews of c2d6834), open items

Seven reviews (map-reduce engine, providers, configuration and engine, prompts, documentation,
benchmark tooling, tests) checked the branch at c2d6834; what they found is fixed (CHANGELOG,
Unreleased) except:

- The plan's chunk estimate cannot know a counted window (it loads no model), so for a local model its
  ETA of a dense file stays an estimate.
- `P` covers the map and reduce prompts also for files that never used them, so with
  `regenerateOn.prompt` (the default) a changed map or reduce prompt rewrites every file of the rule,
  not only its chunked ones.
- The token count of a remote `llama-server` could use its `/tokenize`; OpenRouter has none.
- Benchmark: the copy filter misses `~~~` fences, indented code and near-copies; the invented-name
  rate is taken inside backticks only and by substring; recall counts names, not faithfulness (a bare
  name list scores 100 %, wrong claims are not measured); with three runs against three a Fisher test
  cannot reach 0.05, and hit@1 is compared unpaired (McNemar fits); every prompt version was tuned on
  the corpus it is scored on; the meta lacks the srcmorph commit, the Python version and a corpus
  hash, and `large.py`'s `random` is not guaranteed across Python versions (pin a golden hash); `score.py`
  scores files srcmorph does not index and fails on non-UTF-8 sources; `--out/<key>` is deleted
  without the marker check `--work` has; 21 of 21 mutations of the tools went unnoticed by their tests.

- Tests (the test review): `AiFieldGenerationSupport` holds most of the pure map-reduce logic off the
  PIT gate; split it the way `LlamaCppJniProviderSupport` was. The checksum over bytes has no
  indexer-level test. A chunk of exactly 2000 characters (the split boundary) is not tested. The broad
  fb-contrib SpotBugs match hides 15 benign findings in four classes and suppresses nothing else; split
  it per class. Drift from TEST_WRITING_GUIDE in older tests (two-segment names, no arrange/act/assert
  comments, tests outside folds, fully qualified names); random temp-file names feed a prompt length;
  the test logger's level is never restored; the calibration paste block is never read back through
  the strict loader.

**Core (engine, indexer, provider, config):**

- CodeQL #270 (`RequestRateLimiter`: the return value of `awaitNanos` ignored) is a false positive:
  the loop in `acquire()` re-checks the window after every wake-up; the owner dismisses it.
- `srcmorph/spotbugs-exclude.xml` still gives the operator's POM (XML) as the source of the prompt
  templates and the model settings in about five suppressions; it is the configuration file now.
- The configuration echo of the CLI and the messages that name a `baseUrl` (`ListModelsEngine`,
  `EngineSupport`, the provider's endpoint errors, the rate limiter) print it as written, user info
  included. `HttpURLConnection` does not authenticate with a URL's user info, so a key there does not
  work anyway; mask it if a server ever takes one.
- Smaller: `AiGenerationUsage.add` counts one call whatever it adds (also `NONE` from a provider
  that knows no usage); the plan's token figures ignore the oversize strategy; the reduce estimate
  assumes a fan-in of 16; the per-file time of a local group's first file includes the model load;
  the record's totals have no skipped count and no run wall time; `AiCompletionParser` keeps
  reasoning when the template injected the opening `<think>`.

**Fact counters (`profiles/v3/`, after the benchmark runs, which read the profile):**

- Shell and Perl `[ignore]`, what is left of the heredoc reading: the shell arithmetic check looks back
  120 characters and misses a nested `((a))` close before the `<<`; a Perl `<<` after `}`
  (`$h{x} <<BITS`) and one inside a regex or `q{}` still open a heredoc.
- `sql-facts.tsv` `[ignore]`: no PostgreSQL `$$`, Oracle `q'[…]'`, MySQL `"…"` strings or `#`
  comments; an apostrophe inside one blanks real statements.
- Smaller: `yield f(x);` and `record X(` count as methods, nested generic bounds and same-line
  annotations are missed, an enum constant with a body counts as a constructor; Javadoc "public"
  counts (Java has no `[ignore]`); `${MSG:-…}` assignments; `VALUES(col)` of `ON DUPLICATE KEY UPDATE` counted as rows;
  MariaDB `CREATE OR REPLACE TABLE`, T-SQL columnstore indexes, `CREATE FOREIGN TABLE`; the README's
  table lacks PostgreSQL's `'C:\'` and nested comments; removing `(?![!+])` from the SQL ignore keeps
  two tests green; XML `name = 'a'` in element text; `lineBreaks` keeps only `\r` and `\n`; the
  fallback prompt's example `MAX_RETRIES` is a scored id of the language corpus.

**Benchmark tooling (after the runs, which read `run.py` and `search.py`):**

- `score.py`: `report.py` averages the kinds unweighted; `LEAK` matches inside
  backticks (Mistral's `` `<|channel|>` ``), and a complete summary after a `<think>` block counts as
  leak-only; an everyday word in a column or field listing (`email VARCHAR(254)`, 53 misses) does
  not count; `facts.json` words lack `histogram`, `CHECKOUT`, `Since`; a backtick span check can match
  across two spans; thousands separators defeat the numbers check.
- `languages-2026-11/README.md`: its score command omits `--meta` (the card's cut-offs are used),
  and the language-corpus scores are two-part means no committed command produces.
- Card text (with D2): the correlation −0.46 is from an older population (today −0.18), the BM25 range
  "67 to 86 %" ends at 91 %, the Fisher test with 3 against 3 runs can never reach p < 0.1.

**Tests and gates:**

- No core test calls an engine's `check()` where it differs from the plan (`planOnly`, model
  files); the provider's interrupt branch has no test; a changed prompt hash in the run record is not
  asserted; the overflow test does not assert the smaller window; the plugin's base-directory
  default cannot be told from the file's directory (every fixture puts the file in the project
  directory).
- SpotBugs: `SrcMorphConfigurationFileLoader` sits in a Match that suppresses seven patterns for a
  reason that covers two; a stale `NP_NULL_ON_SOME_PATH` suppression.
- CI: the smoke step that counts the planned files runs from `examples/`, where the working directory
  is the file's directory, so it cannot catch a base-directory regression; run it from the root.
- SonarCloud reports 98 new issues that are not readable without a token.


### Other open items

- **The Claude Code Review workflow fails on every pull request**: it runs with the
  `anthropic_api_key` secret (`claude_code_oauth_token` is empty) and the model call ends after
  ~300 ms with `is_error: true`, no turn, no cost: the key is refused or its account has no credit.
  The owner renews the key or its credit, or the workflow stays red.

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

- **The OpenAI-compatible provider's calibration warning is logged under the llama.cpp class.**
  `generateWithTimings` reuses `LlamaCppJniProviderSupport.warnOnTruncatedAnswer`, so a cut-off
  calibration answer of a remote model is logged under that logger name.

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

- **`@VisibleForTesting` audit.** Nothing is annotated, and the inventory has two kinds of package-private members that must not be lumped together. **Production API, not candidates:** the four of `provider.LlamaCppJniProviderSupport` (`buildChatTemplateKwargs`, `buildInferenceParameters`, `warnOnTruncatedAnswer`, `logPromptCacheReuse`) and the static `lazyMode`/`cacheType` are package-private because `LlamaCppJniAiGenerationProvider` calls them (they were moved out of it so the PIT gate reaches them); their visibility serves the provider, the tests only ride along. **Test seams, the actual candidates:** `document.AiMdDocumentCodec.read(List)` and the `String write(AiMdDocument)` overload (their Javadoc says so; production goes through the `Path` overloads) and `prompt.AiPromptPreparationSupport.trimSourceAtLineBreak` (called only inside its own class and by `AiPromptPreparationSupportTest`). The plugin has no such seam any more: `GenerateMojo.buildGenerateConfiguration()` is called only by its own `execute()`, and the protected `AbstractAiIndexMojo.buildConfiguration(RuleSection)` serves the goal mojos (`AbstractAiIndexMojoTest` calls it too). Guava is not a dependency, so closing this means either a project-local marker annotation on the second group (there is precedent: `support.ConvertToRecord`) or recording that the convention is not adopted here. Decide and act rather than re-auditing.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.
