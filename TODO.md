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

8. **Benchmark and measurement.** `score.py`: the coverage line out of the scored text like the facts
   line, code fences out of the name recall, common words only in
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

- **`@VisibleForTesting` audit.** Nothing is annotated, and the inventory has two kinds of package-private members that must not be lumped together. **Production API, not candidates:** the four of `provider.LlamaCppJniProviderSupport` (`buildChatTemplateKwargs`, `buildInferenceParameters`, `warnOnTruncatedAnswer`, `logPromptCacheReuse`) and the static `lazyMode`/`cacheType` are package-private because `LlamaCppJniAiGenerationProvider` calls them (they were moved out of it so the PIT gate reaches them); their visibility serves the provider, the tests only ride along. **Test seams, the actual candidates:** `document.AiMdDocumentCodec.read(List)` and the `String write(AiMdDocument)` overload (their Javadoc says so; production goes through the `Path` overloads), `prompt.AiPromptPreparationSupport.trimSourceAtLineBreak` (called only inside its own class and by `AiPromptPreparationSupportTest`), and the three mojos' `build*Configuration()` (called only by their own `execute()` and by `MojoConfigurationMappingTest`). Guava is not a dependency, so closing this means either a project-local marker annotation on the second group (there is precedent: `support.ConvertToRecord`) or recording that the convention is not adopted here. Decide and act rather than re-auditing.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.
