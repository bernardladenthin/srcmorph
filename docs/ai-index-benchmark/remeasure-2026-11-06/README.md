<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Measurements, 2026-11-06

The measurements of the run plan's phase C on PR #269 (TODO rows 13, 32, 35 and 36), after the
renames and the test sweep. srcmorph 1.5.0-SNAPSHOT (each run's `meta/*.json` names the commit),
`profiles/v3`, the tools of `modelcard/`, OpenRouter for gpt-oss-20b and GPT-6 Luna, the local CPU
for Granite. Scored with `score.py` (ids, everyday words only where marked as code).

| Directory | What | Row |
|---|---|---|
| `large/run1..3` | the large corpus (`large.py`, seed 20261031) through gpt-oss-20b as in [`remeasure-2026-11-01/`](../remeasure-2026-11-01/README.md) (`--facts --oversize mapReduce --chars-per-token 2`), with `--parts`: every map and reduce answer is kept in `parts/` | 32 |
| `large-family-rule/run1..3`, `luna` | the same with a sharper family rule in `map-chunk` and `reduce-parts` (not adopted, see below) | 32 |
| `big-pom/xml-1..3`, `generic-1..3`, `deterministic-1` | netty-parent 4.2.0.Final's POM through the self-test's `big-pom` tier, Granite H-Tiny map-reduce | 35 |
| `real-projects/kinds-1..2`, `fallback-1..2` | 218 files of the new kinds from three open-source projects, gpt-oss-20b and GPT-6 Luna | 36 |

The summaries of the open-source projects are not kept here, only the runs' metadata, the names the
scoring expected (`facts.json`) and the script that derives them (`real-projects/build_facts.py`).

## Row 32: where gpt-oss-20b loses the middle of a file

Each large file carries names only its middle holds (`large.py`). The kept parts show at which step
they survive; numbers are the middle names a step's answer states:

| File (chunks) | Middle of the file | run 1 | run 2 | run 3 |
|---|---|---|---|---|
| SQL dump (9) | 0.49 | chunk 5 half 1: 3/3, reduce 3/3, summary 3/3 | chunk 5: 3/3, reduce 3/3, summary 3/3 | chunk 5: 3/3, reduce 3/3, summary 3/3 |
| XML catalog (5) | 0.47 to 0.53 | chunk 3: 1/1, reduce 1/1, summary 1/1 | the same | the same |
| Java enum (2) | 0.44 to 0.55, across the chunk border | chunk 1: 1/1, reduce 1/1, summary 1/1 | chunk 1: 1/1, **reduce 0/1** | **chunks 0/1** |

- The dump's and the catalog's middles reach the summary in every run; on 2026-11-01 gpt-oss-20b
  named the catalog's element and the enum's area in none of its three runs (the map and reduce
  prompts of `map-chunk` and `reduce-parts` came after them).
- The enum's middle is lost twice in three runs, once at each step. In run 3 the map answer says
  "ledger lock failures" in words and lists the first six constants as examples instead of the
  family `LEDGER_LOCK_*`; in run 2 the map answer names it (`LEDGER_LOCK_02701..LEDGER_LOCK_03300
  (600 total)`) and the reduce replaces the first part's families by examples of the second part.
- Both prompts already ask for it ("Keep every name any part states", "A family ... stays one family
  across the parts"). A sharper rule for families in both (`large-family-rule/`) did worse: 10 of
  the 15 middle names in three runs against 13 of 15, the dump's middle lost once at the map step,
  the catalog's once, the enum's still in two runs, and up to 14 invented names per 100 in the enum
  against 4 to 8. It is not adopted. GPT-6 Luna names the middles with either prompt.
- What follows: the loss is the model's, at random and at both steps, not a gap of the prompts. A
  file of this size wants a stronger model than gpt-oss-20b, or several runs.

| Run | SQL recall | XML recall | Java recall | invented per 100 (Java) |
|---|---|---|---|---|
| `large/run1..3` | 100 / 100 / 100 % | 87.5 / 87.5 / 100 % | 87.5 % each | 5.4 / 7.5 / 4.2 |
| `large-family-rule/run1..3` | 81.8 / 100 / 100 % | 87.5 / 87.5 / 100 % | 87.5 % each | 13.9 / 14.0 / 5.3 |

(`score.py` counts `LEDGER_LOCK` only as a name of its own, not as the prefix of
`LEDGER_LOCK_02701`, so its Java recall reads 87.5 % in every run; the trace above reads the parts.)

## Row 35: a real POM in the `big-pom` tier

netty-parent 4.2.0.Final (Apache-2.0), 590 KB: 54 modules, 30 profiles, 15 build plugins, the 99
names of `big-pom/facts.json`. Granite H-Tiny with the self-test's `granite-4.0-h-tiny-mapreduce`
preset (`--facts --oversize mapReduce`), three runs per prompt; the runs of one prompt wrote the same
bytes (seed 42).

| Prompt | names found | invented per 100 | body | file phase |
|---|---|---|---|---|
| `file-body-xml` | 6 of 99 | 0 | 1.1 KB | 32 to 33 min |
| `file-body-fallback` (the tier's prompt until now) | 4 of 99 | 50.9 | 6.4 KB | 22 min |
| `onOversize: deterministic` | 0 of 99 | 0 | 1.9 KB | 8 s |

Neither summary is a good one: both state a wrong version (4.1.73.Final), two part summaries were
cut at the output budget. The facts line (`dependencies: 66; plugins: 35; modules: 54; profiles: 30;
...`) is what such a file gets reliably. The self-test's `big-pom` takes `file-body-xml` now, for
the invented names; `huge-pom` is unchanged, since no real POM of a megabyte was found.

## Row 36: the new kinds on real projects

jpetstore-6 (`8f11fab`, MyBatis mappers, JSP, `web.xml`, Spring XML, the POM), microservices-demo
(`c7cb6d5`, Kubernetes manifests, Kustomize, Dockerfiles, Protocol Buffers, Terraform, GitHub
Actions) and camunda-bpm-examples (`6c7f4c4`, BPMN, DMN, eight POMs); all Apache-2.0. 218 files,
1072 names derived from the sources per kind (`build_facts.py`: a mapper's statement ids, a JSP's
includes and bean classes, a BPMN's process id and element names, a manifest's object, container and
image names, a Dockerfile's base images and stages, a proto's messages, services and rpcs, a
Terraform file's resource, variable, module and output names, a workflow's jobs and actions, a POM's
artifactIds). Variant `kinds`: `profiles/v3` with `--facts`, every kind its rule as `run.py` routes
it; variant `fallback`: the same profile without the kinds' prompts and without counters, every file
through `file-body-fallback`. Two runs each; recall of both runs together:

| Kind | Luna fallback | Luna kinds | gpt-oss-20b fallback | gpt-oss-20b kinds |
|---|---|---|---|---|
| GitHub Actions | 95.6 | 100.0 | 81.6 | 98.2 |
| BPMN | 77.3 | 94.5 | 56.5 | 89.7 |
| DMN (one file) | 87.5 | 87.5 | 56.2 | 75.0 |
| Dockerfile | 74.3 | 100.0 | 61.4 | 100.0 |
| Jakarta EE `web.xml` | 100.0 | 100.0 | 91.7 | 100.0 |
| JSP | 100.0 | 100.0 | 100.0 | 100.0 |
| Kubernetes | 94.8 | 99.6 | 78.9 | 96.6 |
| Kustomization | 100.0 | 100.0 | 63.6 | 100.0 |
| MyBatis | 97.1 | 100.0 | 100.0 | 100.0 |
| POM | 91.9 | 100.0 | 81.0 | 90.5 |
| Protocol Buffers | 95.6 | 100.0 | 96.2 | 99.2 |
| Spring XML | 100.0 | 90.3 | 90.3 | 97.1 |
| Terraform | 89.6 | 100.0 | 68.8 | 91.7 |
| **all** | **92.2** | **97.7** | **81.2** | **97.3** |

- For gpt-oss-20b, the self-test's model, every kind is at least as good with its rule; for Luna the
  Spring XML files are not (the `xml` rule, 90 % against 100 %). The self-test and its CLI twin route
  these kinds now (TypeScript and npm from the measurements of 2026-11-05 and row 13 too).
- The file phase cost 0.07 to 0.09 USD per run for Luna and 0.02 to 0.03 USD for gpt-oss-20b at list
  prices (`tokenCostUsd`); a run of both models took about 80 minutes.

## Row 13: an npm manifest (internal)

The package.json of a VS Code extension, measured internally (the data stays out of the
repository), three runs per variant, 45 names: gpt-oss-20b named 45 / 13 / 28 / 15 of them with the
fallback prompt, 21 / 21 / 28 with the npm rule and the ops prompt as it was (without the
extension's 17 language model tools in every run), and 45 / 28 / 34 once the ops prompt had a line
for a manifest; GPT-6 Luna 43 to 45, 33 / 45 / 45 and 45 / 45 / 45.
