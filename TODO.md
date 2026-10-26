# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open.

## Open

- **After the 1.3.0 release: build `examples/windows-fatjar` once against Central only**, with an
  empty local repository. The release PR set it to `1.3.0` and removed the `central-snapshots`
  block; the same check against the `1.3.0-SNAPSHOT` from Publish run
  [40367427445](https://github.com/bernardladenthin/srcmorph/actions/runs/40367427445) passed
  (2026-10-26, 32 MiB jar), so a failure now would point at the release upload, not the example.

- **The GPU classifier fat jars are verified structurally, never launched.** Since 1.2.0
  `.github/verify-classifier-fatjars.sh` asserts each is the artifact its name claims (one jar per
  classifier, the promised backend directory next to the default CPU natives, so a broken
  `-Dllama.classifier=` cannot silently ship CPU-only builds under GPU names). What it cannot assert
  is that the jar *works*: a GitHub-hosted runner has no CUDA/ROCm/SYCL/OpenVINO device, and the only
  command that would load the native library is a real generation. Closing this needs hardware —
  a self-hosted runner, or a manual pre-release pass on one GPU box per backend. Worth knowing which
  half is covered before reading the green check as "the CUDA jar runs".

  **`cuda13-windows-x86-64` has had that pass (2026-10-23, against `llama 6.0.0-SNAPSHOT`).** On an
  RTX 3070 Laptop the `-P gpu-cuda` fat jar (258 MB, carrying `Windows/x86_64/cuda13/` next to the six
  CPU library directories) ran a real `GenerateFileIndex` over the production prompt set through the
  CLI *and* through `srcmorph:generate`, both at 3474 t/s prefill / 86.8 t/s decode, with the loader
  naming the module (`with GPU module(s) cuda13`) and ggml registering `[CUDA, RPC, CPU]`. The same
  config on the default CPU fat jar is 63 / 7.1 with `[RPC, CPU]`, so the jar demonstrably loads its
  backend rather than falling back. **Still unlaunched: `rocm`, `sycl`, `openvino` and `vulkan`** —
  no AMD, Intel or second GPU here. Vulkan is reachable on any NVIDIA box and is the cheapest one to
  add next.

- **Drop the fat jars in 1.4.0 (owner's decision, 2026-10-26).** 1.3.0 is the last release with
  fat jars on GitHub; they were never on Central (`<attach>false</attach>`, `--maven-only`, and no
  `"central": true` in `.github/release-assets.json`), so removing them touches only the GitHub
  release. Users build their own jar locally, as `examples/windows-fatjar` shows. Since its
  modular natives java-llama.cpp ships no fat jar at all: the thin jar on Central is the deliverable,
  a GPU is `jbang --deps net.ladenthin:llama:<v>:<classifier> …`, and its smoke jobs launch the
  published jars on the classpath a consumer resolves. The same shape fits `srcmorph-cli`
  (`jbang net.ladenthin:srcmorph-cli:<v> config.json` resolves slf4j-simple and llama-platform from its
  pom; a GPU is one more `--deps` jar). Removing them drops the default and 14 signed classifier fat
  jars per release, the build loop in `publish.yml`, `sign-fatjars.sh`, `verify-classifier-fatjars.sh`
  and `.github/release-assets.json`'s file rules. It also closes a gap the current shape has: the
  artifact `smoke-fatjar` launches is the fat jar built by the `build` job (a GitHub asset, rebuilt by
  the publish jobs' loop), while the `srcmorph-cli` jar that goes to Central is never launched by any
  job. The smoke has to move to the thin jar plus its resolved runtime classpath (what
  java-llama.cpp's smoke sets are), so it tests what is published. `README.md`,
  `srcmorph-cli/README.md`, `CLAUDE.md` and the eight `examples/run_*` scripts that name the fat jar
  go with it.

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory (declared in `srcmorph/pom.xml`, the only reactor module with a jqwik test dependency).

- **`@VisibleForTesting` audit.** Nothing is annotated, and the inventory has two kinds of package-private members that must not be lumped together. **Production API, not candidates:** the four of `provider.LlamaCppJniProviderSupport` (`buildChatTemplateKwargs`, `buildInferenceParameters`, `warnOnTruncatedAnswer`, `logPromptCacheReuse`) and the static `lazyMode`/`cacheType` are package-private because `LlamaCppJniAiGenerationProvider` calls them (they were moved out of it so the PIT gate reaches them); their visibility serves the provider, the tests only ride along. **Test seams, the actual candidates:** `document.AiMdDocumentCodec.read(List)` and the `String write(AiMdDocument)` overload (their Javadoc says so; production goes through the `Path` overloads), `prompt.AiPromptPreparationSupport.trimSourceAtLineBreak` (called only inside its own class and by `AiPromptPreparationSupportTest`), and the three mojos' `build*Configuration()` (called only by their own `execute()` and by `MojoConfigurationMappingTest`). Guava is not a dependency, so closing this means either a project-local marker annotation on the second group (there is precedent: `support.ConvertToRecord`) or recording that the convention is not adopted here. Decide and act rather than re-auditing.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.
