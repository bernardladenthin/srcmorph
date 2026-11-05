# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open. The plan's table keeps its row numbers (other notes name
them): a finished row is deleted, the numbers of the others stay.

## Plan (2026-11-05): every open item

Each row is one item of the sections below (the "Details" column names its section). **Who**:
`Claude` = can be done without the owner, `Owner` = waits for a decision or an action of the owner
(the proposal is the default once the owner agrees), `External` = waits for something outside this
repository.

| # | Item | Proposal | Who | Details |
|---|---|---|---|---|
| 1 | CodeQL #270, `awaitNanos` ignored | false positive: dismiss it on GitHub | Owner | Review of 2026-11-02, core |
| 3 | SonarCloud: the analysis of PR #269 reads (159 code smells, no bug, no vulnerability, gate passed), the 98 issues of `main` do not: SonarCloud's main branch is `master`, `main` is a short branch there, and the plan serves no issues of another branch | rename SonarCloud's main branch to `main` (Administration, Branches); then the rest is read | Owner | Review of 2026-11-02, tests and gates |
| 13 | A rule for `package.json`: the rule and its counters are in (`npm-facts`); measuring it waits | measure the ops prompt with `npm-facts` on it once (internally) | Claude | TypeScript measurement |
| 29 | Test upkeep: the guide drift of older tests (433 two-part and 221 one-part test names, missing arrange/act/assert comments, 195 qualified names) | a sweep, or fix a file's drift when it is touched | Owner | Review of 2026-11-02, tests |
| 32 | Measure where gpt-oss-20b loses the middle; the part summaries are kept on request (`partsDirectory`) | one large-corpus run with `partsDirectory` | Claude | Audit of 2026-11-01 |
| 35 | A large POM for `big-pom` / `huge-pom` (local Granite, hours) | three runs each, as for `big-xml` | Claude | Verification of 2026-11-01, D3 |
| 36 | The self-test routes the new kinds after a measurement on real projects (Claude picks open-source ones) | measure, then route | Claude | Enterprise kinds |
| 37 | Card text and the method's limits (correlation, BM25 range, Fisher, faithfulness) | with the next re-render of the card | Owner | Review of 2026-11-02, benchmark tooling |
| 38 | Use jllama's `ChatStreamAssembler` (not in `llama` 6.0.0) | switch with its release | External | Other open items |
| 39 | ROCm, SYCL, OpenVINO: no hardware here | keep or drop | Owner | Other open items |
| 40 | Merge PR #269 | merge | Owner | Enterprise kinds |
| 45 | The review's further proposals: renames N4 to N15 (`AiRule`, `AiMdHeader`'s accessors, the `Ai` prefix, `generatorVersion`, ...) and P9 (export only `config` and `engine`) | which of them, if any | Owner | `docs/code-review-2026-11-05.md` sections 1 and 2 |

## Open

### Enterprise kinds of profile v3 (2026-11-04)

- **PR #269** (branch `feat/profiles-v3-kinds-and-safety`): CI green but the Claude review action;
  the owner merges, then this line goes.
- The plugin's self-test profile routes only the kinds measured on this repository; the new ones are
  routed in `examples/copilot-api-gateway/srcmorph-luna.yaml` and measured on the enterprise corpus
  (`docs/ai-index-benchmark/remeasure-2026-11-04b/`), which is written for the benchmark. Once
  measured on a corpus of real projects, the self-test can take them too.

### TypeScript measurement of 2026-11-05 (internal; the data stays out of the repository)

- **`package.json` varies with gpt-oss-20b**: 45, 13, 28 and 15 of its 45 participants, commands,
  tools and dependencies in four runs (Luna 43 to 45 each time), with the fallback prompt. `run.py`
  routes it to the ops prompt with `npm-facts` (scripts, dependencies, engines, workspaces) now; that
  rule is the next thing to measure (`pyproject.toml` maybe after it).

### Audit of 2026-11-01 (PR #261)

- **gpt-oss-20b's map-reduce loses the middle of a file.** Since the map and reduce prompts it names
  the catalog's planted middle element in every run, the enum's middle area in none of the
  4096-token runs (once at 16384; Luna in every run), on the large corpus of
  [`remeasure-2026-11-01/`](docs/ai-index-benchmark/remeasure-2026-11-01/README.md). Where it is
  lost is not known yet: `partsDirectory` keeps every map and reduce answer of a file, so the next
  run shows it.

### Verification of 2026-11-01 (eleven reviews of b74aa6b)

- **D3. The self-test's Granite tiers for a big POM**: `big-xml` and `huge-xml` take `file-body-xml`
  since three runs each (`remeasure-2026-11-02/`). Open: a large POM for `big-pom` / `huge-pom`,
  which keep the generic prompt unmeasured.

### Review of 2026-11-02 (seven reviews of c2d6834)

- Benchmark: recall counts names, not faithfulness (a bare name list scores 100 %, wrong claims are
  not measured); with three runs against three a Fisher test cannot reach 0.05, and hit@1 is
  compared unpaired (McNemar fits); every prompt version was tuned on the corpus it is scored on.
- Card text (with the next re-render): the correlation −0.46 is from an older population (today
  −0.18), the BM25 range "67 to 86 %" ends at 91 %, the Fisher test with 3 against 3 runs can never
  reach p < 0.1.
- Tests (row 29): drift from TEST_WRITING_GUIDE in older tests: 433 test names of two parts and 221
  of one where the guide wants three, tests without arrange/act/assert comments or outside folds,
  195 fully qualified names (counted 2026-11-05); a sweep renames about 650 tests, which CLAUDE.md
  counts as a large refactor to report, not to apply.
- CodeQL #270 (`RequestRateLimiter`: the return value of `awaitNanos` ignored) is a false positive:
  the loop in `acquire()` re-checks the window after every wake-up; the owner dismisses it.
- SonarCloud (row 3): the 98 issues of `main` are not readable through the API. Of the 159 code
  smells of PR #269 left are the test style ones (public test methods, a lambda with several calls
  in `assertThrows`, many assertions in one test), the cognitive complexity of nine methods and an
  eight-parameter method (refactors to report, not to apply), and a few false positives (comments
  read as code).

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
  natives only and runs `Plan`; a GitHub-hosted runner has no GPU device, and only a real generation
  loads the native library. `cuda13-windows-x86-64` (2026-10-23) and `vulkan-windows-x86-64`
  (2026-11-05) had a manual pass on this machine's RTX 3070 Laptop (`build.sh -Dgpu=<classifier>`,
  then a `GenerateFileIndex` with `gpuLayers` set). **Still unlaunched: `rocm`, `sycl` and
  `openvino`**: no AMD discrete, Intel or other GPU here (row 39).
