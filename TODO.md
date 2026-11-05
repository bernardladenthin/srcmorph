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
| 13 | A rule for `package.json`: the rule and its counters are in (`npm-facts`); measuring it waits | measure the ops prompt with `npm-facts` on it once (internally) | Claude | Run plan, C1 |
| 29 | Test upkeep: the guide drift of older tests (433 two-part and 221 one-part test names, missing arrange/act/assert comments, 195 qualified names) | decided 2026-11-05: the whole sweep, after phase A | Claude | Run plan, phase B |
| 32 | Measure where gpt-oss-20b loses the middle; the part summaries are kept on request (`partsDirectory`) | one large-corpus run with `partsDirectory` | Claude | Run plan, C2 |
| 35 | A large POM for `big-pom` / `huge-pom` (local Granite, hours) | three runs each, as for `big-xml` | Claude | Run plan, C3 |
| 36 | The self-test routes the new kinds after a measurement on real projects (Claude picks open-source ones) | measure, then route | Claude | Run plan, C4 |
| 37 | Card text and the method's limits (correlation, BM25 range, Fisher, faithfulness) | with the next re-render of the card | Owner | Review of 2026-11-02, benchmark tooling |
| 38 | Use jllama's `ChatStreamAssembler` (not in `llama` 6.0.0) | switch with its release | External | Other open items |
| 40 | Merge PR #269 | merge | Owner | Enterprise kinds |
| 45 | The review's further proposals: renames N4 to N15 and P9 | decided 2026-11-05: all of them, the configuration keys included | Claude | Run plan, phase A |

## Run plan (2026-11-05): rows 45, 29, 13, 32, 35 and 36, then the reviews

**How the run works.** A `/loop` names this section and nothing else; everything it needs is here,
and the run may change this section itself (a step added, split, reordered or reworded) when the
work shows it should.

- **One step per pass.** Take the first unticked step, do it fully with its tests, tick it (`[x]`
  with the commit), commit and push. A step found on the way is added where it belongs; a step
  that cannot be done is marked `[!]` with the reason and skipped.
- **Green before a tick.** Every step of phases A and B ends green: `mvn verify` of the reactor
  (tests, SpotBugs, NullAway, Checker, ArchUnit), Spotless applied. PIT at 100 runs at the end of
  each phase. The guides apply as CLAUDE.md says (`CODE_WRITING_GUIDE`, `TEST_WRITING_GUIDE`);
  every breaking change gets its line in the CHANGELOG's Upgrading section in the same commit.
- **Everything on the branch** `feat/profiles-v3-kinds-and-safety` (PR #269); no new branch, no
  force push, no remote branch touched; releases, tags, repository settings and the merge stay with
  the owner. A commit is preceded by the leak scan (no private names or paths in the diff).
- **Watch the PR after every push**: CI (`gh pr checks 269`; on a red job of the run's own making:
  cancel the run, commit the fix, push), the SonarCloud analysis of the PR (its new issues, read
  through the API) and the reviews and comments on GitHub (CodeRabbit and any other reviewer). A
  verified finding becomes a step of the phase it belongs to, or is fixed at once when it is small;
  one that is not real gets a short reply saying why. The Claude review job and FOSSA are ignored.
- **Long jobs** (PIT, a measurement) run in the background; the pass waits for their notice instead
  of polling.
- **Measurements** (phase C) follow the benchmark's rules: the owner's model list, one OpenRouter
  run at a time (a local run may run beside it), keys read per command and never printed, internal
  corpora and their outputs stay outside the repository, `scores/` may be re-scored, the model card
  is not re-rendered.
- **The end.** When phases A to C are ticked, phase R runs. When R is ticked and only `Owner` and
  `External` rows are left in the plan: the ticked steps and this section go, the plan's finished
  rows go, CI is green, a final report (in German) goes to the owner, and the loop stops.

**Phase A: the review's renames and P9** (`docs/code-review-2026-11-05.md` sections 1 and 2; a
breaking change, the CHANGELOG's Upgrading section names each one)

- [x] A1 (a1ce28f) The small ones: N15 the plugin's package Javadoc, N14 the CLI's names (`logConfigurationEcho`,
  `configurationToYaml`, `readString` / `fromJson` / `fromYaml` package-private), N13
  `basePromptLength`, N12 one `UNKNOWN_CONTEXT_LENGTH` value (`-1`), N11 `TimedGeneration`
- [x] A2 (6381089) N5 `AiMdHeader`'s accessors by name (`headerVersion()`, `checksum()`, `sourceDate()`,
  `generatedAt()`, `generatorVersion()`, `aiVersion()`, `nodeType()`, `promptHash()`, `factsHash()`,
  `modelHash()`, `childrenHash()`); the codec keeps the letters
- [x] A3 (6300ad9) N8 `AiIndexPlan.Entry` to `Route`, `RunRecord.Entry` to `FileRecord`, a `RecordedHashes`
  value instead of `Map.Entry<String, String>`
- [x] A4 (9f2546e) N9 `pluginVersion` to `generatorVersion` (configuration key, bean, `RunRecord`)
- [x] A5 N10 drop `AiGenerationConfig`: the model registry builds `ProviderConfig` from the
  `AiModelDefinition` directly
- [ ] A6 N4a, N6 and N7 in one pass per package, each class renamed once (the `Ai` prefix goes but
  on `AiMd*`; `RoutingRule`, `RuleSelector`, `ModelRegistry`, `PromptCatalog`, `PromptPreparer`,
  `FactGroups`, `AiMdRewritePolicy` with `shouldRewrite`, `MapReduceMath`; `AiCondition` to
  `RuleCondition`, `AiPathSupport` to `SourcePaths`); the test classes, PIT's `targetClasses`,
  `spotbugs-exclude.xml`, ArchUnit and CLAUDE.md move with them
  - [ ] A6a `support`
  - [ ] A6b `config`
  - [ ] A6c `facts`, `routing`
  - [ ] A6d `prompt`, `document`
  - [ ] A6e `provider`
  - [ ] A6f `indexer`, `indexer.generation`
  - [ ] A6g `engine`, the CLI, the plugin
- [ ] A7 N4b the configuration keys `rules`, `packageRules`, `projectRules` (the loader, the CLI,
  the plugin, `examples/`, the profile's configurations, the plugin-it fixture, the benchmark's
  `run.py`, the READMEs)
- [ ] A8 P9 `module-info` exports `config` and `engine`, and `support` to the CLI only
- [ ] A9 PIT at 100 in all three modules; CLAUDE.md's counts; `docs/code-review-2026-11-05.md` says
  what was applied

**Phase B: the tests follow `TEST_WRITING_GUIDE`** (row 29: three-part names, arrange/act/assert
comments, folds, no fully qualified names; the test style findings of SonarCloud: package-private
test methods, one call in an `assertThrows` lambda)

- [ ] B1 `support` (14 files)
- [ ] B2 `config` (15)
- [ ] B3 `facts` (16), `routing` (3)
- [ ] B4 `document` (7), `prompt` (4)
- [ ] B5 `provider` (20)
- [ ] B6 `indexer` (9)
- [ ] B7 `indexer.generation` (15)
- [ ] B8 `engine` (12) and the root package (3)
- [ ] B9 `srcmorph-cli` (6), `srcmorph-maven-plugin` (8)
- [ ] B10 PIT at 100 in all three modules; the counts in this file's sections are deleted

**Phase C: the measurements** (internal where the data is; OpenRouter runs one at a time, a local
Granite run may run beside one; the scores go to `scores/`, the model card is not re-rendered)

- [ ] C1 Row 13: the ops prompt with `npm-facts` on `package.json` (gpt-oss-20b and Luna, three runs)
- [ ] C2 Row 32: a large-corpus run of gpt-oss-20b with `partsDirectory`; where the middle is lost,
  and what follows from it for the map or reduce prompt
- [ ] C3 Row 35: a large POM for `big-pom` / `huge-pom`, three runs each with the local Granite tiers;
  the self-test's rule follows the result
- [ ] C4 Row 36: the new kinds on real open-source projects (picked and named in the writeup); the
  self-test routes the kinds that hold up
- [ ] C5 The writeups (`docs/ai-index-benchmark/`), CHANGELOG, this file

**Phase R: the reviews** (after phase C; at least two review agents per round, each started fresh
with the code, not with the run's conclusions)

- [ ] R1 Round 1: one agent reviews the whole repository, one the delta of the branch against
  `main` (`git diff main...HEAD`): correctness, the guides, tests, documentation against the code,
  the benchmark's claims against its data. Each finding is verified against the code before it
  counts
- [ ] R2 The verified findings of R1 fixed, each with its tests (a large refactor is reported in
  this file instead of applied, as CLAUDE.md says)
- [ ] R3 Round 2 the same way, on the state after R2; its verified findings fixed. Another round
  follows while a round finds anything of high or medium weight, at most four rounds in all
- [ ] R4 CI green, the SonarCloud analysis of the PR without a new issue of the run's own, no open
  review comment on the PR left unanswered

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
