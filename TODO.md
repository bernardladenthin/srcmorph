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
| 1 | CodeQL #270, `awaitNanos` ignored | false positive: dismiss it on GitHub | Owner | PR #269 |
| 3 | SonarCloud: the analysis of PR #269 reads, that of `main` does not: `main` was analysed as a short branch (`Branch name: main, type: short` in the run of 2026-11-04), the main branch `master` last three months ago | the owner renamed the main branch to `main` (2026-11-07); left: one analysis of `main` as the long branch (the push of the merge of #269, or a re-run of the last SonarQube run on `main`), then its issues are read | Owner | PR #269 |
| 38 | Use jllama's `ChatStreamAssembler` (not in `llama` 6.0.0) | switch with its release | External | Other open items |
| 40 | Merge PR #269 | merge | Owner | PR #269 |
| 99 | A kept or produced summary occupies only its own directory: a package above a directory without `package.ai.md` is pruned | occupy every ancestor up to the output root; tests: moved into a new subdirectory, an intermediate directory without a package file | Claude | Review 2026-11-08 (2) |
| 100 | `secrets` misses `aws.secretKey=`, `app.secret-key=`, `jwt.secret.key=` and a blank separator in `.properties` | the word anywhere in the key, a blank separator; four copies, tests | Claude | Review 2026-11-08 (2) |
| 101 | An extensionless script whose `#!` line cannot be read loses its summary to prune | add it to the unreadable sources, test | Claude | Review 2026-11-08 (2) |
| 102 | A missing default `src` empties the plan, and prune deletes the whole index | no prune when a subtree was skipped or the plan routes no file, test | Claude | Review 2026-11-08 (2) |
| 103 | `SrcMorphConfiguration` Javadoc of `pruneOrphans` names file summaries only | name the package files | Claude | Review 2026-11-08 (2) |
| 104 | No test of a kept package keeping the packages above it | test (with row 99) | Claude | Review 2026-11-08 (2) |
| 105 | Plugin README: "seventeen fact-counter files" (35) | fix the number | Claude | Review 2026-11-08 (2) |
| 106 | `secrets` comment "never sent ... whatever their extension" overclaims | name what it covers | Claude | Review 2026-11-08 (2) |
| 107 | `openapi` condition takes `swagger:\n  enabled: true` | require a version value, test | Claude | Review 2026-11-08 (2) |
| 108 | `camel-yaml` condition takes a redirect list `- from: /docs/old` | require a Camel marker, test | Claude | Review 2026-11-08 (2) |
| 109 | `Dockerfile.*` takes `Dockerfile.dockerignore` | leave `*.dockerignore` out, test | Claude | Review 2026-11-08 (2) |
| 110 | `scala-facts.tsv` header names `.sc`, which nothing routes | drop it | Claude | Review 2026-11-08 (2) |
| 111 | `mybatis-facts.tsv` header names iBATIS | drop it | Claude | Review 2026-11-08 (2) |
| 112 | MODEL-CARD: "the card's first ranking (Luna, Qwen3.8, gpt-oss)" | among the repeated configurations other than Muse | Claude | Review 2026-11-08 (2) |
| 113 | MODEL-CARD: "Every other number ... one run" and the caveat against the Recommendations' means | the Results tables and charts | Claude | Review 2026-11-08 (2) |
| 114 | MODEL-CARD: "3.5 times as much as Luna" (3.56) | 3.6 | Claude | Review 2026-11-08 (2) |
| 115 | run.py's possessive quantifiers need Python 3.11 | document it | Claude | Review 2026-11-08 (2) |
| 116 | `score.py` applies the card's cut-off lists to another `--out` without `--meta` | require `--meta` then, test | Claude | Review 2026-11-08 (2) |
| 117 | `plot.load()` (the card's quality metric) has no unit test | test | Claude | Review 2026-11-08 (2) |

## Open

### PR #269 (branch `feat/profiles-v3-kinds-and-safety`)

- CI green but the Claude review action and FOSSA; the owner merges, then this section goes.
- **CodeQL #270** (row 1, `RequestRateLimiter`: the return value of `awaitNanos` ignored) is a false
  positive: the loop in `acquire()` re-checks the window after every wake-up; the owner dismisses it.
- **SonarCloud** (row 3): once `main` is analysed as the long branch, its issues are read. Of PR
  #269's code smells left: the cognitive complexity of nine methods and an eight-parameter method
  (refactors to report, not to apply), "test methods should not be public" (the owner's guides decide
  the other way) and a few false positives (comments read as code).

### Review 2026-11-08 (2)

The two reviews of dce04df5 (whole tree, delta to `main`): rows 99 to 117; medium are 99 (prune) and
100 (secrets).

### Known limits, for the next model round (no open work)

Measured and written up; listed so a later model round checks them again:

- gpt-oss-20b's map-reduce loses the middle of a large file at random (C2,
  [`remeasure-2026-11-06/`](docs/ai-index-benchmark/remeasure-2026-11-06/README.md)); a file of that
  size wants a stronger model.
- gpt-oss-20b leaves a batch file's `goto` labels out with either prompt (37 %); GPT-6 Luna
  summarises a Log4j 2 file's dotted keys as groups with the ops prompt (properties 93 % against 100 %
  through the fallback)
  ([`remeasure-2026-11-07/`](docs/ai-index-benchmark/remeasure-2026-11-07/README.md)).
- The self-test's `huge-pom` keeps the generic prompt: no real POM of a megabyte was found to
  measure `file-body-xml` on (`big-pom` takes it since C3).
- `plot.py`'s heatmap counts "Seed counts" from `numbersStated` also for a failed row and divides by
  four; `score.summarize` does neither. No row of the card is affected.

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
  with its test. The stub-server tests in `OpenAiCompatibleGenerationProviderTest` stay and prove the
  switch.
