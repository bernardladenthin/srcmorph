<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Re-measurement, 2026-11-04

The runs behind the profile changes of 2026-11-04: the secrets rule in every file prompt, the XML
prompt's definitions of the enterprise kinds, and the shared prompt `file-body-oop` for Java, Kotlin,
Groovy and Scala. gpt-oss-20b and GPT-6 Luna on OpenRouter, three runs each, one after the other.
Every run directory holds `meta/<model>.json` and `out/<model>/`.

**What ran.** srcmorph at `f5dc742`, `profiles/v3` at that commit (which still had
`file-body-java.md`), the tools of `modelcard/` at that commit.

| Directory | Corpus | Options |
|---|---|---|
| `benchmark-java/run1..3` | the model-card corpus | `--profile v3 --java-prompt file-body-java` |
| `benchmark-oop/run1..3` | the same | `--profile v3 --java-prompt file-body-oop` |
| `languages/run1..3` | [`corpus-languages/`](../corpus-languages/) | `--profile v3` |
| `enterprise/run1..3` | [`corpus-enterprise/`](../corpus-enterprise/README.md) | `--profile v3 --facts --java-prompt file-body-oop` |

Scored with `score.py` (the language corpus with `facts-languages.json`, the enterprise corpus with
`facts-enterprise.json`); quality is the model card's, the mean of the kinds' recalls and the format
share (`repeat.quality`). No reader search ran.

| | Run 1 / 2 / 3 | Mean | s/file | USD per run (account delta) |
|---|---|---|---|---|
| gpt-oss-20b, card corpus, `file-body-java` | 99.4 / 100 / 100 % | 99.8 % | 4.1 to 9.1 | 0.002 to 0.004 |
| gpt-oss-20b, card corpus, `file-body-oop` | 100 / 99.7 / 100 % | 99.9 % | 4.3 to 6.5 | 0.003 to 0.004 |
| GPT-6 Luna, card corpus, `file-body-java` | 100 / 99.2 / 100 % | 99.7 % | 7.3 to 8.1 | 0.017 to 0.027 |
| GPT-6 Luna, card corpus, `file-body-oop` | 100 / 100 / 100 % | 100 % | 6.8 to 7.6 | 0.017 to 0.022 |
| gpt-oss-20b, language corpus | 99.0 / 98.6 / 98.3 % | 98.7 % | 3.7 to 4.3 | 0.001 to 0.002 |
| GPT-6 Luna, language corpus | 100 / 99.3 / 100 % | 99.8 % | 7.9 to 8.4 | 0.006 to 0.008 |
| gpt-oss-20b, enterprise corpus | 97.2 / 99.3 / 97.2 % | 97.9 % | 4.1 to 4.2 | 0.002 to 0.003 |
| GPT-6 Luna, enterprise corpus | 97.5 / 97.3 / 97.1 % | 97.3 % | 5.7 to 5.9 | 0.009 to 0.014 |

No run failed a file, wrote a code fence or a `###` heading, or copied the password the enterprise
corpus's `persistence.xml` holds (`secretsLeaked` 0 in every run).

- **The shared prompt holds Java.** On the card corpus the Java recall is the same with either
  prompt (gpt-oss 99.9 to 100 %, Luna 100 %), and every miss is one seen before: `setCachePrompt` of
  `AiGenerationConfig` (gpt-oss, two runs with `file-body-java`, one with `file-body-oop`) and
  `book_free_le_total` of the SQL migration, which no Java prompt touches. Java therefore moved to
  `file-body-oop`, and `file-body-java.md` went: one prompt serves every class-based language.
- **The language corpus**, after the secrets rule and the XML prompt's new definitions: Luna 99.8 %
  (99.5 % on 2026-11-02), gpt-oss 98.7 % (99.1 %, scored the same way). gpt-oss's misses are those of
  2026-11-02 (`LEVEL_RANK`, the XSLT's `format-number`, the deploy script's `lib/common.sh`) plus
  `systemctl` of the deploy script in two runs and `settled_payment` of the ksh script in one. Three
  runs against three cannot tell that from variation.
- **The enterprise corpus** is new, so it has nothing to compare against. What the models left out
  is mostly private: Luna the Kotlin service's `nextNumber` and `PREFIX` and the Java resource's
  `refund` call in every run (the prompt asks for the public API), gpt-oss the workflow's name
  `release-backoffice` in every run (the ops prompt does not ask for a workflow's name), the Java
  resource's `PURGE_AFTER_DAYS` and the Kotlin `defaultCurrency` in two, and in one run both
  session types `Stateless` / `Stateful` of `ejb-jar.xml` (80 % for that file), in another and the Facelets
  view's template path. `web.xml`, both persistence files, the MyBatis mapper, the Camel routes, the
  BPMN process, the WSDL, the JSP, the OpenAPI contract, the Kubernetes manifest and the Dockerfile
  scored 100 % in all six runs.
