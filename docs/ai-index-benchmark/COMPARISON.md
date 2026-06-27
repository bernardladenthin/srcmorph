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

## 5. Recommendations

- **Default for this task: `Qwen3-Coder-30B-A3B-Instruct`** — clean, accurate, code-specialized,
  Apache-2.0, and fast for its quality (~71 s/file via ~3.3B active MoE). The current production
  choice is validated.
- **Best speed/quality for large repos or overnight runs: `Granite-4.0-H-Tiny`** — ~4× faster
  than the dense coders, Apache-2.0, surprisingly accurate. The standout new candidate.
- **Best permissive small dense coder: `Seed-Coder-8B-Instruct`** — MIT, clean output. Good
  middle option.
- **Budget / laptop: `Qwen3-4B-Instruct-2507`** (non-thinking) over `Qwen3.5-4B` — faster, no
  thinking tax, equal-or-better output.
- **Avoid for batch summarization:** `gpt-oss-20b` (highest fidelity but slowest — reasoning
  overhead not worth it), `Qwen2.5-Coder-7B` (slowest dense), `DeepSeek-Coder-V2-Lite v2`
  (formatting noise).

### Prompt v1 vs v2

v2 is **promising but not yet shippable**: it genuinely tightens output and cuts accessor bloat,
but the **code-fence regression on code-specialized models** is a blocker. Recommended before
adoption: add an explicit, example-backed "never wrap any section in a code fence" instruction
(and/or strip leading ```` ```lang ```` deterministically), then re-test. For non-code models v2 is
already a net improvement. The trivial-file omission and keyword-ban goals are better served by
the indexer's filtering than by prompt wording.

---

## 6. Caveats

- Quality tiers (§4) are from a **representative file sample**, not a full read of all 368
  summaries; metrics (§1) cover every file.
- The `config`+`provider` scope is small and light on package-info/enum-heavy or
  keyword-prone files, so §2.2 (keywords) and §2.5 (omission) are under-exercised here.
- `s/file` includes one-time model load amortized across 23 generations; absolute numbers are
  hardware-specific (Ryzen 7 5800H, CPU-only, 8 threads).
- The full-project 30B baseline (`*__fullproject-c64k`) differs in scope and context (47 files,
  65536) and is reference-only.
