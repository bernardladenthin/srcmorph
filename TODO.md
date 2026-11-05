# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open. The one exception is the plan's table: its numbers stay
until every row is done, a finished row says `done` with its commit, and then the table goes.

## Plan (2026-11-05): every open item, the simplest first

Each row is one item of the sections below (the "Details" column names its section); a row is worked
off or decided in this order. **Who**: `Claude` = can be done now without the owner, `Owner` = waits
for a decision or an action of the owner (the proposal is the default once the owner agrees),
`External` = waits for something outside this repository.

**Wave 1: decisions** (minutes for the owner, the work after them is short)

| # | Item | Proposal | Who | Status | Details |
|---|---|---|---|---|---|
| 1 | CodeQL #270, `awaitNanos` ignored | false positive: dismiss it on GitHub | Owner | open | Review of 2026-11-02, core |
| 2 | The Claude Code Review workflow fails on every PR | no budget this month; green again with the next | Owner | done 2026-11-05 | — |
| 3 | SonarCloud's 98 issues need a token to read | give one, or drop the item | Owner | open | Review of 2026-11-02, tests and gates |
| 4 | gpt-oss-20b runs away on a big TypeScript file | `onIncomplete: retryOnce` in `run.py` and the examples | Owner | done 2026-11-05 | — |
| 5 | `G` records the project's version in the plugin | record the plugin's version (`G` names the generator) | Owner | done 2026-11-05 | — |
| 6 | `${...}` has no escape | `$${...}` writes a literal `${...}` | Owner | done 2026-11-05 | — |
| 7 | The rate limiter keeps a window per server and limit | one window per server, at the smallest limit | Owner | done 2026-11-05 | — |
| 8 | `P` covers the map and reduce prompts of every file | keep it, document it | Claude | done 2026-11-05 | — |
| 9 | The plan's chunk estimate cannot know a counted window | a known limit, documented | Claude | done 2026-11-05 | — |
| 10 | Count a remote `llama-server`'s tokens with `/tokenize` | dropped: OpenRouter has none, little gain | Claude | done 2026-11-05 | — |
| 11 | `@VisibleForTesting` | not adopted: `CODE_WRITING_GUIDE.md` section 8 | Owner | done 2026-11-05 | — |
| 12 | TypeScript fact counters | dropped: recall is 98 to 100 % with the oop prompt | Owner | done 2026-11-05 | — |
| 13 | A rule for `package.json` and other manifests | measure the ops prompt on it once (internally) | Claude | open | TypeScript measurement |
| 14 | jqwik pin | decided: the pin stays, a rule of `CLAUDE.md` | Owner | done 2026-11-05 | — |

**Wave 2: quick fixes** (each up to 30 min, with tests)

| # | Item | Who | Status | Details |
|---|---|---|---|---|
| 15 | `srcmorph/spotbugs-exclude.xml` names the POM as the source of prompts and model settings | Claude | done 2026-11-05 | — |
| 16 | The smoke step that counts the planned files runs from `examples/`, not the root | Claude | done 2026-11-05 | — |
| 17 | `run.py` deletes `--out/<key>` without a marker check | Claude | done 2026-11-05 | — |
| 18 | `languages-2026-11/README.md`: the score command lacks `--meta` | Claude | done 2026-11-05 | — |
| 19 | The remote calibration warning is logged under the llama.cpp class | Claude | done 2026-11-05 | — |
| 20 | A `baseUrl`'s user info is printed as written | Claude | done 2026-11-05 | — |
| 21 | `AiGenerationUsage.add` counts `NONE` as a call | Claude | done 2026-11-05 | — |
| 22 | Tests: `check()` with `planOnly` and model files, the interrupt branch, a changed prompt hash, the smaller window after an overflow, the plugin's base directory | Claude | done 2026-11-05 | — |
| 23 | SpotBugs: split the `SrcMorphConfigurationFileLoader` match, the stale `NP_NULL_ON_SOME_PATH` | Claude | done 2026-11-05 | — |

**Wave 3: medium fixes** (each 1 to 3 h)

| # | Item | Who | Status | Details |
|---|---|---|---|---|
| 24 | Fact counter gaps: shell `((a))`, Perl `<<` after `}` / in a regex / `q{}`, SQL strings and comments, the smaller ones | Claude | open | Review of 2026-11-02, fact counters |
| 25 | `score.py` and `report.py`: `LEAK` in backticks, a summary after `<think>`, words, backtick spans, thousands separators, weighted kinds | Claude | open | Review of 2026-11-02, benchmark tooling |
| 26 | Benchmark meta (srcmorph commit, Python, corpus hash), `large.py` golden hash, non-UTF-8 sources, the tools' mutations | Claude | open | Review of 2026-11-02 |
| 27 | `AiCompletionParser` keeps reasoning when the template opened `<think>` | Claude | open | Review of 2026-11-02, core |
| 28 | Run record totals (skipped, wall time), plan tokens by oversize strategy, the first file's model load | Claude | open | Review of 2026-11-02, core |
| 29 | Test upkeep: guide drift, logger levels, the 2000-character chunk, the byte checksum, the calibration block through the loader, the fb-contrib match per class | Claude | open | Review of 2026-11-02, tests |
| 30 | API reviews: null-safety, package hierarchy, naming (`code-quality-todos.md`) | Claude | open | Other open items |

**Wave 4: larger work**

| # | Item | Who | Status | Details |
|---|---|---|---|---|
| 31 | Split the pure map-reduce logic out of `AiFieldGenerationSupport` onto the PIT gate | Claude | open | Review of 2026-11-02, tests |
| 32 | Keep the map-reduce part summaries on request; then measure where gpt-oss-20b loses the middle | Claude | open | Audit of 2026-11-01 |
| 33 | Launch the Vulkan module once on this machine's RTX 3070 | Claude | open | Other open items |

**Wave 5: measurements and what waits outside**

| # | Item | Who | Status | Details |
|---|---|---|---|---|
| 34 | `ringbuf.c`: three more gpt-oss-20b card runs | Claude | open | Enterprise kinds |
| 35 | A large POM for `big-pom` / `huge-pom` (local Granite, hours) | Claude | open | Verification of 2026-11-01, D3 |
| 36 | The self-test routes the new kinds after a measurement on real projects (Claude picks open-source ones) | Claude | open | Enterprise kinds |
| 37 | Card text and the method's limits (correlation, BM25 range, Fisher, faithfulness) | Owner (when the card is re-rendered) | open | Review of 2026-11-02, benchmark tooling |
| 38 | Use jllama's `ChatStreamAssembler` | External (its release) | open | Other open items |
| 39 | ROCm, SYCL, OpenVINO: no hardware here | Owner (keep or drop) | open | Other open items |
| 40 | Merge PR #269 | Owner | open | Enterprise kinds |
| 41 | Run `ProfileFactPatternsLinearityTest` on the oldest supported JDK too (the read budget counts `charAt` calls) | Claude | open | Reviews of 2026-11-04 |

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

Fixed on PR #269: the finish reason `error`, the TypeScript rule, the time estimate's basis,
`retryOnce` against gpt-oss-20b's runaways. Open:

- **`package.json` varies with gpt-oss-20b**: 45, 13, 28 and 15 of its 45 participants, commands,
  tools and dependencies in four runs (Luna 43 to 45 each time), with the fallback prompt. A rule for
  manifests (`package.json`, maybe `pyproject.toml`) with the ops prompt and counters for scripts and
  dependencies would be the next thing to measure.

### Reviews of 2026-11-04 (PR #269 at 98c6b77, the delta and the whole repository), open items

Two reviews, verified against the code; fixed on the branch except these, which wait for a decision:

- The read budget of fact and `contentMatches` patterns counts the matcher's `charAt` calls, which
  may differ between JDK versions; `ProfileFactPatternsLinearityTest` runs on JDK 25 only (not
  measured). Running it on the oldest supported JDK too would show it.

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

- Benchmark: the copy filter misses `~~~` fences, indented code and near-copies; the invented-name
  rate is taken inside backticks only and by substring; recall counts names, not faithfulness (a bare
  name list scores 100 %, wrong claims are not measured); with three runs against three a Fisher test
  cannot reach 0.05, and hit@1 is compared unpaired (McNemar fits); every prompt version was tuned on
  the corpus it is scored on; the meta lacks the srcmorph commit, the Python version and a corpus
  hash, and `large.py`'s `random` is not guaranteed across Python versions (pin a golden hash); `score.py`
  scores files srcmorph does not index and fails on non-UTF-8 sources; 21 of 21 mutations of the tools went unnoticed by their tests.

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
- Smaller: the plan's token figures ignore the oversize strategy; the reduce estimate
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
- The language-corpus scores are two-part means no committed command produces (`languages-2026-11/`).
- Card text (with D2): the correlation −0.46 is from an older population (today −0.18), the BM25 range
  "67 to 86 %" ends at 91 %, the Fisher test with 3 against 3 runs can never reach p < 0.1.

**Tests and gates:**

- SonarCloud reports 98 new issues that are not readable without a token.


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

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.
