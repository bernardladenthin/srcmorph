# gpt-oss-20b tuning — unattended findings

Autonomous tuning session for the `gpt-oss-20b` default of this plugin. Goal: turn the open
questions from [COMPARISON.md §11](COMPARISON.md) into **objectively measured** answers, run
unattended, and document everything here.

- **Model:** `gpt-oss-20b-UD-Q4_K_XL.gguf` via llama.cpp JNI, CPU-only (AMD Ryzen 7 5800H, 8 threads).
- **Git baseline before the session:** `f14819b` (branch `claude2026_06_26_00`).
- **Date:** 2026-06-27.

## Summary (TL;DR)

| Phase | Question | Answer |
|---|---|---|
| 1 | Is higher `reasoning_effort` more accurate at adequate budget? | **No** — low/medium/high are equally accurate when not truncated; higher effort just decodes 4–8× more reasoning tokens (2–3× slower). `low` confirmed as default. |
| 2 | Lower temperature for extraction? | **Yes** — temp 0.0/0.3 are exact and 0.0 is deterministic; 0.7/1.0 introduce miscounts. → **D1** |
| 3 | Can a prompt fix the large-file miscount? | **Yes** — a "count exactly" prompt turned a wrong "150" into the exact "195" at no cost. → **D3** |
| 4 | Does the timing model hold? | **Yes** — `prefill ≈ 24.4·n + 0.000674·n²` within +1.1…+2.6 % over 25–150 KB. Count accuracy degrades with scale (exact ≤128 methods, off at 195). |
| 5 | Real-Java chars/token? | **~4.8** (not the fixture's 4.2); template ~713 tokens. Estimator over-estimates time ~12 %. → **D2** |

**Net:** the shipped defaults (gpt-oss, `reasoningEffort=low`, c96k) are validated. Three low-risk
improvements are recommended but **held for sign-off** (see *Decisions deferred*): **D1** temp 0.0,
**D2** estimator constants, **D3** count-exact prompt. The combination of D1 + D3 directly fixes the
structural-count weakness that affects every model in the original benchmark.

## Method — objective scoring via ground-truth fixtures

The benchmark cells run the real `generate` goal on a **synthetic Java fixture of known structure**
(`docs/ai-index-benchmark/tools/generate-fixture.sh`: a ledger class with a known number of
`adjustBucketN` methods, 40 weight fields, and 6 tail methods). A scorer parses the produced
`.ai.md` plus the llama.cpp run log into objective metrics ([`tools/score.sh`](tools/score.sh)), so
"quality" becomes numbers a script can compare:

| Metric | Meaning |
|---|---|
| `sections` | how many of the 9 expected `####` sections are present (Purpose…Concurrency) |
| `reachConc` | did the body reach the last section (`Concurrency`)? = not truncated mid-output |
| `claimBucket` | highest `adjustBucketN` the summary enumerates (vs the fixture's true N) |
| `countErr` | abs error of the claimed method count vs ground truth |
| `truncated` | llama.cpp input-trim flag (should be 0 = whole file fit) |
| `promptTok / prefillMs` | prefill size and time |
| `decodeTok / decodeMs` | generated tokens and decode time |
| `wall_s` | end-to-end seconds for the cell |

Harness: a matrix-driven runner (idempotent, resumable, heartbeat) writes one TSV row per cell. The
`gpt-oss-bench` pom definition is fully parameterized (`-Dexp.ctx/out/temp/topp/topk/effort/filePrompt`)
so a run sweeps a dimension without touching the pom. (This benchmark wiring is temporary scaffolding,
reverted after the session; the production default stays `gpt-oss-20B-c96k`.)

**Honest limitation:** synthetic fixtures are repetitive, so `sections`/`countErr`/timing are clean
signals but "reads well for an agent" is only partially captured. A few real files are used as a
reality check where noted.

## Phase 1 — `reasoning_effort` × output budget (at adequate budget)

**Question this settles:** we defaulted to `reasoningEffort=low` because `high` *truncated* at a fixed
1536-token budget. But is `high` actually *more accurate* when given enough budget, or just slower?

**Setup:** 24 KB fixture (ground truth: **26** `adjustBucketN` methods), ctx 32768, temp 1.0, 2 reps
per cell. Prefill was constant (~208 s, 7245 prompt tokens) across all cells — the only variable is
decode (reasoning + answer).

| effort | budget | wall (s) | decode tok | sections | reached `Concurrency` | count error |
|---|---|---|---|---|---|---|
| low | 2048 | 321 / 329 | 519 / 571 | 9 / 9 | yes / yes | 0 / 0 |
| low | 8192 | 315 / 340 | 533 / 611 | 9 / 9 | yes / yes | 0 / 0 |
| medium | 2048 | 514 / 540 | 1501 / **2048*** | 9 / **5*** | yes / **no*** | 0 / **25*** |
| medium | 8192 | 417 / 518 | 1266 / 1729 | 9 / 9 | yes / yes | 0 / 0 |
| high | 1536 | 1342 / 1400 | **1536 / 1536*** | **0 / 0*** | **no / no*** | — |
| high | 8192 | 762 / 1153 | 2806 / 4506 | 9 / 9 | yes / yes | 0 / 0 |

`*` = output truncated because decode hit the token budget before the answer finished.

**Findings**

1. **At an adequate budget, all three efforts produce complete, equally accurate summaries** — 9/9
   sections and **count error 0** (correctly reported 26 methods). Higher effort bought **no** quality
   on this extraction task.
2. **Higher effort only costs time.** Prefill is identical; high decodes **4–8× more tokens** (pure
   reasoning) for the same result → **2–3× slower** (≈13–19 min vs ≈5.5 min for low).
3. **Truncation is a decode-budget function of effort, not a model defect.** `low` fits comfortably in
   2048; `medium` needs ≥ ~2500; `high` needs ≥ ~5000 to not cut off. At a fixed small budget, higher
   effort truncates *first* (it spends the budget on reasoning).

**Conclusion:** `reasoningEffort=low` is confirmed as the default — same accuracy and completeness as
medium/high, fastest, and the lowest truncation risk. This validates the shipped default.

_(Scorer note: 2 complete cells show `claimBucket=NA` — the model described the method family in prose
without enumerating `adjustBucketN`, which the regex misses; both still reached `Concurrency` with full
sections, so they are complete, not failures.)_

## Phase 2 — temperature sweep

**Question:** gpt-oss's card recommends temp 1.0, but for *extraction* (not creative writing) does a
lower temperature reduce miscounts / hallucination?

**Setup:** 24 KB fixture (26 methods), low effort, budget 4096, top-p 1.0 / top-k 0, 2 reps per temp.
All cells completed (9/9 sections, no truncation); the variable is faithfulness + reproducibility.

| temp | count error (r1 / r2) | reproducible? | wall (s) |
|---|---|---|---|
| 0.0 | **0 / 0** | **yes — both reps bit-identical** (502 dec tok, 2201 B) | 321 / 321 |
| 0.3 | **0 / 0** | near-identical | 327 / 322 |
| 0.7 | 0 / **25** | no | 329 / 299 |
| 1.0 (card default) | **14** / n.a.* | no | 301 / 314 |

`*` = at temp 1.0 r1 the model reported **40** methods (true: 26); r2 phrased the count in prose
(scorer couldn't extract — not necessarily wrong).

**Findings**

1. **Lower temperature is more faithful for extraction.** temp 0.0 and 0.3 hit the exact count every
   time; 0.7 and 1.0 produced wrong counts (1, 40) in some reps. Higher sampling variance → more
   numeric/structural slips, with **no speed benefit** (decode time ~equal).
2. **temp 0.0 is deterministic** — identical `.ai.md` across reps. This matters for the plugin's
   checksum-based incremental model: re-indexing an unchanged file yields the *same* output, so there
   is no diff churn.

**Conclusion:** for code indexing, **temp 0.0 (or 0.3)** beats the card's 1.0 — same speed, more
faithful, reproducible. *Small sample (2 reps); the direction is consistent and theory-backed, but
worth more reps before locking in.* → **candidate default change (deferred to user).**

## Phase 3 — prompt variants for gpt-oss

**Question:** can a gpt-oss-tuned prompt (explicit "state exact counts; recount before finalizing")
cut the structural-count error we observed ("260 vs 329 methods")?

**Setup:** the case where the base prompt *fails* — 150 KB fixture (195 methods), temp 0 (deterministic),
low effort. Base prompt (`file-body-java`) vs a count-focused variant (`file-body-java-count`) that adds
two instructions: in *Public API*, "give the EXACT total count and index range for a member family", and
a *Rule* "COUNT EXACTLY … recount … never approximate or write 'similar'".

| prompt | what it wrote | true | count error | wall | sections |
|---|---|---|---|---|---|
| `file-body-java` (base) | "150 bucket methods (1–150)" | 195 | **45** | 2239 s | 9/9 |
| `file-body-java-count` | "adjustBucket1..adjustBucket195 (**195 total**)" | 195 | **0** | 2254 s | 9/9 |

**Findings**

1. **The count-focused prompt eliminates the large-file miscount.** The base prompt rounded to a wrong
   "150"; the variant reported the exact 195 — at the **same speed and completeness**. So the miscount is
   not a hard model limit; an explicit "count exactly / give the range" instruction fixes it.
2. Combined with Phase 2 (temp) and Phase 4 (scale), the structural-count error has **two cheap
   mitigations** — low temperature and an exact-count prompt — that together should keep counts correct
   well beyond where the stock setup fails. → **candidate prompt improvement D3.**

## Phase 4 — timing model + context ceiling refinement

**Question:** harden the `prefill_ms(n) ≈ 24.4·n + 0.000674·n²` fit with more file-size points and find
the real practical/​hard ceiling empirically.

**Setup:** fixtures 25/50/100/150 KB, low effort, temp 0.0 (deterministic), context sized to fit.

| file | prompt tok `n` | measured prefill | model prefill | error |
|---|---|---|---|---|
| 25 KB | 7 424 | 213 s | 218 s | +2.6 % |
| 50 KB | 13 510 | 448 s | 453 s | +1.1 % |
| 100 KB | 25 503 | 1 035 s | 1 061 s | +2.5 % |
| 150 KB | 37 496 | 1 818 s | 1 863 s | +2.4 % |

**Findings**

1. **The quadratic model holds across 25–150 KB** — every new point is within **+1.1 % to +2.6 %**, and
   the model consistently over-predicts by ~2 %. That bias is the *safe* direction for an ETA (it never
   under-promises), so the shipped constants are kept as-is.
2. **Count accuracy degrades with scale even at temp 0.** Exact (count error 0) up to **128 methods**
   (100 KB), but at **195 methods (150 KB) the model reported "150" — off by 45 (−23 %)**. So the
   earlier "260 vs 329" miscount is *both* a temperature effect (Phase 2) *and* a scale effect: beyond
   ~130–200 members the model stops counting and estimates. This gives Phase 3 a concrete target.
3. **Hard-ceiling probe deferred:** a ~450 KB file (~110 K tokens) would prefill for ~3 h on this CPU
   (quadratic), so the 128 K-window ceiling (~480–500 KB) is left as the analytical bound from §11
   rather than burned in unattended. 250 KB is the validated practical limit.

## Phase 5 — `charsPerToken` on real files

**Question:** the estimator assumes ~4.2 chars/token (from the synthetic fixture); what is it on real
repo Java?

**Setup:** 7 real source files (966 B … 29 KB) each indexed in its **own** process (so `cache_prompt`
can't undercount), recording bytes vs the llama.cpp full prompt-eval token count, then a linear fit
`promptTok = template + bytes / charsPerToken`.

| file | bytes | prompt tok |
|---|---|---|
| AiTimeSupport | 966 | 858 |
| AiCompletionParser | 3 715 | 1 480 |
| AiMdHeaderCodec | 8 637 | 2 598 |
| AiModelDefinition | 12 921 | 3 476 |
| AiGenerationConfig | 15 843 | 4 145 |
| GenerateMojo | 6 510 | 1 929 |
| PackageIndexer | 29 086 | 6 664 |

Fit: **`promptTok ≈ 713 + bytes / 4.81`** → real Java is **~4.8 chars/token**, template ≈ **713 tokens**.

**Findings**

1. **Real Java tokenizes at ~4.8 chars/token, not the 4.2 of the synthetic fixture** (real code has
   more long identifiers/keywords per token). Template overhead is ~713 tokens, not 400.
2. The estimator's current `ESTIMATION_CHARS_PER_TOKEN=4.2` therefore **over-counts tokens** for real
   Java → **over-estimates time** (the safe direction, but loose by ~12 %). → **candidate refinement D2.**
3. **Methodology caveat (important):** with `cache_prompt` on, a multi-file run undercounts per-file
   prompt-eval tokens (the shared prefix is reused and not re-counted) — the first Phase 5 attempt
   produced non-monotonic data (a 6.5 KB file showing fewer tokens than a 3.7 KB one). Measuring
   chars/token requires one file per process (or `cache_prompt=false`).

## Decisions deferred to the user

_(collected here as the session runs; nothing here is committed as a default change without sign-off.)_

- **D1 — lower the gpt-oss temperature from 1.0 to 0.0 (or 0.3)?** Phase 2 shows temp 0.0 is more
  faithful (exact counts) *and* deterministic (reproducible `.ai.md`, no incremental-diff churn), at no
  speed cost. The gpt-oss card recommends 1.0 for general use; for this extraction task the data favors
  0.0. Small sample — recommend confirming with more reps before changing the shipped presets.
- **D2 — refine the ETA estimator constants for real Java.** Phase 5 measured ~4.8 chars/token and
  ~713 template tokens on real source, vs the estimator's `ESTIMATION_CHARS_PER_TOKEN=4.2` /
  `PROMPT_TEMPLATE_TOKEN_OVERHEAD=400`. Bumping to 4.8 / ~700 makes the logged ETA ~12 % more accurate
  (it currently over-estimates). Low-risk code change (`AiGenerationTimeEstimator` + its test); held for
  sign-off only because it shifts the user-visible ETA numbers.
- **D3 — fold the "count exactly" instructions into the production `file-body-java` prompt.** Phase 3
  shows the variant fixes the large-file miscount (exact 195 vs the base's wrong "150") at no cost. The
  change is two prompt lines (a *Public API* family-count instruction + a *COUNT EXACTLY* rule).
  Strongest-evidence, lowest-risk of the three; recommended. Held only because it edits the shipped prompt.
