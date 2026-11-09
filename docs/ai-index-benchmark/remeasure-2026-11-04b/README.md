<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Re-measurement after the prompt changes, 2026-11-04

The runs behind three changes that followed [`remeasure-2026-11-04/`](../remeasure-2026-11-04/README.md)
the same day: the user message of a file starts with its path below the base directory instead of its
name, `file-body-oop` names the private functions, constants and fields the logic uses and each call
on a dependency, and `file-body-ops` names a CI workflow or pipeline and defines Gradle scripts.
gpt-oss-20b and GPT-6 Luna on OpenRouter, one run after the other. Every run directory holds
`meta/<model>.json` and `out/<model>/`.

**What ran.** srcmorph at `51fcd23` (the core jar built from it; `meta` records the CLI jar, which did
not change), `profiles/v3` at that commit, the tools of `modelcard/` at that commit. Between the two
measurements the fact counters changed as well (`java-facts` skips comments and strings, new facts
files); a facts line reaches the model only in a reduce call, which no file of these corpora needs,
and the scores read names, not counts.

| Directory | Corpus | Options |
|---|---|---|
| `benchmark/run1..6` | the model-card corpus | `--profile v3` (Java takes `file-body-oop`); runs 4 to 6 gpt-oss-20b only |
| `languages/run1..9` | [`corpus-languages/`](../corpus-languages/) | `--profile v3`; runs 4 to 9 gpt-oss-20b only |
| `enterprise/run1..3` | [`corpus-enterprise/`](../corpus-enterprise/README.md) | `--profile v3 --facts` |

Scored with `score.py` (the language corpus with `facts-languages.json`, the enterprise corpus with
`facts-enterprise.json`); quality is the model card's, the mean of the kinds' recalls and the format
share (`repeat.quality`). No reader search ran.

| | Runs | Mean | Before (`remeasure-2026-11-04`) | s/file | USD per run (token cost) |
|---|---|---|---|---|---|
| gpt-oss-20b, card corpus | 100 / 98.8 / 99.2 / 99.1 / 100 / 99.7 % | 99.5 % | 99.9 % (3 runs) | 4.4 to 8.5 | 0.005 |
| GPT-6 Luna, card corpus | 100 / 100 / 100 % | 100 % | 100 % | 7.5 to 8.5 | 0.011 to 0.018 |
| gpt-oss-20b, language corpus | 99.0 / 98.9 / 99.3 / 99.3 / 98.8 / 99.3 / 98.6 / 99.0 / 98.6 % | 99.0 % | 98.7 % (3 runs) | 3.0 to 6.3 | 0.001 |
| GPT-6 Luna, language corpus | 99.3 / 99.3 / 100 % | 99.5 % | 99.8 % | 7.4 to 8.0 | 0.004 to 0.005 |
| gpt-oss-20b, enterprise corpus | 99.9 / 99.8 / 99.4 % | 99.7 % | 97.9 % | 3.8 to 11.1 | 0.002 to 0.003 |
| GPT-6 Luna, enterprise corpus | 100 / 100 / 99.8 % | 99.9 % | 97.3 % | 5.9 to 6.9 | 0.005 to 0.006 |

The cost column is the file phase's tokens at the list prices (`tokenCostUsd`); the account delta
(`costUsd`, all three phases) came back as 0 for two of the short language runs, booked late. No run
failed a file, wrote a code fence or a `###` heading, or copied the password the enterprise corpus's
`persistence.xml` holds (`secretsLeaked` 0 in every run).

- **The enterprise corpus** is where the two prompt sentences aimed, and both took: the workflow's
  name `release-backoffice` (missing in all three gpt-oss runs before) and the Kotlin service's
  `nextNumber` and `PREFIX` and the Java resource's `refund` call (missing in all three Luna runs)
  are named in all six runs. What is left: gpt-oss the Kotlin `defaultCurrency` (two runs, as
  before), the Spock specification's `heavyOrder` (two) and the MyBatis mapper's `CUSTOMER` table
  (one); Luna the JSP's `pageSize` (one). `ejb-jar.xml`'s session types, which one gpt-oss run lost
  before, are named in every run.
- **The language corpus** answers what three runs could not: gpt-oss scores 99.0 % over nine runs
  (99.1 % over three on 2026-11-02, 98.7 % over three in the morning), and the deploy script's
  `systemctl`, missing in two of the morning's three runs, is named in all nine. Its misses are
  those of every earlier round: the XSLT's `format-number` (nine of nine), the deploy script's
  `lib/common.sh` (five), the Perl module's `LEVEL_RANK` (three) and once `HOSTS`. Luna lost
  `format-number` in two of three runs (one of three before).
- **The card corpus** holds Java: Luna 100 % in all three runs, gpt-oss's Java misses are the one
  of every earlier round (`setCachePrompt` of `AiGenerationConfig`, two of six). Its other misses are
  in files whose prompts did not change but for the path line: the SQL migration's
  `book_free_le_total` (three of six; one of three before) and once
  `joined_on` and `member_since`, and in two
  of six runs `ringbuf.c`'s `malloc` and `memcpy`, where the summary named the headers
  (`stdlib.h`, `string.h`) and not the functions it uses from them. Six runs against three cannot
  tell that from variation (Fisher's exact test, p = 0.5): the three extra runs named both.
