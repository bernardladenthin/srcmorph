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
| 3 | SonarCloud: the analysis of PR #269 reads, that of `main` does not: `main` was analysed as a short branch (`Branch name: main, type: short` in the run of 2026-11-04), the main branch `master` last three months ago | the owner renamed the main branch to `main` (2026-11-07); left: one analysis of `main` as the long branch (the push of the merge of #269, or a re-run of the last SonarQube run on `main`), then its issues are read | Owner | Review of 2026-11-02, tests and gates |
| 38 | Use jllama's `ChatStreamAssembler` (not in `llama` 6.0.0) | switch with its release | External | Other open items |
| 40 | Merge PR #269 | merge | Owner | Enterprise kinds |
| 58 | `java-facts` `constructors` misses a constructor whose first parameter is generic (`Reg(List<String> names)`) | the nested type-argument group the methods pattern has; tests with `List<X>` and `Map<K, V>` first | Claude | Review of 2026-11-07 (whole tree), W1 |
| 59 | A source file named `package` (or `project` at the root) is written to `package.ai.md` (`project.ai.md`), which the package (project) phase owns | `checkTargetsAndWindows` refuses such a target, case-insensitively, naming the file; a test | Claude | Review of 2026-11-07 (whole tree), W2 |
| 60 | The summary of a deleted, renamed or newly excluded source stays, and the package and project phases go on reading it | the file phase reports the `.ai.md` files no candidate produced, and deletes them with a new switch (`pruneOrphans`, default off); documented | Owner | Review of 2026-11-07 (whole tree), W3 |
| 61 | From 260 KB on every XML kind takes `xml-facts`: the `big-xml`/`huge-xml` tiers outrank the kind rules | the tiers keep the kind's counters: a `big-`/`huge-` tier per XML kind as for the POM is many rules; proposal: the tiers exclude the files a kind rule matches by content, so a large kind file stays on gpt-oss with map-reduce; or document it | Owner | Review of 2026-11-07 (whole tree), W4 |
| 62 | A Kubernetes `kind: Secret` manifest is skipped only by its name (`*secret*`) | a branch of the `secrets` rule: YAML whose head has `kind: Secret`, unless a Helm template; tests | Claude | Review of 2026-11-07 (whole tree), W5 |
| 63 | `reasoningEffort` defaults to `low` and goes to every openai-compatible request as `reasoning.effort` | the default is empty: only a configured effort is sent; the Luna example and `models.json` state `low` where they were measured with it, so their results stay comparable; CHANGELOG (breaking) | Owner | Review of 2026-11-07 (whole tree), W6 |
| 64 | `run.py`: after a paid model run the price and usage lookups can fail and lose the meta and the remaining models | write the meta first, the cost in a `try` after it (`null` with the error) | Claude | Review of 2026-11-07 (whole tree), W7 |
| 65 | `collectCandidates` walks every directory: an unreadable one aborts the run even when excluded | `Files.walkFileTree`: an excluded `dir/**` is skipped as a subtree, an unreadable directory logged and passed | Claude | Review of 2026-11-07 (whole tree), W8 |
| 66 | `run.py --model-dir` relative resolves against the generated configuration's directory | resolve it with the other paths; a test | Claude | Review of 2026-11-07 (whole tree), W9 |
| 67 | CLI `All` runs the whole file phase before it finds the package phase has no rule | `All` checks both phase engines before the file phase; a test | Claude | Review of 2026-11-07 (whole tree), W10 |
| 68 | openai-compatible sends `top_k` and `repetition_penalty` always, and the effort only in OpenRouter's shape | send them only when configured; the Javadoc says the body is shaped for OpenRouter and llama-server | Owner | Review of 2026-11-07 (whole tree), W11 |
| 69 | `CalibrateEngine` calibrates skip rules' models, does not validate the keys, lets a JNI `IllegalStateException` through | filter skip rules, `requireDefinedModels`, wrap the runtime exception naming the model; tests | Claude | Review of 2026-11-07 (whole tree), W12 |
| 70 | `batch-facts` `errorlevel` without a leading word boundary (`:exitWithErrorLevel` counts) | add the word boundary; a test with gradlew.bat's labels | Claude | Review of 2026-11-07 (whole tree), W13 |
| 71 | `openapi-facts` `operations (approx)` counts schema properties named `get`, `options`, `head` | count only keys one level below a path key; a test | Claude | Review of 2026-11-07 (whole tree), W14 |
| 72 | `java-facts`: the header's "methods include constructors" is wrong for constructors without a modifier; names with `$` inside are missed | correct the header; names `[A-Za-z_$][\w$]*` in the methods, fields and constructors patterns; tests | Claude | Review of 2026-11-07 (whole tree), W15 |
| 73 | `sql-facts`: `CREATE TEMP/RECURSIVE VIEW`, `EVENT TRIGGER`, Oracle `NOFORCE`/`EDITIONING VIEW`, `PRIVATE TEMPORARY TABLE` count 0; `DOMAIN` uncounted | the optional keywords; `CREATE DOMAIN` under types; tests | Claude | Review of 2026-11-07 (whole tree), W16 |
| 74 | `actions-facts`: a reusable workflow's input `runs-on`, a job output `matrix` count; inline `schedule: [{cron: ...}]` does not | skip the keys under `inputs:`/`outputs:`, add the inline cron; tests | Claude | Review of 2026-11-07 (whole tree), W17 |
| 75 | `ansible-facts` `FQCN module calls`: any key with two dots (`net.ipv4.ip_forward: 1`) | only a task key, next to its `name:`; a test | Claude | Review of 2026-11-07 (whole tree), W18 |
| 76 | Kotlin `expect`/`actual`, Scala `implicit final class`/`implicit override def`, Groovy nested method type parameters are missed | the extra modifiers and the nested type-parameter group of java-facts; tests | Claude | Review of 2026-11-07 (whole tree), W19 |
| 77 | `jsp-facts` `input fields` misses `<form:checkboxes>`, `<form:radiobutton(s)>`, `<form:hidden>` | add them; a test | Claude | Review of 2026-11-07 (whole tree), W20 |
| 78 | `score.py` `fenced_blocks`: a fence opened in a list item runs to the text's end | close it with the item (CommonMark), as its docstring says; a test | Claude | Review of 2026-11-07 (whole tree), W21 |
| 79 | `score.py`/`search.py` ignore the skip rule; `search.py` reads sources as strict UTF-8 | apply the skip condition's file names; decode with `score.source_text`; tests | Claude | Review of 2026-11-07 (whole tree), W22 |
| 80 | `correlate.py`: an empty table divides by zero, a constant series prints `None` as a number | guard both, print `n/a`; tests | Claude | Review of 2026-11-07 (whole tree), W23 |
| 81 | Model card prose against its own tables: Qwen3.8-27B "best in every run" (Muse is above), Qwen3.5-35B "longest" summaries, three USD-per-MB figures on 0.23 MB, Grok's 111 KB in decimal, `run.py --prompts v2` | correct each sentence; numbers and tables stay as published | Claude | Review of 2026-11-07 (whole tree), W24 |
| 82 | Javadoc against the code: a timeout "sent again", "thirty-seven" fields (44), the mock as the calibration's fallback (it is openai-compatible), a KV-cache warning for remote models | correct each | Claude | Review of 2026-11-07 (whole tree), W25 |
| 83 | Example comments: JavaScript "takes the fallback" (not indexed), "every file up to ~260 KB" on gpt-oss (tiers only for Java, SQL, XML, POM), "reads its compile source roots" (two subtrees) | correct each | Claude | Review of 2026-11-07 (whole tree), W26 |
| 84 | `large.py` docstring: three tables and two indexes (it writes four and three) | correct it | Claude | Review of 2026-11-07 (whole tree), W27 |

## Open

### Enterprise kinds of profile v3 (2026-11-04)

- **PR #269** (branch `feat/profiles-v3-kinds-and-safety`): CI green but the Claude review action;
  the owner merges, then this line goes.
- The self-test routes every kind of the Luna example but the Java EE classes (its `java` rule takes
  their prompt) since the real-project runs of 2026-11-07
  ([`remeasure-2026-11-07/`](docs/ai-index-benchmark/remeasure-2026-11-07/README.md)). Open from
  them: GPT-6 Luna summarises a Log4j 2 file's dotted keys as groups with the ops prompt (properties
  93 % against 100 % through the fallback; the Luna example keeps the ops rule), and gpt-oss-20b
  leaves a batch file's `goto` labels out with either prompt (37 %).

### TypeScript measurement of 2026-11-05 (internal; the data stays out of the repository)

- **`package.json` varies with gpt-oss-20b**: 45, 13, 28 and 15 of its 45 participants, commands,
  tools and dependencies in four runs (Luna 43 to 45 each time), with the fallback prompt. `run.py`
  routes it to the ops prompt with `npm-facts` (scripts, dependencies, engines, workspaces) now; that
  rule is measured (C1): with a line for a manifest in the ops prompt gpt-oss-20b names 36 of 45 on
  average (fallback 25), Luna all; a `pyproject.toml` takes the ops prompt since 2026-11-07 (gpt-oss-20b
  98 % against 88 %, Luna 97 % against 86 %), without counters of its own.

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

### Review of 2026-11-07 (the whole tree at da2f2ec3)

The delta review's ten findings are done (rows 48 to 57). The whole-tree review's findings W1 to W27
are rows 58 to 84; the review reproduced the counter findings with the profile's own counting classes
and found no high-severity defect. Details beyond the rows:

- **W1**: srcmorph's own `ModelRegistry`, `FactGroups`, `PromptCatalog` and `SourceExcludeFilter` count
  0 constructors; on jeromq, jline3 and lmdbjava 119 of 197 where the count differs (JavaParser).
- **W2**: on a case-insensitive file system `Package` collides as well; with other summaries in the
  directory the package phase and the file phase rewrite the file in turn, two calls every run.
- **W3**: nothing under `srcmorph/src/main` deletes a file; `package.ai.md` keeps linking the
  summary, and its checksum is unchanged, so nothing is rewritten either.
- **W5**: the "another kind than Secret" exclusion reads only the head (16 384 characters), so a
  multi-document `app-secrets.yaml` whose `kind: Secret` comes later is sent too.
- **W6**: `models.json`'s notes record routes that "spent the output budget on reasoning". Changing
  the default changes what a remote run sends, so the measured configurations name their effort.
- **W11**: not tested against a strict OpenAI endpoint; llama-server reads `reasoning_effort`, which
  `LlamaCppJniProviderSupport` names itself.

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
