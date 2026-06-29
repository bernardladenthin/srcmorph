# AI Index — Model & Prompt Comparison

Scored comparison of 8 local GGUF models × 2 prompt versions for the plugin's `.ai.md`
code-summarization task. Scope, hardware, and reproduction: see [README.md](README.md).
Generated 2026-06-27 from the trees in [outputs/](outputs/).

All cells: 12 source files (`config` + `provider`) → 23 `.ai.md` each, 16K context, CPU.

---

## 1. Quantitative metrics

Columns: **avgLn** = mean lines per `.ai.md` (verbosity); **KB** = total output size;
**kw** = "keyword" hallucination hits; **fence** = stray ```` ``` ```` code fences (prompt forbids
them); **leak** = reasoning/`<think>`/harmony markers in output; **enum** = lines for the
trivial `AiGenerationKind` enum (lower = better section-omission); **pojo** = lines for the
`AiGenerationConfig` bean (lower = less accessor bloat); **s/file** = wall-seconds per summary.

| Cell | avgLn | KB | kw | fence | leak | enum | pojo | s/file |
|---|---|---|---|---|---|---|---|---|
| deepseek-coder-v2-lite v1 | 62.4 | 70 | 0 | 0 | 0 | 39 | 77 | 84.1 |
| deepseek-coder-v2-lite v2 | 52.9 | 62 | 0 | **24** | 0 | 41 | 56 | 66.6 |
| gptoss-20b v1 | 55.5 | 60 | 0 | 0 | 0 | 35 | 77 | 129.0 |
| gptoss-20b v2 | 45.3 | 43 | 0 | 0 | 0 | **13** | 62 | 121.4 |
| granite4-h-tiny v1 | 60.7 | 82 | 0 | 0 | 0 | 53 | 84 | 59.8 |
| granite4-h-tiny v2 | 49.2 | 44 | 0 | 0 | 0 | 40 | 78 | **33.9** |
| qwen25coder-7b v1 | 53.8 | 57 | 0 | 0 | 0 | 21 | 109 | 141.1 |
| qwen25coder-7b v2 | 47.9 | 51 | 0 | **12** | 0 | 23 | 71 | 132.8 |
| qwen3-4b-2507 v1 | 54.4 | 63 | 0 | 0 | 0 | 40 | 64 | 80.6 |
| qwen3-4b-2507 v2 | 53.8 | 60 | 0 | 0 | 0 | 40 | 70 | 72.3 |
| qwen35-4b v1 | 50.7 | 71 | 0 | 0 | 0 | 33 | 82 | 100.8 |
| qwen35-4b v2 | 49.7 | 62 | 0 | 0 | 0 | 38 | 76 | 90.6 |
| qwen3coder-30b v1 | 51.9 | 52 | 0 | 0 | 0 | 40 | 84 | 72.6 |
| qwen3coder-30b v2 | 53.2 | 57 | 0 | 0 | 0 | 33 | 73 | 71.4 |
| seed-coder-8b v1 | 49.7 | 39 | 0 | 0 | 0 | 38 | 78 | 79.5 |
| seed-coder-8b v2 | 49.7 | 44 | 0 | **4** | 0 | 35 | 67 | 85.1 |

---

## 2. Headline findings

1. **No reasoning leakage anywhere (leak = 0 in all 16 cells).** llama.cpp's default
   reasoning parsing strips Qwen `<think>` *and* gpt-oss *harmony* channels server-side, so
   `chatCompleteText` returns clean `content`. The Gemma-only `AiCompletionParser` was never
   needed. **gpt-oss output is clean** — the feared harmony-marker leakage does not occur.
2. **No "keyword" hallucination (kw = 0 everywhere).** The keyword tic seen in the original
   full-project 30B run did **not** reproduce in the `config`+`provider` scope — so v2's explicit
   keyword ban is **untested here** (nothing to suppress). Re-check on the mojo/indexer packages.
3. **Prompt v2 reduces verbosity and accessor bloat broadly** — lower `avgLn`/`KB` for most
   models, and `pojo` drops notably (qwen2.5-coder 109→71, deepseek 77→56, 30B 84→73, gpt-oss
   77→62). This is v2's clearest win.
4. **v2 induces code-fence violations on the *code-specialized* models** — DeepSeek (24),
   Qwen2.5-Coder (12), Seed-Coder (4) wrap e.g. the `#### Type` line in ```` ```java ````; the
   general models (gpt-oss, granite, qwen3-4b, qwen3.5-4b, qwen3-coder-30b) stay at **0**. This
   is a v2 **regression** that must be fixed before v2 ships.
5. **Trivial-file omission (enum) is only partially honored** by v2 — gpt-oss obeyed it well
   (35→13), 30B and granite partly; most models did not shrink. Prompt wording is not a reliable
   lever for small models — filtering/deterministic handling is more dependable.
6. **Reasoning/thinking is a pure throughput tax with no quality payoff here.**
   `qwen3.5-4b` (hybrid, thinks) ran 90–101 s/file vs the non-thinking `qwen3-4b-2507` at
   72–81 s/file with equal-or-better output. `gpt-oss-20b` is slowest of all (121–129 s/file)
   despite being MoE — it spends tokens on harmony reasoning that is then discarded.

---

## 3. Speed ranking (v2, s/file, fastest first)

`granite4-h-tiny` **33.9** ≪ `deepseek-v2-lite` 66.6 < `qwen3coder-30b` 71.4 < `qwen3-4b-2507`
72.3 < `seed-coder-8b` 85.1 < `qwen35-4b` 90.6 < `gptoss-20b` 121.4 < `qwen25coder-7b` 132.8.

Active-param count dominates CPU decode: the ~1B-active Granite hybrid is **~4× faster** than the
dense 7B Qwen2.5-Coder, and MoE low-active models (Granite, DeepSeek 2.4B, Qwen3-Coder 3.3B) lead.

---

## 4. Qualitative quality (sampled: `provider/AiGenerationProviderFactory.java`)

| Tier | Models | Notes |
|---|---|---|
| **A — clean + accurate** | gpt-oss-20b, seed-coder-8b, qwen3-coder-30b | gpt-oss had the highest fidelity (captured the exact `"Unsupported AI provider: " + providerName` message and full dependency list) but is the slowest. Seed-Coder and Qwen3-Coder are clean, terse, and accurate. |
| **B — good, minor gaps** | granite-4.0-h-tiny, qwen3-4b-2507 | Granite is impressively accurate for ~1B active and by far the fastest (slightly incomplete dependency lists). Qwen3-4B-2507 is solid, terse, non-thinking. |
| **C — accurate but noisy** | deepseek-coder-v2-lite, qwen2.5-coder-7b, qwen3.5-4b | DeepSeek repeats the heading inside each section (`- **Purpose**: …`), leaks `{@link}` Javadoc tags, and triggers the most fences. Qwen2.5-Coder is the slowest and fences under v2. Qwen3.5-4B pays the thinking tax with no quality edge over Qwen3-4B-2507. |

---

## 5. Per-model pros & cons

| Model (active params) | Pros | Cons |
|---|---|---|
| **Qwen3-Coder-30B-A3B** (MoE ~3.3B) | Most complete & faithful content; code-specialized; clean (0 fences / 0 leak); fast for its quality (~71 s/file); Apache-2.0; 262K native ctx | Wrongly tags the class `final`; drops the `@SuppressWarnings` annotation; minor dangling `implements` line |
| **gpt-oss-20b** (MoE ~3.6B) | Highest fidelity — captured exact exception strings, constructor calls, and the unmodifiable/nullable `stopStrings` contract; clean; Apache-2.0 | Slowest (~121–129 s/file — harmony reasoning overhead); "final by default" nonsense; drops `@SuppressWarnings` |
| **Granite-4.0-H-Tiny** (MoE ~1B) | Fastest by far (~34 s/file); the only model that captured `@SuppressWarnings`; Apache-2.0; flat-KV hybrid → cheap long context | Verbose (repeats field lists across sections); wrong `final`; junk self-package dependency entry |
| **Seed-Coder-8B** (dense, MIT) | Clean, terse, accurate; avoided the `final` trap; permissive MIT | Calls the private fields "public fields"; drops `@SuppressWarnings`; v2 emits a few code fences |
| **Qwen3-4B-Instruct-2507** (dense) | Fast (~72 s/file); non-thinking; good core-logic capture; no fences | **Dropped all setters** from Public API (incomplete on a long file); overclaims thread-safety; "final fields" wrong |
| **Qwen2.5-Coder-7B** (dense) | Accurate, code-specialized; captured constructor + unmodifiable `stopStrings` | Slowest dense (~133–141 s/file); wrong `final`; accessor bloat (v1: 109 lines); v2 fences |
| **Qwen3.5-4B** (dense, hybrid) | Thorough API listing; avoided the `final` trap | Thinking tax (~90–101 s/file) with no quality edge; **fabricated field counts** (claimed 25 constants / 16 fields vs real ~13 / 14); junk self-dependency |
| **DeepSeek-Coder-V2-Lite** (MoE ~2.4B) | Fast (~67–84 s/file); accurate body content | **Copied the prompt's "Calculates VAT for invoices" example as the lead** — instruction-following failure; redundant `- **Label**:` echoing; most fences (24); leaked `{@link}` Javadoc tags; **license is a use-restricted custom *DeepSeek Model License* (weights), not OSI/commercial-OK** |

## 6. Source-faithfulness deep-dive — `AiGenerationConfig.java` (largest in-scope file, 414 lines)

Each model's summary of this complex POJO was checked field-by-field against the real source.
Cross-model findings:

- **`final` is hallucinated by 6 of 8 models.** The class is `public class AiGenerationConfig`
  (NOT final); only Qwen3.5-4B and Seed-Coder got it right. Likely pattern-matching — most classes
  in this repo *are* `final`. A reliable, recurring inaccuracy.
- **Annotation capture is poor.** Only Granite reported `@SuppressWarnings({"NullAway.Init", …})`;
  every other model dropped it. Most did capture `@ToString`.
- **The blockquote lead is the riskiest field.** DeepSeek emitted the prompt's literal *example*
  (`Calculates VAT for invoices`); the others produced correct domain leads.
- **Completeness varies on a long member list.** gpt-oss / Qwen3-Coder-30B / Qwen2.5-Coder listed
  the full accessor set accurately; Qwen3-4B-2507 omitted every setter; Qwen3.5-4B invented counts.
- **`equals`/`hashCode` intentionally absent** (managed by identity) — no model surfaced this
  subtlety (acceptable; it requires reasoning about a *negative*).

Implication: the structural facts models get wrong (class modifiers, annotations, exact member
sets, dependency lists) are exactly the ones an **AST / deterministic extractor would get right**.
The prose sections (lead, purpose, core logic) are where the model adds real value — which argues
for a hybrid deterministic-structure + AI-prose design.

## 7. Recommendations

- **Production default (this project): `gpt-oss-20B-mxfp4`** (native-MXFP4 swap of the benchmarked
  `c96k`/UD-Q4_K_XL; same 96K window, quant choice within noise per E5). Chosen for maximum per-file
  fidelity, run at `reasoningEffort=low` and a 96K window so it covers files up to ~250 KB untrimmed
  (§11). It is the slowest of the set, accepted here because accuracy is the priority — details below.
- **Throughput alternative: `Qwen3-Coder-30B-A3B-Instruct`** — clean, accurate, code-specialized,
  Apache-2.0, ~71 s/file (~3.3B active MoE); pick it when throughput beats the last points of fidelity.
- **Why gpt-oss is the precision pick:** it is the most *faithful* model in the study.
  It won the per-file matrix **5 of 6** (§8), was the only model to consistently avoid the `final`
  hallucination, copied no prompt examples, and captured exact exception messages, the full member
  sets, and the complete JNI pipeline — including correct (unsynchronized) lazy-init where others
  invented "double-checked locking". Its harmony chain-of-thought — the very thing that makes it
  the slowest (~121–129 s/file, §3) — is what buys the extra accuracy, so the speed cost is
  irrelevant in this scenario. Pair it with the **v1 prompt** and a **generous `maxOutputTokens`**
  so long member lists are never truncated, and run each file at the model's full context. Output is
  clean (0 harmony leakage, §2). For an even higher ceiling you *could* trial larger local models
  not benchmarked here (e.g. `Qwen3.6-35B-A3B`, `gemma-4-26B-A4B`), but among the tested set
  `gpt-oss-20b` is the precision winner. (For the same one-off job where you still want it to finish
  noticeably sooner with only a small fidelity trade, fall back to `Qwen3-Coder-30B-A3B`.)
- **Best for large Java files: `Qwen3-Coder-30B-A3B-Instruct`**, with `Granite-4.0-H-Tiny` as the
  high-throughput alternative. Large files mean a big prefill + a long member list to enumerate.
  On the 414-line `AiGenerationConfig` the 30B kept the member list complete and accurate, and its
  262K native context + ~3.3B-active MoE handle long input affordably; the full-project baseline
  (28 KB `PackageIndexer.java`) confirmed it stays complete (one `Serializable` slip). Granite's
  flat-KV hybrid makes very large/​many files the cheapest on CPU (~4× faster) — use it when
  throughput beats the last few points of fidelity. **Caveat:** large files were not directly swept
  across all models (scope was `config`+`provider`, max 414 lines); this extrapolates from that
  file, model architecture, and the 30B full-project baseline. Avoid `Qwen3-4B-2507` for large
  files specifically — it dropped members on the long list.
- **Best permissive small dense coder: `Seed-Coder-8B-Instruct`** — MIT, clean output, no fence
  issue. `Qwen2.5-Coder-7B` (Apache-2.0) matches it on *accuracy* once fences are post-stripped
  (see §9 re-test) but is the slowest dense model — prefer Seed-Coder unless you specifically want
  the Qwen tokenizer/ecosystem.
- **Budget / laptop: `Qwen3-4B-Instruct-2507`** (non-thinking) over `Qwen3.5-4B` — faster, no
  thinking tax, equal-or-better output (but not for the largest files; see above).
- **Avoid for *batch / throughput* work:** `gpt-oss-20b` (highest fidelity but slowest — reserve it
  for the max-precision use above, not bulk runs), `Qwen2.5-Coder-7B` (slowest dense),
  `DeepSeek-Coder-V2-Lite` (the VAT-lead and fence/format noise).
- **Regardless of model:** expect the `final`/annotation/exact-member inaccuracies above. The
  durable fix is deterministic AST extraction for the structural sections (see §6).

### Prompt v1 vs v2

v2 is **promising but not yet shippable**: it genuinely tightens output and cuts accessor bloat,
but the **code-fence regression on code-specialized models** is a blocker. Recommended before
adoption: add an explicit, example-backed "never wrap any section in a code fence" instruction
(and/or strip leading ```` ```lang ```` deterministically), then re-test. For non-code models v2 is
already a net improvement. The trivial-file omission and keyword-ban goals are better served by
the indexer's filtering than by prompt wording.

---

## 8. Per-file model matrix

Six source files of deliberately different archetypes were each summarized by all 8 models
(**v1 / production prompt**) and scored field-by-field against the real source. One table per file.

### AiGenerationKind.java — enum / marker
*Source:* A trivial `public enum AiGenerationKind` with two constants, `FILE_SUMMARY` and `PACKAGE_SUMMARY`, marking AI-generation scope (file vs. package); no methods.

| Model | Faithfulness (vs source) | Completeness | Key issues | Verdict |
|---|---|---|---|---|
| Qwen3-Coder-30B | Mostly accurate; "final" ok | Over-complete, bloated | Constants as methods `FILE_SUMMARY()`; verbose | Fair — method formatting |
| gpt-oss-20b | Accurate; constants correct | Right-sized | Minor: trailing whitespace only | Good — clean, faithful |
| Granite-4.0-H-Tiny | Accurate; `public` correct | Complete but padded | Many empty "No..." filler lines | Good — faithful, verbose |
| Seed-Coder-8B | Wrong: claims `sealed` | Adequate | Hallucinated `sealed`; self-ref dependency | Poor — invented modifier |
| DeepSeek-Coder-V2-Lite | Accurate; `public enum` right | Good | Constants as methods `FILE_SUMMARY()` | Fair — method formatting |
| Qwen2.5-Coder-7B | Fully accurate | Concise, appropriate | None notable | Excellent — terse, correct |
| Qwen3-4B-2507 | Wrong API signatures | Over-stated | `FILE_SUMMARY(file)→void`; self-ref dep | Poor — invented params |
| Qwen3.5-4B | Wrong: "no modifiers" | Concise | Missed `public`; self-ref dependency | Fair — modifier error |

**Best for this file:** Qwen2.5-Coder-7B — accurate, no hallucinations, appropriately short for a trivial enum.

### AiGenerationConfig.java — large config POJO
*Source:* A mutable, non-final JavaBean (`@ToString` + `@SuppressWarnings`) carrying ~13 `DEFAULT_*` constants and ~14 fields with full getter/setter pairs, where `getStopStrings` returns an unmodifiable list or null; equals/hashCode intentionally absent.

| Model | Faithfulness (vs source) | Completeness | Key issues | Verdict |
|---|---|---|---|---|
| Qwen3-Coder-30B | Mostly right; getStopStrings nuance ok | Full getters+setters, deps correct | Says "final" (wrong); misses @SuppressWarnings | Good — final error |
| gpt-oss-20b | Strong; clean, no fabrication | Full API, deps, lead all correct | "final by default" (wrong); misses @SuppressWarnings | Good — lone final slip |
| Granite-4.0-H-Tiny | Captured @SuppressWarnings correctly | Full API, all fields listed | "Final: Yes" (wrong); junk referenced types | Good — final error, minor junk |
| Seed-Coder-8B | Avoids final but "Public fields" wrong | Full API present | Drops `List` dep; sloppy concurrency claim | Fair — field/dep errors |
| DeepSeek-Coder-V2-Lite | Lead copied from prompt example | Full API present | "Calculates VAT" lead; says final twice | Poor — copied lead + final |
| Qwen2.5-Coder-7B | "No return types" contradicts getters | Full API present | Says "Final"; self-contradictory output | Fair — final + contradiction |
| Qwen3-4B-2507 | "final fields" wrong; bad concurrency | Drops ALL setters | Setters missing; claims thread-safe | Poor — setters dropped |
| Qwen3.5-4B | Correctly non-final `public class` | Full getters+setters | Invents 25 constants/16 fields; junk self-dep | Fair — fabricated counts |

**Best for this file:** gpt-oss-20b — most complete and faithful with no fabricated counts or junk deps; only flaw is the "final by default" note.

### AiGenerationProvider.java — interface / contract
*Source:* A `public interface AiGenerationProvider extends AutoCloseable` with an abstract `generate(request)`, a default temperature-override `generate(request, float)` that delegates, and a default no-op `close()`.

| Model | Faithfulness (vs source) | Completeness | Key issues | Verdict |
|---|---|---|---|---|
| Qwen3-Coder-30B | Correct interface + AutoCloseable, both generate methods | Misses `close()` in API | Lists AutoCloseable as a "dependency" | Good — accurate, slight gap |
| gpt-oss-20b | Fully correct; interface, extends, all 3 members | All methods incl. default `close()` | None notable | Excellent — complete & precise |
| Granite-4.0-H-Tiny | Correct interface/extends, both generate | Omits `close()`; deps miss AiGenerationRequest | Thin coverage | Good — terse but right |
| Seed-Coder-8B | Wrong: "Extends AiGenerationRequest" | Has close, both generate | Fabricated extends; false thread-safety | Poor — invented inheritance |
| DeepSeek-Coder-V2-Lite | Wrong: "Extends AiGenerationRequest" | All 3 methods listed | Fabricated extends; false thread-safety claim | Poor — invented inheritance |
| Qwen2.5-Coder-7B | Correct interface/extends, exact signatures | Misses `close()` in API | "Constructor params" odd for interface | Good — signatures accurate |
| Qwen3-4B-2507 | Correct interface, extends, all 3 members | Complete incl. `close()` | Minor: omits "may be blank" | Good — clean and faithful |
| Qwen3.5-4B | Interface/extends correct | All 3 methods | Invented core logic (validates, runs llama.cpp inference) | Fair — hallucinated impl detail |

**Best for this file:** gpt-oss-20b — correct interface, all three members with accurate signatures and deps, no hallucination.

### AiGenerationProviderFactory.java — factory / wiring
*Source:* A `@ToString` (non-final) factory whose `create(providerName, llamaConfig, promptSupport)` returns `MockAiGenerationProvider` for null/blank/"mock", `LlamaCppJniAiGenerationProvider` for "llamacpp-jni", else throws `IllegalArgumentException("Unsupported AI provider: " + providerName)`.

| Model | Faithfulness (vs source) | Completeness | Key issues | Verdict |
|---|---|---|---|---|
| Qwen3-Coder-30B | Accurate logic, but asserts "final" | All deps (6), full switch | Wrong: class not final; message unquoted | Good — false final |
| gpt-oss-20b | Fully accurate, no final claim | Exact message, full switch | Missing only LlamaCppJniConfig dep | Excellent — exact message |
| Granite-4.0-H-Tiny | Lead is prompt's "Calculates VAT" | Omits "mock" case; 3 deps only | VAT lead; "final"; deps incomplete | Poor — copied lead |
| Seed-Coder-8B | Accurate, correctly not final | Full switch incl. mock | Missing LlamaCppJniConfig + provider iface deps | Good — solid |
| DeepSeek-Coder-V2-Lite | Accurate, not final | Default-throw only in Exceptions section | `{@link}` leakage; deps miss provider classes | Fair — deps thin |
| Qwen2.5-Coder-7B | Very accurate, notes isBlank | Full switch, not final | Self-referential dep (Factory); deps incomplete | Good — junk dep |
| Qwen3-4B-2507 | Accurate; hedged "final (not marked)" | 5 deps, full switch | Confusing final wording; misses lombok dep | Good — final hedge |
| Qwen3.5-4B | Accurate; correctly says final *field* | 6 deps, full switch | Mild invention ("integration into system"); odd phrasing | Good — complete |

**Best for this file:** gpt-oss-20b — only one with the exact exception message, correct non-final type, and clean full switch logic.

### AiCompletionParser.java — algorithm / string parsing
*Source:* A non-final parser that strips a Gemma-4 thinking block (markers `<|channel>thought` / `<channel|>`), returning text after the last end marker, throwing IOException on an unclosed block and mapping null to empty.

| Model | Faithfulness (vs source) | Completeness | Key issues | Verdict |
|---|---|---|---|---|
| Qwen3-Coder-30B | Accurate; non-final, logic right | All sections, IOException + null | "Checks presence" not "last index" (minor) | Excellent |
| gpt-oss-20b | Fully accurate; last-index, null, msg | Thorough; markers + maxOutputTokens note | None significant | Excellent |
| Granite-4.0-H-Tiny | Logic right but calls class **final** | Good; but contradicts self on final | "Type: final" wrong; verbatim purpose copy | Fair — wrong modifier |
| Seed-Coder-8B | Accurate; non-final, edge cases noted | Concise; less marker detail | No "last marker" specificity | Good |
| DeepSeek-Coder-V2-Lite | Logic right but says **final class** | Very detailed sections | "public final class" wrong; not "last" | Fair — wrong modifier |
| Qwen2.5-Coder-7B | Accurate; non-final, last-index, null | Complete | Purpose claims it "stores" file (overreach) | Good |
| Qwen3-4B-2507 | Fully accurate; non-final, null, msg | Complete, clean | None significant | Excellent |
| Qwen3.5-4B | Content accurate where present | Missing Purpose + Type sections | Leaks stray code-fence; truncated header | Fair — corrupted output |

**Best for this file:** gpt-oss-20b — accurate logic, captures last-index, null-handling, and the actionable maxOutputTokens message, with no errors.

### LlamaCppJniAiGenerationProvider.java — heavy implementation (JNI)
*Source:* Lazy-loading llama.cpp JNI provider (implements `AiGenerationProvider` + `AutoCloseable`) that builds inference params via immutable withers, runs `chatCompleteText`, and parses output through `AiCompletionParser`.

| Model | Faithfulness (vs source) | Completeness | Key issues | Verdict |
|---|---|---|---|---|
| Qwen3-Coder-30B | Accurate; lazy-load, withers, parser captured | Full: API, deps, exclude | Invented "double-checked locking" | Good — one false concurrency claim |
| gpt-oss-20b | Fully accurate; correct unsynchronized lazy-init | Full: fields, deps, withers, parser | None notable | Excellent — precise, complete |
| Granite-4.0-H-Tiny | Lead copied prompt example; wrong thread-safe claim | Moderate; misses parser class name | "Calculates VAT for invoices" lead | Poor — prompt-leak lead |
| Seed-Coder-8B | Mostly accurate; lazy + parser noted | Good | Wrong: thread-safe + double-checked locking | Fair — false concurrency |
| DeepSeek-Coder-V2-Lite | Lead copied prompt example; thread-safe wrong | Good core logic | "Calculates VAT for invoices" lead | Poor — prompt-leak lead |
| Qwen2.5-Coder-7B | Accurate; lazy, parser, withers all noted | Full deps + API | Honest "not handled" concurrency | Good — solid, no hallucination |
| Qwen3-4B-2507 | Accurate logic; lazy + parser captured | Full withers, deps | Wrong AiCompletionParser package; muddled concurrency | Good — minor dep/package slip |
| Qwen3.5-4B | Accurate; lifecycle, parser, exclude noted | Full deps + API | close() doesn't throw IOException | Good — small exception error |

**Best for this file:** gpt-oss-20b — fully faithful, complete (lazy-load, wither chain, parser pipeline), correct concurrency, no hallucinations.

### Matrix synthesis

**"Best for this file" tally:** gpt-oss-20b **5/6**, Qwen2.5-Coder-7B **1/6** (the trivial enum).

So **gpt-oss-20b is the per-file accuracy leader** — it alone avoided the `final` hallucination on
most files, copied no prompt examples, and captured exact exception messages and the JNI pipeline.
But this must be read against §3: **gpt-oss is the slowest model (~121–129 s/file)** because of
harmony reasoning overhead. The practical pick remains **Qwen3-Coder-30B-A3B** — consistently
"Good" across every archetype at **~71 s/file** — with gpt-oss reserved for when per-file fidelity
outweighs throughput. The dominant cross-file error for nearly every model is the **`final`
modifier hallucination** (a structural fact an AST extractor would get right — see §6); the worst
single failures are DeepSeek/Granite **copying the prompt's "Calculates VAT for invoices" example
as the lead**, and Qwen3-4B-2507 **dropping members** on longer files.

## 9. External corroboration (published benchmarks)

A web research pass cross-checked these findings against public benchmarks, leaderboards, and
official model cards. **Headline:** our *structural* findings are corroborated; our *accuracy
ranking* is novel and cannot be corroborated, because no public benchmark scores source-faithfulness
of code-index summaries and IFEval exists for only **2 of our 8** models.

**Corroborated (at the mechanism / category level):**
- **Speed ↔ active params:** CPU decode is memory-bandwidth-bound and reads only active expert
  weights, so a 30B-A3B decodes like a dense ~6B ([llama.cpp #19890](https://github.com/ggml-org/llama.cpp/discussions/19890)).
  IBM publishes Granite 4.0 "~2× faster / >70% less RAM" ([IBM](https://www.ibm.com/granite/docs/models/granite)).
  *(Our exact "~4× fastest Granite" magnitude is our own measurement.)*
- **Reasoning is a throughput tax** that helps hard tasks but not short summaries
  ([OptimalThinkingBench](https://arxiv.org/html/2508.13141v1); "Stop Overthinking"
  [survey](https://arxiv.org/pdf/2503.16419)) — supports our Qwen3.5-4B result. The *conditional*
  exception (reasoning genuinely raised gpt-oss accuracy at a latency cost) is also documented
  ([BrowseComp-Plus](https://arxiv.org/pdf/2508.06600), [DataRobot](https://www.datarobot.com/blog/testing-gpt-oss-models/)).
- **All four hallucination modes are named categories:** attribute/faithfulness hallucination (the
  `final` slip), few-shot/prompt leakage (the "Calculates VAT for invoices" copy), format drift
  (code fences), and negative-constraint instruction-following failure (ignoring "omit trivial
  sections").
- **Tooling:** llama.cpp `reasoning_format` parsing and the `--reasoning-format none` harmony-leak
  are confirmed in the repo ([server README](https://github.com/ggml-org/llama.cpp/blob/master/tools/server/README.md),
  [#15341](https://github.com/ggml-org/llama.cpp/discussions/15341)).

**Novel (no public equivalent):** our source-faithfulness scoring for code-index summaries, the
per-file head-to-head (gpt-oss-20b 5/6), and CPU timings for these exact Q4_K_M quants at 16K on a
Ryzen 7 5800H. **Treat the §5/§8 accuracy ranking as internal evidence, not a public-record claim.**

**The only published IFEval anchors for our set:** Granite-4.0-H-Tiny ≈ **81.4** avg (84.8
instruct-strict) and Qwen3-4B-Instruct-2507 ≈ **83.4**. The other six models have no published
IFEval (gpt-oss-20b's official card has none; a ~69.5% figure circulating is unofficial).

**Where we may be exposed:**
- **Qwen2.5-Coder-7B was under-ranked — confirmed by a follow-up re-test.** Public coding
  benchmarks rank it strongly (Qwen reports it beats DeepSeek-Coder-V2-Lite and Codestral-22B; leads
  CRUXEval at its size). We re-scored its **v2** output across all 6 archetype files with the stray
  code fences treated as the deterministically-fixable format error they are. Fence-normalized, its
  **comprehension is top-tier (top-3, alongside gpt-oss-20b)**: it correctly reports the *non-final*
  classes (no `final` hallucination — unlike most models), the full accessor set with the
  unmodifiable-list nuance, the interface's default-delegation contract, the parser's three-branch
  control flow + IOException trigger, and the JNI lazy-load/inference/`close()` chain — with no
  invented members, hallucinated inheritance, or copied-example leads. Residual issues are cosmetic
  (one null-handling phrasing slip in `AiCompletionParser`; an "implements vs extends" nit). **So its
  mid-pack matrix placement was a *format* artifact, not a comprehension deficit** — with a one-line
  fence-stripping post-process it is a strong, permissive (Apache-2.0) option. The remaining catch is
  speed: it is the slowest *dense* model (~133–141 s/file).
- **DeepSeek-Coder-V2-Lite low** agrees with the public record (its own paper concedes an
  instruction-following gap).

**License correction (applied above):** DeepSeek-Coder-V2-Lite weights are under a use-restricted
custom **DeepSeek Model License** (the repo *code* is MIT) — not "commercial-OK". Codestral remains
MNPL (non-commercial); the rest are Apache-2.0 (Qwen family, gpt-oss, Granite) or MIT (Seed-Coder).

**Caveat — model-name collisions:** public IFEval figures are easy to mis-attribute across
`Qwen3-Coder-30B-A3B` vs `Qwen3-30B-A3B-2507` (the 83.7 figure is the non-thinking 30B, *not* the
Coder or the 4B) and `Qwen3.5-4B` vs `Qwen3-4B-Instruct-2507`. Anchors above are attributed carefully.

## 10. Prompt-cache optimization (`cache_prompt`)

The provider keeps the model loaded once and reuses the shared prompt-template prefix's KV across
files via llama.cpp `cache_prompt` (pinned to one slot); only the differing per-file source is
re-prefilled. A/B on the same model (Qwen3-Coder-30B-A3B @ 16K), file phase over `config`+`provider`
(12 files):

| `cachePrompt` | wall time (12 files) | ~ per file |
|---|---|---|
| `false` (baseline) | 1055 s | ~88 s |
| `true` (default) | 950 s | ~79 s |

**≈ 10 % faster (−105 s)**, output unchanged (prefix reuse is logit-exact). This is the *entire*
cacheable share: the remainder — per-file source prefill + decode — is inherently uncacheable, so a
system/user prompt split would add no further speed (only a possible, separate quality effect).
Enabled by default (`cachePrompt=true` on each model definition; needs `net.ladenthin:llama`
≥ 5.0.3 for the pinned-slot API). Measured at 16K context (the new CPU default — 64K's ~6 GB KV
caused memory paging on the test box that confounded timing).

## 11. gpt-oss `reasoning_effort` (low / medium / high)

gpt-oss is a *reasoning* model: it emits an internal `analysis` channel before the `final` answer,
and llama.cpp counts those reasoning tokens **in-band** against `maxOutputTokens`. The benchmark ran
the file phase (`config`+`provider`, 12 files) with `EXP-gpt-oss-20B` @ 16K, `maxOutputTokens=1536`,
flipping only `-Dai.reasoningEffort` across `low` / `medium` / `high`.

**Headline: higher effort is strictly worse for this task — it is slower *and* truncates the
deliverable.** Because reasoning shares the fixed 1536-token budget, more reasoning leaves fewer
tokens for the actual summary, so the `final` answer gets cut off (or never starts).

| Aspect (gpt-oss-20B @ 16K, file phase, 12 files) | `low` | `medium` | `high` |
|---|---|---|---|
| Wall time (12 files) | **1072 s** | 1545 s | aborted (≈ 8 min/file, ~4×) |
| ~ per file | **~89 s** | ~129 s | ~8 min/file (aborted) |
| Relative speed | **1.0× (fastest)** | ~1.44× slower | ~4× slower |
| Reasoning (`analysis`) tokens spent | minimal | moderate | large |
| Share of 1536-token budget left for the summary | ~all | ~half | little / none |
| `AiGenerationConfig.java` body | **complete** (all sections → `Concurrency`) | truncated mid `Public API` | truncated after `Annotated` (≈ 2 body lines) |
| `AiModelDefinition.java` body | **complete** (→ `Concurrency`) | complete | **header only — no body produced** |
| Truncation risk at `maxOutputTokens=1536` | low | medium (file-dependent) | high (frequent / total) |
| Summary structure when it completes | full, well-sectioned | comparable when not cut | rarely reaches the sections |
| Harmony-marker leakage into body | 0 | 0 | 0 |
| Net usefulness for this short-summary task | **best** | borderline | unusable at this budget |

The `high` run was **aborted** after confirming the pattern: at the 1536 budget it truncates even
worse than `medium` (e.g. `AiModelDefinition.java` produced a header with no body) while running ~4×
slower — strictly dominated, so finishing all 12 files added no information.

### Is there any benefit to medium/high?

For this task — short, dense bullet summaries — **not at a tight output budget**. The summary content is
extraction/structuring, not multi-step problem solving, so the extra reasoning buys little quality; it
only consumes the budget the deliverable needs. The truncation above is a **budget artifact, not a
model limit**: give the reasoning room and the summary completes (see below). `low` remains the best
default — fastest, and the whole budget goes to the summary.

### Clean runs, large files, and the context-window limit

Two independent limits must both be satisfied for a clean run:

| Limit | Controls | Failure if too small |
|---|---|---|
| `contextSize` | input (prompt + source) that fits the window | source **trimmed** before the model sees it |
| `maxOutputTokens` | reasoning **+** final answer (gpt-oss reasons in-band) | summary **truncated** mid-output |

`charsPerToken=3` is set on all presets as a deliberately conservative *trim* guard (the synthetic
fixture measured ~4.2 chars/token; a later real-Java regression found **~4.8** — see
[gpt-oss-tuning.md](gpt-oss-tuning.md) Phase 5 — and 3 stays safely below both so a big file is never
silently over-fed past the real token window). (The earlier 1536-budget truncation was a *budget*
artifact, not a model limit — give the output room and the summary completes.)

**How big can one file be?** gpt-oss-20b's native window is **`n_ctx_train = 131072` (128K tokens)**.
At ~4.2 chars/token that is the hard ceiling; the practical ceiling on CPU is *time*, not the window:

| File size | ≈ tokens | fits 128K window? | practical on CPU? |
|---|---|---|---|
| ~40 KB | ~10 K | yes | yes — ~1–2 min/file |
| ~100 KB | ~24 K | yes | yes — ~25 min/file |
| ~250 KB | ~62 K | yes | borderline — **~80 min/file** |
| ~480–500 KB | ~115–120 K | edge (needs tiny output budget) | no — hours |
| > ~500 KB | > 122 K | **no — exceeds the window** | n/a |

**Three size-tiered presets ship in the pom** (all `reasoningEffort=low`, `charsPerToken=3`):

| Preset | `contextSize` | `maxOutputTokens` | covers file up to | ~ time (CPU) |
|---|---|---|---|---|
| `gpt-oss-20B-c16k` | 16384 | 2048 | ~40 KB | ~1–2 min |
| `gpt-oss-20B-c48k` | 49152 | 4096 | ~125 KB | ~25 min @ 100 KB |
| **`gpt-oss-20B-c96k` (default)** | 98304 | 6144 | ~260 KB | ~80 min @ 250 KB |

**`c96k` is the default.** The A/B above proves a wider window costs only RAM, not per-file time, so
the widest practical window is the safe universal choice: one preset covers *every* file up to ~250 KB
with no trimming and no truncation, while small files stay just as fast. Downshift to `c48k`/`c16k`
only to save RAM on memory-constrained machines.

### Size-tiering: one model per execution, routed by file size (E3)

If you want the *RAM economy* of small contexts on small files **and** the reach of a big context on the
few large ones, split the `generate` goal into several executions, each filtered to a size band by
`minFileSizeBytes` (exclusive lower) / `maxFileSizeBytes` (inclusive upper) and pointed at the matching
preset (and, if you like, its own prompt). The model loads **once per execution** — no per-file reload —
and the source-checksum skip means each file is indexed exactly once: a small file done by the c16k band
is skipped by the later bands.

```xml
<!-- bind all three before aggregate-packages (e.g. process-classes); Maven runs same-phase
     executions in declaration order, so file indexing is complete before packages aggregate -->
<execution><id>idx-small</id><goals><goal>generate</goal></goals>
  <configuration><maxFileSizeBytes>16384</maxFileSizeBytes>
    <fieldGenerations>… aiDefinitionKey=gpt-oss-20B-c16k …</fieldGenerations></configuration></execution>
<execution><id>idx-medium</id><goals><goal>generate</goal></goals>
  <configuration><minFileSizeBytes>16384</minFileSizeBytes><maxFileSizeBytes>49152</maxFileSizeBytes>
    <fieldGenerations>… gpt-oss-20B-c48k …</fieldGenerations></configuration></execution>
<execution><id>idx-large</id><goals><goal>generate</goal></goals>
  <configuration><minFileSizeBytes>49152</minFileSizeBytes>   <!-- maxFileSizeBytes=0 → catch the rest -->
    <fieldGenerations>… gpt-oss-20B-c96k …</fieldGenerations></configuration></execution>
```

Two rules: (1) use **non-overlapping** bands (`band2.min == band1.max`) — the skip is by source checksum
only, so it can't tell which prompt produced an existing `.ai.md`, and an overlapping band would leave
the first writer's output in place; (2) make the **last band unbounded** (`maxFileSizeBytes=0`) so files
above every band still get indexed (otherwise they get no `.ai.md` and vanish from the package listing).
Defaults are `0`/`0` (no size filter), so a single-execution setup is unaffected.

**Validated end-to-end** (synthetic Java fixtures from
[`tools/generate-fixture.sh`](tools/generate-fixture.sh); `mvn generate-resources`, CPU-only):

| Run | file | `truncated` | stop | output | total | prefill | decode |
|---|---|---|---|---|---|---|---|
| `c48k` | 96 KB / 2470 ln | **0** | natural (`n_decoded=1198`) | complete to `#### Concurrency` | ~25 min | ~978 s (24 K tok @ ~25 t/s) | ~522 s (@ ~2.3 t/s) |
| `c96k` | 253 KB / 6441 ln | **0** | natural (`n_decoded=738`) | complete to `#### Concurrency` | **~80 min** | ~4053 s (61 K tok @ ~15 t/s) | ~754 s (@ **~1.0 t/s**) |

Both ran with no trim, no retries, `BUILD SUCCESS`, and summaries that span the whole file (first
field → last `adjustBucketN` → tail methods). The 250 KB summary was compact (738 tokens) and faithful
but mis-counted the bucket methods ("260" vs 329) — large inputs invite small quantitative slips even
when the structure is right.

**Cost reality:** the bottleneck on big files is **CPU prefill + large-context decode, not the effort
level** — at 96K context decode drops to **~1 t/s** (vs ~2.3 t/s at 48K, ~10 t/s for small files),
and prefill is effectively O(n²). The output budget is barely used (738–1198 tokens), so for large
files the knobs that matter are `contextSize` + `charsPerToken` (fit untrimmed); the budget just needs
headroom.

### Per-file timing model (why throughput "shrinks", and the logged ETA)

There is no constant tokens/second. Prefill **per-token** cost grows *linearly* with the prompt
length `n` (attention is O(n) per token), so total prefill is **O(n²)**. Fitting the three measured
runs gives, on the reference CPU (Ryzen 7 5800H, 8 threads, gpt-oss-20b UD-Q4_K_XL):

```
prefill_ms(n) ≈ 24.4·n + 0.000674·n²          n = prompt tokens
decode_ms(n, out) ≈ out·(56.8 + 0.01568·n)    out = generated tokens
tokens n ≈ source_chars / 4.8 + ~700 (template)   # real-Java fit; see gpt-oss-tuning.md Phase 5
```

The plugin's `AiGenerationTimeEstimator` ships these constants (4.8 / 700) plus a +15 % display margin.

The model reproduces every measured point to within ~0.5 %:

| prompt tokens `n` | measured prefill | model prefill |
|---|---|---|
| 3 309 (~10 KB) | 88.3 s | 88.1 s |
| 24 081 (~96 KB) | 978.0 s | 978.5 s |
| 61 484 (~253 KB) | 4053 s | 4049 s |

This is exactly why throughput "shrinks" as the prefill grows: per-token cost rises with `n`
(26.7 → 40.6 → 65.9 ms/tok across the three runs), and decode slows the same way (9.1 → 2.3 → 1.0 t/s)
because each generated token attends over the whole context.

**The plugin logs this estimate per file** before generating, e.g.:

```
[INFO] Processing file '.../Foo.java' (96 KB source, ~24009 tokens) — estimated ~25 min (rough; ...)
```

implemented in `AiGenerationTimeEstimator` (the constants above are its calibration defaults, kept
honest by a regression test that asserts they reproduce the three measured points). The numbers are a
**rough, hardware-specific** order-of-magnitude hint: a different CPU/threads/model — or a GPU —
shifts the coefficients; only the quadratic *shape* is universal.

### Strategy for a repo with mixed file sizes

The plugin routes the model per file by **extension, not by size** (`AiFieldGenerationSelector`), so
one run cannot auto-pick a preset per file size. Key fact: **`contextSize` costs KV RAM (one-time),
not per-small-file speed** — prefill and decode scale with each file's *actual* token count, not with
the allocated window, so a small file stays fast even in a large context; you only pay the RAM.

This was confirmed by an A/B run: the **same 10 KB file**, gpt-oss, everything identical except
`contextSize` (16K → 96K):

| `contextSize` | proj. RAM (KV+compute) | prefill | decode | wall |
|---|---|---|---|---|
| 16384 | 2050 MiB | 26.67 ms/tok | 9.10 t/s | 159 s |
| 32768 | 2450 MiB | 26.61 ms/tok | 9.16 t/s | 165 s |
| 49152 | 2850 MiB | 26.55 ms/tok | 9.33 t/s | 159 s |
| 98304 | 4050 MiB | 26.52 ms/tok | 9.20 t/s | 168 s |

RAM rises ~linearly (**~+400 MiB per +16K**) while prefill/decode/wall stay flat — a wider window is
**RAM-only** for small files. (These are the window-driven KV+compute figures at small *actual* input;
when a wide window is genuinely filled — ~60 K tokens plus context checkpoints up to 8 GB — real usage
climbs to the ~23 GB seen in the 250 KB run, but the *window-size* component scales as above.) Two
workable approaches:

1. **One preset, sized to your largest file (simplest).** If your biggest class fits ~125 KB and you
   have the RAM, run `c48k` for everything — small files are unaffected in speed.
2. **Split into scoped runs (RAM-constrained, or rare monster files).**
   - cheap run: `c16k` with `<excludes>` skipping the known big files (or `<subtrees>` limited to the
     normal packages);
   - targeted run: `c48k`/`c96k` with `<subtrees>` limited to the big file(s).
   The model reloads per run, but the large context is paid only where needed; checksum incrementality
   makes re-runs cheap once a file is summarized.

Size-based auto-routing (file bytes → preset) is **not currently a feature** — it would be the clean
fix if mixed-size repos become common.

## 12. Caveats

- Quality tiers (§4) are from a **representative file sample**, not a full read of all 368
  summaries; metrics (§1) cover every file.
- The `config`+`provider` scope is small and light on package-info/enum-heavy or
  keyword-prone files, so §2.2 (keywords) and §2.5 (omission) are under-exercised here.
- `s/file` includes one-time model load amortized across 23 generations; absolute numbers are
  hardware-specific (Ryzen 7 5800H, CPU-only, 8 threads).
- The full-project 30B baseline (`*__fullproject-c64k`) differs in scope and context (47 files,
  65536) and is reference-only.
