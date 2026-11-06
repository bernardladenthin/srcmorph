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
| 38 | Use jllama's `ChatStreamAssembler` (not in `llama` 6.0.0) | switch with its release | External | Other open items |
| 40 | Merge PR #269 | merge | Owner | Enterprise kinds |

## Open

### Enterprise kinds of profile v3 (2026-11-04)

- **PR #269** (branch `feat/profiles-v3-kinds-and-safety`): CI green but the Claude review action;
  the owner merges, then this line goes.
- The plugin's self-test routes the kinds measured on real projects (C4, 2026-11-06); still only in
  the Luna example, because no real project of theirs was measured: persistence and integration XML,
  WSDL, Facelets, OpenAPI, AsyncAPI, Avro, GraphQL, Camel YAML, Compose, Ansible, Gradle, properties,
  PowerShell, batch, Kotlin, Groovy, Scala and the Java EE classes.

### TypeScript measurement of 2026-11-05 (internal; the data stays out of the repository)

- **`package.json` varies with gpt-oss-20b**: 45, 13, 28 and 15 of its 45 participants, commands,
  tools and dependencies in four runs (Luna 43 to 45 each time), with the fallback prompt. `run.py`
  routes it to the ops prompt with `npm-facts` (scripts, dependencies, engines, workspaces) now; that
  rule is measured (C1): with a line for a manifest in the ops prompt gpt-oss-20b names 36 of 45 on
  average (fallback 25), Luna all; `pyproject.toml` is not measured.

### Audit of 2026-11-01 (PR #261)

- **gpt-oss-20b's map-reduce loses the middle of a file** at random and at both steps (C2,
  [`remeasure-2026-11-06/`](docs/ai-index-benchmark/remeasure-2026-11-06/README.md)): the dump's and
  the catalog's middles reach the summary in every run, the enum's is lost in two of three, once by
  the map answer and once by the reduce. A sharper family rule in both prompts did worse and is not
  adopted; a file of this size wants a stronger model. Open only as a note for the next model round.

### Verification of 2026-11-01 (eleven reviews of b74aa6b)

- **D3. The self-test's Granite tiers for a big POM**: `big-pom` takes `file-body-xml` since C3
  (a real 590 KB POM, `remeasure-2026-11-06/`); `huge-pom` keeps the generic prompt, as no real POM
  of a megabyte was found.

### Review of 2026-11-02 (seven reviews of c2d6834)

- CodeQL #270 (`RequestRateLimiter`: the return value of `awaitNanos` ignored) is a false positive:
  the loop in `acquire()` re-checks the window after every wake-up; the owner dismisses it.
- SonarCloud (row 3): the 98 issues of `main` are not readable through the API. Of the code smells
  of PR #269 the test style ones are done in phase B (one call per `assertThrows` lambda), except
  "test methods should not be public", which the owner's guides decide the other way; left are the
  cognitive complexity of nine methods and an eight-parameter method (refactors to report, not to
  apply), and a few false positives (comments read as code).

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
