# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Tests of run.py, search.py, large.py, report.py, repeat.py and correlate.py that need no network and no
model.

Run from the repository root: python -m unittest discover -s docs/ai-index-benchmark/modelcard/tests
"""
import argparse
import http.client
import io
import json
import os
import re
import subprocess
import sys
import tempfile
import unittest
import urllib.error
from pathlib import Path
from unittest import mock

HERE = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(HERE))
import correlate  # noqa: E402
import run  # noqa: E402
import repeat  # noqa: E402
import score  # noqa: E402
import search  # noqa: E402

MODELS = json.loads((HERE / "models.json").read_text(encoding="utf-8"))


def args(**overrides):
    values = {"models": "gpt-oss-20b", "backend": "", "oversize": "", "max_chunks": None, "chars_per_token": None}
    values.update(overrides)
    return argparse.Namespace(**values)


class ValidateTest(unittest.TestCase):

    def assert_rejected(self, **overrides):
        parser = argparse.ArgumentParser()
        with mock.patch.object(sys, "stderr", io.StringIO()), self.assertRaises(SystemExit):
            run.validate(parser, args(**overrides), MODELS)

    def test_valid(self):
        run.validate(argparse.ArgumentParser(), args(oversize="sampleChunks", max_chunks=6, chars_per_token=2.5),
                     MODELS)

    def test_rejected(self):
        self.assert_rejected(max_chunks=6)
        self.assert_rejected(oversize="mapReduce", max_chunks=6)
        self.assert_rejected(oversize="sampleChunks")
        self.assert_rejected(oversize="sampleChunks", max_chunks=0)
        self.assert_rejected(chars_per_token=0)
        self.assert_rejected(models="gpt-oss-20b,gpt-6-lunna")
        self.assert_rejected(models="", backend="nowhere")


class RulesTest(unittest.TestCase):

    def test_maxChunks_isSetWheneverGiven(self):
        rules = run.file_rules(run.PROFILES / "v3", "m", oversize="sampleChunks", max_chunks=1)

        self.assertTrue(all(r["onOversize"] == "sampleChunks" and r["maxChunks"] == 1 for r in rules if not r.get("skip")))

    def test_theRunRecord_onlyInTheFilePhase(self):
        definition = {"key": "m", "provider": "mock"}
        files = run.phase_config("GenerateFileIndex", Path("w"), Path("o"), run.PROFILES / "v3", definition,
                                 run_record=Path("r.json"))
        packages = run.phase_config("AggregatePackages", Path("w"), Path("o"), run.PROFILES / "v3", definition,
                                    run_record=Path("r.json"))

        self.assertEqual(files["srcMorph"]["runRecordFile"], "r.json")
        self.assertNotIn("runRecordFile", packages["srcMorph"])

    def test_clearOut_aPartsDirectory_deletesOnlyPartsFiles(self):
        with tempfile.TemporaryDirectory() as tmp:
            parts = Path(tmp) / "parts"
            (parts / "java").mkdir(parents=True)
            (parts / "java" / "Big.java.parts.md").write_text("# old", encoding="utf-8")

            run.clear_out(parts, ".parts.md")

            self.assertFalse(parts.exists())

    def test_clearOut_aPartsDirectoryWithAForeignFile_refuses(self):
        with tempfile.TemporaryDirectory() as tmp:
            parts = Path(tmp) / "parts"
            parts.mkdir()
            (parts / "Big.java").write_text("class Big {}", encoding="utf-8")

            with self.assertRaises(SystemExit):
                run.clear_out(parts, ".parts.md")
            self.assertTrue((parts / "Big.java").exists())

    def test_thePartsDirectory_onlyInTheFilePhaseAndOnlyWhenAsked(self):
        definition = {"key": "m", "provider": "mock"}
        files = run.phase_config("GenerateFileIndex", Path("w"), Path("o"), run.PROFILES / "v3", definition,
                                 parts=Path("p"))
        packages = run.phase_config("AggregatePackages", Path("w"), Path("o"), run.PROFILES / "v3", definition,
                                    parts=Path("p"))
        unasked = run.phase_config("GenerateFileIndex", Path("w"), Path("o"), run.PROFILES / "v3", definition)

        self.assertEqual(files["srcMorph"]["partsDirectory"], "p")
        self.assertNotIn("partsDirectory", packages["srcMorph"])
        self.assertNotIn("partsDirectory", unasked["srcMorph"])

    def test_theAggregationPhases_takeTheirRuleWhereTheCliReadsIt(self):
        definition = {"key": "m", "provider": "mock"}
        packages = run.phase_config("AggregatePackages", Path("w"), Path("o"), run.PROFILES / "v3", definition)
        project = run.phase_config("AggregateProject", Path("w"), Path("o"), run.PROFILES / "v3", definition)

        chunked = {"mapPromptKey": "map-chunk", "reducePromptKey": "reduce-parts", "onIncomplete": "retryOnce"}
        self.assertEqual(packages["packageRules"],
                         [{"promptKey": "package-body", "aiDefinitionKey": "m", **chunked}])
        self.assertNotIn("projectRules", packages)
        self.assertEqual(project["projectRules"],
                         [{"promptKey": "project-body", "aiDefinitionKey": "m", **chunked}])

    def test_chunkedRules_nameTheMapAndReducePrompts_otherRulesNot(self):
        profile = run.PROFILES / "v3"
        chunked = run.file_rules(profile, "m", oversize="mapReduce")
        sampled = run.file_rules(profile, "m", oversize="sample")

        self.assertTrue(all(r["mapPromptKey"] == "map-chunk" and r["reducePromptKey"] == "reduce-parts"
                            for r in chunked if not r.get("skip")))
        self.assertTrue(all("mapPromptKey" not in r and "reducePromptKey" not in r for r in sampled))

    def test_javaPrompt_namesTheJavaAndTheJavaEeRule(self):
        profile = run.PROFILES / "v3"
        default = {r["id"]: r for r in run.file_rules(profile, "m")}
        with tempfile.TemporaryDirectory() as tmp:
            # a profile of the history before 2026-11-04, with its Java prompt
            (Path(tmp) / "file-body-java.md").write_text("Java.", encoding="utf-8")
            older = {r["id"]: r for r in run.file_rules(Path(tmp), "m", java_prompt="file-body-java")}

        self.assertEqual(default["java"]["promptKey"], "file-body-oop")
        self.assertEqual(default["java-ee"]["promptKey"], "file-body-oop")
        self.assertEqual(default["java-ee"]["priority"], 10)
        self.assertEqual(older["java"]["promptKey"], "file-body-java")
        self.assertEqual(older["java-ee"]["promptKey"], "file-body-java")

    def test_enterpriseKinds_takeTheirSharedPromptAndTheirOwnFacts(self):
        rules = {r["id"]: r for r in run.file_rules(run.PROFILES / "v3", "m", facts=True)}

        self.assertEqual((rules["kotlin"]["promptKey"], rules["kotlin"]["factsKey"]), ("file-body-oop", "kotlin-facts"))
        self.assertEqual((rules["jee"]["promptKey"], rules["jee"]["factsKey"]), ("file-body-xml", "jee-facts"))
        self.assertEqual((rules["jsp"]["promptKey"], rules["jsp"]["factsKey"]), ("file-body-view", "jsp-facts"))
        self.assertEqual(rules["openapi"]["promptKey"], "file-body-contract")
        self.assertEqual(rules["dockerfile"]["factsKey"], "dockerfile-facts")
        self.assertEqual((rules["scala"]["promptKey"], rules["scala"]["factsKey"]), ("file-body-oop", "scala-facts"))
        self.assertEqual((rules["proto"]["promptKey"], rules["proto"]["factsKey"]), ("file-body-contract", "proto-facts"))
        self.assertEqual((rules["gradle"]["promptKey"], rules["gradle"]["priority"]), ("file-body-ops", 10))
        # a kind without a facts file of its own counts nothing
        self.assertNotIn("factsKey", rules["ops"])

    def test_typeScript_takesTheOopPromptWithoutFacts(self):
        rules = {r["id"]: r for r in run.file_rules(run.PROFILES / "v3", "m", facts=True)}

        self.assertEqual(rules["typescript"]["promptKey"], "file-body-oop")
        self.assertEqual(rules["typescript"]["condition"], {"extensions": [".ts", ".mts", ".cts"]})
        self.assertNotIn("factsKey", rules["typescript"])
        self.assertTrue({".ts", ".mts", ".cts"} <= set(run.EXTENSIONS))

    def test_aProfileWithoutAKindsPromptOrFacts_hasNoRuleForIt(self):
        with tempfile.TemporaryDirectory() as tmp:
            rules = {r["id"] for r in run.file_rules(Path(tmp), "m", facts=True)}

        self.assertEqual(rules, {"secrets", "java", "sql", "fallback"})

    def test_secretsRule_skipsCredentialFilesBeforeEveryOtherRule(self):
        rules = run.file_rules(run.PROFILES / "v3", "m", facts=True, oversize="mapReduce")
        secrets = rules[0]
        names = self.file_names(secrets["condition"])
        selftest = (run.REPO / "srcmorph-maven-plugin" / "srcmorph-selftest.yaml").read_text(encoding="utf-8")

        self.assertEqual((secrets["id"], secrets["skip"], secrets["priority"]), ("secrets", True, 1000))
        # a skip rule names no prompt, model or strategy
        self.assertEqual(set(secrets), {"id", "skip", "priority", "condition"})
        for secret in (".env", "prod.tfvars", "prod.pkrvars.hcl", "secrets.hcl", "client_secret.json",
                       "settings.xml", "vault.yml", "db-credentials.yaml"):
            self.assertTrue(any(run.name_glob(name).fullmatch(secret) for name in names), secret)
        # the self-test's rule names the same files, and skips the same properties files by their content
        for name in names:
            self.assertIn(f'"{name}"', selftest, name)
        properties = [c for c in secrets["condition"]["or"]["conditions"]
                      if c.get("and", {}).get("conditions", [{}])[0] == {"extensions": [".properties"]}]
        self.assertEqual(len(properties), 1)
        pattern = properties[0]["and"]["conditions"][1]["contentMatches"]
        self.assertIn(f"contentMatches: '{pattern}'", selftest)
        self.assertTrue(re.search(pattern, "a=1\nsigning.password = x\n"))
        self.assertFalse(re.search(pattern, "spring.datasource.password=${DB_PASSWORD}\n# token=x\n"))
        for text in ("aws.secretKey=AKIAX\n", "app.secret-key=abc\n", "aws.secret-access-key=x\n",
                     "jwt.secret.key=x\n", "mail.smtp.password hunter2\n"):
            self.assertTrue(re.search(pattern, text), text)
        for text in ("password.label=Password\n", "token.expiry 3600\n", "password=\n"):
            self.assertFalse(re.search(pattern, text), text)
        # and a Kubernetes Secret by its kind, whatever its name, but not a Helm chart's template
        kinds = [c for c in secrets["condition"]["or"]["conditions"]
                 if c.get("and", {}).get("conditions", [{}])[0] == {"extensions": run.YAML_EXTENSIONS}]
        self.assertEqual(len(kinds), 1)
        kind, helm = kinds[0]["and"]["conditions"][1:]
        self.assertEqual(kind, {"contentMatches": run.K8S_SECRET})
        self.assertEqual(helm, {"not": run.HELM_TEMPLATE})
        self.assertIn("contentMatches: '" + run.K8S_SECRET.replace("'", "''") + "'", selftest)
        for manifest in ("apiVersion: v1\nkind: Secret\ndata:\n  password: eA==\n", 'kind: "Secret"\n'):
            self.assertTrue(re.search(run.K8S_SECRET, manifest), manifest)
        for other in ("kind: SecretProviderClass\n", "kind: Deployment\n", "title: x\n  kind: Secret\n"):
            self.assertFalse(re.search(run.K8S_SECRET, other), other)
        # the hashes of the files the rules name pass the skip rule by
        self.assertIn("file-body-oop.md", run.profile_hashes(run.PROFILES / "v3", rules))

    def test_secretsRule_isTheSelfTestsSecretsRule(self):
        import yaml
        selftest = yaml.safe_load((run.REPO / "srcmorph-maven-plugin" / "srcmorph-selftest.yaml")
                                  .read_text(encoding="utf-8"))
        theirs = next(r for r in selftest["srcMorph"]["rules"] if r["id"] == "secrets")

        self.assertEqual(run.SECRETS_RULE, theirs)

    def test_skipped_readsTheSecretsRuleAsSrcmorphDoes(self):
        secret = "apiVersion: v1\nkind: Secret\ndata:\n  password: eA==\n"
        for rel, text in ((".env", "TOKEN=x\n"), ("other/deploy/.env.production", ""),
                          ("other/prod.TFVARS", ""), ("other/settings.xml", "<settings>\n"),
                          ("other/gradle.properties", "signing.password=x\n"), ("other/deploy/postgres.yaml", secret),
                          ("other/k8s/db-credentials.yaml", "password: x\n"),
                          ("other/charts/shop/templates/db-secret.yaml", secret)):
            with self.subTest(rel=rel):
                self.assertTrue(run.skipped(rel, text))
        for rel, text in (("other/app/src/main/res/xml/settings.xml", "<PreferenceScreen/>\n"),
                          ("other/application.properties", "spring.datasource.password=${DB_PASSWORD}\n"),
                          ("other/charts/shop/templates/postgres.yaml", secret + "  user: {{ .Values.user }}\n"),
                          ("other/.github/workflows/secret-scan.yml", "on: push\n"),
                          ("other/k8s/vault-deployment.yaml", "kind: Deployment\n"),
                          ("other/deploy/provider.yaml", "kind: SecretProviderClass\n"),
                          ("java/SecretManager.java", "class SecretManager {}\n")):
            with self.subTest(rel=rel):
                self.assertFalse(run.skipped(rel, text))
        # contentMatches reads the head only, as srcmorph does
        self.assertFalse(run.skipped("other/late.yaml", "x" * run.CONTENT_HEAD_CHARS + "\nkind: Secret\n"))

    def test_pathGlob_asSrcmorphCompilesIt(self):
        self.assertTrue(run.path_glob("**/templates/**").fullmatch("templates/a.yaml"))
        self.assertTrue(run.path_glob("**/templates/**").fullmatch("charts/x/templates/a.yaml"))
        self.assertFalse(run.path_glob("**/templates/**").fullmatch("charts/mytemplates/a.yaml"))
        self.assertTrue(run.path_glob("**/.github/{workflows,actions}/**/*.{yml,yaml}").fullmatch(
            ".github/workflows/ci.yml"))
        self.assertFalse(run.path_glob("**/*pom.xml").fullmatch("a/b/POM.xml"))
        with self.assertRaises(ValueError):
            run.condition_matches({"size": {"min": 1}}, "a.java", "")

    def test_gradleKotlinScripts_takeTheGradleRuleOverKotlin(self):
        rules = {r["id"]: r for r in run.file_rules(run.PROFILES / "v3", "m", facts=True)}

        self.assertIn("*.gradle.kts", self.file_names(rules["gradle"]["condition"]))
        self.assertGreater(rules["gradle"]["priority"], rules["kotlin"].get("priority", 0))
        self.assertTrue(run.indexes("build.gradle.kts"))

    def test_everyRuleOfEveryPhase_asksOnceMoreOnAnIncompleteAnswer(self):
        definition = {"key": "m", "provider": "mock"}
        for phase, section in (("GenerateFileIndex", None), ("AggregatePackages", "packageRules"),
                               ("AggregateProject", "projectRules")):
            config = run.phase_config(phase, Path("w"), Path("o"), run.PROFILES / "v3", definition, facts=True)
            rules = config["srcMorph"]["rules"] + (config[section] if section else [])
            self.assertTrue(rules)
            self.assertEqual({r["onIncomplete"] for r in rules if not r.get("skip")}, {"retryOnce"})

    def test_aPhaseConfig_indexesFilesWithoutAnExtensionByName(self):
        definition = {"key": "m", "provider": "mock"}
        config = run.phase_config("GenerateFileIndex", Path("w"), Path("o"), run.PROFILES / "v3", definition)

        self.assertEqual(config["srcMorph"]["fileNames"], ["Dockerfile", "Dockerfile.*", "Containerfile", "package.json",
                                                           "pyproject.toml", "openapi*.json", "swagger*.json",
                                                           "asyncapi*.json"])

    def test_theKindsOfTheLunaExample_routeAsTheExampleRoutesThem(self):
        import yaml
        luna = yaml.safe_load((run.REPO / "examples" / "copilot-api-gateway" / "srcmorph-luna.yaml")
                              .read_text(encoding="utf-8"))
        theirs = {r["id"]: r for r in luna["srcMorph"]["rules"]}
        ours = {r["id"]: r for r in run.file_rules(run.PROFILES / "v3", "m", facts=True)}

        for kind in ("persistence", "integration", "wsdl", "facelets", "openapi", "asyncapi", "avro", "graphql",
                     "camel-yaml", "compose", "ansible", "gradle", "properties", "powershell", "batch", "pyproject", "kotlin",
                     "groovy", "scala", "java-ee"):
            for field in ("condition", "promptKey", "factsKey"):
                self.assertEqual(ours[kind].get(field), theirs[kind].get(field), f"{kind} {field}")
            self.assertEqual(ours[kind].get("priority", 0), theirs[kind].get("priority", 0), kind)
            for extension in self.extensions(theirs[kind]["condition"]):
                self.assertIn(extension, run.EXTENSIONS, kind)

    @staticmethod
    def extensions(condition):
        """Every extensions entry of a condition, through its and / or groups."""
        found = list(condition.get("extensions", []))
        for group in ("and", "or"):
            for inner in condition.get(group, {}).get("conditions", []):
                found += RulesTest.extensions(inner)
        return found

    @staticmethod
    def file_names(condition):
        """Every fileNames entry of a condition, through its and / or groups."""
        names = list(condition.get("fileNames", []))
        for group in ("and", "or"):
            for inner in condition.get(group, {}).get("conditions", []):
                names += RulesTest.file_names(inner)
        return names

    def test_fileNames_ofAnOrGroup_areFound(self):
        self.assertEqual(self.file_names(run._or({"extensions": [".bpmn"]}, {"fileNames": ["*.bpmn20.xml"]})),
                         ["*.bpmn20.xml"])

    def test_everyFileNameRule_namesAFileTheRunIndexes(self):
        # a rule on a file name the walk never collects never fires (package.json had no extension
        # of the list and no name either, so its npm rule routed nothing; Dockerfile.* had no glob)
        for name, condition, _, _ in run.LANGUAGES:
            for file_name in self.file_names(condition):
                # a name the glob matches: a wildcard as "x", a choice as its first option
                sample = re.sub(r"\{([^,}]*)[^}]*\}", r"\1", file_name).replace("*", "x").replace("?", "x")
                with self.subTest(rule=name, fileName=file_name):
                    self.assertTrue(run.name_glob(file_name).fullmatch(sample), sample)
                    self.assertTrue(run.indexes(sample), sample)

    def test_indexes_byExtensionOrNameGlob_inAnyCase(self):
        for indexed in ("A.java", "A.JAVA", "schema.Sql", "Dockerfile", "dockerfile", "Dockerfile.prod",
                        "package.json", "Containerfile"):
            self.assertTrue(run.indexes(indexed), indexed)
        for other in ("notes.txt", "Dockerfile2", "xpackage.json", "java"):
            self.assertFalse(run.indexes(other), other)
        self.assertTrue(run.name_glob("docker-compose*.{yml,yaml}").fullmatch("docker-compose.prod.YAML"))

    def test_theK8sRule_readsAKindQuotedOrNot_asItsFactsDo(self):
        k8s = next(c for n, c, _, _ in run.LANGUAGES if n == "k8s")
        pattern = re.compile(next(c["contentMatches"] for c in k8s["and"]["conditions"] if "contentMatches" in c))

        for manifest in ("kind: Deployment\n", 'kind: "Deployment"\n', "kind: 'Service'\n"):
            self.assertTrue(pattern.search("apiVersion: v1\n" + manifest), manifest)
        self.assertFalse(pattern.search("kind: deployment\n  kind: Pod\n"))

    def test_chunkPrompts_onlyThoseTheProfileHas(self):
        self.assertEqual(run.chunk_prompts(Path("no-such-profile")), {})

    def test_profileHashes_ofTheFilesTheRulesName(self):
        profile = run.PROFILES / "v3"
        hashes = run.profile_hashes(profile, run.file_rules(profile, "m", facts=True))

        self.assertIn("file-body-oop.md", hashes)
        self.assertIn("sql-facts.tsv", hashes)
        self.assertIn("package-body.md", hashes)
        self.assertIn("map-chunk.md", hashes)
        self.assertIn("reduce-parts.md", hashes)
        self.assertEqual(hashes["file-body-oop.md"], run.sha256_of(profile / "file-body-oop.md"))


class CostTest(unittest.TestCase):

    def test_cachedPromptTokens_atTheCacheReadPrice(self):
        totals = {"promptTokens": 1000, "cachedPromptTokens": 600, "completionTokens": 100}

        self.assertEqual(run.token_cost(totals, (1e-6, 2e-6, 1e-7)), round(400e-6 + 600e-7 + 200e-6, 6))
        self.assertEqual(run.token_cost({"promptTokens": 10, "completionTokens": 0}, (1e-3, 0, 0)), 0.01)


class ScrubTest(unittest.TestCase):

    def test_theRunDirectory_inEitherSlashForm_becomesAPlaceholder(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "run1"
            lines = [f"failed: {root / 'work' / 'A.java'}", "failed: " + str(root).replace("\\", "/") + "/work/B.java",
                     "no path here"]

            out = run.scrubbed(lines, root)

        self.assertEqual(out[0].replace("\\", "/"), "failed: <run>/work/A.java")
        self.assertEqual(out[1], "failed: <run>/work/B.java")
        self.assertEqual(out[2], "no path here")


class WorkTest(unittest.TestCase):

    def test_aWorkDirectoryRunPyDidNotCreate_isRefused(self):
        with tempfile.TemporaryDirectory() as tmp:
            work = Path(tmp) / "work"
            (work / "keep").mkdir(parents=True)

            with self.assertRaises(SystemExit):
                run.prepare_work(work, run.JAVA_COMMIT, corpus=str(Path(tmp) / "corpus"))
            self.assertTrue((work / "keep").is_dir())

    def test_anOutputDirectoryOfSummaries_isDeleted(self):
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "out" / "m"
            (out / "java").mkdir(parents=True)
            (out / "java" / "A.java.ai.md").write_text("summary", encoding="utf-8")
            (out / "package.ai.md").write_text("package", encoding="utf-8")

            run.clear_out(out)

            self.assertFalse(out.exists())

    def test_anOutputDirectoryHoldingOtherFiles_isRefused(self):
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "src"
            (out / "main").mkdir(parents=True)
            (out / "main" / "A.java").write_text("class A {}", encoding="utf-8")
            (out / "A.java.ai.md").write_text("summary", encoding="utf-8")

            with self.assertRaises(SystemExit):
                run.clear_out(out)
            self.assertTrue((out / "main" / "A.java").is_file())

    def test_itsOwnWorkDirectory_isReplaced(self):
        with tempfile.TemporaryDirectory() as tmp:
            corpus, work = Path(tmp) / "corpus", Path(tmp) / "work"
            (corpus / "other").mkdir(parents=True)
            (corpus / "other" / "a.sh").write_text("echo", encoding="utf-8")

            run.prepare_work(work, run.JAVA_COMMIT, corpus=str(corpus))
            (work / "stale").write_text("", encoding="utf-8")
            run.prepare_work(work, run.JAVA_COMMIT, corpus=str(corpus))

            self.assertEqual(sorted(p.relative_to(work).as_posix() for p in work.rglob("*") if p.is_file()),
                             ["other/a.sh"])


class SearchTest(unittest.TestCase):

    def test_bm25_ranksOnlyFilesSharingAWord(self):
        ranked = search.bm25_rank({"b/x.java": "", "a/y.java": "checkout audit", "c/z.sql": ""}, "audit trail")

        self.assertEqual(ranked, ["a/y.java"])
        self.assertEqual(search.bm25_rank({"a": "", "b": ""}, "anything"), [])

    def test_theFiles_areTheSourcesTheRunsIndex_andASummaryIsTheModelsText(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "other").mkdir(parents=True)
            for name in ("Dockerfile", "a.sh", "notes.txt"):
                (work / "other" / name).write_text("x", encoding="utf-8")
            (out / "m" / "other").mkdir(parents=True)
            (out / "m" / "other" / "a.sh.ai.md").write_text(
                "h: 1\n---\n**Facts (exact, whole file):** functions: 3\n\n> Rotates the backups.\n", encoding="utf-8")

            files = search.corpus_files(work)
            docs = search.documents("m", out, work, files)

        self.assertEqual(files, ["other/Dockerfile", "other/a.sh"])
        self.assertEqual(docs, {"other/Dockerfile": "", "other/a.sh": "> Rotates the backups."})

    def test_theFiles_leaveOutWhatTheSecretsRuleSkips_andASourceIsReadAsSrcmorphReadsIt(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "other" / "deploy").mkdir(parents=True)
            (work / "other" / "deploy" / "postgres.yaml").write_text("kind: Secret\ndata: {}\n", encoding="utf-8")
            (work / "other" / ".env").write_text("TOKEN=x\n", encoding="utf-8")
            (work / "other" / "deploy" / "app.yaml").write_text("kind: Deployment\n", encoding="utf-8")
            # Latin-1, no UTF-8: srcmorph reads it with a replacement character, as score.source_text does
            (work / "other" / "a.sh").write_bytes("echo Größe\n".encode("latin-1"))

            files = search.corpus_files(work)
            docs = search.documents("source", out, work, files)
            rows = score.score_model(out / "m", work, {})

        self.assertEqual(files, ["other/a.sh", "other/deploy/app.yaml"])
        self.assertEqual(docs["other/a.sh"], "echo Gr��e\n")
        # a skipped file is no missing summary
        self.assertEqual(sorted(r["file"] for r in rows if r.get("missing") and r["kind"] == "other"),
                         ["other/a.sh", "other/deploy/app.yaml"])

    def test_aSummary_isTheAnswerAfterALeadingReasoningBlock_asScorePyReadsIt(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "other").mkdir(parents=True)
            (work / "other" / "a.sh").write_text("x", encoding="utf-8")
            (out / "m" / "other").mkdir(parents=True)
            (out / "m" / "other" / "a.sh.ai.md").write_text(
                "h: 1\n---\n<think>about the audit trail</think>\n> Rotates the backups.\n", encoding="utf-8")

            docs = search.documents("m", out, work, search.corpus_files(work))

        self.assertEqual(docs, {"other/a.sh": "> Rotates the backups."})

    def test_readerRank_dropsALeadingDotSlashOnly(self):
        files = [".github/workflows/ci.yml", "other/a.sh"]

        self.assertEqual(search.reader_rank('{"files": [".github/workflows/ci.yml", "./other/a.sh", "../other/a.sh"]}',
                                            files), files)
        self.assertEqual(search.reader_rank("first .github/workflows/ci.yml, then ./other/a.sh", files), files)

    def test_cacheKey_changesWithTheSystemPrompt(self):
        key = search.cache_key("m", "ctx", "q")
        with mock.patch.object(search, "SYSTEM", search.SYSTEM + " Be brief."):
            self.assertNotEqual(search.cache_key("m", "ctx", "q"), key)
        self.assertEqual(search.cache_key("m", "ctx", "q"), key)

    def test_backoff_retriesARateLimit_failsABadRequestAtOnce(self):
        def http(code):
            return urllib.error.HTTPError("u", code, "x", {}, io.BytesIO(b""))

        answer = mock.MagicMock()
        answer.__enter__.return_value = io.StringIO('{"choices": []}')
        sleeps = []
        with mock.patch.object(search.urllib.request, "urlopen", side_effect=[http(429), http(503), answer]):
            self.assertEqual(search.post_with_backoff(object(), sleeps.append), {"choices": []})
        self.assertEqual(sleeps, [2, 4])

        with mock.patch.object(search.urllib.request, "urlopen", side_effect=[http(400)]):
            with self.assertRaises(urllib.error.HTTPError):
                search.post_with_backoff(object(), sleeps.append)

        error = mock.MagicMock()
        error.__enter__.return_value = io.StringIO('{"error": {"message": "no"}}')
        with mock.patch.object(search.urllib.request, "urlopen", side_effect=[error]):
            with self.assertRaises(IOError):
                search.post_with_backoff(object(), sleeps.append)

    def test_backoff_retriesANonJsonOrTruncatedAnswer(self):
        def answer(text):
            response = mock.MagicMock()
            response.__enter__.return_value = io.StringIO(text)
            return response

        truncated = mock.MagicMock()
        truncated.__enter__.return_value.read.side_effect = http.client.IncompleteRead(b'{"choi')
        sleeps = []
        with mock.patch.object(search.urllib.request, "urlopen",
                               side_effect=[answer("<html>Bad gateway</html>"), truncated, answer('{"choices": ['),
                                            answer('{"choices": []}')]):
            self.assertEqual(search.post_with_backoff(object(), sleeps.append), {"choices": []})
        self.assertEqual(sleeps, [2, 4, 8])

        with mock.patch.object(search.urllib.request, "urlopen",
                               side_effect=[answer("not json") for _ in range(search.RETRIES)]):
            with self.assertRaises(ValueError):
                search.post_with_backoff(object(), sleeps.append)

    @staticmethod
    def reply(content, usage=None):
        return {"choices": [{"message": {"content": content}}], "usage": usage}

    @staticmethod
    def ask(cache, replies):
        """A reader's answer to one question over the given cache, its requests and its cost."""
        with mock.patch.dict(os.environ, {"OPENROUTER_API_KEY": "k"}), \
                mock.patch.object(search, "post_with_backoff", side_effect=replies) as post:
            reader = search.Reader("m", cache)
            return reader.ask("ctx", "q"), post.call_count, reader.cost

    def test_ask_anEmptyReply_isAskedAgain_andNeverCached(self):
        with tempfile.TemporaryDirectory() as tmp:
            cache = Path(tmp) / "cache.jsonl"

            answer, calls, _ = self.ask(cache, [self.reply(""), self.reply('{"files": ["a"]}')])
            cached, cached_calls, _ = self.ask(cache, [])
            empty, empty_calls, _ = self.ask(Path(tmp) / "empty.jsonl", [self.reply("  ")] * search.RETRIES)

            self.assertEqual((answer, calls), ('{"files": ["a"]}', 2))
            self.assertEqual((cached, cached_calls), ('{"files": ["a"]}', 0))
            self.assertEqual((empty, empty_calls), ("", search.RETRIES))
            self.assertFalse((Path(tmp) / "empty.jsonl").exists())

    def test_ask_anEmptyAnswerAnOlderRunCached_isAskedAgain(self):
        with tempfile.TemporaryDirectory() as tmp:
            cache = Path(tmp) / "cache.jsonl"
            cache.write_text(json.dumps({"id": search.cache_key("m", "ctx", "q"), "answer": ""}) + "\n",
                             encoding="utf-8")

            answer, calls, _ = self.ask(cache, [self.reply("x")])

        self.assertEqual((answer, calls), ("x", 1))

    def test_ask_aNullUsageOrCost_stillCachesThePaidAnswer(self):
        with tempfile.TemporaryDirectory() as tmp:
            cache = Path(tmp) / "cache.jsonl"

            answer, _, cost = self.ask(cache, [self.reply("x", usage=None)])
            cached = self.ask(cache, [])[:2]
            priced, _, priced_cost = self.ask(Path(tmp) / "priced.jsonl", [self.reply("y", {"cost": 0.25})])
            unpriced, _, unpriced_cost = self.ask(Path(tmp) / "unpriced.jsonl", [self.reply("z", {"cost": None})])

        self.assertEqual((answer, cost), ("x", 0.0))
        self.assertEqual(cached, ("x", 0))
        self.assertEqual((priced, priced_cost), ("y", 0.25))
        self.assertEqual((unpriced, unpriced_cost), ("z", 0.0))

    def test_theCardsScores_withoutTheReaderOrWithAnotherOut_isRefused(self):
        with tempfile.TemporaryDirectory() as tmp:
            for extra in (["--no-reader"], ["--out", tmp], ["--scores", str(HERE / "scores"), "--no-reader"]):
                with self.subTest(extra=extra):
                    result = subprocess.run([sys.executable, str(HERE / "search.py"), "--work", tmp] + extra,
                                            capture_output=True, text=True, cwd=tmp)

                    self.assertEqual(result.returncode, 2, result.stderr)
                    self.assertIn("--scores", result.stderr)

    def test_theCardsRun_orAnotherScoresDirectory_isNotRefused(self):
        parser = argparse.ArgumentParser()
        for out, scores, no_reader in ((search.DEFAULT_OUT, search.DEFAULT_SCORES, False),
                                       (search.DEFAULT_OUT, "elsewhere", True),
                                       ("other", "elsewhere", False)):
            with self.subTest(out=out, scores=scores, no_reader=no_reader):
                search.refuse_replacing_the_cards_scores(
                    parser, argparse.Namespace(out=str(out), scores=str(scores), no_reader=no_reader))


class LargeTest(unittest.TestCase):

    def test_factsBesideTheCorpus_namesInTheMiddle(self):
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "large"
            subprocess.run([sys.executable, "-I", str(HERE / "large.py"), "--out", str(out), "--scale", "0.05"],
                           check=True, capture_output=True)
            facts = json.loads((Path(tmp) / "large-facts.json").read_text(encoding="utf-8"))

            self.assertFalse(any(p.suffix == ".json" for p in out.rglob("*")))
            for rel, fact in facts.items():
                if rel.startswith("_"):
                    continue
                text = (out / rel).read_text(encoding="utf-8")
                for name in fact["middle"]:
                    position = text.index(name.split(":")[-1]) / len(text)
                    self.assertTrue(0.2 < position < 0.8, (rel, name, position))
                    self.assertIn(name, fact["ids"])

    def test_aScaleOfZero_isRefused(self):
        result = subprocess.run([sys.executable, "-I", str(HERE / "large.py"), "--out", "unused", "--scale", "0"],
                                capture_output=True, text=True)

        self.assertNotEqual(result.returncode, 0)
        self.assertIn("--scale must be above 0", result.stderr)

    def test_theMeasuredCorpus_isTheSameBytes(self):
        # the large corpus of the 2026-11 runs (--scale 1.0, Python 3.12): a Python whose random draws
        # differently writes another corpus, and the runs measured on it are not comparable any more
        golden = {"sql/ledger/ledger_dump.sql": "a805309ebab7bf2f91d4d18a700fae646099c465a59e1bb6abf7cb90888fe04d",
                  "other/catalog/catalog.xml": "88b5017de0f759537aaba872011e668aece0d3e3e72f3d8c7fa04e66479446a5",
                  "java/errors/PlatformErrorCode.java":
                      "0f950b74d1372eae4b6971f86e7e77879b95c0ae66eea905b9a533a66e233ed6"}
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "large"
            subprocess.run([sys.executable, "-I", str(HERE / "large.py"), "--out", str(out), "--scale", "1.0"],
                           check=True, capture_output=True)

            self.assertEqual({rel: run.sha256_of(out / rel) for rel in golden}, golden)


class MetaTest(unittest.TestCase):

    def test_treeHash_readsPathsAndBytesInPathOrder(self):
        with tempfile.TemporaryDirectory() as tmp:
            a, b = Path(tmp) / "a", Path(tmp) / "b"
            for root, order in ((a, ("x/1.sh", "y.sql")), (b, ("y.sql", "x/1.sh"))):
                for rel in order:
                    (root / rel).parent.mkdir(parents=True, exist_ok=True)
                    (root / rel).write_text(rel, encoding="utf-8")
            same = run.tree_hash(a) == run.tree_hash(b)
            (b / "y.sql").write_text("changed", encoding="utf-8")
            changed = run.tree_hash(a) != run.tree_hash(b)
            (b / "y.sql").rename(b / "z.sql")
            (b / "z.sql").write_text("y.sql", encoding="utf-8")
            renamed = run.tree_hash(a) != run.tree_hash(b)

        self.assertTrue(same)
        self.assertTrue(changed)
        self.assertTrue(renamed)

    def test_srcmorphCommit_isTheCheckoutsHead(self):
        head = subprocess.run(["git", "-C", str(run.REPO), "rev-parse", "HEAD"], capture_output=True, text=True)

        self.assertTrue(run.srcmorph_commit().startswith(head.stdout.strip()))



class CutOffTest(unittest.TestCase):

    def log(self, text):
        path = Path(tempfile.mkdtemp()) / "phase.log"
        path.write_text(text, encoding="utf-8")
        return path

    def test_anAnswerNotWritten_andOneKept_areCutOff_aKeptPartIsNot(self):
        work, out = Path("W"), Path("O")
        log = self.log(
            "WARN AiFieldGenerationSupport - Incomplete answer for file '" + str(work / "sql" / "a.sql")
            + "' (field 'file-body-sql', TRUNCATED): The answer for x was cut off at the 4096-token output"
            " budget; not written, left for the next run\n"
            "WARN AiFieldGenerationSupport - Incomplete answer for " + str(work / "java" / "B.java")
            + ": The answer for x was cut off at the 4096-token output budget; kept as it is (onIncomplete: keep)\n"
            "WARN AiFieldGenerationSupport - Incomplete answer for a part of " + str(work / "other" / "c.xml")
            + ": The answer for x was cut off at the 4096-token output budget; kept, the reduce reads it\n"
            "WARN AiFieldGenerationSupport - Incomplete answer for file '" + str(work / "sql" / "e.sql")
            + "' (field 'file-body-sql', EMPTY): The model answered nothing\n")

        self.assertEqual(run.cut_off_files(log, work, out), ["java/B.java", "sql/a.sql"])

    def test_aPackageCall_isItsPackageSummary_asScorePyNamesIt(self):
        # srcmorph's PackageIndexer names a package call by its path below the output root, the root package by
        # the output directory's name; a file call by its path below the corpus, the project by its file
        work, out = Path("W"), Path("O") / "model-a"
        cut = (" (field '{}', TRUNCATED): The answer for x was cut off at the 4096-token output budget; not written,"
               " left for the next run\n")
        packages = self.log(
            "WARN SummaryGenerator - Incomplete answer for package '" + str(Path("java") / "config") + "'"
            + cut.format("package-body")
            + "WARN SummaryGenerator - Incomplete answer for package 'model-a'" + cut.format("package-body")
            + "WARN SummaryGenerator - Incomplete answer for " + str(Path("sql") / "library")
            + ": The answer for x was cut off at the 4096-token output budget; kept as it is (onIncomplete: keep)\n")
        files = self.log(
            "WARN SummaryGenerator - Incomplete answer for file '" + str(Path("java") / "config" / "B.java") + "'"
            + cut.format("file-body-oop")
            + "WARN SummaryGenerator - Incomplete answer for " + str(Path("other") / "Dockerfile")
            + ": The answer for x was cut off at the 4096-token output budget; kept as it is (onIncomplete: keep)\n")
        project = self.log("WARN SummaryGenerator - Incomplete answer for project '" + str(out / "project.ai.md") + "'"
                           + cut.format("project-body"))

        self.assertEqual(run.cut_off_files(packages, work, out, "AggregatePackages"),
                         ["java/config/package.ai.md", "package.ai.md", "sql/library/package.ai.md"])
        self.assertEqual(run.cut_off_files(files, work, out, "GenerateFileIndex"), ["java/config/B.java", "other/Dockerfile"])
        self.assertEqual(run.cut_off_files(project, work, out, "AggregateProject"), ["project.ai.md"])
        # the names score.py expects the aggregates under, so a cut-off package row is found
        self.assertTrue({"java/config/package.ai.md", "package.ai.md", "sql/library/package.ai.md", "project.ai.md"}
                        <= set(score.expected_aggregates(["java/config/B.java", "sql/library/a.sql"])))

    def test_anOlderLogsAbsolutePackageSummary_readsAsItIs(self):
        work, out = Path("W"), Path("O") / "model-a"
        log = self.log("WARN AiFieldGenerationSupport - Incomplete answer for package '"
                       + str(out / "java" / "package.ai.md") + "' (field 'package-body', TRUNCATED): cut\n"
                       "WARN AiFieldGenerationSupport - Incomplete answer for package '" + str(out / "package.ai.md")
                       + "' (field 'package-body', TRUNCATED): cut\n")

        self.assertEqual(run.cut_off_files(log, work, out, "AggregatePackages"), ["java/package.ai.md", "package.ai.md"])

    def test_filesNotWritten_isTheOnlyFailureTheNextPhasesRunAfter(self):
        self.assertTrue(run.files_not_written(self.log("SrcMorphException: 2 file(s) were not written (an incomplete")))
        self.assertFalse(run.files_not_written(self.log("SrcMorphException: Invalid rules: two fallbacks")))
        packages = self.log("SrcMorphException: 1 package(s) were not written (an incomplete answer")
        self.assertTrue(run.files_not_written(packages, "AggregatePackages"))
        # each phase's own message: a package message ends no file phase, and the project phase has no next one
        self.assertFalse(run.files_not_written(packages, "GenerateFileIndex"))
        self.assertFalse(run.files_not_written(packages, "AggregateProject"))


class RunModelTest(unittest.TestCase):
    """run_model's phases against a stand-in for the CLI: no Java, no model."""

    def run_phases(self, failures, model=None, envs=None, record=None):
        """The phases run_model starts when the CLI fails the given phases with the given log text (for a
        local model unless one is given; the CLI's environment of every phase goes to envs; the file phase
        writes record as its run record when given). The meta file as written is self.written."""
        started = []
        self.written = None

        def fake_run(command, stdout=None, **kwargs):
            if command[-1].endswith(".json") and "-cp" in command:
                phase = Path(command[-1]).stem[len("config_"):]
                started.append(phase)
                if envs is not None:
                    envs.append(kwargs.get("env"))
                config = json.loads(Path(command[-1]).read_text(encoding="utf-8"))["srcMorph"]
                if record is not None and "runRecordFile" in config:
                    Path(config["runRecordFile"]).write_text(json.dumps(record), encoding="utf-8")
                text = failures.get(phase)
                stdout.write(text or "done\n")
                return subprocess.CompletedProcess(command, 1 if text else 0)
            return subprocess.CompletedProcess(command, 1, stdout="", stderr="")

        with tempfile.TemporaryDirectory() as tmp:
            tmp = Path(tmp)
            (tmp / "work" / "java").mkdir(parents=True)
            (tmp / "cp.txt").write_text("x.jar", encoding="utf-8")
            options = argparse.Namespace(
                model_dir=str(tmp), seed=None, chars_per_token=None, facts=False, oversize="", max_chunks=None,
                java_prompt=run.JAVA_PROMPT, out=str(tmp / "out"), parts="", work=str(tmp / "work"),
                cp=str(tmp / "cp.txt"), prepend="", java="java", corpus="", commit=run.JAVA_COMMIT,
                profile="v3", meta=str(tmp / "meta"))
            model = model or next(m for m in MODELS["models"] if m["backend"] == "local")
            try:
                with mock.patch.object(run.subprocess, "run", side_effect=fake_run), \
                        mock.patch("sys.stdout", new_callable=io.StringIO):
                    meta = run.run_model(options, MODELS, run.PROFILES / "v3", model)
            finally:
                written = tmp / "meta" / f"{model['key']}.json"
                if written.exists():
                    self.written = json.loads(written.read_text(encoding="utf-8"))
        return started, {phase: p["exit"] for phase, p in meta["phases"].items()}

    OPENROUTER = next(m for m in MODELS["models"] if m["backend"] == "openrouter")
    RECORD = {"totals": {"promptTokens": 1000, "cachedPromptTokens": 0, "completionTokens": 100},
              "files": [{"file": "java/A.java", "calls": 1, "finishReason": "stop", "outcome": "written"}]}

    def test_aPaidRun_whoseCostLookupsFail_keepsItsMetaWithNullCosts(self):
        # no network: the price list times out, the usage after the run is refused
        with mock.patch.object(run, "openrouter_usage", side_effect=[2.5, urllib.error.URLError("refused")]), \
                mock.patch.object(run, "openrouter_price", side_effect=TimeoutError("timed out")):
            started, exits = self.run_phases({}, self.OPENROUTER, record=self.RECORD)

        self.assertEqual(started, list(run.PHASES))
        self.assertEqual(set(exits.values()), {0})
        self.assertIsNotNone(self.written)
        self.assertEqual((self.written["tokenCostUsd"], self.written["costUsd"]), (None, None))
        self.assertEqual(self.written["costErrors"], {
            "tokenCostUsd": "the model's prices: TimeoutError: timed out",
            "costUsd": "the usage after the run: URLError: <urlopen error refused>"})
        self.assertEqual(self.written["fileTokens"], self.RECORD["totals"])

    def test_aPaidRun_whoseUsageBeforeFails_stillRunsAndPricesItsTokens(self):
        with mock.patch.object(run, "openrouter_usage", side_effect=[OSError("no route")]) as usage, \
                mock.patch.object(run, "openrouter_price", return_value=(1e-6, 2e-6, 1e-7)):
            started, _ = self.run_phases({}, self.OPENROUTER, record=self.RECORD)

        self.assertEqual(started, list(run.PHASES))
        self.assertEqual(usage.call_count, 1)
        self.assertEqual(self.written["tokenCostUsd"], run.token_cost(self.RECORD["totals"], (1e-6, 2e-6, 1e-7)))
        self.assertIsNone(self.written["costUsd"])
        self.assertEqual(self.written["costErrors"], {"costUsd": "the usage before the run: OSError: no route"})

    def test_aPaidRun_writesItsMetaBeforeTheCostLookups(self):
        # whatever a lookup raises, the meta of the finished run is on disk already
        with mock.patch.object(run, "openrouter_usage", return_value=1.0), \
                mock.patch.object(run, "openrouter_price", side_effect=RuntimeError("unexpected")), \
                self.assertRaises(RuntimeError):
            self.run_phases({}, self.OPENROUTER, record=self.RECORD)

        self.assertIsNotNone(self.written)
        self.assertIn("finishedAt", self.written)
        self.assertNotIn("tokenCostUsd", self.written)

    def test_aPaidRun_whoseLookupsAnswer_recordsBothCosts(self):
        with mock.patch.object(run, "openrouter_usage", side_effect=[2.0, 2.25]), \
                mock.patch.object(run, "openrouter_price", return_value=(1e-6, 2e-6, 1e-7)):
            self.run_phases({}, self.OPENROUTER, record=self.RECORD)

        self.assertEqual((self.written["tokenCostUsd"], self.written["costUsd"]), (0.0012, 0.25))
        self.assertNotIn("costErrors", self.written)

    def test_absolutePaths_includeTheModelDirectory(self):
        options = argparse.Namespace(work="w", out="o", meta="m", parts="", model_dir="models")

        run.absolute_paths(options)
        model = next(m for m in MODELS["models"] if m["backend"] == "local")
        definition = run.ai_definition(MODELS, model, options.model_dir)

        self.assertEqual(options.model_dir, str(Path("models").resolve()))
        self.assertEqual(options.work, str(Path("w").resolve()))
        # an option not given stays empty
        self.assertEqual(options.parts, "")
        # so a local model's modelPath no longer resolves against the generated configuration's directory
        self.assertTrue(Path(definition["modelPath"]).is_absolute())

    def test_aPackagePhaseThatWroteAllButSomePackages_stillRunsTheProjectPhase(self):
        started, exits = self.run_phases(
            {"AggregatePackages": "SrcMorphException: 1 package(s) were not written (an incomplete answer"})

        self.assertEqual(started, list(run.PHASES))
        self.assertEqual(exits, {"GenerateFileIndex": 0, "AggregatePackages": 1, "AggregateProject": 0})

    def test_anyOtherFailure_endsTheModel(self):
        started, _ = self.run_phases({"AggregatePackages": "SrcMorphException: Invalid rules: two fallbacks"})
        after_files, _ = self.run_phases({"GenerateFileIndex": "SrcMorphException: 2 file(s) were not written"})

        self.assertEqual(started, ["GenerateFileIndex", "AggregatePackages"])
        self.assertEqual(after_files, list(run.PHASES))

    def test_theCli_getsOnlyTheApiKeyItsOwnModelNames(self):
        keys = {"OPENROUTER_API_KEY": "o", "HF_TOKEN": "h", "COPILOT_GATEWAY_API_KEY": "c"}
        copilot = next(m for m in MODELS["models"] if m["backend"] == "copilot")
        for model, expected in ((copilot, {"COPILOT_GATEWAY_API_KEY": "c"}), (None, {})):
            with self.subTest(model=model and model["key"]):
                envs = []
                with mock.patch.dict(os.environ, {**keys, "KEEP_ME": "1"}):
                    self.run_phases({}, model, envs)

                self.assertEqual(len(envs), len(run.PHASES))
                for env in envs:
                    self.assertEqual({k: v for k, v in env.items() if k in keys}, expected)
                    self.assertEqual(env.get("KEEP_ME"), "1")

    def test_theCliEnvironment_dropsEveryKeyVariableTheModelsName_butTheModelsOwn(self):
        models = {"common": {}, "backends": {"a": {"apiKeyEnv": "A_KEY"}, "b": {"apiKeyEnv": "B_KEY"}, "c": {}},
                  "models": [{"key": "x", "backend": "a", "settings": {"apiKeyEnv": "X_KEY"}}]}
        environ = {"A_KEY": "a", "B_KEY": "b", "X_KEY": "x", "OPENROUTER_API_KEY": "o", "HF_TOKEN": "h", "PATH": "p"}

        self.assertEqual(run.cli_environment(environ, models, {"apiKeyEnv": "B_KEY"}), {"B_KEY": "b", "PATH": "p"})
        self.assertEqual(run.cli_environment(environ, models, {"apiKeyEnv": "X_KEY"}), {"X_KEY": "x", "PATH": "p"})
        self.assertEqual(run.cli_environment(environ, models, {}), {"PATH": "p"})


class ReportTest(unittest.TestCase):

    def test_theInventedRate_weighsEveryBacktickedName(self):
        import report

        # 1 of 3 and 0 of 3000: one invented name of 3003, not the mean of 33.3 and 0
        rate = report.invented_per_100([{"ticked": 3, "invented": 1}, {"ticked": 3000, "invented": 0}])

        self.assertAlmostEqual(rate, 100 / 3003)
        self.assertIsNone(report.invented_per_100([{"ticked": 0, "invented": 0}]))

    def test_aSummaryWithoutTheTotals_isRefused(self):
        import report

        with self.assertRaises(SystemExit):
            report.invented_per_100([{"inventedPer100": 3.0}])

    def test_theGeneratedBlock_holdsARowPerModelAndTheirFigures(self):
        import report

        kind = {"files": 2, "compaction": 3.456, "recall": 0.9876, "numbers": "1/2", "ticked": 10, "invented": 1,
                "noLead": 1, "badHeadings": 2, "fences": 3, "leaks": 4, "cutOff": 1, "failed": {"empty": 1}}
        models = [{"key": "a", "name": "Model A", "backend": "openrouter", "origin": "june"},
                  {"key": "b", "name": "Model B", "backend": "local"},
                  {"key": "c", "name": "Model C", "backend": "copilot"}]
        summary = {"a": {"java": kind, "sql": kind, "other": kind}, "b": {"java": kind}}
        runs = {"a": {"backend": "openrouter", "files": 6, "costUsd": 0.12345,
                      "phases": {"GenerateFileIndex": {"exit": 0, "seconds": 120.0, "cutOff": ["sql/x.sql"]},
                                 "AggregatePackages": {"exit": 1, "seconds": 60.0}}},
                "b": {"backend": "local", "files": 2, "phases": {"GenerateFileIndex": {"exit": 0, "seconds": 30.0}}}}

        block = report.generated_block(models, summary, runs)

        self.assertTrue(block.startswith(report.BEGIN) and block.endswith(report.END))
        self.assertIn("| Model A ¹ | OpenRouter | 6 (failed: AggregatePackages) | 3.0 | 20.0 | 0.1235 | 1 (`sql/x.sql`) |",
                      block)
        self.assertIn("| Model B | local GGUF (RTX 3070) | 2 | 0.5 | 15.0 | 0 (local) | 0 |", block)
        self.assertIn("| Model C | Copilot API Gateway | not run | | | | |", block)
        self.assertIn("| Model A ¹ | 3.46 | 99% | 3.46 | 99% | 1/2 | 3.46 | 99% | 10.0 |", block)
        self.assertIn("| Model B | 3.46 | 99% | – | – | – | – | – | 10.0 |", block)
        self.assertIn("| Model A ¹ | 3 | 6 | 9 | 12 | 3 (empty 3) | 3 |", block)
        self.assertIn("### Format discipline (all 2/6 `.ai.md` per model)", block)

    def test_main_replacesOnlyTheGeneratedBlockOfTheCardItIsGiven(self):
        with tempfile.TemporaryDirectory() as tmp:
            card = Path(tmp) / "card.md"
            card.write_text("before\n<!-- generated:begin -->\nold\n<!-- generated:end -->\nafter\n", encoding="utf-8")

            result = subprocess.run([sys.executable, str(HERE / "report.py"), "--card", str(card)],
                                    capture_output=True, text=True)
            text = card.read_text(encoding="utf-8")

        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertTrue(text.startswith("before\n<!-- generated:begin -->\n### Run overview\n"))
        self.assertTrue(text.endswith("<!-- generated:end -->\nafter\n"))
        self.assertNotIn("\nold\n", text)


class RepeatTest(unittest.TestCase):

    @staticmethod
    def row(kind, found, expected, failed=False, lead=True):
        return {"kind": kind, "failed": failed, "idsFound": found, "idsExpected": expected, "lead": lead,
                "badHeadings": 0, "fences": 0, "leaks": 0, "recall": score.file_recall(failed, found, expected)}

    def test_quality_ofACorpusOfOtherFilesOnly_isRecallAndFormatHalfEach(self):
        rows = [self.row("other", 3, 4), self.row("other", 2, 2, lead=False)]

        result = repeat.quality(rows)

        self.assertEqual(result["recall"], {"other": 0.875})
        self.assertEqual(result["format"], 0.5)
        self.assertEqual(result["quality"], round((0.875 + 0.5) / 2, 4))

    def test_quality_aRunWithoutItsPackageAndProjectSummaries_holdsTheFormatOverAllOfThem(self):
        # one Java file in java/: a full run writes it, java/package.ai.md, package.ai.md and project.ai.md
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "java").mkdir(parents=True)
            (work / "java" / "A.java").write_text("public class A {}", encoding="utf-8")
            (out / "java").mkdir(parents=True)
            (out / "java" / "A.java.ai.md").write_text(
                "h: 1\n---\n> `A` is the one class of the tree. It holds no state and is read by nothing else here.\n",
                encoding="utf-8")

            result = repeat.quality(score.score_model(out, work, {}))

        self.assertEqual(result["format"], 0.25)
        self.assertEqual(result["recall"], {"java": 1.0})

    def test_runs_scoresEachRunWithoutTheCardsRun(self):
        with tempfile.TemporaryDirectory() as tmp:
            roots = []
            for name in ("run1", "run2"):
                (Path(tmp) / name / "out" / "m").mkdir(parents=True)
                roots.append(Path(tmp) / name)
            with mock.patch.object(repeat, "score_model", return_value=[self.row("other", 1, 1)]) as scored, \
                    mock.patch.object(sys, "stdout", io.StringIO()):
                result = repeat.repeat(roots, Path(tmp), {}, card=False)

        self.assertEqual([r["run"] for r in result["m"]["runs"]], ["run1", "run2"])
        self.assertEqual(scored.call_count, 2)

    def test_runs_withTheCardsScoresOrWithRepeat_isRefused(self):
        for extra in (["--repeat", "x"], ["--scores", str(HERE / "scores")]):
            with self.subTest(extra=extra):
                result = subprocess.run([sys.executable, str(HERE / "repeat.py"), "--work", ".", "--runs", "r"]
                                        + extra, capture_output=True, text=True)

                self.assertEqual(result.returncode, 2, result.stderr)
                self.assertIn("--runs", result.stderr)


class CorrelateTest(unittest.TestCase):

    def test_pearson_aLinearAndAnAntiLinearSeries_isOneAndMinusOne(self):
        self.assertAlmostEqual(correlate.pearson([1, 2, 3], [2, 4, 6]), 1.0)
        self.assertAlmostEqual(correlate.pearson([1, 2, 3], [6, 4, 2]), -1.0)

    def test_pearson_aSeriesThatDoesNotVary_isNone(self):
        self.assertIsNone(correlate.pearson([1, 2, 3], [5, 5, 5]))

    def test_pearson_anEmptySeries_isNone(self):
        self.assertIsNone(correlate.pearson([], []))
        self.assertIsNone(correlate.spearman([], []))

    def test_resultLine_anEmptyTableOrAConstantSeries_printsNotAvailable(self):
        empty = correlate.correlations([])
        constant = correlate.correlations([("a", 100, 0.5, 0.9), ("b", 200, 0.5, 0.8)])

        self.assertEqual(correlate.result_line(empty),
                         "0 runs: Pearson r n/a with hit@1, n/a with the MRR; Spearman n/a and n/a")
        self.assertEqual(correlate.result_line(constant),
                         "2 runs: Pearson r n/a with hit@1, -1.00 with the MRR; Spearman n/a and -1.00")

    def test_main_anExcludedEveryRun_printsNotAvailable(self):
        with tempfile.TemporaryDirectory() as tmp:
            (Path(tmp) / "summary.json").write_text(json.dumps({"a": {"java": {"bodyBytes": 1}}}), encoding="utf-8")
            (Path(tmp) / "search.json").write_text(json.dumps({"runs": {"a": {"reader": {"hit1": 1, "mrr": 1}}}}),
                                                   encoding="utf-8")
            result = subprocess.run([sys.executable, str(HERE / "correlate.py"), "--scores", tmp, "--exclude", "a"],
                                    capture_output=True, text=True)

        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("0 runs: Pearson r n/a", result.stdout)

    def test_ranks_ties_shareTheMeanOfTheirRanks(self):
        self.assertEqual(correlate.ranks([10, 30, 20, 30]), [1.0, 3.5, 2.0, 3.5])

    def test_spearman_aMonotoneButNotLinearSeries_isOne(self):
        self.assertAlmostEqual(correlate.spearman([1, 2, 3, 4], [1, 4, 9, 100]), 1.0)

    def test_rows_fileSummariesOnly_andTheExcludedAndUnsearchedRunsLeftOut(self):
        summary = {
            "a": {"java": {"bodyBytes": 100}, "sql": {"bodyBytes": 20}, "package": {"bodyBytes": 999}},
            "b": {"other": {"bodyBytes": 50}},
            "c": {"java": {"bodyBytes": 1}},
            "d": {"java": {"bodyBytes": 1}},
        }
        search = {name: {"reader": {"hit1": 0.5, "mrr": 0.6}} for name in ("a", "b", "c")}

        table = correlate.rows(summary, search, {"c"})

        self.assertEqual(table, [("a", 120, 0.5, 0.6), ("b", 50, 0.5, 0.6)])

    def test_correlations_theCardsScores_coverTheCardsThirtyTwoRuns(self):
        summary = json.loads((HERE / "scores" / "summary.json").read_text(encoding="utf-8"))
        search = json.loads((HERE / "scores" / "search.json").read_text(encoding="utf-8"))["runs"]

        result = correlate.correlations(correlate.rows(summary, search, set(correlate.CARD_EXCLUDED)))

        self.assertEqual(result["runs"], 32)
        for key in ("pearsonHit1", "pearsonMrr", "spearmanHit1", "spearmanMrr"):
            self.assertTrue(-1.0 <= result[key] <= 1.0, key)

    def test_correlations_aSmallTable_areItsPearsonAndSpearman(self):
        table = [("a", 100, 0.9, 0.95), ("b", 200, 0.8, 0.9), ("c", 300, 0.7, 0.85)]

        result = correlate.correlations(table)

        self.assertEqual(result["runs"], 3)
        self.assertAlmostEqual(result["pearsonHit1"], -1.0)
        self.assertAlmostEqual(result["spearmanMrr"], -1.0)


if __name__ == "__main__":
    unittest.main()
