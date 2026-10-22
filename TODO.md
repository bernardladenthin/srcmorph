# TODO — srcmorph reactor (Maven plugin now `srcmorph-maven-plugin`, formerly `llamacpp-ai-index-maven-plugin`)

Open work items for this repo. Cross-cutting tracking lives in
[`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md); items here are
repo-specific or this repo's slice of a cross-cutting initiative.

**Completed work is not recorded here.** It lives in git history and in
`crossrepostatus.md`; a finished item is deleted from this file rather than annotated, so
everything below is genuinely still open.

## Open

- **`main` does not build until `net.ladenthin:llama` 6.0.0 is released.** `srcmorph/pom.xml` pins
  `<llama.version>6.0.0</llama.version>` (`b710796`; jllama renumbered its next release from 5.2.0 to
  6.0.0), and Central's newest is still `5.1.0`
  (`llama-6.0.0.pom` is HTTP 404, checked 2026-10-20). This reactor declares no repository besides
  Central, so every CI run is red at the first Maven step until that release exists. It passes locally
  wherever an earlier `mvn install` of java-llama.cpp left the version in `~/.m2`; reproduce the real
  state with `mvn -Dmaven.repo.local=/tmp/empty dependency:get -Dartifact=net.ladenthin:llama:6.0.0`.
  **Reverting is not an option**: the pom comment explains why 6.0.0 is a breaking bump this module
  needs (the binding removed `enableFlashAttn()`/`FLASH_ATTN` and renamed `setTensorReadLazy` to
  `setLazyMode`; the provider uses the new forms, so it cannot compile against 5.1.0), and the reactor additionally depends on 6.0.0's
  natives-jar split (`net.ladenthin:llama-platform`, GPU natives jars via `-Dllama.classifier`). Against a locally installed snapshot of it
  (`-Dllama.version=<v>-SNAPSHOT install`, measured under the old 5.2.0 number) the whole reactor was
  green, so the jllama release is the
  only thing missing -- this item closes with it, nothing to change here.

- **The GPU classifier fat jars are verified structurally, never launched.** Since 1.2.0
  `.github/verify-classifier-fatjars.sh` asserts each is the artifact its name claims (one jar per
  classifier, the promised backend directory next to the default CPU natives, so a broken
  `-Dllama.classifier=` cannot silently ship CPU-only builds under GPU names). What it cannot assert
  is that the jar *works*: a GitHub-hosted runner has no CUDA/ROCm/SYCL/OpenVINO device, and the only
  command that would load the native library is a real generation. Closing this needs hardware —
  a self-hosted runner, or a manual pre-release pass on one GPU box per backend. Worth knowing which
  half is covered before reading the green check as "the CUDA jar runs".

- **Follow java-llama.cpp and drop the per-classifier fat jars? (owner's decision)** Since its
  modular natives java-llama.cpp ships no fat jar at all: the thin jar on Central is the deliverable,
  a GPU is `jbang --deps net.ladenthin:llama:<v>:<classifier> …`, and its smoke jobs launch the
  published jars on the classpath a consumer resolves. The same shape fits `srcmorph-cli`
  (`jbang net.ladenthin:srcmorph-cli:<v> config.json` resolves slf4j-simple and llama-platform from its
  pom; a GPU is one more `--deps` jar) and would remove the 14 signed classifier fat jars per release,
  the loop above and the structural-only check. It would also close a gap the current shape has: the
  artifact `smoke-fatjar` launches is the fat jar built by the `build` job (a GitHub asset, rebuilt by
  the publish jobs' loop), while the `srcmorph-cli` jar that goes to Central is never launched by any
  job -- a smoke over the thin jar plus its resolved runtime classpath (what java-llama.cpp's smoke
  sets are) would test what is published. Decide before the next release that bumps to a published
  llama 6.0.0; the classifier list is current for 6.0.0 either way.

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory (declared in `srcmorph/pom.xml`, the only reactor module with a jqwik test dependency).

- **`@VisibleForTesting` audit.** Nothing is annotated, and the inventory has two kinds of package-private members that must not be lumped together. **Production API, not candidates:** the four of `provider.LlamaCppJniProviderSupport` (`buildChatTemplateKwargs`, `buildInferenceParameters`, `warnOnTruncatedAnswer`, `logPromptCacheReuse`) and the static `lazyMode`/`cacheType` are package-private because `LlamaCppJniAiGenerationProvider` calls them (they were moved out of it so the PIT gate reaches them); their visibility serves the provider, the tests only ride along. **Test seams, the actual candidates:** `document.AiMdDocumentCodec.read(List)` and the `String write(AiMdDocument)` overload (their Javadoc says so; production goes through the `Path` overloads), `prompt.AiPromptPreparationSupport.trimSourceAtLineBreak` (called only inside its own class and by `AiPromptPreparationSupportTest`), and the three mojos' `build*Configuration()` (called only by their own `execute()` and by `MojoConfigurationMappingTest`). Guava is not a dependency, so closing this means either a project-local marker annotation on the second group (there is precedent: `support.ConvertToRecord`) or recording that the convention is not adopted here. Decide and act rather than re-auditing.

- **Null-safety refinement.** JSpecify + NullAway are enforced at compile time in strict JSpecify mode in every module (see each module's own `pom.xml`); `@NullMarked` on the package; framework-populated POJOs carry class-level `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`. Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.
