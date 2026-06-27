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

_Results: pending._

## Phase 3 — prompt variants for gpt-oss

**Question:** can a gpt-oss-tuned prompt (explicit "state exact counts; recount before finalizing")
cut the structural-count error we observed ("260 vs 329 methods")?

_Results: pending._

## Phase 4 — timing model + context ceiling refinement

**Question:** harden the `prefill_ms(n) ≈ 24.4·n + 0.000674·n²` fit with more file-size points and find
the real practical/​hard ceiling empirically.

_Results: pending._

## Phase 5 — `charsPerToken` on real files

**Question:** the estimator assumes ~4.2 chars/token (from the synthetic fixture); what is it on real
repo Java?

_Results: pending._

## Decisions deferred to the user

_(collected here as the session runs; nothing here is committed as a default change without sign-off.)_
