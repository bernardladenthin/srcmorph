# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open.

## Open

### Enterprise kinds of profile v3 (2026-11-04)

Branch `feat/profiles-v3-kinds-and-safety`, in this order:

- **Measurement** (runs of 2026-11-04 under `docs/ai-index-benchmark/remeasure-2026-11-04/`): score
  them, write the README there with the numbers next to those of 2026-11-02, and say in
  `profiles/README.md` which kinds are measured.
- **Java on the shared prompt:** when the card corpus shows `file-body-oop` no worse than
  `file-body-java` for gpt-oss-20b and GPT-6 Luna, route Java to `file-body-oop` everywhere (the
  self-test and its `build-your-own-jar` twin, the examples, `config_All.json`, the plugin-it fixture
  and `.github/plugin-it.sh`, `run.py`'s default `--java-prompt`, the plugin README) and delete
  `file-body-java.md`, so one prompt serves every class-based language. If it is worse, find which
  sentence costs it before switching.
- **Push and open the pull request** for the branch.

Then, each its own step:

- **Files selected by their content:** a script without an extension that only its shebang
  identifies (`#!/bin/bash`, `#!/usr/bin/env perl`) is never selected: `fileExtensions` and
  `fileNames` select, `contentMatches` only routes what they selected. A selection condition (a
  `#!` line read like the 16 KB head), or a `contentMatches` that also selects, would close it.
- **Fact counters for the kinds that have a prompt but none:** Scala (`scala-facts`, in the shape of
  `kotlin-facts`), Protobuf / AsyncAPI / Avro / GraphQL (`proto-facts`, `asyncapi-facts`, ...),
  Terraform, Ansible and Compose (`terraform-facts`, ...), and Camel's YAML DSL (the `camel-yaml`
  rule of the Luna example takes the ops prompt without counters; `integration-facts` reads XML
  only).
- **DMN** is counted by `bpmn-facts` (decisions, rules); a `dmn-facts` of its own (inputs, outputs,
  hit policy) would count a decision table in full.
- **Gradle, `.properties`, `.bat`, `.ps1`** take the fallback prompt without counters. `.gradle`
  is in the Luna example's extensions now (fallback); a Groovy build script could take
  `file-body-ops` or `groovy-facts`.
- **`pathGlob` matches in the case written**, also on Windows, so a pattern counts the same on every
  system; `fileNames` and `extensions` match in any case. Decide whether `pathGlob` should too.
- **`java-facts` has no `[ignore]` line:** its "TODO/FIXME" counter lives in comments, so blanking
  comments would zero it, and a `public` or `class` in a comment or string counts. Splitting the
  counter (TODOs counted before the blanking) or an ignore line for strings and text blocks only
  would fix the rest.
- From the reviews of 2026-11-03, not proposed yet: a counter counts occurrences, never distinct
  names (12 `@Inject` of one type are 12); a fact pattern has no time limit, which the linearity
  test now stands in for, but a user's own pattern is not measured; the user message of a file
  holds its name, not its path, so two `web.xml` of different modules read alike to the model.
- The plugin's self-test profile routes only the measured kinds; the new ones are routed in
  `examples/copilot-api-gateway/srcmorph-luna.yaml`. Once measured on a corpus of real projects
  (the enterprise corpus is written for the benchmark), the self-test can take them too.
- The `java-ee` rule finds Jakarta EE through the imports; a class that names its annotations
  fully qualified without an import (`@jakarta.ejb.Stateless`) takes the plain Java rule and its
  counters.
- `contentMatches` reads the first 16,384 characters; a YAML file whose `kind:` or `openapi:` comes
  later (a long license header) takes the generic rule.

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
  `regenerateOnProfileChange` (the default) a changed map or reduce prompt rewrites every file of
  the rule, not only its chunked ones.
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

- **The Claude Code Review workflow fails on every pull request**: its `claude_code_oauth_token`
  secret is empty. The owner sets it, or the workflow stays red.

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
