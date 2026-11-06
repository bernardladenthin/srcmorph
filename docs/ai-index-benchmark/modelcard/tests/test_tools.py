# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Tests of run.py, search.py, large.py, report.py and repeat.py that need no network and no model.

Run from the repository root: python -m unittest discover -s docs/ai-index-benchmark/modelcard/tests
"""
import argparse
import io
import json
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

        self.assertTrue(all(r["onOversize"] == "sampleChunks" and r["maxChunks"] == 1 for r in rules))

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
                            for r in chunked))
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

        self.assertEqual(rules, {"java", "sql", "fallback"})

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

        self.assertEqual(config["srcMorph"]["fileNames"], ["Dockerfile", "Dockerfile.*", "Containerfile", "package.json"])

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


if __name__ == "__main__":
    unittest.main()
