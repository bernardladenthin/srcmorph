# gpt-oss-20b tuning — unattended findings

Autonomous tuning session for the `gpt-oss-20b` default of this plugin. Goal: turn the open
questions from [COMPARISON.md §11](COMPARISON.md) into **objectively measured** answers, run
unattended, and document everything here.

- **Model:** `gpt-oss-20b-UD-Q4_K_XL.gguf` via llama.cpp JNI, CPU-only (AMD Ryzen 7 5800H, 8 threads).
- **Git baseline before the session:** `f14819b` (branch `claude2026_06_26_00`).
- **Date:** 2026-06-27.

## Method — objective scoring via ground-truth fixtures

The benchmark cells run the real `generate` goal on a **synthetic Java fixture of known structure**
(`docs/ai-index-benchmark/tools/generate-fixture.sh`: a ledger class with a known number of
`adjustBucketN` methods, 40 weight fields, and 6 tail methods). A scorer parses the produced
`.ai.md` plus the llama.cpp run log into objective metrics, so "quality" becomes numbers a script can
compare:

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

_Results: pending._

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

_Results: pending._

## Decisions deferred to the user

_(collected here as the session runs; nothing here is committed as a default change without sign-off.)_

- **D1 — lower the gpt-oss temperature from 1.0 to 0.0 (or 0.3)?** Phase 2 shows temp 0.0 is more
  faithful (exact counts) *and* deterministic (reproducible `.ai.md`, no incremental-diff churn), at no
  speed cost. The gpt-oss card recommends 1.0 for general use; for this extraction task the data favors
  0.0. Small sample — recommend confirming with more reps before changing the shipped presets.
