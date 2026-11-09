<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Measurements, 2026-11-07

The kinds the self-test did not route yet, because no real project of theirs had been measured:
persistence and integration XML, WSDL, Facelets, OpenAPI, AsyncAPI, Avro, GraphQL, Camel YAML,
Compose, Ansible, Gradle, properties, PowerShell, batch, Kotlin, Groovy, Scala and the Java EE
classes, and `pyproject.toml`. srcmorph 1.5.0-SNAPSHOT (each run's `meta/*.json` names the commit),
`profiles/v3`, the tools of `modelcard/`, OpenRouter for gpt-oss-20b and GPT-6 Luna. Scored with
`score.py` (ids, everyday words only where marked as code).

| Directory | What |
|---|---|
| `real-projects/kinds-1..2`, `fallback-1..2` | 272 files of 20 kinds from open-source projects, gpt-oss-20b and GPT-6 Luna |

The summaries are not kept here, only the runs' metadata, the names the scoring expected
(`facts.json`, 2046 names) and the script that derives them (`real-projects/build_facts.py`).

## The projects

At most 12 files of one kind from one project, the first in path order:

| Project | Commit | License | Kinds |
|---|---|---|---|
| apache/cxf | `3934fca` | Apache-2.0 | WSDL, Java EE, Groovy, batch |
| apache/tomee | `8a4aa2f` | Apache-2.0 | Java EE, persistence, properties |
| apache/camel-examples | `869cde2` | Apache-2.0 | Camel YAML, integration XML, properties |
| apache/avro | `3a207fa` | Apache-2.0 | Avro |
| apache/tomcat | `b80121c` | Apache-2.0 | batch |
| eclipse-ee4j/jakartaee-examples | `49408d9` | BSD-3-Clause | Java EE, Facelets, persistence |
| spring-projects/spring-graphql | `2b2b878` | Apache-2.0 | GraphQL, Gradle |
| spring-projects/spring-petclinic | `500158f` | Apache-2.0 | Gradle |
| Netflix/dgs-examples-kotlin | `8a96f04` | Apache-2.0 | Kotlin, GraphQL |
| spockframework/spock-example | `8155d1e` | Apache-2.0 | Groovy, Gradle, batch |
| scala/scala3-example-project | `c7bfe5f` | CC0-1.0 | Scala |
| asyncapi/spec | `4166274` | Apache-2.0 | AsyncAPI |
| OAI/learn.openapis.org | `82b82f3` | CC-BY-4.0 | OpenAPI |
| docker/awesome-compose | `30f4b7f` | CC0-1.0 | Compose |
| geerlingguy/ansible-for-devops | `86e1bf8` | MIT | Ansible |
| PowerShell/PowerShell | `1aac7f5` | MIT | PowerShell |
| 12 Python projects | see below | their own | `pyproject.toml` |

The `pyproject.toml` files: pallets/flask `49943b5`, pallets/click `8abb77a`, psf/black `b4f1fca`,
encode/httpx `b5addb6`, pydantic/pydantic `dabcc64`, pypa/pip `a4a111d`, pytest-dev/pytest `171c924`,
python-poetry/poetry `6ecadf5`, fastapi/fastapi `1ca09c1`, sphinx-doc/sphinx `035a07c`,
pandas-dev/pandas `831f55c`, psf/requests `3c7baaa` (django `a1f2b45` and scikit-learn `6e37063` were
cloned too; the limit of 12 left them out).

The names per kind (`build_facts.py`): a JPA mapping's units, entity classes, data sources and
provider; a Camel or Spring Integration file's route ids, beans, bean classes and endpoints; a WSDL's
services, port types, bindings and operations; a Facelets page's templates, includes and the beans
its expressions start with; an OpenAPI contract's paths, operation ids and schemas, an AsyncAPI
contract's channels, operations, messages and schemas; an Avro schema's records, enums and the top
record's fields; a GraphQL schema's types and root fields; a Camel YAML route's ids and endpoints; a
Compose file's services, images, volumes and networks; an Ansible play's hosts, roles and variables
and each task's module; a Gradle script's plugins, dependencies and tasks; a properties file's keys
(the first 40); a PowerShell script's functions and parameters; a batch file's labels, variables and
called scripts; a Kotlin, Groovy or Scala file's types and functions; a Java EE class's types, public
methods and `@Path` values; a `pyproject.toml`'s project name, build backend, dependencies, scripts,
extras and tool sections.

## Recall by kind

Variant `kinds`: `profiles/v3` with `--facts`, every kind its rule as `run.py` and the Luna example
route it (`pyproject.toml` the ops prompt, without counters); variant `fallback`: the same profile
without the kinds' prompts and without counters, every file through `file-body-fallback`. Two runs
each; recall of both runs together:

| Kind | Files | Luna fallback | Luna kinds | gpt-oss-20b fallback | gpt-oss-20b kinds |
|---|---|---|---|---|---|
| Ansible | 12 | 100.0 | 100.0 | **95.3** | 85.8 |
| AsyncAPI | 12 | 96.3 | 96.3 | 77.3 | 89.3 |
| Avro | 12 | 100.0 | 100.0 | 100.0 | 100.0 |
| batch | 16 | 82.0 | 86.1 | 36.7 | 36.5 |
| Camel YAML | 10 | 68.7 | 85.8 | 64.2 | 73.1 |
| Compose | 20 | 98.9 | 100.0 | 98.4 | 99.5 |
| Facelets | 12 | 93.8 | 97.3 | 78.1 | 93.8 |
| Gradle | 11 | 59.3 | 100.0 | 61.7 | 99.0 |
| GraphQL | 6 | 100.0 | 100.0 | 100.0 | 100.0 |
| Groovy | 11 | 100.0 | 100.0 | 97.3 | 100.0 |
| integration XML | 12 | 89.4 | 97.0 | 90.2 | 100.0 |
| Java EE classes | 30 | 95.5 | 99.1 | 98.2 | 98.2 |
| Kotlin | 10 | 100.0 | 100.0 | 98.9 | 100.0 |
| OpenAPI | 8 | 100.0 | 100.0 | 98.4 | 100.0 |
| persistence XML | 20 | 100.0 | 99.2 | 100.0 | 100.0 |
| PowerShell | 13 | 97.8 | 97.5 | 80.8 ¹ | 89.6 |
| properties | 20 | **100.0** | 93.0 | 100.0 | 100.0 |
| `pyproject.toml` | 12 | 86.3 | 97.3 | 88.1 | 97.6 |
| Scala | 13 | 97.9 | 100.0 | 83.1 | 97.9 |
| WSDL | 12 | 90.5 | 100.0 | 83.1 | 100.0 |
| **all** | **272** | **88.6** | **96.6** | **79.7** | **88.9** |

¹ In `fallback-1` gpt-oss-20b's answer for `tools/ci.psm1` ran to the 4096 output tokens; srcmorph
did not write the file, and it scores as missing.

- **For gpt-oss-20b, the self-test's model, every kind but Ansible is at least as good with its rule**
  (batch one name of 482 lower, level). Ansible is not: with the ops prompt and its counters it names
  fewer of a play's modules (`command`, `lineinfile`, `add_host`) than with the fallback prompt. The
  self-test and its CLI twin route these kinds now, Ansible to `file-body-fallback` without counters
  (as measured), and `pyproject.toml` to the ops prompt; the Java EE classes stay on the self-test's
  `java` rule, which already takes `file-body-oop` (the `java-ee` rule adds only its counters, and
  was level with the fallback).
- **For GPT-6 Luna every kind but properties is at least as good with its rule** (persistence XML
  and PowerShell less than one point lower, level): in three Log4j 2 files it summarises the dotted
  keys (`appender.out.type`) as groups with the ops prompt. The Luna example routes `pyproject.toml`
  to the ops prompt now and is otherwise unchanged.
- **Batch files are weak for gpt-oss-20b either way** (37 %): most expected names are the labels a
  `goto` jumps to (`:gotHome`, `:okHome`), which it leaves out with both prompts; Luna names 82 to
  86 %.
- **Cost:** `kinds-1` ran alone, 0.30 USD for Luna and 0.10 USD for gpt-oss-20b, all three phases
  (the account's usage delta). The other three ran beside each other and beside the search re-run of
  the model card, so the `costUsd` in their metadata holds the others' spending too; the file phase at
  list prices (`tokenCostUsd`) is 0.10 to 0.12 USD per run for Luna and about 0.03 USD for gpt-oss-20b.
  A run of both models took about 95 minutes alone, more than half of it the package phase.
