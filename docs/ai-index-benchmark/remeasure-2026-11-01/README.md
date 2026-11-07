<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Re-measurement, 2026-11-01

The runs of TODO item 8 after the audit of PR #261: gpt-oss-20b and GPT-6 Luna three times each on
the three corpora of [`languages-2026-11/`](../languages-2026-11/), and the large corpus locally on
the RTX 3070 Laptop. Every run directory holds `meta/<model>.json` and `out/<model>/`, the card
corpus's runs also `search.json` / `search.tsv`.

**What ran.** srcmorph at `8a2d413` (the core as of `9c0289b`: `mapReduce` reads every chunk, the
coverage line, the run record), `profiles/v3`, the tools of `modelcard/` at that commit. The fixes
the verification of the same day made afterwards (the chunker's repeated chunks, the overflow retry,
a provider's unchecked error ending the run, the read-timeout resend) are not in these jars; where a
run met one of them, it says so below.

| Directory | Corpus | Options |
|---|---|---|
| `languages/run1..3` | [`corpus-languages/`](../corpus-languages/) | `--profile v3` |
| `benchmark/run1..3` | the model-card corpus | `--profile v3`, then `search.py` (reader Mistral Small 3.2) |
| `large/run1..3` | `large.py` (seed 20261031, with the names planted in the middle) | `--facts --oversize mapReduce`, gpt-oss-20b `--chars-per-token 2` |
| `local/granite-xml-prompt`, `local/granite-generic-prompt` | the same | the two Granite presets, `profiles/v3` with and without `file-body-xml` |
| `local/gpt-oss-20b` | the same | `gpt-oss-20b-local` (MXFP4, 32K window), `--chars-per-token 2` |

Scored with today's `score.py` (`--meta` of each run; the large corpus with the
`<dir>-facts.json` that `large.py` writes next to it). Quality is the card's: the mean of
the kinds' recalls and the format share; the language corpus has only other files, so there it is
the mean of other-file recall and format.

## Hosted, OpenRouter

| | Run 1 / 2 / 3 | Mean | Reader hit@1 | s/file | USD per run (account delta) |
|---|---|---|---|---|---|
| gpt-oss-20b, language corpus | 98.9 / 100 / 99.3 % | 99.4 % | – | 4.8 to 10.6 | 0.001 to 0.002 |
| GPT-6 Luna, language corpus | 100 / 99.3 / 100 % | 99.8 % | – | 7.9 to 8.9 | 0.006 to 0.027 |
| gpt-oss-20b, card corpus | 99.3 / 100 / 100 % | 99.8 % | 95 / 95 / 91 % | 5.5 to 10.0 | 0.003 |
| GPT-6 Luna, card corpus | 100 / 100 / 100 % | 100 % | 93 / 88 / 100 % | 8.2 to 9.7 | 0.017 to 0.021 |
| gpt-oss-20b, large corpus | 90.6 / 55.2 / 88.3 % | 78.1 % | – | 56 to 61 | 0.007 to 0.010 |
| GPT-6 Luna, large corpus | 100 / 100 / 93.8 % | 97.9 % | – | 33 to 36 | 0.05 to 0.17 |

The reader's hit@1 is that of the re-run of 2026-11-07, which reads a summary without the facts and
coverage lines (published: gpt-oss-20b 95 / 93 / 91 %, GPT-6 Luna 95 / 86 / 93 %).

- **On the two small corpora both models are at the ceiling**: every expected name in most runs, no
  failed summary, a clean format.
- **The large corpus: every chunk read, and what reaches the summary.** Luna names the table and index
  planted in the middle of the SQL dump, the catalog's middle element (`inv:recall`) and the enum's
  middle area (`LEDGER_LOCK`) in all three runs. gpt-oss-20b names the dump's planted table in all
  three, but never the catalog's element nor the enum's area. Whether its chunk summaries held them
  and the reduce dropped them, the runs cannot tell: the partial summaries are not kept. In run 2 its enum answer was cut off at 4096 tokens and the
  file was not written (the score counts it as missing).
- **Cost.** `tokenCostUsd` in the meta is recomputed with today's `run.py` (cached prompt tokens at the
  cache-read price, list prices of 2026-11-01): Luna's large runs cost 0.104 USD with a cold cache and
  0.017 USD with 99 % of the prompt tokens cached; gpt-oss-20b's list price (0.040 USD) is an upper
  bound, the account was charged 0.007 to 0.010 USD. The account delta of a run also books its
  package and project phases, and late.

## Local, RTX 3070 Laptop, one run each

| | Quality | Enum | Catalog | Dump | s (file phase) |
|---|---|---|---|---|---|
| Granite 4.0 H-Tiny, 384K, XML prompt | 21.9 % | 7 of 8 names | cut off at 4096, not written | 0 of 11 | 1210 |
| Granite 4.0 H-Tiny, 384K, generic prompt | 21.9 % | 7 of 8 | cut off at 4096, not written | 0 of 11 | 1154 |
| Granite 4.0 H-1B, 16K chunks, XML prompt | 0.0 % | cut off at 1536 | too short | cut off at 1536 | 425 |
| Granite 4.0 H-1B, 16K chunks, generic prompt | 18.8 % | cut off at 1536 | 6 of 8 | cut off at 1536 | 496 |
| gpt-oss-20b, 32K window | 0.0 % | too short ("No Java source code present") | run ended | cut off at 4096 after 43 chunks | 1602 |

- **On the large files the local models fail through the output budget.** Since an incomplete answer
  is never written, an answer cut off at `maxOutputTokens` fails its file; with the previous code
  these were written as they came. The presets' budgets (H-1B 1536, the others 4096) do not hold a
  final answer about 30 000 rows or 5 000 products.
- **The XML-prompt question of the self-test's Granite tiers** (finding 7): for H-Tiny both prompts
  fail on the catalog the same way; for H-1B the generic prompt keeps 6 of 8 names where the XML
  prompt's answer is too short. One run each.
- **The local gpt-oss-20b run ended in the middle.** llama.cpp could not parse one of its harmony
  answers for the catalog ("The model produced output that does not match the expected peg-native
  format") and threw an unchecked `LlamaException`, which ended the whole run, so the catalog has no
  summary and the run record was not written. That is the engine review's first finding; since
  `e9a178c` such an error fails its file only, and the record survives.
