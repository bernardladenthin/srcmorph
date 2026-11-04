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

### Reviews of 2026-11-04 (PR #269 at 98c6b77, the delta and the whole repository), open items

Two reviews, verified against the code. Header and rewrite decision:

- The project's `C` is the CRC32 of its package links only (`ProjectIndexer`), not of the packages'
  `C`: with `regenerateOn.children: false` a changed source rewrites its file and package, the
  package gets a new lead, and `project.ai.md` keeps the old one (1.4.0 rewrote it). Build `C` from
  link and `C` of every package; a test for this case.
- `P` hashes the prompt files as read (`AiHeaderHashes.prompt`), while the source checksum, `.tsv`
  and `.ai.md` are read line-normalized: a profile checked out with CRLF on Windows and LF in CI
  rewrites every summary on each change of side. Normalize before hashing; a CRLF test.
- CHANGELOG (Unreleased): "set the switches to `false` for that run to keep them" only postpones the
  rewrite, since `M` and `K` are then never recorded; say which switches must stay off to keep the
  summaries (files `model`; packages `prompt`, `model`, `children`; the project `source`), or record
  a missing field without a model call.
- CHANGELOG (Unreleased): the API breaks of 98c6b77 are missing: `AiMdHeader.withHashes` takes four
  hashes, the `PackageIndexer` and `ProjectIndexer` constructors take `AiRegenerateOn`,
  `SourceFileIndexer.indexFile` takes `modelHash`; an "Upgrading from 1.4.0" note.
- Javadoc: `GenerateEngine` (the `contentHeadChars` check) says "below 1", the bound is 256;
  `AiRegenerateOn` says all switches off "freezes the index", true for files only (`K` still
  rewrites packages). The aggregate engines do not log `regenerateOn`.

Selection and fact counters:

- `fileNames` under `srcMorph` takes a glob with `/` silently and it never matches; the condition of
  the same name rejects it. Run the same check (`validateFileNames`) in `SourceFileIndexer`.
- `AiShebang`: BSD `env -P altpath` is not skipped (`#!/usr/bin/env -P /usr/local/bin bash` reads
  `bin`), nor combined short options or quotes; `python3.1` also matches `python3.12` (a version
  must end at a non-digit).
- `contentMatches` has no read budget (a backtracking pattern of a user's rule hangs the plan) and is
  compiled per file and rule: reuse `AiFactExtractor.ReadBudget`, cache the pattern.
- The read budget counts `charAt` calls, which may differ between JDK versions; the linearity test
  runs on JDK 25 only (not measured).

Configuration (on `main` before the PR):

- A JSON configuration with a UTF-8 byte order mark does not load
  (`SrcMorphConfigurationFileLoader`); Windows PowerShell 5.1 writes one. Skip a BOM as the source
  reader does.
- Duplicate keys in `aiDefinitions`, `promptDefinitions`, `factDefinitions` overwrite each other, the
  last wins (`AiModelDefinitionSupport`, `AiPromptSupport`, `AiFactDefinitionSupport`): fail instead.
- In the plugin, `G` records the version of the user's project, not the plugin's
  (`AbstractAiIndexMojo`); check whether that is meant.
- `${…}` has no escape; the rate limiter keys a window per server and limit, not per server
  (uncertain whether meant).

Documentation:

- The root README, `srcmorph-cli/README.md`, `docs/windows-intel-core-ultra.md` and
  `examples/build-your-own-jar/pom.xml` say the examples need 1.4.0, but they use keys and facts
  markers 1.4.0 rejects (`regenerateOn`, `fileNames`, `fileInterpreters`, `[raw]`, ...); CI builds
  them against the reactor, so it cannot see it. Say "the next release, or a reactor build" until
  it is out.
- `srcmorph-maven-plugin/README.md`, the routing example: `file-body-java-terse` and
  `file-body-java-detailed` are not prompt keys of any profile.
- `CONTRIBUTING.md` describes JUnit 4, Maven `Log` instead of SLF4J, CI on Java 8 and `record`s.
- `docs/copilot-api-gateway.md` describes the Luna example before the enterprise kinds.
- The PIT badge says 78 classes (83); `AiMdHeaderCodec`'s Javadoc of the header letters lacks `M`
  and `K`; `HSMALL-VS-GPTOSS.md` names the goal `ai-index:calibrate`.
- A one-line `[ignore]` region at the start of a line breaks a counter anchored with `^` (the
  blanking keeps the line, but not what `^` expects); check the profiles for it.
- The language-corpus mean of gpt-oss-20b on 2026-11-04 reads 98.7 %; its per-run values average
  98.6 % (probably a mean of unrounded values; check).

Repository:

- `docs/ai-index-benchmark/tools/agentic-bench.sh` and `agentic-cpu-ctx.sh` name `llama-server.exe`
  by a local user profile path: take it from a variable (`LLAMA_SERVER`), defaulting to
  `llama-server` on the `PATH`.

### After the release of 1.4.0

- `java-facts` `type declarations` counts private and protected nested types and `@interface`s since
  `0374f73`: the facts line of such a Java file differs from the runs of
  `docs/ai-index-benchmark/remeasure-2026-11-02/`; scores read names, not this count.

### Audit of 2026-11-01 (PR #261), in this order

Six reviews of the codebase found the items below; each package is one commit with its tests, and a
finished package is deleted here. The design goal is a clean state, not backward compatibility:
behaviour may change where the old one was wrong, and the CHANGELOG says so.

8. **Benchmark and measurement.** `score.py` and the tools are done and `scores/` is re-scored; the
   runs are done: [`remeasure-2026-11-01/`](docs/ai-index-benchmark/remeasure-2026-11-01/README.md)
   (hosted: gpt-oss-20b 99.4 / 99.8 / 78.1 %, GPT-6 Luna 99.8 / 100 / 97.9 % on the language, card and
   large corpora; locally every large-file run fails at least one file), and in finding 7 of the
   card. What the runs found:
   - **On large files an answer outgrew the output budget.** Since an incomplete answer is never
     written, an answer cut off at `maxOutputTokens` (a chunk's part summary at the time, or the final
     answer) failed its file: every local preset lost at least one of the three large files, and hosted
     gpt-oss-20b the enum once. Since then a cut part summary is kept, map and reduce calls say what
     their input is, the reduce reads the exact facts, and a local model's windows are counted in
     tokens (D3). Measured again on 2026-11-02
     ([`remeasure-2026-11-02/`](docs/ai-index-benchmark/remeasure-2026-11-02/README.md)): gpt-oss-20b
     99.1 / 99.9 / 75.2 %, Luna 99.5 / 100 / 99.0 %; H-Tiny over 64K 66.6 % (over 384K on 2026-11-01:
     21.9), H-1B 17.1 % with the generic prompt. gpt-oss-20b lost the SQL dump in two of three runs
     when one chunk's answer was all reasoning (4096 tokens, no text; 16384 made it worse, provider
     and `reasoning.max_tokens` did not matter); since such a part is read again in two halves the
     dump is written in every run (93.0 % over three runs, `oss-runaway/` of the same folder).
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
- `srcmorph/spotbugs-exclude.xml` still gives "the operator's POM XML" as the reason of about eight
  suppressions, and the plugin's `spotbugs-exclude.xml` explains its Mojo suppressions with the
  `<configuration>` block of a POM; both are the configuration file now.
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
- `pom-facts.tsv`: an unclosed `<plugin>` in a comment runs the plugin-body region to the end.
- Smaller: `yield f(x);` and `record X(` count as methods, nested generic bounds and same-line
  annotations are missed, an enum constant with a body counts as a constructor; Javadoc "public"
  counts (Java has no `[ignore]`); `${MSG:-…}` assignments; `VALUES(col)` of `ON DUPLICATE KEY UPDATE` counted as rows;
  MariaDB `CREATE OR REPLACE TABLE`, T-SQL columnstore indexes, `CREATE FOREIGN TABLE`; the README's
  table lacks PostgreSQL's `'C:\'` and nested comments; removing `(?![!+])` from the SQL ignore keeps
  two tests green; XML `name = 'a'` in element text; `lineBreaks` keeps only `\r` and `\n`; the
  fallback prompt's example `MAX_RETRIES` is a scored id of the language corpus.

**Benchmark tooling (after the runs, which read `run.py` and `search.py`):**

- `score.py`: lower-case SQL types (`integer`) count as invented names (Qwen3-Coder's 18.6 per 100 on
  SQL is all `integer`), and `report.py` averages the kinds unweighted; `LEAK` matches inside
  backticks (Mistral's `` `<|channel|>` ``), and a complete summary after a `<think>` block counts as
  leak-only; an everyday word in a column or field listing (`email VARCHAR(254)`, 53 misses) does
  not count; `facts.json` words lack `histogram`, `CHECKOUT`, `Since`; a backtick span check can match
  across two spans; thousands separators defeat the numbers check.
- `languages-2026-11/README.md`: its score command omits `--meta` (the card's cut-offs are used),
  and the language-corpus scores are two-part means no committed command produces.
- Card text (with D2): the correlation −0.46 is from an older population (today −0.18), the BM25 range
  "67 to 86 %" ends at 91 %, the Fisher test with 3 against 3 runs can never reach p < 0.1.

**Tests and gates:**

- No core test calls an engine's `check()` (where it differs from the plan: `planOnly`, model files);
  the provider's timeout retry and its interrupt branch have no test; the run record's per-file
  usage, `rule` and `factsHash` and a changed prompt hash are not asserted; the overflow test does not
  assert the smaller window; the plugin's base-directory default cannot be told from the file's
  directory (every fixture puts the file in the project directory).
- SpotBugs: `SrcMorphConfigurationFileLoader` sits in a Match that suppresses seven patterns for a
  reason that covers two; a stale `NP_NULL_ON_SOME_PATH` suppression; the plugin's excludes name
  removed `@Parameter` fields.
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
